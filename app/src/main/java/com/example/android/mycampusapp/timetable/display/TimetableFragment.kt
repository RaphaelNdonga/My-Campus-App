package com.example.android.mycampusapp.timetable.display

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.android.mycampusapp.R
import com.example.android.mycampusapp.databinding.FragmentTimetableBinding
import com.google.android.material.tabs.TabLayoutMediator

class TimetableFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil.inflate<FragmentTimetableBinding>(
            inflater,
            R.layout.fragment_timetable,
            container,
            false
        )
        val timetableAdapter =
            TimetableViewPagerAdapter(
                this
            )
        val viewPager = binding.viewPager
        val tabLayout = binding.tabLayout
        viewPager.adapter = timetableAdapter

        val days =
            listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = days[position]
        }.attach()

        return binding.root
    }

}