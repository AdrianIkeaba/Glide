package com.example.fastchat

import HomeFragments.CallFrag
import HomeFragments.ChannelFrag
import HomeFragments.ChatFrag
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class HomeAdapter(fragmentActivity: FragmentActivity): FragmentStateAdapter(fragmentActivity) {
    private val fragmentSize = 3
    override fun getItemCount(): Int {
        return fragmentSize
    }

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> ChatFrag()
            1 -> ChannelFrag()
            2 -> CallFrag()
            else -> ChatFrag()
        }
    }
}