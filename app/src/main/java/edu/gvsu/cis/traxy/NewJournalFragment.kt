package edu.gvsu.cis.traxy

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import kotlinx.android.synthetic.main.fragment_new_journal.*
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter


/**
 * A simple [Fragment] subclass.
 * Use the [NewJournalFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class NewJournalFragment : Fragment() /*, View.OnFocusChangeListener*/ {
    companion object {
        val PLACE_REQUEST_CODE = 0xACE0
    }

    private var startDate:DateTime? = null
    private var endDate:DateTime? = null
    private var isChoosingStartDate = true
    private lateinit var inputFormatter: DateTimeFormatter
    private lateinit var outputFormatter: DateTimeFormatter
    private lateinit var viewModel: UserDataViewModel

    private fun dateRange(): String {
        if (startDate == null) return ""
        val s = startDate!!.toString(outputFormatter)
        if (endDate == null) return "($s to ??)"
        val e = endDate!!.toString(outputFormatter)
        return "($s to $e)"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inputFormatter = DateTimeFormat.forPattern("yyyy/M/d")
        outputFormatter = DateTimeFormat.forPattern("yyyy-MMM-dd")
    }

    override fun onResume() {
        super.onResume()
        isChoosingStartDate = true
        val d = DateTime.now()
        trip_calendar.date = d.toDate().time
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_new_journal, container, false)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == PLACE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            data?.let {
                val place = Autocomplete.getPlaceFromIntent(it)
                trip_location.text.clear();
                trip_location.text.insert(0, place.name)
            }
        } else
            super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        trip_name.setOnFocusChangeListener(this)
//        trip_location.setOnFocusChangeListener(this)

        trip_location.setOnClickListener {
            val placeIntent = Autocomplete.IntentBuilder(
                AutocompleteActivityMode.FULLSCREEN,
                listOf<Place.Field>(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS)
            )
                .setTypeFilter(TypeFilter.ADDRESS)
                .build(requireActivity())
            startActivityForResult(placeIntent, PLACE_REQUEST_CODE)

        }

        trip_calendar.setOnDateChangeListener { calendarView, yr, mo, dy ->
            val d:DateTime = inputFormatter.parseDateTime("$yr/${mo + 1}/$dy")
            trip_calendar.date = d.toDate().time
            if (isChoosingStartDate) {
                startDate = d
                isChoosingStartDate = !isChoosingStartDate
            } else {
                if (d.isAfter(startDate)) {
                    endDate = d
                    isChoosingStartDate = !isChoosingStartDate
                }
            }
//            this.onFocusChange(null, false)
            trip_duration.text = dateRange()
        }
        add_button.setOnClickListener {
            viewModel.addJournal(Journal("key-???",
                trip_name.text.toString(),
                trip_location.text.toString(),
                startDate!!,
                endDate!!))
            findNavController().popBackStack()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(requireActivity()).get(UserDataViewModel::class.java)
    }
//    override fun onFocusChange(p0: View?, p1: Boolean) {
//        add_button.isEnabled = trip_name.text.length > 0 &&
//                trip_location.text.length > 0 &&
//                startDate.length > 0 && endDate.length > 0
//    }

}