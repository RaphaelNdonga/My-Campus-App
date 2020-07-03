package com.example.android.mycampusapp.timetable.days.monday

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.android.mycampusapp.Event
import com.example.android.mycampusapp.data.MondayClass
import com.example.android.mycampusapp.data.timetable.local.TimetableDataSource

class MondayViewModel(repository:TimetableDataSource): ViewModel() {

    val mondayClasses = repository.observeAllMondayClasses()

    private val _addNewClass = MutableLiveData<Event<Unit>>()
    val addNewClass:LiveData<Event<Unit>> = _addNewClass

    private val _openMondayClass =  MutableLiveData<Event<MondayClass>>()
    val openMondayClass:LiveData<Event<MondayClass>>
        get() = _openMondayClass

    fun displayMondayClassDetails(mondayClass: MondayClass){
        _openMondayClass.value = Event(mondayClass)
    }

    fun addNewTask() {
        _addNewClass.value = Event(Unit)
    }
}