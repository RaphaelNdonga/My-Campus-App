package com.example.android.mycampusapp.acmanagement

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.android.mycampusapp.LoginActivity
import com.example.android.mycampusapp.R
import com.example.android.mycampusapp.databinding.FragmentAccountManagementBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ManageAccountFragment : Fragment() {
    @Inject
    lateinit var auth: FirebaseAuth

    @Inject
    lateinit var courseCollection: CollectionReference

    @Inject
    lateinit var messaging: FirebaseMessaging

    private lateinit var viewModel: ManageAccountViewModel

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
        val app = requireActivity().application
        viewModel = ViewModelProvider(
            this,
            ManageAccountViewModelFactory(app, courseCollection, messaging, auth)
        ).get(ManageAccountViewModel::class.java)

        binding.accountDetailsEmail.text = viewModel.getEmail()
        binding.accountDetailsCourse.text = viewModel.getCourseId()

        binding.logOutBtn.setOnClickListener {
            it.isClickable = false
            viewModel.logOut()
            viewModel.performClearance()
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
                viewModel.delete()
                viewModel.performClearance()
                val loginIntent = Intent(this.context, LoginActivity::class.java)
                startActivity(loginIntent)
                requireActivity().finish()
            }
        }
        builder.setMessage(R.string.dialog_delete_confirm)
        builder.create().show()
    }
}