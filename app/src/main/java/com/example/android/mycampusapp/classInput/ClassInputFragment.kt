package com.example.android.mycampusapp.classInput

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.android.mycampusapp.EventObserver
import com.example.android.mycampusapp.R
import com.example.android.mycampusapp.data.timetable.local.TimetableDataSource
import com.example.android.mycampusapp.data.timetable.local.TimetableLocalDataSource
import com.example.android.mycampusapp.databinding.FragmentClassInputBinding
import com.example.android.mycampusapp.di.TimetableDatabase
import com.example.android.mycampusapp.util.TimePickerValues
import com.example.android.mycampusapp.util.setupTimeDialog
import com.example.android.mycampusapp.util.setupSnackbar
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class ClassInputFragment : Fragment() {

    @TimetableDatabase
    @Inject
    lateinit var timetableRepository: TimetableDataSource

    private val mondayArgs by navArgs<ClassInputFragmentArgs>()

    private val viewModel by viewModels<ClassInputViewModel> {
        ClassInputViewModelFactory(
            timetableRepository as TimetableLocalDataSource,
            mondayArgs.mondayClass
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil.inflate<FragmentClassInputBinding>(
            inflater,
            R.layout.fragment_class_input,
            container,
            false
        )
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        viewModel.navigator.observe(viewLifecycleOwner, EventObserver {
            findNavController().navigate(ClassInputFragmentDirections.actionClassInputFragmentToTimetableFragment())
        })

        val time = binding.classTimeEditText

        viewModel.hourMinuteSet.observe(viewLifecycleOwner, Observer { hourMinute->
            time.setText(hourMinute)
        })
        setupSnackbar()
        setupTimePickerDialog()
        return binding.root
    }

    private fun setupSnackbar() {
        view?.setupSnackbar(this, viewModel.snackbarText, Snackbar.LENGTH_SHORT)
    }

    private fun setupTimePickerDialog() {
        activity?.setupTimeDialog(this, viewModel.hourMinuteDisplay)
    }
}
