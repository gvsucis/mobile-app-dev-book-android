package edu.gvsu.cis.traxy

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.material.snackbar.Snackbar
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import kotlinx.android.synthetic.main.fragment_media_details_entry.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.joda.time.LocalDate


class MediaDetailsFragment : Fragment() {

    val mediaModel by activityViewModels<MediaViewModel>()
    lateinit var datePickerDialog: DatePickerDialog
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

        datePickerDialog = DatePickerDialog.newInstance { _, year, month, day ->
            val eventDate = LocalDate.parse("%4d-%02d-%02d".format(year, month + 1, day))
            date_time.text = eventDate.toString()
            mediaModel.mediaDate.value = eventDate.toString()
        }
        datePickerDialog.version = DatePickerDialog.Version.VERSION_2
        date_time.setOnClickListener {
            datePickerDialog.show(requireFragmentManager(), "datePick")
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
            upload_progress.animate()
            CoroutineScope(Dispatchers.IO).launch {
                val mediaObj = JournalMedia(
                    caption = caption.text.toString(),
                    date = date_time.text.toString(),
                    type = MediaType.PHOTO,
                    lat = mediaModel.mediaLocation.value?.latLng?.latitude ?: 0.0,
                    lng = mediaModel.mediaLocation.value?.latLng?.longitude ?: 0.0)
                mediaModel.addMediaEntry(mediaObj, mediaUri!!)
                upload_progress.visibility = View.INVISIBLE
                Snackbar.make(confirm_fab, "Media uploaded...", Snackbar.LENGTH_LONG ).show()
//                findNavController().popBackStack()
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