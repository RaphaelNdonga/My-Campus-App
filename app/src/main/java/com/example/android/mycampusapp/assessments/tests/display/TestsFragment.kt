package com.example.android.mycampusapp.assessments.tests.display

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
import com.example.android.mycampusapp.databinding.TestsFragmentBinding
import com.example.android.mycampusapp.util.COURSE_ID
import com.example.android.mycampusapp.util.EventObserver
import com.example.android.mycampusapp.util.sharedPrefFile
import com.google.firebase.firestore.CollectionReference
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class TestsFragment : Fragment() {

    private lateinit var viewModel: TestsViewModel
    private lateinit var binding: TestsFragmentBinding

    @Inject
    lateinit var courseCollection:CollectionReference
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        sharedPreferences = requireActivity().getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)
        val courseId = sharedPreferences.getString(COURSE_ID,"")!!
        val testsCollection = courseCollection.document(courseId).collection("tests")

        binding = DataBindingUtil.inflate(inflater, R.layout.tests_fragment, container, false)
        viewModel = ViewModelProvider(this).get(TestsViewModel::class.java)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        viewModel.inputNavigator.observe(viewLifecycleOwner, EventObserver {
            findNavController().navigate(TestsFragmentDirections.actionTestsFragmentToTestsInputFragment())
            Timber.i("Navigating to input")
        })
        return binding.root
    }
}