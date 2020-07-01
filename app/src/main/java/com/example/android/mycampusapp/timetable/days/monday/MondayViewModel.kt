package com.example.android.mycampusapp.timetable.days.monday

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.android.mycampusapp.Event
import com.example.android.mycampusapp.data.MondayClass
import com.example.android.mycampusapp.data.timetable.local.TimetableDataSource

class MondayViewModel(repository:TimetableDataSource): ViewModel() {

    val mondayClasses = repository.observeAllMondayClasses()

    private val _navigateToSelectedClass = MutableLiveData<Event<Unit>>()
    val navigateToSelectedClass:LiveData<Event<Unit>> = _navigateToSelectedClass

    fun displayMondayClassDetails(mondayClass: MondayClass){
        //TODO("Not yet implemented")
    }

    fun addNewTask() {
        _navigateToSelectedClass.value = Event(Unit)
    }
}