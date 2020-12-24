package com.example.android.mycampusapp.assessments.assignments.input

import android.app.DatePickerDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
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
import com.example.android.mycampusapp.databinding.FragmentAssignmentInputBinding
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
    private lateinit var dateSetListener: DatePickerDialog.OnDateSetListener
    private val assignmentArgs by navArgs<AssignmentInputFragmentArgs>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentAssignmentInputBinding.inflate(inflater, container, false)
        val assignmentParcel = assignmentArgs.assignment
        var displayDate = assignmentParcel?.let {
            CustomDate(
                assignmentParcel.year,
                assignmentParcel.month,
                assignmentParcel.day
            )
        }

        sharedPreferences =
            requireActivity().getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)
        courseId = sharedPreferences.getString(COURSE_ID, "")!!

        viewModel = ViewModelProvider(
            this,
            AssignmentInputViewModelFactory(
                courseCollection.document(courseId).collection("assignments"),
                assignmentParcel
            )
        ).get(AssignmentInputViewModel::class.java)

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner


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
        dateSetListener =
            DatePickerDialog.OnDateSetListener { _: DatePicker, year: Int, month: Int, day: Int ->
                displayDate = CustomDate(year,month,day)
                val dateText = "${displayDate?.day}/${displayDate?.month}/${displayDate?.year}"
                displayDate?.let {
                    binding.assignmentDateEditText.setText(dateText)
                }
                viewModel.setDate.value = displayDate
            }

        viewModel.snackBarEvent.observe(viewLifecycleOwner, EventObserver {
            Snackbar.make(requireView(), it, Snackbar.LENGTH_SHORT)
        })
        viewModel.displayNavigator.observe(viewLifecycleOwner, EventObserver {
            findNavController().navigate(AssignmentInputFragmentDirections.actionAssignmentInputToAssignmentsFragment())
        })
        return binding.root
    }
}