package edu.gvsu.cis.traxy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import kotlinx.android.synthetic.main.fragment_pager.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [JournalPagerFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class JournalPagerFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pager, container, false)
    }

    val pageChangeCallback  =  object:ViewPager2.OnPageChangeCallback(){
        override fun onPageSelected(position: Int) {
            val m = bottom_nav.menu.getItem(position)
            m.setChecked(true)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        pager.adapter = JournalPagerAdapter(this)
        pager.registerOnPageChangeCallback(pageChangeCallback)
        bottom_nav.setOnNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.all_journals -> pager.setCurrentItem(0, true)
                R.id.monthly_journals -> pager.setCurrentItem(1, true)
            }
            true
        }
//
//        TabLayoutMediator(tabs, pager) { tab, pos ->
//            when(pos) {
//                0 -> tab.text = "Journals"
//                1 -> tab.text = "Calendar"
//            }
//        }.attach()
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PagerFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            JournalPagerFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}