package com.example.fastchat

import FragmentImages.FragImageOne
import FragmentImages.FragImageThree
import FragmentImages.FragImageTwo
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class Adapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    private val fragmentsSize = 3

    override fun getItemCount(): Int {
        return fragmentsSize
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> FragImageOne()
            1 -> FragImageTwo()
            2 -> FragImageThree()
            else -> FragImageOne()
        }
    }
}
