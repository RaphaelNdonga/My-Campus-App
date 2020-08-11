package com.example.android.mycampusapp.timetable.display

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.android.mycampusapp.settings.WeekendDays
import com.example.android.mycampusapp.timetable.display.days.friday.FridayFragment
import com.example.android.mycampusapp.timetable.display.days.monday.MondayFragment
import com.example.android.mycampusapp.timetable.display.days.saturday.SaturdayFragment
import com.example.android.mycampusapp.timetable.display.days.sunday.SundayFragment
import com.example.android.mycampusapp.timetable.display.days.thursday.ThursdayFragment
import com.example.android.mycampusapp.timetable.display.days.tuesday.TuesdayFragment
import com.example.android.mycampusapp.timetable.display.days.wednesday.WednesdayFragment

class TimetableViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int {
        return WeekendDays.weekendDays.value!!
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