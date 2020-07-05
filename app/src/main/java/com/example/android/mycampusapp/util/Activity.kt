package com.example.android.mycampusapp.util

import android.app.Activity
import android.app.TimePickerDialog
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.android.mycampusapp.Event
import com.example.android.mycampusapp.R
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

object TimePickerValues {
    val hourMinuteSet = MutableLiveData<String>()
}

fun Activity.showTimeDialog(hour: Int, minute: Int) {
    TimePickerDialog(
        this,
        R.style.TimePicker,
        TimePickerDialog.OnTimeSetListener { view, hourOfDay, minuteOfDay ->
            val inputTime = SimpleDateFormat("HH:mm", Locale.US).parse("$hourOfDay:$minuteOfDay")
            val tf = DateFormat.getTimeInstance(DateFormat.SHORT, Locale.US)
            val outputTime = tf.format(inputTime!!)
            TimePickerValues.hourMinuteSet.value = outputTime.toString()
        },
        hour,
        minute,
        true
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