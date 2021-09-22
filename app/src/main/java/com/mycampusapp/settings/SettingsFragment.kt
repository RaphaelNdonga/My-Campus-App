package com.mycampusapp.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.navigation.fragment.findNavController
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.mycampusapp.R
import com.mycampusapp.util.PRIVACY_POLICY_LINK

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setHasOptionsMenu(true)
        setPreferencesFromResource(R.xml.fragment_settings, rootKey)

        val intentPreference: Preference? = findPreference("privacy_policy")
        intentPreference?.setOnPreferenceClickListener { preference ->
            val browserUri = Uri.parse(PRIVACY_POLICY_LINK)
            val intent = Intent(Intent.ACTION_VIEW, browserUri)
            startActivity(intent)
            true
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.settings_toolbar_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.save_settings -> {
                findNavController().navigate(
                    SettingsFragmentDirections.actionSettingsFragmentToTimetableFragment()
                )
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}