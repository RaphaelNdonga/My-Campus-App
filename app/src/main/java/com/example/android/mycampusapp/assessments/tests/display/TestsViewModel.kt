package com.example.android.mycampusapp.assessments.tests.display

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.android.mycampusapp.util.Event

class TestsViewModel : ViewModel() {
    private val _inputNavigator = MutableLiveData<Event<Unit>>()
    val inputNavigator: LiveData<Event<Unit>>
        get() = _inputNavigator

    fun navigateToInput(){
        initializeEvent(_inputNavigator)
    }
    private fun initializeEvent(mutableLiveData:MutableLiveData<Event<Unit>>){
        mutableLiveData.value = Event(Unit)
    }
}