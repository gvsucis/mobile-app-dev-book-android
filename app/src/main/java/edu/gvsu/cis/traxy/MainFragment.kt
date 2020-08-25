package edu.gvsu.cis.traxy

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.content_main.view.*
import org.joda.time.DateTime
import kotlin.random.Random

class MainFragment : Fragment() {
    // "lateinit" is required when the variable is not initialized
    // inside a constructor

    lateinit var viewModel: UserDataViewModel
    lateinit var adapter: JournalAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = JournalAdapter()
        with(view) {
            journal_list.adapter = adapter
            val layoutMgr = LinearLayoutManager(context)
            journal_list.layoutManager = layoutMgr
            journal_list.addItemDecoration(DividerItemDecoration(context, layoutMgr.orientation))
        }
        val today = DateTime.now()
        val rand = Random(0)

        adapter.submitList(List(100) {
            val startOn = today.plusDays(rand.nextInt(-100,100))
            val endOn = startOn.plusDays(1 + rand.nextInt(7))
            Journal("key-$it", "Name $it", "Location $it", startOn, endOn)
        })
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(requireActivity()).get(UserDataViewModel::class.java)
        viewModel.userId.observe(this.viewLifecycleOwner, Observer { z ->
//            userEmail.text = z
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_main, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_logout) {
            findNavController().navigate(R.id.action_logout)
            return true
        }
        return false
    }
}