package com.example.android.mycampusapp.display

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.android.mycampusapp.display.days.*
import com.example.android.mycampusapp.display.days.monday.MondayFragment
import com.example.android.mycampusapp.display.days.sunday.SundayFragment
import com.example.android.mycampusapp.display.days.thursday.ThursdayFragment
import com.example.android.mycampusapp.display.days.tuesday.TuesdayFragment
import com.example.android.mycampusapp.display.days.wednesday.WednesdayFragment

class TimetableViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int {
        return 7
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> MondayFragment()
            1 -> TuesdayFragment()
            2 -> WednesdayFragment()
            3 -> ThursdayFragment()
            4 -> FridayFragment()
            5 -> SaturdayFragment()
            6 -> SundayFragment()

            else -> throw Exception()
        }
    }
}