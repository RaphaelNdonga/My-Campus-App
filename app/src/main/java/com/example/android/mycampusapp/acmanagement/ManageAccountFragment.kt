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
import com.example.android.mycampusapp.LoginActivity
import com.example.android.mycampusapp.R
import com.example.android.mycampusapp.databinding.FragmentAccountManagementBinding
import com.example.android.mycampusapp.util.USER_EMAIL
import com.example.android.mycampusapp.util.sharedPrefFile
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ManageAccountFragment : Fragment() {
    @Inject
    lateinit var auth: FirebaseAuth
    private lateinit var sharedPreferences: SharedPreferences

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

        binding.accountDetailsEmail.text = sharedPreferences.getString(USER_EMAIL, "email")

        binding.logOutBtn.setOnClickListener {
            auth.signOut()
            sharedPreferences.edit().clear().apply()
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
                sharedPreferences.edit().clear().apply()
                val loginIntent = Intent(this.context, LoginActivity::class.java)
                startActivity(loginIntent)
                requireActivity().finish()
            }
        }
        builder.setMessage(R.string.dialog_delete_confirm)
        builder.create().show()
    }
}