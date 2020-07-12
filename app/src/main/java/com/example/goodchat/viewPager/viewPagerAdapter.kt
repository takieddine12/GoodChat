package com.example.goodchat.viewPager

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.goodchat.fragments.FriendsFragment
import com.example.goodchat.fragments.HistoryFragment
import com.example.goodchat.fragments.PostsFragment
import com.example.goodchat.fragments.ProfileFragment

class viewPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    private val TABS = 4

    override fun getItemCount(): Int {
        return TABS
    }

    override fun createFragment(position: Int): Fragment {
        when(position){
            0 -> {
                return HistoryFragment()
            }
            1 -> {
                return FriendsFragment()
            }
            2 -> {
               return  PostsFragment()
            }
            3 -> {
               return  ProfileFragment()
            }
        }
        return HistoryFragment()
    }
}