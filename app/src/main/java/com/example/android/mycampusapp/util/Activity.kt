package com.example.android.mycampusapp.util

import android.app.Activity
import android.app.TimePickerDialog
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.android.mycampusapp.Event
import com.example.android.mycampusapp.R
import com.example.android.mycampusapp.classInput.ClassInputViewModel

object TimePickerValues {
    val hourMinuteSet = MutableLiveData<List<Int>>()
}

fun Activity.showTimeDialog(hour: Int, minute: Int) {
    TimePickerDialog(
        this,
        R.style.TimePicker,
        TimePickerDialog.OnTimeSetListener { view, hourOfDay, minuteOfDay ->
            TimePickerValues.hourMinuteSet.value = listOf(hourOfDay,minuteOfDay)
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