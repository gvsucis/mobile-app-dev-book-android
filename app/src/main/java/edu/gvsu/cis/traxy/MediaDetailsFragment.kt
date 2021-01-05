package edu.gvsu.cis.traxy

//import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import android.app.Activity
import android.content.Intent
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
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.timepicker.MaterialTimePicker
import kotlinx.android.synthetic.main.fragment_media_details_entry.*
import kotlinx.android.synthetic.main.media_item.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.joda.time.DateTime
import org.joda.time.DateTimeZone


class MediaDetailsFragment : Fragment() {

    val mediaModel by activityViewModels<MediaViewModel>()
    val datePickerDialog = MaterialDatePicker.Builder.datePicker().build()
    val timePickerDialog = MaterialTimePicker.Builder().build()
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

        datePickerDialog.addOnPositiveButtonClickListener {
            mediaModel.mediaDate.value = DateTime(it, DateTimeZone.UTC)
            timePickerDialog.show(parentFragmentManager, "timePick")
        }
        timePickerDialog.addOnPositiveButtonClickListener {
            val updated = mediaModel.mediaDate.value?.run {
                plusHours(timePickerDialog.hour).plusMinutes(timePickerDialog.minute)
            }
            mediaModel.mediaDate.value = updated
        }
        date_time.setOnClickListener {
            datePickerDialog.show(parentFragmentManager, "datePick")
        }
        location.setOnClickListener {
            val placeIntent = Autocomplete.IntentBuilder(
                AutocompleteActivityMode.FULLSCREEN,
                listOf<Place.Field>(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS)
            )
                .setTypeFilter(TypeFilter.ADDRESS)
                .build(requireActivity())
            startActivityForResult(placeIntent, NewJournalFragment.PLACE_REQUEST_CODE)
        }
        confirm_fab.setOnClickListener {
            mediaModel.mediaCaption.value = caption.text.toString()
            upload_progress.visibility = View.VISIBLE
            upload_progress.visibility = View.INVISIBLE
            upload_progress.animate()
            var mType = MediaType.TEXT
            mediaUri?.lastPathSegment?.let {
                when {
                    it.endsWith(".mp4") -> mType = MediaType.VIDEO
                    else -> mType = MediaType.PHOTO
                }
            }
            CoroutineScope(Dispatchers.IO).launch {
                val mediaObj = JournalMedia(
                    caption = caption.text.toString(),
                    date = date_time.text.toString(),
                    type = mType.ordinal,
                    lat = mediaModel.mediaLocation.value?.latLng?.latitude ?: 0.0,
                    lng = mediaModel.mediaLocation.value?.latLng?.longitude ?: 0.0)
                mediaModel.addMediaEntry(mediaObj, mediaUri!!)
                launch(Dispatchers.Main) {
                    Snackbar.make(view, "Media uploaded...", Snackbar.LENGTH_LONG ).show()
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
            if (it.lastPathSegment!!.endsWith(".mp4")) {
                video_view.setVideoURI(it)
                video_view.start()
                val mc = MediaController(requireContext())
                video_view.setMediaController(mc)
                video_view.visibility = View.VISIBLE
                photo_view.visibility = View.INVISIBLE
            } else {
                val istream = requireContext().contentResolver.openInputStream(it)
                val bmp = BitmapFactory.decodeStream(istream)
                photo_view.setImageBitmap(bmp)
                photo_view.visibility = View.VISIBLE
                video_view.visibility = View.INVISIBLE
                istream?.close()
            }

        }
        mediaModel.mediaDate.observe(viewLifecycleOwner) {
            media_date_time.setText(it.toPrettyDateTime())
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == NewJournalFragment.PLACE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            data?.let {
                val place = Autocomplete.getPlaceFromIntent(it)
                mediaModel.mediaLocation.value = place
                location.text = place.name
            }
        } else
            super.onActivityResult(requestCode, resultCode, data)
    }
}