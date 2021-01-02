package com.example.android.mycampusapp.assessments

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.android.mycampusapp.assessments.assignments.display.AssignmentsFragment
import com.example.android.mycampusapp.assessments.tests.display.TestsFragment

class AssessmentsViewPagerAdapter(fragment:Fragment):FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0->{
                AssignmentsFragment()
            }
            1->{
                TestsFragment()
            }
            else -> throw Exception("Fragments not found")
        }
    }
}