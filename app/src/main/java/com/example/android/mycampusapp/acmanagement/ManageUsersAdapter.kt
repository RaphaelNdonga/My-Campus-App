package com.example.android.mycampusapp.acmanagement

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class ManageUsersAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount() = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> RegularsFragment()
            1 -> AdminsFragment()
            else -> throw IllegalArgumentException("No other fragment should be obtained")
        }
    }
}