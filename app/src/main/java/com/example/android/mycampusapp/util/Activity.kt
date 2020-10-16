package com.example.android.mycampusapp.util

import android.app.Activity
import android.app.TimePickerDialog
import android.text.format.DateFormat.is24HourFormat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.example.android.mycampusapp.R
import timber.log.Timber
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

fun Activity.showTimeDialog(hour: Int, minute: Int) {
    val is24HourFormat = is24HourFormat(this)
    TimePickerDialog(
        this,
        R.style.MyCampusApp_Dialog,
        TimePickerDialog.OnTimeSetListener { _, hourOfDay, minuteOfDay ->
            TimePickerValues.timePickerHourSet.value = hourOfDay
            TimePickerValues.timePickerMinuteSet.value = minuteOfDay
            val inputTime = SimpleDateFormat("HH:mm", Locale.US).parse("$hourOfDay:$minuteOfDay")
            Timber.i("The hour of day is $hourOfDay")
            if (is24HourFormat) {
                val tf = DateFormat.getTimeInstance(DateFormat.SHORT, Locale.UK)
                val outputTime = tf.format(inputTime!!)
                TimePickerValues.timeSetByTimePicker.value = outputTime.toString()
                Timber.i("The time set by the time picker is $outputTime")
            }else{
                val tf = DateFormat.getTimeInstance(DateFormat.SHORT, Locale.US)
                val outputTime = tf.format(inputTime!!)
                TimePickerValues.timeSetByTimePicker.value = outputTime.toString()
                Timber.i("The time set by the time picker is $outputTime")
            }

        },
        hour,
        minute,
        is24HourFormat
    ).run { show() }
}

fun Activity.setupTimeDialog(
    lifecycleOwner: LifecycleOwner,
    time: LiveData<Event<List<Int>>>
) {
    time.observe(lifecycleOwner, Observer { event ->
        event.getContentIfNotHandled()?.let {
            showTimeDialog(it[0], it[1])
        }
    })
}