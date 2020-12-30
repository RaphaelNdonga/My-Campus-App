package com.example.android.mycampusapp.assessments.tests.input

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.android.mycampusapp.data.CustomDate
import com.example.android.mycampusapp.data.Test

class TestsInputViewModel(private val test: Test?) : ViewModel() {

    val textBoxSubject = MutableLiveData<String>()
    val textBoxTime = MutableLiveData<String>()
    val textBoxDate = MutableLiveData<String>()
    val textBoxLocation = MutableLiveData<String>()
    val textBoxRoom = MutableLiveData<String>()

    private val _dateSet =
        MutableLiveData<CustomDate>(test?.let { CustomDate(test.year, test.month, test.day) })
    val dateSet:LiveData<CustomDate>
        get() = _dateSet

    fun setDateFromDatePicker(dateSet: CustomDate) {
        val dateText = "${dateSet.day}/${dateSet.month}/${dateSet.year}"
        textBoxDate.value = dateText
        _dateSet.value = dateSet
    }
}