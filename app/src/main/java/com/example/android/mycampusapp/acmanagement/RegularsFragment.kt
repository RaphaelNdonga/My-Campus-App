package com.example.android.mycampusapp.acmanagement

import android.app.AlertDialog
import android.content.Context
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
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class RegularsFragment : Fragment() {

    private lateinit var viewModel: RegularsViewModel
    private lateinit var binding: UsersFragmentBinding
    private lateinit var snapshotListener: ListenerRegistration

    @Inject
    lateinit var courseCollection: CollectionReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = UsersFragmentBinding.inflate(inflater, container, false)

        val sharedPreferences =
            requireActivity().getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)
        val courseId = sharedPreferences.getString(COURSE_ID, "")!!
        val regularsCollection = courseCollection.document(courseId).collection("regulars")

        viewModel = ViewModelProvider(this, RegularViewModelFactory(regularsCollection)).get(
            RegularsViewModel::class.java
        )

        val adapter = UserAdapter(UserListener {
            showDialogBox(it)
        })

        viewModel.regularsList.observe(viewLifecycleOwner, {
            adapter.submitList(it)
        })

        binding.recyclerView.adapter = adapter
        return binding.root
    }

    private fun showDialogBox(email: UserEmail) {
        AlertDialog.Builder(requireContext(), R.style.MyCampusApp_Dialog)
            .setTitle("Upgrade to Admin")
            .setMessage("Are you sure you want to make ${email.email} an admin?")
            .setPositiveButton(R.string.dialog_positive) { _, _ ->

            }
            .setNegativeButton(R.string.dialog_negative) { _, _ ->

            }
            .create().show()
    }

    override fun onStart() {
        super.onStart()
        snapshotListener = viewModel.addSnapshotListener()
    }

    override fun onStop() {
        super.onStop()
        snapshotListener.remove()
    }
}