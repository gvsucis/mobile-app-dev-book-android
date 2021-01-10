package edu.gvsu.cis.traxy

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import edu.gvsu.cis.traxy.model.Header
import kotlinx.android.synthetic.main.content_main.view.*
import kotlinx.android.synthetic.main.fragment_main.*
import org.joda.time.DateTime

class MainFragment : Fragment() {
    // "lateinit" is required when the variable is not initialized
    // inside a constructor

    private val viewModel by activityViewModels<UserDataViewModel>()
    private val mediaModel by activityViewModels<MediaViewModel>()
    private lateinit var adapter: JournalAdapter
//    val args by navArgs<MainFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = JournalAdapter(R.layout.journal_card_tall) { jdata, action ->
            mediaModel.selectedJournal.value = jdata
            val destination = if (action == "VIEW")
                JournalPagerFragmentDirections.actionToMediaList(jdata.name)
            else
                JournalPagerFragmentDirections.actionEditJournal(jdata.name)
            findNavController().navigate(destination)
        }
        with(view) {
            journal_list.adapter = adapter
            val layoutMgr = LinearLayoutManager(context)
            journal_list.layoutManager = layoutMgr
            journal_list.addItemDecoration(DividerItemDecoration(context, layoutMgr.orientation))
        }
        fab_add.setOnClickListener {
            // Reset selectedJournal to null to the target fragment knows that we are
            // inserting a new journal
            mediaModel.selectedJournal.value = null
            val action = JournalPagerFragmentDirections.actionEditJournal("New Journal")
            findNavController().navigate(action)
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_main, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_logout) {
            viewModel.signOut()
            findNavController().navigate(R.id.action_logout)
            return true
        }
        return false
    }
}