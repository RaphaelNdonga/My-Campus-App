package com.example.android.mycampusapp.timetable.days.monday

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.android.mycampusapp.Event
import com.example.android.mycampusapp.data.MondayClass
import com.example.android.mycampusapp.data.timetable.local.TimetableDataSource

class MondayViewModel(repository:TimetableDataSource): ViewModel() {

    val mondayClasses = repository.observeAllMondayClasses()

    private val _navigateToSelectedClass = MutableLiveData<Event<MondayClass>>()
    val navigateToSelectedClass:LiveData<Event<MondayClass>> = _navigateToSelectedClass

    fun displayMondayClassDetails(mondayClass: MondayClass){
        _navigateToSelectedClass.value = Event(mondayClass)
    }
}