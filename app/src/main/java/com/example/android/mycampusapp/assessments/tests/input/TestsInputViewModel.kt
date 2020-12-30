package com.example.android.mycampusapp.assessments.tests.input

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TestsInputViewModel : ViewModel() {
    val textBoxSubject = MutableLiveData<String>()
    val textBoxTime = MutableLiveData<String>()
    val textBoxDate = MutableLiveData<String>()
    val textBoxLocation = MutableLiveData<String>()
    val textBoxRoom = MutableLiveData<String>()
}