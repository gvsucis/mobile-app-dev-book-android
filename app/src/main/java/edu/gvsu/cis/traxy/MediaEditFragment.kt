package edu.gvsu.cis.traxy

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.timepicker.MaterialTimePicker
import edu.gvsu.cis.traxy.model.MediaType
import kotlinx.android.synthetic.main.fragment_media_edit.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.joda.time.DateTime
import org.joda.time.DateTimeZone


/**
 * A simple [Fragment] subclass.
 * create an instance of this fragment.
 */
class MediaEditFragment : Fragment() {

    val mediaModel by activityViewModels<MediaViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        setHasOptionsMenu(true)
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_media_edit, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fab_cover_photo.setOnClickListener {
            fab_cover_photo.isSelected = !fab_cover_photo.isSelected
        }
    }

    override fun onResume() {
        super.onResume()
        mediaModel.selectedMedia.observe(viewLifecycleOwner) {
            when (it.type) {
                MediaType.PHOTO.ordinal -> {
                    Glide.with(this@MediaEditFragment)
                        .load(it.url)
                        .into(media_image)
                    media_image.visibility = View.VISIBLE
                }
                MediaType.VIDEO.ordinal -> {
                    Glide.with(this@MediaEditFragment)
                        .load(it.thumbnailUrl)
                        .into(media_image)
                    media_image.visibility = View.VISIBLE
                }
                MediaType.AUDIO.ordinal -> {
                    media_image.visibility = View.INVISIBLE
                }
                else -> media_image.visibility = View.GONE
            }
            try {
                val m_date = DateTime(it.date, DateTimeZone.UTC)
                mediaModel.mediaDate.value = m_date
            } catch (e: Exception) {
                mediaModel.mediaDate.value = DateTime.now()
            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_edit_media, menu)
    }

    private fun undoSavedMedia() = with(mediaModel) {
        restoreMediaCopy()
        selectedMedia.value?.let {
            CoroutineScope(Dispatchers.IO).launch {
                updateJournalMedia(it)
            }
        }

    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_save_media) {
            mediaModel.saveMediaCopy()
            mediaModel.selectedMedia.value?.apply {
                caption = mediaModel.mediaCaption.value ?: "None"
                date = mediaModel.mediaDate.value.toString()
                lat = mediaModel.mediaLocation.value?.latLng?.latitude ?: 0.0
                lng = mediaModel.mediaLocation.value?.latLng?.longitude ?: 0.0
            }?.let {
                CoroutineScope(Dispatchers.IO).launch {
                    mediaModel.updateJournalMedia(it)
                    withContext(Dispatchers.Main) {
                        Snackbar.make(fab_cover_photo, "Updates saved", Snackbar.LENGTH_LONG)
                            .setAction("Undo") { undoSavedMedia() }
                            .show();
                    }
                }
            }
            return true
        }
        return false
    }

}