package com.example.android.mycampusapp.util

import androidx.lifecycle.MutableLiveData

object TimePickerValues {
    val timePickerHourSet = MutableLiveData<Int>()
    val timePickerMinuteSet = MutableLiveData<Int>()
    val timeSetByTimePicker = MutableLiveData<String>()
}