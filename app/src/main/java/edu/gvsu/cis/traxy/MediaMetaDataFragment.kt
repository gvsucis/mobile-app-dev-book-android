package edu.gvsu.cis.traxy

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import kotlinx.android.synthetic.main.fragment_media_meta_data.*
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

class MediaMetaDataFragment : Fragment() {
    val mediaModel by activityViewModels<MediaViewModel>()
    private lateinit var datePickerDialog: MaterialDatePicker<Long>
    val timePickerDialog = MaterialTimePicker.Builder().build()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_media_meta_data, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val datePickerBuilder = MaterialDatePicker.Builder.datePicker()
        var constraint: CalendarConstraints? = null
        mediaModel.selectedJournal.value?.let {
            val dateConstraints = CalendarConstraints.Builder()
            val start = DateTime(it.startDate)
            val end = DateTime(it.endDate)
            dateConstraints.setStart(start.millis)
            dateConstraints.setEnd(end.millis)
            constraint = dateConstraints.build()
        }

        if (constraint != null)
            datePickerDialog = datePickerBuilder.setCalendarConstraints(constraint).build()
        else
            datePickerDialog = datePickerBuilder.build()
        datePickerDialog.addOnPositiveButtonClickListener {
            mediaModel.mediaDate.value = DateTime(it, DateTimeZone.UTC)
            timePickerDialog.show(parentFragmentManager, "timePick")
        }
        timePickerDialog.addOnPositiveButtonClickListener {
            val updated = mediaModel.mediaDate.value?.run {
                plusHours(timePickerDialog.hour).plusMinutes(timePickerDialog.minute)
            }
            mediaModel.mediaDate.value = updated
            date_time.text = updated?.toPrettyDateTime()
        }
        date_time.setOnClickListener {
            datePickerDialog.show(parentFragmentManager, "datePick")
        }
        location.setOnClickListener {
//            val placeIntent = Autocomplete.IntentBuilder(
//                AutocompleteActivityMode.FULLSCREEN,
//                listOf<Place.Field>(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS,
//                    Place.Field.LAT_LNG)
//            )
//                .setTypeFilter(TypeFilter.ADDRESS)
//                .build(requireActivity())
//            startActivityForResult(placeIntent, NewJournalFragment.PLACE_REQUEST_CODE)
            println("TODO: will be completed in a later chapter")
        }
        caption.addTextChangedListener {
            mediaModel.mediaCaption.value = caption.text.toString()
        }
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        if (requestCode == NewJournalFragment.PLACE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
//            data?.let {
//                val place = Autocomplete.getPlaceFromIntent(it)
//                place.latLng?.run {
//                    GlobalScope.launch(Dispatchers.IO) {
//                        val weatherData = TraxyRepository.getWeatherData(latitude, longitude)
//                        mediaModel.weatherTemperature.postValue(weatherData?.first)
//                        mediaModel.weatherConditionIcon.postValue(weatherData?.second)
//                    }
//                }
//                mediaModel.mediaLocation.value = place
//                location.text = place.name
//            }
//        } else
//            super.onActivityResult(requestCode, resultCode, data)
//    }

}