package com.example.android.mycampusapp.assessments.tests.display

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
import com.example.android.mycampusapp.util.EventObserver
import timber.log.Timber

class TestsFragment : Fragment() {

    private lateinit var viewModel: TestsViewModel
    private lateinit var binding: TestsFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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