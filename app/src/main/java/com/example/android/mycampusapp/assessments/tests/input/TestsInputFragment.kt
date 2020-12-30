package com.example.android.mycampusapp.assessments.tests.input

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.example.android.mycampusapp.R
import com.example.android.mycampusapp.assessments.tests.display.TestsFragmentArgs
import com.example.android.mycampusapp.data.CustomDate
import com.example.android.mycampusapp.databinding.TestsInputFragmentBinding
import java.util.*

class TestsInputFragment : Fragment() {
    private lateinit var viewModel: TestsInputViewModel
    private lateinit var binding: TestsInputFragmentBinding
    private lateinit var dateSetListener: DatePickerDialog.OnDateSetListener
    private val testArgs by navArgs<TestsFragmentArgs>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.tests_input_fragment, container, false)
        viewModel = ViewModelProvider(this, TestsInputViewModelFactory(testArgs.test)).get(
            TestsInputViewModel::class.java
        )
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        var dateSet:CustomDate? = null

        viewModel.dateSet.observe(viewLifecycleOwner, androidx.lifecycle.Observer{ customDate->
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

        return binding.root
    }
}