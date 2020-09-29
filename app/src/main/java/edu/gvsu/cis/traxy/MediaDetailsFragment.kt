package edu.gvsu.cis.traxy

import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.observe
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import kotlinx.android.synthetic.main.fragment_media_details_entry.*
import kotlinx.android.synthetic.main.fragment_new_journal.*
import org.joda.time.DateTime
import org.joda.time.LocalDate
import java.util.*
import kotlin.concurrent.fixedRateTimer


class MediaDetailsFragment : Fragment() {

    val mediaModel by activityViewModels<MediaViewModel>()
    lateinit var datePickerDialog: DatePickerDialog
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

        }
    }

    override fun onResume() {
        super.onResume()
        mediaModel.photoUri.observe(viewLifecycleOwner) {
            val istream = requireContext().contentResolver.openInputStream(it)
            val bmp = BitmapFactory.decodeStream(istream)
            photo_view.setImageBitmap(bmp)
            istream?.close()

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == NewJournalFragment.PLACE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            data?.let {
                val place = Autocomplete.getPlaceFromIntent(it)
                location.text = place.name
            }
        } else
            super.onActivityResult(requestCode, resultCode, data)
    }
}