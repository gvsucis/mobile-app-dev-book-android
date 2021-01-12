package edu.gvsu.cis.traxy

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.applandeo.materialcalendarview.CalendarUtils
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import edu.gvsu.cis.traxy.model.Journal
import edu.gvsu.cis.traxy.model.JournalMedia
import edu.gvsu.cis.traxy.model.MediaType
import kotlinx.android.synthetic.main.fragment_edit_journal.*
import kotlinx.android.synthetic.main.fragment_monthly.*
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter


/**
 * A simple [Fragment] subclass.
 * Use the [JournalEditFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class JournalEditFragment : Fragment(), View.OnFocusChangeListener {
    companion object {
        val PLACE_REQUEST_CODE = 0xACE0
    }

    private var startDate: DateTime? = null
    private var endDate: DateTime? = null
    private var isEditing = false
    private lateinit var inputFormatter: DateTimeFormatter
    private lateinit var outputFormatter: DateTimeFormatter
    private val viewModel: UserDataViewModel by activityViewModels<UserDataViewModel>()
    private val mediaModel: MediaViewModel by activityViewModels<MediaViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inputFormatter = DateTimeFormat.forPattern("yyyy/M/d")
        outputFormatter = DateTimeFormat.forPattern("yyyy-MMM-dd")
    }

    override fun onResume() {
        super.onResume()
        if (mediaModel.selectedJournal.value == null) {
            isEditing = false
            photo_list.visibility = View.GONE
        }
        else {
            isEditing = true
            mediaModel.selectedJournal.value?.let {
                trip_name.setText(it.name)
                trip_location.setText(it.address)
                val start = DateTime(it.startDate).minusDays(1).toGregorianCalendar()
                val end = DateTime(it.endDate).plusDays(1).toGregorianCalendar()
                val dateRange = CalendarUtils.getDatesRange(start, end)
                trip_calendar.setDate(start)
                trip_calendar.selectedDates = dateRange
            }
            photo_list.visibility = View.VISIBLE
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_edit_journal, container, false)
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
            val placeIntent = Autocomplete.IntentBuilder(
                AutocompleteActivityMode.FULLSCREEN,
                listOf<Place.Field>(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS)
            )
                .setTypeFilter(TypeFilter.CITIES)
                .build(requireActivity())
            startActivityForResult(placeIntent, PLACE_REQUEST_CODE)

        }

        trip_calendar.setOnDayClickListener {
            val current = DateTime(it.calendar)
            val range = trip_calendar.selectedDates
            if (range.size > 0) {
                val last = DateTime(range[0])
                if (current.isBefore(last)) {
                    startDate = current
                    endDate = last
                } else {
                    startDate = last
                    endDate = current
                }
                add_button.isEnabled = trip_name.text.length > 0 &&
                        trip_location.text.length > 0

            }
        }

        add_button.setOnClickListener {
            if (isEditing) {
                mediaModel.selectedJournal.value?.let {
                    viewModel.updateJournal(it)
                }
            }
            else {
                viewModel.addJournal(Journal(key = "key-???",
                    name = trip_name.text.toString(),
                    address = trip_location.text.toString(),
                    startDate = startDate?.toString()!!,
                    endDate = endDate?.toString()!!))
            }
            findNavController().popBackStack()
        }
        photo_list.layoutManager = GridLayoutManager(requireContext(), 3)
        mediaModel.selectedJournal.observe(viewLifecycleOwner, Observer {
            val photoQuery = mediaModel.mediaQuery().whereEqualTo("type", MediaType.PHOTO.ordinal)
            val option = FirestoreRecyclerOptions.Builder<JournalMedia>()
                .setLifecycleOwner(viewLifecycleOwner)
                .setQuery(photoQuery, JournalMedia::class.java)
                .build()
            photo_list.adapter = PhotoAdapter(option) {
                mediaModel.selectedJournal.value?.apply {
                    coverPhotoUrl = it.url
                }
            }
        })
    }

    override fun onFocusChange(p0: View?, p1: Boolean) {
        add_button.isEnabled = trip_name.text.length > 0 &&
                trip_location.text.length > 0 &&
                startDate != null && endDate != null
    }

}