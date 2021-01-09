package edu.gvsu.cis.traxy

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.applandeo.materialcalendarview.listeners.OnCalendarPageChangeListener
import edu.gvsu.cis.traxy.model.Header
import edu.gvsu.cis.traxy.model.Journal
import kotlinx.android.synthetic.main.content_main.view.*
import kotlinx.android.synthetic.main.fragment_monthly.*
import org.joda.time.DateTime
import org.joda.time.Interval
import java.time.Month
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

/**
 * A simple [Fragment] subclass.
 * Use the [MonthlyFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MonthlyFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private val viewModel by activityViewModels<UserDataViewModel>()
    private val mediaModel by activityViewModels<MediaViewModel>()

    private lateinit var adapter: JournalAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_monthly, container, false)
    }

    val monthChangeListener = OnCalendarPageChangeListener {
        viewModel.remoteJournals.value?.let {
            filterByCurrentMonth(it)
        }
    }

    private fun filterByCurrentMonth(input: List<Journal>) {
        // Step 1: get the current month
        val thisMonth = DateTime(calendar.currentPageDate)
        // Step 2: create the date interval representing the current month
        val d1 = thisMonth.dayOfMonth().dateTime
        val d2 = thisMonth.plusMonths(1).dayOfMonth().dateTime
        val range = Interval(d1, d2)
        // Step 3: filter the input based of its date range
        val filtered = input.filter {
            val jStart = DateTime(it.startDate)
            val jEnd = DateTime(it.endDate)
            jStart.isBefore(jEnd) && Interval(jStart, jEnd).overlaps(range)
        }
        adapter.submitList(filtered)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = JournalAdapter(R.layout.journal_card_mini) { journal, action ->
            mediaModel.selectedJournal.value = journal
            val action = if (action == "VIEW")
                JournalPagerFragmentDirections.actionToMediaList(journal.name)
            else
                JournalPagerFragmentDirections.actionEditJournal()
            findNavController().navigate(action)
        }
        calendar.setOnForwardPageChangeListener(monthChangeListener)
        calendar.setOnPreviousPageChangeListener(monthChangeListener)
        with(view) {
            journal_list.adapter = adapter
            val layoutMgr = LinearLayoutManager(context)
            journal_list.layoutManager = layoutMgr
            journal_list.addItemDecoration(DividerItemDecoration(context, layoutMgr.orientation))
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.remoteJournals.observe(this.viewLifecycleOwner, Observer {
            filterByCurrentMonth(it)
        })

    }
}