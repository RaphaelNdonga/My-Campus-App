package com.example.android.mycampusapp.assessments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.android.mycampusapp.R
import com.example.android.mycampusapp.databinding.FragmentAssignmentsBinding
import com.example.android.mycampusapp.util.COURSE_ID
import com.example.android.mycampusapp.util.EventObserver
import com.example.android.mycampusapp.util.sharedPrefFile
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ListenerRegistration
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class AssignmentsFragment : Fragment() {
    @Inject
    lateinit var courseCollection: CollectionReference
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var courseId: String
    private lateinit var viewModel: AssignmentsViewModel
    private lateinit var snapshotListener:ListenerRegistration

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil.inflate<FragmentAssignmentsBinding>(
            inflater,
            R.layout.fragment_assignments,
            container,
            false
        )
        sharedPreferences =
            requireActivity().getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)
        courseId = sharedPreferences.getString(COURSE_ID, "")!!
        val adapter = AssignmentsAdapter()
        binding.assignmentsRecyclerView.adapter = adapter
        viewModel = ViewModelProvider(
            this,
            AssignmentsViewModelFactory(
                courseCollection.document(courseId).collection("assignments")
            )
        ).get(AssignmentsViewModel::class.java)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        viewModel.inputNavigator.observe(viewLifecycleOwner,EventObserver{
            findNavController().navigate(AssignmentsFragmentDirections.actionAssignmentsFragmentToAssignmentInput())
            Timber.i("input navigator observer")
        })

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        snapshotListener = viewModel.addSnapshotListener()
    }

    override fun onPause() {
        super.onPause()
        snapshotListener.remove()
    }

}