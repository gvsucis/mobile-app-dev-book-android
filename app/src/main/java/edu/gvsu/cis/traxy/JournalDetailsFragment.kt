package edu.gvsu.cis.traxy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.journal_details_fragment.*

class JournalDetailsFragment : Fragment() {
    private val viewModel by activityViewModels<UserDataViewModel>()

    companion object {
        fun newInstance() = JournalDetailsFragment()
    }

    private lateinit var journalKey:String
    override fun onResume() {
        super.onResume()
        journalKey = arguments?.getString("JOURNAL_KEY","n/a")!!
        println("Key is $journalKey")
        viewModel.getJournalByKey(journalKey)?.let {
            key_text.text = it.key
            name_text.text = it.name
            location_text.text = it.address
            start_date_text.text = it.startDate.toString().substring(0, 10)
            end_date_text.text = it.endDate.toString().substring(0, 10)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.journal_details_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hide_ne.setOnClickListener { findNavController().popBackStack() }
    }

}