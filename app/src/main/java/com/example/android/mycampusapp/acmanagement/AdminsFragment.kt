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
import com.example.android.mycampusapp.util.EventObserver
import com.example.android.mycampusapp.util.sharedPrefFile
import com.google.android.material.snackbar.Snackbar
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

        viewModel.snackBarText.observe(viewLifecycleOwner, EventObserver {
            Snackbar.make(requireView(), it, Snackbar.LENGTH_LONG).show()
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
                startLoading()
                viewModel.demoteToRegular(userEmail, courseId).addOnCompleteListener {
                    stopLoading()
                }
            }
            .setTitle("Demote to regular")
            .setMessage("Are you sure you want to demote ${userEmail.email} to a regular user?")
            .create().show()
    }

    private fun startLoading() {
        binding.recyclerView.visibility = View.GONE
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun stopLoading() {
        binding.recyclerView.visibility = View.VISIBLE
        binding.progressBar.visibility = View.GONE
    }

}