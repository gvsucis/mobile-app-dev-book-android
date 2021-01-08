package edu.gvsu.cis.traxy

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

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