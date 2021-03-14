package com.example.android.mycampusapp.acmanagement

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.example.android.mycampusapp.LoginActivity
import com.example.android.mycampusapp.R
import com.example.android.mycampusapp.databinding.FragmentAccountManagementBinding
import com.example.android.mycampusapp.util.COURSE_ID
import com.example.android.mycampusapp.util.USER_EMAIL
import com.example.android.mycampusapp.util.sharedPrefFile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ManageAccountFragment : Fragment() {
    @Inject
    lateinit var auth: FirebaseAuth
    @Inject
    lateinit var firebaseMessaging: FirebaseMessaging
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var settingsPreferences:SharedPreferences
    private lateinit var courseId:String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil.inflate<FragmentAccountManagementBinding>(
            inflater,
            R.layout.fragment_account_management,
            container,
            false
        )
        sharedPreferences =
            requireActivity().getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)
        settingsPreferences =  PreferenceManager.getDefaultSharedPreferences(this.context)


        binding.accountDetailsEmail.text = sharedPreferences.getString(USER_EMAIL, "email")
        courseId = sharedPreferences.getString(COURSE_ID,"courseId")!!
        binding.accountDetailsCourse.text = courseId

        binding.logOutBtn.setOnClickListener {
            auth.signOut()
            firebaseMessaging.unsubscribeFromTopic(courseId)
            sharedPreferences.edit().clear().apply()
            settingsPreferences.edit().clear().apply()
            val loginIntent = Intent(this.context, LoginActivity::class.java)
            startActivity(loginIntent)
            requireActivity().finish()
        }
        binding.deleteAccountBtn.setOnClickListener {
            showConfirmationDialog()
        }

        return binding.root
    }

    private fun showConfirmationDialog() {
        val builder = AlertDialog.Builder(requireActivity(), R.style.MyCampusApp_Dialog)
        builder.setNegativeButton(R.string.dialog_negative) { _, _ -> }
        builder.setPositiveButton(R.string.dialog_delete) { _, _ ->
            run {
                auth.currentUser?.delete()
                firebaseMessaging.unsubscribeFromTopic(courseId)
                sharedPreferences.edit().clear().apply()
                settingsPreferences.edit().clear().apply()
                val loginIntent = Intent(this.context, LoginActivity::class.java)
                startActivity(loginIntent)
                requireActivity().finish()
            }
        }
        builder.setMessage(R.string.dialog_delete_confirm)
        builder.create().show()
    }
    //TODO:2. This fragment needs a viewmodel
}