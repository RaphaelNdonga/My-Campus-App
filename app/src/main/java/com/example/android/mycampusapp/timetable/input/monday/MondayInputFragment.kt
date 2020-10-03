package com.example.android.mycampusapp.timetable.input.monday

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
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
import com.example.android.mycampusapp.databinding.FragmentMondayInputBinding
import com.example.android.mycampusapp.util.*
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MondayInputFragment : Fragment() {

    @Inject
    lateinit var firestore: FirebaseFirestore

    @Inject
    lateinit var coursesCollection: CollectionReference

    private val mondayArgs by navArgs<MondayInputFragmentArgs>()
    private lateinit var viewModel: MondayInputViewModel
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var courseId:String


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        sharedPreferences = requireActivity().getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)
        courseId = sharedPreferences.getString(COURSE_ID,"")!!
        val binding = DataBindingUtil.inflate<FragmentMondayInputBinding>(
            inflater,
            R.layout.fragment_monday_input,
            container,
            false
        )
        val app = requireActivity().application
        viewModel = ViewModelProvider(
            this,
            MondayInputViewModelFactory(
                coursesCollection.document(courseId),
                mondayArgs.mondayClass,
                app
            )
        ).get(MondayInputViewModel::class.java)


        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        viewModel.navigator.observe(viewLifecycleOwner,
            EventObserver {
                findNavController().navigate(MondayInputFragmentDirections.actionMondayInputFragmentToTimetableFragment())
            })

        val time = binding.classTimeEditText

        viewModel.timeSetByTimePicker.observe(viewLifecycleOwner, Observer { hourMinute ->
            time.setText(hourMinute)
        })

        createChannel(
            getString(R.string.timetable_notification_channel_id),
            getString(R.string.timetable_notification_channel_name)
        )
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

    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId, channelName, NotificationManager.IMPORTANCE_HIGH
            ).apply {
                setShowBadge(false)
            }
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = getString(R.string.timetable_channel_description)

            val notificationManager =
                requireActivity().getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(notificationChannel)
        }
    }
}
