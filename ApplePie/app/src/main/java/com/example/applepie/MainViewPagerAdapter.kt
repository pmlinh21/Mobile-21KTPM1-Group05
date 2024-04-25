package com.example.tablayout

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.applepie.MainFragment
import com.example.applepie.SearchFragment

// Custom ViewPager2 adapter to handle fragments
class MainViewPagerAdapter(fragment: FragmentActivity) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int {
        return 2
    }
    // Creates and returns the fragment based on its position
    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> SearchFragment()
            1 -> MainFragment()
            else -> {
                Fragment()
            }
        }
    }
}