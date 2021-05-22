package com.example.android.mycampusapp.acmanagement

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.android.mycampusapp.R
import com.example.android.mycampusapp.data.UserEmail
import com.example.android.mycampusapp.databinding.UsersFragmentBinding
import com.example.android.mycampusapp.util.COURSE_ID
import com.example.android.mycampusapp.util.sharedPrefFile
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.functions.FirebaseFunctions
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AdminsFragment : Fragment() {

    private lateinit var viewModel: AdminsViewModel
    private lateinit var binding: UsersFragmentBinding
    private lateinit var snapshotListener: ListenerRegistration
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var courseId: String


    @Inject
    lateinit var courseCollection: CollectionReference

    @Inject
    lateinit var functions: FirebaseFunctions

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = UsersFragmentBinding.inflate(inflater, container, false)

        sharedPreferences =
            requireActivity().getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)
        courseId = sharedPreferences.getString(COURSE_ID, "")!!

        val adminsCollection = courseCollection.document(courseId).collection("admins")
        viewModel =
            ViewModelProvider(
                this,
                AdminsViewModelFactory(adminsCollection, functions)
            ).get(AdminsViewModel::class.java)

        val adapter = UserAdapter(UserListener {
            showAlertDialogBox(it)
        })

        viewModel.adminsList.observe(viewLifecycleOwner, {
            adapter.submitList(it)
        })

        binding.recyclerView.adapter = adapter

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        snapshotListener = viewModel.addSnapshotListener()
    }

    override fun onStop() {
        super.onStop()
        snapshotListener.remove()
    }

    private fun showAlertDialogBox(userEmail: UserEmail) {
        AlertDialog.Builder(requireContext(), R.style.MyCampusApp_Dialog)
            .setNegativeButton(R.string.dialog_negative) { _, _ -> }
            .setPositiveButton(R.string.dialog_positive) { _, _ ->
                viewModel.demoteToRegular(userEmail.email, courseId).addOnSuccessListener {
                    viewModel.deleteAdminsDocument(userEmail.email)
                    viewModel.createRegularsDocument(userEmail)
                }
            }
            .setTitle("Demote to regular")
            .setMessage("Are you sure you want to demote ${userEmail.email} to a regular user?")
            .create().show()
    }
}