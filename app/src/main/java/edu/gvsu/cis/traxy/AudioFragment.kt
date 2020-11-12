package edu.gvsu.cis.traxy

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.*
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.getSystemService
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import edu.gvsu.cis.traxy.model.JournalMedia
import edu.gvsu.cis.traxy.model.MediaType
import kotlinx.android.synthetic.main.fragment_audio.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException


private const val RECORD_AUDIO_PERM_REQUEST = 317

fun Int.toMinuteSecond(): String {
    var seconds = this
    val h = seconds / 3600
    seconds %= 3600
    val m = seconds / 60
    seconds %= 60
    return String.format("%02d:%02d", m, seconds)
}

class AudioFragment : Fragment() {
    enum class Status { START, RECORDING, RECORD_STOP, PLAYING, PLAY_PAUSE }

    private var audioRec: MediaRecorder? = null
    private var audioPlay: MediaPlayer? = null
    private lateinit var audioMgr: AudioManager
    private lateinit var audioFocusRequest: AudioFocusRequest
    private var currentState = Status.START
    private var recordPermissionGranted = false
    private var playbackAuthorized = false
//    private var playbackDelayed = false
//    private var resumePlaybackAfterFocus = false
    private val mediaModel by activityViewModels<MediaViewModel>()
    val focusLock = Any()

    val myHandler = Handler()
    var elapse_time = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_audio, container, false)
    }

    private val myRunner: Runnable by lazy {
        Runnable {
            elapse_time++
            time_marker.setText(elapse_time.toMinuteSecond())
            myHandler.postDelayed(myRunner, 1000)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        leftBtn.visibility = View.INVISIBLE
        rightBtn.visibility = View.INVISIBLE

        requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), RECORD_AUDIO_PERM_REQUEST)
        leftBtn.setOnClickListener {
            currentState = Status.START
            audio_state.setText("")
            it.visibility = View.GONE
            centerBtn.setImageResource(R.drawable.ic_baseline_fiber_manual_record_24)
            with(rightBtn) {
                visibility = View.GONE
                setImageResource(R.drawable.ic_baseline_play_arrow_24)
            }
        }
        centerBtn.setOnClickListener {
            when (currentState) {
                Status.START -> {
                    startRecording()
                    currentState = Status.RECORDING
                    elapse_time = 0
                    myHandler.post(myRunner)
                }
                Status.RECORDING -> {
                    stopRecording()
                    currentState = Status.RECORD_STOP
                    myHandler.removeCallbacks(myRunner)
                    elapse_time = 0
                }
                Status.RECORD_STOP, Status.PLAY_PAUSE ->
                    findNavController().popBackStack()
            }
        }
        rightBtn.setOnClickListener {
            when (currentState) {
                Status.PLAYING -> {
                    pausePlayback()
                    currentState = Status.PLAY_PAUSE
                    myHandler.removeCallbacks(myRunner)
                }
                else -> {
                    startPlayback()
                    currentState = Status.PLAYING
                    myHandler.post(myRunner)
                }
            }
        }
        saveBtn.setOnClickListener {
            with(mediaModel) {
                val mediaObj = JournalMedia(
                    caption = mediaCaption.value ?: "None",
                    date = mediaDate.value.toString(),
                    type = MediaType.AUDIO.ordinal,
                    lat = mediaLocation.value?.latLng?.latitude ?: 0.0,
                    lng = mediaLocation.value?.latLng?.longitude ?: 0.0
                )
                CoroutineScope(Dispatchers.IO).launch {
                    mediaModel.addMediaEntry(mediaObj, mediaUri.value!!)
                }
                findNavController().popBackStack()
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        audioMgr = requireActivity().getSystemService(Context.AUDIO_SERVICE) as AudioManager
    }

    override fun onResume() {
        super.onResume()
        time_marker.setText(0.toMinuteSecond())
    }

    override fun onPause() {
        super.onPause()
        if (audioRec != null) stopRecording()
        if (audioPlay != null) pausePlayback()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == RECORD_AUDIO_PERM_REQUEST) {
            recordPermissionGranted = grantResults[0] == PackageManager.PERMISSION_GRANTED
        }
        if (!recordPermissionGranted)
            findNavController().popBackStack()
    }

    private fun startRecording() {
        audioRec = MediaRecorder()
        with(audioRec!!) {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            mediaModel.mediaFile.value?.let {
                setOutputFile(it.absolutePath)
            }

            try {
                centerBtn.setImageResource(R.drawable.ic_baseline_stop_24)
                prepare()
                start()
            } catch (e: IOException) {
                Snackbar.make(centerBtn, "Unable to initialize MediaRecorder:" + e.message,
                    Snackbar.LENGTH_LONG)
                    .show()
            }
        }
        audio_state.setText("Recording")
    }

    private fun stopRecording() {
        audioRec?.apply {
            stop()
            release()
        }
        audioRec = null
        centerBtn.setImageResource(R.drawable.ic_baseline_done_24)
        leftBtn.visibility = View.VISIBLE
        rightBtn.visibility = View.VISIBLE
        audio_state.setText("Recorded")
    }

    val afChangeListener = AudioManager.OnAudioFocusChangeListener {focusChange ->
        when(focusChange) {
            AudioManager.AUDIOFOCUS_GAIN -> startPlayback()
            AudioManager.AUDIOFOCUS_LOSS -> pausePlayback()
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> pausePlayback()
            else -> {}
        }
    }
    private fun initAudioPlayer() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            audioFocusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN).run {
                setAudioAttributes(AudioAttributes.Builder().run {
                    setUsage(AudioAttributes.USAGE_MEDIA)
                    setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    build()
                })
                setAcceptsDelayedFocusGain(false)
                setOnAudioFocusChangeListener (afChangeListener)
                build()

            }
            val result = audioMgr.requestAudioFocus(audioFocusRequest)
            playbackAuthorized = result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
        } else {
            val result = audioMgr.requestAudioFocus(afChangeListener,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN)
            playbackAuthorized = result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
        }
        audioPlay = MediaPlayer().apply {
            val audioAttr = AudioAttributes.Builder().run {
                setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                setUsage(AudioAttributes.USAGE_MEDIA)
                build()
            }
            setAudioAttributes(audioAttr)
            setOnCompletionListener {
                currentState = Status.PLAY_PAUSE
                rightBtn.setImageResource(R.drawable.ic_baseline_play_arrow_24)
                myHandler.removeCallbacks(myRunner)
                if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.O)
                    audioMgr.abandonAudioFocus (afChangeListener)
            }
            mediaModel.mediaUri.value?.let {
                setDataSource(requireContext(), it)
                prepare()
            }
        }
    }

    private fun startPlayback() {
        try {
            if (audioPlay == null)
                initAudioPlayer()
            if (playbackAuthorized) {
                audioPlay?.start()
                rightBtn.setImageResource(R.drawable.ic_baseline_pause_24)
            }
        } catch (ioe: IOException) {
            // error handling
        }
        audio_state.setText("Playing")
    }

    private fun pausePlayback() {
        rightBtn.setImageResource(R.drawable.ic_baseline_play_arrow_24)
        audioPlay?.pause()
        audio_state.setText("Paused")
    }
}