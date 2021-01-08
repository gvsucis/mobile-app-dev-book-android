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
import edu.gvsu.cis.traxy.model.Header
import kotlinx.android.synthetic.main.content_main.view.*
import org.joda.time.DateTime

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = JournalAdapter(R.layout.journal_card_mini) {
            mediaModel.selectedJournal.value = it
            val action = JournalPagerFragmentDirections.actionToMediaList(it.name)
            findNavController().navigate(action)
        }
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
            val partitioned = it
                .map {
                    val now = DateTime.now().toString()
                    when {
                        it.endDate < now -> "Past" to it
                        it.startDate > now -> "Future" to it
                        else -> "Current" to it
                    }
                }.sortedBy { it.first }
                .groupBy({ it.first }, { it.second })
                .flatMap {
                    listOf(Header(it.key)) + it.value.sortedBy { it.startDate }
                }
            adapter.submitList(partitioned)
        })

    }
}