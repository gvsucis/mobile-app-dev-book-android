package edu.gvsu.cis.traxy

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_audio.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.*
import kotlin.concurrent.timerTask

private const val RECORD_AUDIO_PERM_REQUEST = 317

fun Int.toHMS() : String {
    var seconds = this
    val h = seconds / 3600
    seconds %= 3600
    val m = seconds / 60
    seconds %= 60
    return h.toString().padStart(2, '0') +
            m.toString().padStart(2, '0') +
            seconds.toString().padStart(2, '0')
}

class AudioFragment : Fragment() {
    enum class Status { START, RECORDING, RECORD_STOP, PLAYING, PLAY_PAUSE }

    private var audioRec: MediaRecorder? = null
    private var currentState = Status.START
    private var recordPermissionGranted = false
    private val mediaModel by activityViewModels<MediaViewModel>()

    val timer = Timer()
    var elapse_time = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_audio, container, false)
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
                    audio_state.setText("Recording")
                    elapse_time = 0
                    timer.scheduleAtFixedRate(timerTask {
                        lifecycleScope.launch(Dispatchers.Main) {
                            elapse_time++
                            time_marker.setText(elapse_time.toHMS())

                        }
                    }, 0, 1000)
                }
                Status.RECORDING -> {
                    timer.cancel()
                    stopRecording()
                    currentState = Status.RECORD_STOP
                    audio_state.setText("Recorded")
                }
                Status.RECORD_STOP, Status.PLAY_PAUSE ->
                    findNavController().popBackStack()
            }
        }
        rightBtn.setOnClickListener {
            when (currentState) {
                Status.PLAYING -> {
                    // pausePlayback()
                    currentState = Status.PLAY_PAUSE
                    audio_state.setText("Paused")
                }
                else -> {
                    // startPlayback()
                    currentState = Status.PLAYING
                    audio_state.setText("Playing")

                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        time_marker.setText("00:00:00")
    }

    override fun onPause() {
        super.onPause()
        if (audioRec != null) stopRecording()
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
    }
}