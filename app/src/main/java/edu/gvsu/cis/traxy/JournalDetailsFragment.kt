package edu.gvsu.cis.traxy

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.journal_details_fragment.*

class JournalDetailsFragment : Fragment() {

    companion object {
        fun newInstance() = JournalDetailsFragment()
    }

    private lateinit var journalKey:String
    override fun onResume() {
        super.onResume()
        journalKey = arguments?.getString("JOURNAL_KEY","n/a")!!
        println("Key is $journalKey")
        val jData = viewModel.getJournalByKey(journalKey)
        key_text.text = jData?.key
        name_text.text = jData?.name
        location_text.text = jData?.address
        start_date_text.text = jData?.startDate.toString().substring(0,10)
        end_date_text.text = jData?.endDate.toString().substring(0,10)
    }

    private lateinit var viewModel: UserDataViewModel

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

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(requireActivity()).get(UserDataViewModel::class.java)
        // TODO: Use the ViewModel
    }

}