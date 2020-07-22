package com.example.android.mycampusapp.display.days.wednesday

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.android.mycampusapp.util.Event
import com.example.android.mycampusapp.data.WednesdayClass
import com.example.android.mycampusapp.data.timetable.local.TimetableDataSource
import com.example.android.mycampusapp.util.TimePickerValues
import kotlinx.coroutines.*

class WednesdayViewModel(private val repository: TimetableDataSource) : ViewModel() {

    val wednesdayClasses = repository.observeAllWednesdayClasses()

    private val _addNewClass = MutableLiveData<Event<Unit>>()
    val addNewClass: LiveData<Event<Unit>> = _addNewClass

    private val _openWednesdayClass = MutableLiveData<Event<WednesdayClass>>()
    val openWednesdayClass: LiveData<Event<WednesdayClass>>
        get() = _openWednesdayClass

    private val _deleteWednesdayClasses = MutableLiveData<Event<Unit>>()
    val deleteWednesdayClasses: LiveData<Event<Unit>>
        get() = _deleteWednesdayClasses

    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + job)

    fun displayWednesdayClassDetails(wednesdayClass: WednesdayClass) {
        _openWednesdayClass.value =
            Event(wednesdayClass)

        //Ensures that the timepickervalues object is updated before passing the arguments to wednesdayInput fragment
        TimePickerValues.timeSetByTimePicker.value = wednesdayClass.time
    }

    fun addNewClass() {
        _addNewClass.value = Event(Unit)
        TimePickerValues.timeSetByTimePicker.value = ""
    }

    fun deleteList(list: List<WednesdayClass?>) = uiScope.launch {
        list.forEach { wednesdayClass->
            if (wednesdayClass != null) {
                repository.deleteWednesdayClass(wednesdayClass)
            }
        }
    }

    fun deleteIconPressed() {
        _deleteWednesdayClasses.value =
            Event(Unit)
    }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }
}