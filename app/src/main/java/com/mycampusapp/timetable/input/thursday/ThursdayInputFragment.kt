package com.mycampusapp.timetable.input.thursday

import android.app.AlertDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.InputType
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TimePicker
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.android.mycampusapp.R
import com.example.android.mycampusapp.databinding.FragmentTimetableInputBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.functions.FirebaseFunctions
import com.mycampusapp.data.CustomTime
import com.mycampusapp.location.LocationUtils
import com.mycampusapp.timetable.input.ClassType
import com.mycampusapp.timetable.input.TimetableInputViewModel
import com.mycampusapp.timetable.input.TimetableInputViewModelFactory
import com.mycampusapp.util.DayOfWeek
import com.mycampusapp.util.EventObserver
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class ThursdayInputFragment : Fragment() {

    @Inject
    lateinit var courseCollection: CollectionReference

    @Inject
    lateinit var firebaseFunctions: FirebaseFunctions

    private val thursdayArgs by navArgs<ThursdayInputFragmentArgs>()
    private lateinit var viewModel: TimetableInputViewModel

    private val thursday = DayOfWeek.THURSDAY


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil.inflate<FragmentTimetableInputBinding>(
            inflater,
            R.layout.fragment_timetable_input,
            container,
            false
        )
        val app = requireActivity().application
        viewModel = ViewModelProvider(
            this,
            TimetableInputViewModelFactory(
                thursdayArgs.thursdayClass,
                app,
                courseCollection,
                firebaseFunctions,
                thursday
            )
        ).get(TimetableInputViewModel::class.java)


        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        viewModel.displayNavigator.observe(viewLifecycleOwner,
            EventObserver {
                findNavController().navigateUp()
            })

        viewModel.snackbarText.observe(viewLifecycleOwner, EventObserver {
            Snackbar.make(requireView(), it, Snackbar.LENGTH_LONG).show()
        })

        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        var displayTime = CustomTime(hour, minute)

        viewModel.timeSet.value?.let {
            displayTime = it
        }

        val timePickerListener =
            TimePickerDialog.OnTimeSetListener { _: TimePicker, hourSet: Int, minuteSet: Int ->
                viewModel.setTime(CustomTime(hourSet, minuteSet))
            }
        val timePickerDialog = TimePickerDialog(
            requireContext(),
            R.style.MyCampusApp_Dialog,
            timePickerListener,
            displayTime.hour,
            displayTime.minute,
            DateFormat.is24HourFormat(requireContext())
        )

        binding.classTimeEditText.setOnClickListener {
            timePickerDialog.show()
        }



        binding.classLocationEditText.setOnClickListener {
            setClassType()
        }
        viewModel.classType.observe(viewLifecycleOwner) { classType ->
            when (classType) {
                ClassType.PHYSICAL -> {
                    showLocationsList()
                    binding.classLocationEditText.isCursorVisible = false
                    binding.classLocationEditText.isFocusable = false
                    binding.classLocationEditText.isFocusableInTouchMode = false
                    binding.classLocationEditText.inputType =
                        InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS
                    binding.classLocationInput.helperText = null
                }
                ClassType.ONLINE -> {
                    viewModel.nullifyLocation()
                    binding.classLocationEditText.text = null
                    binding.classLocationEditText.isCursorVisible = true
                    binding.classLocationEditText.isFocusable = true
                    binding.classLocationEditText.isFocusableInTouchMode = true
                    binding.classLocationEditText.inputType =
                        InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS
                    binding.classLocationInput.helperText = "Paste the link here"
                }
                else -> throw IllegalArgumentException("No other argument should be obtained")
            }
        }
        return binding.root
    }

    private fun showLocationsList() {
        val builder = AlertDialog.Builder(requireActivity(), R.style.MyCampusApp_Dialog)
            .setTitle(R.string.location_list_title)
            .setItems(R.array.locations) { _, which ->
                viewModel.setLocation(LocationUtils.getJkuatLocations()[which])
            }
        builder.create().show()
    }

    private fun setClassType() {
        val builder = AlertDialog.Builder(requireActivity(), R.style.MyCampusApp_Dialog)
            .setItems(R.array.class_type) { _, which ->
                if (which == 0) {
                    viewModel.setClassType(ClassType.ONLINE)
                } else {
                    viewModel.setClassType(ClassType.PHYSICAL)
                }
            }
            .setTitle("Is the class online or physical?")
        builder.create().show()
    }
}
