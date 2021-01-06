package edu.gvsu.cis.traxy

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import edu.gvsu.cis.traxy.model.Journal
import kotlinx.android.synthetic.main.fragment_new_journal.*
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter


/**
 * A simple [Fragment] subclass.
 * Use the [NewJournalFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class NewJournalFragment : Fragment(), View.OnFocusChangeListener {
    companion object {
        val PLACE_REQUEST_CODE = 0xACE0
    }

    private var startDate:DateTime? = null
    private var endDate:DateTime? = null
    private var isChoosingStartDate = true
    private lateinit var inputFormatter: DateTimeFormatter
    private lateinit var outputFormatter: DateTimeFormatter
    private val viewModel: UserDataViewModel by activityViewModels<UserDataViewModel>()

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
        trip_name.setOnFocusChangeListener(this)
        trip_location.setOnFocusChangeListener(this)

        trip_location.setOnClickListener {
//            val placeIntent = Autocomplete.IntentBuilder(
//                AutocompleteActivityMode.FULLSCREEN,
//                listOf<Place.Field>(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS)
//            )
//                .setTypeFilter(TypeFilter.ADDRESS)
//                .build(requireActivity())
//            startActivityForResult(placeIntent, PLACE_REQUEST_CODE)
            println("TODO: complete this in a later chapter")
        }

        trip_calendar.setOnDateChangeListener { _, yr, mo, dy ->
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
            this.onFocusChange(null, false)
            trip_duration.text = dateRange()
        }
        add_button.setOnClickListener {
            viewModel.addJournal(Journal("key-???",
                trip_name.text.toString(),
                trip_location.text.toString(),
                startDate?.toString()!!,
                endDate?.toString()!!))
            findNavController().popBackStack()
        }
    }

    override fun onFocusChange(p0: View?, p1: Boolean) {
        add_button.isEnabled = trip_name.text.length > 0 &&
                trip_location.text.length > 0 &&
                startDate != null && endDate != null
    }

}