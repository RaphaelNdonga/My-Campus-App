package com.example.android.mycampusapp.timetable.display

import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.android.mycampusapp.timetable.display.days.friday.FridayFragment
import com.example.android.mycampusapp.timetable.display.days.monday.MondayFragment
import com.example.android.mycampusapp.timetable.display.days.saturday.SaturdayFragment
import com.example.android.mycampusapp.timetable.display.days.sunday.SundayFragment
import com.example.android.mycampusapp.timetable.display.days.thursday.ThursdayFragment
import com.example.android.mycampusapp.timetable.display.days.tuesday.TuesdayFragment
import com.example.android.mycampusapp.timetable.display.days.wednesday.WednesdayFragment
import timber.log.Timber

class TimetableViewPagerAdapter(private val fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(fragment.context)
        val weekendSwitch = sharedPreferences.getBoolean("weekend",false)
        Timber.i("Weekend switch is at $weekendSwitch")
        return when(weekendSwitch){
            true -> 7
            false -> 5
        }
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