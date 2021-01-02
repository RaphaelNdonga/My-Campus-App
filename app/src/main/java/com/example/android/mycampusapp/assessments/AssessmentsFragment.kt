package com.example.android.mycampusapp.assessments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.android.mycampusapp.databinding.FragmentAssessmentsBinding
import com.google.android.material.tabs.TabLayoutMediator

class AssessmentsFragment: Fragment() {
    private lateinit var binding:FragmentAssessmentsBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAssessmentsBinding.inflate(inflater,container,false)

        val adapter = AssessmentsViewPagerAdapter(this)
        val tabLayout = binding.tabLayout
        val viewPager = binding.viewPager
        val assessments = listOf("Assignments","Tests")
        viewPager.adapter = adapter
        val tabLayoutMediator = TabLayoutMediator(tabLayout,viewPager){ tab, position ->
            tab.text = assessments[position]
        }.attach()

        return binding.root
    }
}