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
import com.google.android.material.timepicker.MaterialTimePicker
import kotlinx.android.synthetic.main.fragment_journal_media_edit.*
import kotlinx.android.synthetic.main.fragment_new_journal.*
import org.joda.time.DateTime
import org.joda.time.DateTimeZone


/**
 * A simple [Fragment] subclass.
 * create an instance of this fragment.
 */
class JournalMediaEditFragment : Fragment() {

    val mediaModel by activityViewModels<MediaViewModel>()
    val datePicker = MaterialDatePicker.Builder.datePicker().build()
    val timePicker = MaterialTimePicker.Builder().build()
    val PLACE_REQUEST_CODE = 0xACE0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        setHasOptionsMenu(true)
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_journal_media_edit, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fab_cover_photo.setOnClickListener {
            fab_cover_photo.isSelected = !fab_cover_photo.isSelected
        }
        datePicker.addOnPositiveButtonClickListener {
            mediaModel.mediaDate.value = DateTime(it, DateTimeZone.UTC)
            timePicker.show(parentFragmentManager, "MediaTime")
        }
        timePicker.addOnPositiveButtonClickListener {
            val updated = mediaModel.mediaDate.value?.run {
                plusHours(timePicker.hour).plusMinutes(timePicker.minute)
            }
            mediaModel.mediaDate.value = updated
        }

        media_date_time.setOnClickListener {
            datePicker.show(parentFragmentManager, "MediaDate")
        }
        media_location.setOnClickListener {
            val placeIntent = Autocomplete.IntentBuilder(
                AutocompleteActivityMode.FULLSCREEN,
                listOf<Place.Field>(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG)
            )
                .setTypeFilter(TypeFilter.ADDRESS)
                .build(requireActivity())
            startActivityForResult(placeIntent, PLACE_REQUEST_CODE)
        }
    }

    override fun onResume() {
        super.onResume()
        mediaModel.selectedMedia.observe(viewLifecycleOwner) {
            when (it.type) {
                MediaType.PHOTO.ordinal -> {
                    Glide.with(this@JournalMediaEditFragment)
                        .load(it.url)
                        .into(media_image)
                    media_image.visibility = View.VISIBLE
                }
                MediaType.VIDEO.ordinal -> {
                    Glide.with(this@JournalMediaEditFragment)
                        .load(it.thumbnailUrl)
                        .into(media_image)
                    media_image.visibility = View.VISIBLE
                }
                else -> media_image.visibility = View.GONE
            }
            media_caption.setText(it.caption)
            media_date_time.setText(it.date)
        }
        mediaModel.selectedJournal.observe(viewLifecycleOwner) {
            media_location.setText (it.address)
        }
        mediaModel.mediaDate.observe(viewLifecycleOwner) {
            media_date_time.setText(it.toPrettyDateTime())
        }
        mediaModel.mediaLocation.observe(viewLifecycleOwner) {
            media_location.setText(it.address)
        }
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_edit_media, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_save_media) {
            mediaModel.mediaCaption.value = media_caption.text.toString()
            mediaModel.selectedMedia.value?.apply {
                caption = media_caption.text.toString()
                date = mediaModel.mediaDate.value.toString()
                lat = mediaModel.mediaLocation.value?.latLng?.latitude ?: 0.0
                lng = mediaModel.mediaLocation.value?.latLng?.longitude ?: 0.0
            }?.let {
                mediaModel.updateJournalMedia(it)
            }
            return true
        }
        return false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == PLACE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            data?.let {
                val place = Autocomplete.getPlaceFromIntent(it)
                media_location.setText(place.name)
                mediaModel.mediaLocation.value = place
            }
        } else
            super.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }
}