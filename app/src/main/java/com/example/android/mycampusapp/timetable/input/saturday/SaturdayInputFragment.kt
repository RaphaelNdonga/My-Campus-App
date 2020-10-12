package com.example.android.mycampusapp.timetable.input.saturday

import android.content.Context
import android.content.SharedPreferences
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
import com.example.android.mycampusapp.R
import com.example.android.mycampusapp.databinding.FragmentSaturdayInputBinding
import com.example.android.mycampusapp.util.*
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.CollectionReference
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class SaturdayInputFragment : Fragment() {

    @Inject
    lateinit var courseCollection:CollectionReference

    private val saturdayArgs by navArgs<SaturdayInputFragmentArgs>()
    private lateinit var viewModel: SaturdayInputViewModel
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var courseId:String


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        sharedPreferences = requireActivity().getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)
        courseId = sharedPreferences.getString(COURSE_ID,"")!!
        val binding = DataBindingUtil.inflate<FragmentSaturdayInputBinding>(
            inflater,
            R.layout.fragment_saturday_input,
            container,
            false
        )
        val app = requireActivity().application
        viewModel = ViewModelProvider(
            this,
            SaturdayInputViewModelFactory(
                courseCollection.document(courseId),
                saturdayArgs.saturdayClass,
                app
            )
        ).get(SaturdayInputViewModel::class.java)


        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        viewModel.navigator.observe(viewLifecycleOwner,
            EventObserver {
                findNavController().navigate(SaturdayInputFragmentDirections.actionSaturdayInputFragmentToTimetableFragment())
            })

        val time = binding.classTimeEditText

        viewModel.timeSetByTimePicker.observe(viewLifecycleOwner, Observer { hourMinute ->
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
        activity?.setupTimeDialog(this, viewModel.timePickerClockPosition)
    }
}
