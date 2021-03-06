package edu.gvsu.cis.traxy

import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import edu.gvsu.cis.traxy.model.JournalMedia
import edu.gvsu.cis.traxy.model.MediaType
import kotlinx.android.synthetic.main.fragment_media_details_entry.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MediaDetailsFragment : Fragment() {

    val mediaModel by activityViewModels<MediaViewModel>()
    private var mediaUri: Uri? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_media_details_entry, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        confirm_fab.setOnClickListener {
//            mediaModel.mediaCaption.value = caption.text.toString()
            upload_progress.visibility = View.VISIBLE
            upload_progress.visibility = View.INVISIBLE
            upload_progress.animate()
            var mType = MediaType.TEXT
            mediaUri?.lastPathSegment?.let {
                when {
                    it.endsWith(".mp4") -> mType = MediaType.VIDEO
                    it.endsWith(".m4a") -> mType = MediaType.AUDIO
                    else -> mType = MediaType.PHOTO
                }
            }
            CoroutineScope(Dispatchers.IO).launch {
                val mediaObj = JournalMedia(
                    caption = mediaModel.mediaCaption.value ?: "None",
                    date = mediaModel.mediaDate.value?.toString() ?: "None",
                    type = mType.ordinal,
                    url = mediaModel.mediaUri.value?.toString() ?: "",
                    lat = mediaModel.selectedJournal.value?.lat ?: 0.0,
                    lng = mediaModel.selectedJournal.value?.lng ?: 0.0)
                if (mediaObj.lat != 0.0 && mediaObj.lng != 0.0) {
                    val weatherData = TraxyRepository.getWeatherData(mediaObj.lat, mediaObj.lng)
                    mediaObj.temperature = weatherData?.first
                    mediaObj.weatherIcon = weatherData?.second
                }
                mediaModel.addMediaEntry(mediaObj, mediaUri!!)
                launch(Dispatchers.Main) {
                    Snackbar.make(confirm_fab, "Media uploaded...", Snackbar.LENGTH_LONG).show()
                    findNavController().popBackStack()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        confirm_fab.isEnabled = false
        mediaModel.mediaUri.observe(viewLifecycleOwner) {
            mediaUri = it
            confirm_fab.isEnabled = true
            it.lastPathSegment?.let { path ->
                when {
                    path.endsWith(".mp4") -> {
                        video_view.setVideoURI(it)
                        video_view.start()
                        val mc = MediaController(requireContext())
                        video_view.setMediaController(mc)
                        video_view.visibility = View.VISIBLE
                        photo_view.visibility = View.INVISIBLE
                    }
                    path.endsWith(".m4a") -> {
                        video_view.visibility = View.INVISIBLE
                        photo_view.visibility = View.INVISIBLE
                    }

                    else -> {
                        val istream = requireContext().contentResolver.openInputStream(it)
                        val bmp = BitmapFactory.decodeStream(istream)
                        photo_view.setImageBitmap(bmp)
                        photo_view.visibility = View.VISIBLE
                        video_view.visibility = View.INVISIBLE
                        istream?.close()
                    }
                }
            }
//        mediaModel.mediaDate.observe(viewLifecycleOwner) {
//            date_time.setText(it.toPrettyDateTime())
//        }
        }
    }
}