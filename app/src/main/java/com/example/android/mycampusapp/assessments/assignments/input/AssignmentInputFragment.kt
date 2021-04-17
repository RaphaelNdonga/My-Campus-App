package com.example.android.mycampusapp.assessments.assignments.input

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.os.Bundle
import android.text.format.DateFormat.is24HourFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.android.mycampusapp.R
import com.example.android.mycampusapp.data.CustomDate
import com.example.android.mycampusapp.data.CustomTime
import com.example.android.mycampusapp.databinding.FragmentAssignmentInputBinding
import com.example.android.mycampusapp.location.LocationUtils
import com.example.android.mycampusapp.util.COURSE_ID
import com.example.android.mycampusapp.util.EventObserver
import com.example.android.mycampusapp.util.sharedPrefFile
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.CollectionReference
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.util.*
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
        val assignmentParcel = assignmentArgs.assignment

        sharedPreferences =
            requireActivity().getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)
        courseId = sharedPreferences.getString(COURSE_ID, "")!!

        val application = requireActivity().application

        viewModel = ViewModelProvider(
            this,
            AssignmentInputViewModelFactory(
                courseCollection.document(courseId).collection("assignments"),
                assignmentParcel,
                application
            )
        ).get(AssignmentInputViewModel::class.java)

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        var displayDate: CustomDate? = null
        viewModel.dateSet.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            displayDate = it
        })

        val dateSetListener =
            DatePickerDialog.OnDateSetListener { _: DatePicker, year: Int, month: Int, day: Int ->
                viewModel.setDateFromDatePicker(
                    CustomDate(year, month, day)
                )
            }
        binding.assignmentDateEditText.setOnClickListener {
            val cal = Calendar.getInstance()
            val year = displayDate?.year ?: cal.get(Calendar.YEAR)
            val month = displayDate?.month ?: cal.get(Calendar.MONTH)
            val day = displayDate?.day ?: cal.get(Calendar.DAY_OF_MONTH)
            val datePickerDialog = DatePickerDialog(
                requireActivity(), R.style.MyCampusApp_Dialog, dateSetListener, year, month, day
            )
            Timber.i("The date edit text box has been clicked")
            datePickerDialog.show()
        }

        var displayTime: CustomTime? = null
        viewModel.timeSet.observe(viewLifecycleOwner, androidx.lifecycle.Observer { time ->
            displayTime = time
        })
        val timePickerListener = TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
            viewModel.setTimeFromTimePicker(
                CustomTime(hourOfDay, minute)
            )
        }

        binding.assignmentTimeEditText.setOnClickListener {
            val calendar = Calendar.getInstance()
            val hourDisplayed = displayTime?.hour ?: calendar.get(Calendar.HOUR_OF_DAY)
            val minuteDisplayed = displayTime?.minute ?: calendar.get(Calendar.MINUTE)

            val timePickerDialog = TimePickerDialog(
                requireActivity(),
                R.style.MyCampusApp_Dialog,
                timePickerListener,
                hourDisplayed,
                minuteDisplayed,
                is24HourFormat(requireContext())
            )
            timePickerDialog.show()
        }

        binding.assignmentLocationEditText.setOnClickListener {
            val alertDialog = AlertDialog.Builder(requireContext(),R.style.MyCampusApp_Dialog)
                .setTitle(R.string.location_list_title)
                .setItems(R.array.locations){ _: DialogInterface, selected: Int ->
                    viewModel.setLocation(LocationUtils.getJkuatLocations()[selected])
                }
            alertDialog.show()
        }



        viewModel.snackBarEvent.observe(viewLifecycleOwner, EventObserver {
            Snackbar.make(requireView(), it, Snackbar.LENGTH_SHORT)
        })
        viewModel.displayNavigator.observe(viewLifecycleOwner, EventObserver {
            findNavController().navigate(AssignmentInputFragmentDirections.actionAssignmentInputToAssessmentsFragment())
        })
        return binding.root
    }
}