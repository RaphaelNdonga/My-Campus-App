package com.example.android.mycampusapp.assessments.assignments.input

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.android.mycampusapp.databinding.FragmentAssignmentInputBinding
import com.example.android.mycampusapp.util.COURSE_ID
import com.example.android.mycampusapp.util.EventObserver
import com.example.android.mycampusapp.util.sharedPrefFile
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.CollectionReference
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AssignmentInputFragment : Fragment() {
    private lateinit var viewModel: AssignmentInputViewModel

    @Inject
    lateinit var courseCollection: CollectionReference
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var courseId: String
    private val assignmentArgs by navArgs<AssignmentInputFragmentArgs>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentAssignmentInputBinding.inflate(inflater, container, false)
        sharedPreferences =
            requireActivity().getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)
        courseId = sharedPreferences.getString(COURSE_ID, "")!!

        viewModel = ViewModelProvider(
            this,
            AssignmentInputViewModelFactory(courseCollection.document(courseId).collection("assignments"),assignmentArgs.assignment)
        ).get(AssignmentInputViewModel::class.java)

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        viewModel.snackBarEvent.observe(viewLifecycleOwner, EventObserver {
            Snackbar.make(requireView(), it, Snackbar.LENGTH_SHORT)
        })
        viewModel.displayNavigator.observe(viewLifecycleOwner,EventObserver{
            findNavController().navigate(AssignmentInputFragmentDirections.actionAssignmentInputToAssignmentsFragment())
        })
        return binding.root
    }
}