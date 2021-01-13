package edu.gvsu.cis.traxy.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import edu.gvsu.cis.traxy.view.MainFragment
import edu.gvsu.cis.traxy.view.MonthlyFragment

class JournalPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> return MainFragment()
            1 -> return MonthlyFragment()
            else -> TODO()
        }
    }
}