package com.example.android.mycampusapp.timetable.input.friday

import android.app.AlertDialog
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
import com.example.android.mycampusapp.databinding.FragmentFridayInputBinding
import com.example.android.mycampusapp.location.LocationUtils
import com.example.android.mycampusapp.util.*
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.CollectionReference
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class FridayInputFragment : Fragment() {

    @Inject
    lateinit var courseCollection:CollectionReference

    private val fridayArgs by navArgs<FridayInputFragmentArgs>()
    private lateinit var viewModel: FridayInputViewModel
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var courseId:String


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        sharedPreferences = requireActivity().getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)
        courseId = sharedPreferences.getString(COURSE_ID,"")!!
        val binding = DataBindingUtil.inflate<FragmentFridayInputBinding>(
            inflater,
            R.layout.fragment_friday_input,
            container,
            false
        )
        val app = requireActivity().application
        viewModel = ViewModelProvider(
            this,
            FridayInputViewModelFactory(
                fridayArgs.fridayClass,
                app,
                courseCollection.document(courseId)
            )
        ).get(FridayInputViewModel::class.java)


        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        viewModel.displayNavigator.observe(viewLifecycleOwner,
            EventObserver {
                findNavController().navigate(FridayInputFragmentDirections.actionFridayInputFragmentToTimetableFragment())
            })

        val time = binding.classTimeEditText

        viewModel.timeSetByTimePicker.observe(viewLifecycleOwner, Observer { hourMinute ->
            time.setText(hourMinute)
        })

        binding.classLocationEditText.setOnClickListener {
            showLocationList()
        }
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

    private fun showLocationList() {
        val alertDialogBuilder = AlertDialog.Builder(requireActivity(),R.style.MyCampusApp_Dialog)
            .setTitle(R.string.location_list_title)
            .setItems(R.array.locations) { _, which ->
                viewModel.setLocation(LocationUtils.getJkuatLocations()[which])
            }
        alertDialogBuilder.create().show()
    }
}
