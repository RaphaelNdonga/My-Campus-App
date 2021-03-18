package com.example.android.mycampusapp.timetable.input.friday

import android.app.AlertDialog
import android.app.TimePickerDialog
import android.os.Bundle
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
import com.example.android.mycampusapp.data.CustomTime
import com.example.android.mycampusapp.databinding.FragmentTimetableInputBinding
import com.example.android.mycampusapp.location.LocationUtils
import com.example.android.mycampusapp.timetable.input.TimetableInputViewModel
import com.example.android.mycampusapp.timetable.input.TimetableInputViewModelFactory
import com.example.android.mycampusapp.util.DayOfWeek
import com.example.android.mycampusapp.util.EventObserver
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.functions.FirebaseFunctions
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class FridayInputFragment : Fragment() {

    @Inject
    lateinit var courseCollection: CollectionReference
    @Inject
    lateinit var firebaseFunctions:FirebaseFunctions

    private val fridayArgs by navArgs<FridayInputFragmentArgs>()
    private lateinit var viewModel: TimetableInputViewModel

    private val friday = DayOfWeek.FRIDAY


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
                fridayArgs.fridayClass,
                app,
                courseCollection,
                firebaseFunctions,
                friday
            )
        ).get(TimetableInputViewModel::class.java)


        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        viewModel.displayNavigator.observe(viewLifecycleOwner,
            EventObserver {
                findNavController().navigate(FridayInputFragmentDirections.actionFridayInputFragmentToTimetableFragment())
            })

        viewModel.snackbarText.observe(viewLifecycleOwner,EventObserver{
            Snackbar.make(requireView(),it,Snackbar.LENGTH_LONG).show()
        })

        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        var displayTime = CustomTime(
            hour,
            minute
        )
        viewModel.timeSet.value?.let {
            displayTime = it
        }

        val timePickerListener =
            TimePickerDialog.OnTimeSetListener { _: TimePicker, hourSet: Int, minuteSet: Int ->
                viewModel.setTime(CustomTime(hour,minute))
            }
        val timePickerDialog = TimePickerDialog(
            requireContext(), timePickerListener, displayTime.hour, displayTime.minute,
            DateFormat.is24HourFormat(requireContext())
        )

        binding.classTimeEditText.setOnClickListener {
            timePickerDialog.show()
        }


        binding.classLocationEditText.setOnClickListener {
            showLocationList()
        }
        return binding.root
    }

    private fun showLocationList() {
        val alertDialogBuilder = AlertDialog.Builder(requireActivity(), R.style.MyCampusApp_Dialog)
            .setTitle(R.string.location_list_title)
            .setItems(R.array.locations) { _, which ->
                viewModel.setLocation(LocationUtils.getJkuatLocations()[which])
            }
        alertDialogBuilder.create().show()
    }
}
