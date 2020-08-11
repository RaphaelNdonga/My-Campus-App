package com.example.android.mycampusapp.settings

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.example.android.mycampusapp.R
import timber.log.Timber

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setHasOptionsMenu(true)
        setPreferencesFromResource(R.xml.fragment_settings, rootKey)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.settings_toolbar_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.save_settings -> {
                val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.context)
                val weekendSwitch = sharedPreferences?.getBoolean("weekend", false)
                Timber.i("The switch is at $weekendSwitch")
                flip(weekendSwitch!!)
                findNavController().navigate(
                    SettingsFragmentDirections.actionSettingsFragmentToTimetableFragment()
                )
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun flip(switch: Boolean) {
        if (switch) {
            WeekendDays.weekendDays.value = 7
            return
        }
        WeekendDays.weekendDays.value = 5
    }
}

object WeekendDays {
    val weekendDays = MutableLiveData(5)
}