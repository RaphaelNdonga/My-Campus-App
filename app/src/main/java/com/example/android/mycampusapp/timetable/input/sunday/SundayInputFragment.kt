package com.example.android.mycampusapp.timetable.input.sunday

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
import com.example.android.mycampusapp.databinding.FragmentSundayInputBinding
import com.example.android.mycampusapp.util.*
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.CollectionReference
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class SundayInputFragment : Fragment() {

    @Inject
    lateinit var courseCollection:CollectionReference

    private val sundayArgs by navArgs<SundayInputFragmentArgs>()
    private lateinit var viewModel: SundayInputViewModel
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var courseId:String


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        sharedPreferences = requireActivity().getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)
        courseId = sharedPreferences.getString(COURSE_ID,"")!!
        val binding = DataBindingUtil.inflate<FragmentSundayInputBinding>(
            inflater,
            R.layout.fragment_sunday_input,
            container,
            false
        )
        val app = requireActivity().application
        viewModel = ViewModelProvider(
            this,
            SundayInputViewModelFactory(
                courseCollection.document(courseId),
                sundayArgs.sundayClass,
                app
            )
        ).get(SundayInputViewModel::class.java)


        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        viewModel.navigator.observe(viewLifecycleOwner,
            EventObserver {
                findNavController().navigate(SundayInputFragmentDirections.actionSundayInputFragmentToTimetableFragment())
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
