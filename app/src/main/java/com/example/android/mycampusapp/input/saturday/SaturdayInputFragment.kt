package com.example.android.mycampusapp.input.saturday

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
import com.example.android.mycampusapp.util.EventObserver
import com.example.android.mycampusapp.R
import com.example.android.mycampusapp.data.timetable.local.TimetableDataSource
import com.example.android.mycampusapp.databinding.FragmentSaturdayInputBinding
import com.example.android.mycampusapp.di.TimetableDatabase
import com.example.android.mycampusapp.util.setupTimeDialog
import com.example.android.mycampusapp.util.setupSnackbar
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class SaturdayInputFragment : Fragment() {

    @TimetableDatabase
    @Inject
    lateinit var timetableRepository: TimetableDataSource

    private val saturdayArgs by navArgs<SaturdayInputFragmentArgs>()
    private lateinit var viewModel: SaturdayInputViewModel


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil.inflate<FragmentSaturdayInputBinding>(
            inflater,
            R.layout.fragment_saturday_input,
            container,
            false
        )
        val app = requireActivity().application
        viewModel = ViewModelProvider(
            this,
            SaturdayInputViewModelFactory(timetableRepository, saturdayArgs.saturdayClass, app)
        ).get(SaturdayInputViewModel::class.java)


        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        viewModel.navigator.observe(viewLifecycleOwner,
            EventObserver {
                findNavController().navigate(SaturdayInputFragmentDirections.actionSaturdayInputFragmentToTimetableFragment())
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
