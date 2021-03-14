package com.example.android.mycampusapp.timetable.input.monday

import android.app.AlertDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
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
import com.example.android.mycampusapp.util.COURSE_ID
import com.example.android.mycampusapp.util.EventObserver
import com.example.android.mycampusapp.util.setupSnackbar
import com.example.android.mycampusapp.util.sharedPrefFile
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class MondayInputFragment : Fragment() {

    @Inject
    lateinit var firestore: FirebaseFirestore

    @Inject
    lateinit var coursesCollection: CollectionReference

    @Inject
    lateinit var firebaseFunctions:FirebaseFunctions

    private val mondayArgs by navArgs<MondayInputFragmentArgs>()
    private lateinit var viewModel: TimetableInputViewModel
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var courseId: String


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        sharedPreferences =
            requireActivity().getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)
        courseId = sharedPreferences.getString(COURSE_ID, "")!!
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
                mondayArgs.mondayClass,
                app,
                coursesCollection.document(courseId).collection("monday"),
                firebaseFunctions
            )
        ).get(TimetableInputViewModel::class.java)


        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        viewModel.displayNavigator.observe(viewLifecycleOwner,
            EventObserver {
                findNavController().navigate(MondayInputFragmentDirections.actionMondayInputFragmentToTimetableFragment())
            })

        val timePickerListener =
            TimePickerDialog.OnTimeSetListener { _: TimePicker, hourSet: Int, minuteSet: Int ->
                viewModel.setTime(
                    CustomTime(hourSet, minuteSet)
                )
            }
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        var displayTime = CustomTime(hour,minute)

        viewModel.timeSet.observe(viewLifecycleOwner, {
            it?.let{ displayTime = it }
        })

        val timePickerDialog = TimePickerDialog(
            requireContext(), timePickerListener, displayTime.hour, displayTime.minute,
            android.text.format.DateFormat.is24HourFormat(requireContext())
        )

        binding.classTimeEditText.setOnClickListener {
            timePickerDialog.show()
        }

        binding.classLocationEditText.setOnClickListener {
            showLocationsList()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupSnackbar()
    }

    private fun setupSnackbar() {
        view?.setupSnackbar(this, viewModel.snackbarText, Snackbar.LENGTH_SHORT)
    }

    private fun showLocationsList() {
        val builder = AlertDialog.Builder(requireActivity(), R.style.MyCampusApp_Dialog)
            .setItems(R.array.locations) { _, which ->
                viewModel.setLocation(LocationUtils.getJkuatLocations()[which])
            }
            .setTitle(R.string.location_list_title)
        builder.create().show()
    }
}
