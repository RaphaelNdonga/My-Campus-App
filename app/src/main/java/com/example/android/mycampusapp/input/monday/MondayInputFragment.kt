package com.example.android.mycampusapp.input.monday

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.android.mycampusapp.EventObserver
import com.example.android.mycampusapp.R
import com.example.android.mycampusapp.data.timetable.local.TimetableDataSource
import com.example.android.mycampusapp.databinding.FragmentClassInputBinding
import com.example.android.mycampusapp.di.TimetableDatabase
import com.example.android.mycampusapp.util.setupTimeDialog
import com.example.android.mycampusapp.util.setupSnackbar
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MondayInputFragment : Fragment() {

    @TimetableDatabase
    @Inject
    lateinit var timetableRepository: TimetableDataSource

    private val mondayArgs by navArgs<MondayInputFragmentArgs>()
    private lateinit var viewModel: MondayInputViewModel


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
        val app = requireActivity().application
        viewModel = ViewModelProvider(
            this,
            MondayInputViewModelFactory(timetableRepository, mondayArgs.mondayClass, app)
        ).get(MondayInputViewModel::class.java)


        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        viewModel.navigator.observe(viewLifecycleOwner, EventObserver {
            findNavController().navigate(MondayInputFragmentDirections.actionMondayInputFragmentToTimetableFragment())
        })

        val time = binding.classTimeEditText

        viewModel.hourMinuteSet.observe(viewLifecycleOwner, Observer { hourMinute ->
            time.setText(hourMinute)
        })

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupSnackbar()
        setupTimePickerDialog()
    }

    private fun setupSnackbar() {
        view?.setupSnackbar(this, viewModel.snackbarText, Snackbar.LENGTH_SHORT)
    }

    private fun setupTimePickerDialog() {
        activity?.setupTimeDialog(this, viewModel.hourMinuteDisplay)
    }
}
