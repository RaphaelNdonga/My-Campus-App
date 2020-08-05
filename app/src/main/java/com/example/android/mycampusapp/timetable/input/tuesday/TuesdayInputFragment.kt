package com.example.android.mycampusapp.timetable.input.tuesday

import android.app.NotificationChannel
import android.app.NotificationManager
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
import com.example.android.mycampusapp.databinding.FragmentTuesdayInputBinding
import com.example.android.mycampusapp.util.EventObserver
import com.example.android.mycampusapp.util.setupSnackbar
import com.example.android.mycampusapp.util.setupTimeDialog
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class TuesdayInputFragment : Fragment() {

    @Inject
    lateinit var firestore: FirebaseFirestore

    private val tuesdayArgs by navArgs<TuesdayInputFragmentArgs>()
    private lateinit var viewModel: TuesdayInputViewModel


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil.inflate<FragmentTuesdayInputBinding>(
            inflater,
            R.layout.fragment_tuesday_input,
            container,
            false
        )
        val app = requireActivity().application
        viewModel = ViewModelProvider(
            this,
            TuesdayInputViewModelFactory(
                firestore,
                tuesdayArgs.tuesdayClass,
                app
            )
        ).get(TuesdayInputViewModel::class.java)


        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        viewModel.navigator.observe(viewLifecycleOwner,
            EventObserver {
                findNavController().navigate(TuesdayInputFragmentDirections.actionTuesdayInputFragmentToTimetableFragment())
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
