package com.example.android.mycampusapp.assessments.tests.input

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
import android.widget.TimePicker
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.android.mycampusapp.R
import com.example.android.mycampusapp.data.CustomDate
import com.example.android.mycampusapp.data.CustomTime
import com.example.android.mycampusapp.databinding.TestsInputFragmentBinding
import com.example.android.mycampusapp.location.LocationUtils
import com.example.android.mycampusapp.util.COURSE_ID
import com.example.android.mycampusapp.util.EventObserver
import com.example.android.mycampusapp.util.sharedPrefFile
import com.google.firebase.firestore.CollectionReference
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class TestsInputFragment : Fragment() {
    private lateinit var viewModel: TestsInputViewModel
    private lateinit var binding: TestsInputFragmentBinding
    private lateinit var dateSetListener: DatePickerDialog.OnDateSetListener
    private lateinit var timeSetListener: TimePickerDialog.OnTimeSetListener
    private val testArgs by navArgs<TestsInputFragmentArgs>()

    @Inject
    lateinit var courseCollection: CollectionReference
    private lateinit var sharedPreferences: SharedPreferences


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        sharedPreferences =
            requireActivity().getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)
        val courseId = sharedPreferences.getString(COURSE_ID, "")!!
        val testsCollection = courseCollection.document(courseId).collection("tests")

        binding = DataBindingUtil.inflate(inflater, R.layout.tests_input_fragment, container, false)
        viewModel =
            ViewModelProvider(this, TestsInputViewModelFactory(testArgs.test, testsCollection)).get(
                TestsInputViewModel::class.java
            )
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        var dateSet: CustomDate? = null
        viewModel.dateSet.observe(viewLifecycleOwner, androidx.lifecycle.Observer { customDate ->
            dateSet = customDate
        })

        binding.testDateEditText.setOnClickListener {
            val cal = Calendar.getInstance()
            val yearDisplayed = dateSet?.year ?: cal.get(Calendar.YEAR)
            val monthDisplayed = dateSet?.month ?: cal.get(Calendar.MONTH)
            val dayDisplayed = dateSet?.day ?: cal.get(Calendar.DAY_OF_MONTH)
            val datePickerDialog = DatePickerDialog(
                requireActivity(),
                R.style.MyCampusApp_Dialog,
                dateSetListener,
                yearDisplayed,
                monthDisplayed,
                dayDisplayed
            )
            datePickerDialog.show()
        }
        dateSetListener =
            DatePickerDialog.OnDateSetListener { _: DatePicker, yearSet: Int, monthSet: Int, daySet: Int ->
                viewModel.setDateFromDatePicker(CustomDate(yearSet, monthSet, daySet))
            }

        var timeSet: CustomTime? = null
        viewModel.timeSet.observe(viewLifecycleOwner, androidx.lifecycle.Observer { customTime ->
            timeSet = customTime
        })

        binding.testTimeEditText.setOnClickListener {
            val cal = Calendar.getInstance()
            val hourDisplayed = timeSet?.hour ?: cal.get(Calendar.HOUR_OF_DAY)
            val minuteDisplayed = timeSet?.minute ?: cal.get(Calendar.MINUTE)
            val timePickerDialog = TimePickerDialog(
                requireActivity(),
                R.style.MyCampusApp_Dialog,
                timeSetListener,
                hourDisplayed,
                minuteDisplayed,
                is24HourFormat(requireContext())
            )
            timePickerDialog.show()
        }
        timeSetListener =
            TimePickerDialog.OnTimeSetListener { _: TimePicker, hourSet: Int, minuteSet: Int ->
                viewModel.setTimeFromTimePicker(
                    CustomTime(hourSet, minuteSet)
                )
            }

        binding.testLocationEditText.setOnClickListener {
            val alertDialog = AlertDialog.Builder(requireContext(), R.style.MyCampusApp_Dialog)
                .setTitle(R.string.location_list_title)
                .setItems(R.array.locations) { _: DialogInterface, selected: Int ->
                    viewModel.setLocation(LocationUtils.getJkuatLocations()[selected])
                }
            alertDialog.show()
        }

        viewModel.displayNavigator.observe(viewLifecycleOwner, EventObserver {
            findNavController().navigate(TestsInputFragmentDirections.actionTestsInputFragmentToAssessmentsFragment())
        })

        return binding.root
    }
}