package com.example.android.mycampusapp.display.days.thursday

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.android.mycampusapp.util.Event
import com.example.android.mycampusapp.data.ThursdayClass
import com.example.android.mycampusapp.data.timetable.local.TimetableDataSource
import com.example.android.mycampusapp.util.TimePickerValues
import kotlinx.coroutines.*
import timber.log.Timber

class ThursdayViewModel(private val repository: TimetableDataSource) : ViewModel() {

    val thursdayClasses = repository.observeAllThursdayClasses()

    private val _status = MutableLiveData<ThursdayDataStatus>()
    val status:LiveData<ThursdayDataStatus> = _status

    private val _addNewClass = MutableLiveData<Event<Unit>>()
    val addNewClass: LiveData<Event<Unit>> = _addNewClass

    private val _openThursdayClass = MutableLiveData<Event<ThursdayClass>>()
    val openThursdayClass: LiveData<Event<ThursdayClass>>
        get() = _openThursdayClass

    private val _deleteThursdayClasses = MutableLiveData<Event<Unit>>()
    val deleteThursdayClasses: LiveData<Event<Unit>>
        get() = _deleteThursdayClasses

    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + job)

    init {
        _status.value = ThursdayDataStatus.EMPTY
    }
    fun displayThursdayClassDetails(thursdayClass: ThursdayClass) {
        _openThursdayClass.value =
            Event(thursdayClass)

        //Ensures that the timepickervalues object is updated before passing the arguments to thursdayInput fragment
        TimePickerValues.timeSetByTimePicker.value = thursdayClass.time
    }

    fun addNewClass() {
        _addNewClass.value = Event(Unit)
        Timber.i("Should be navigating to thursday input")
        TimePickerValues.timeSetByTimePicker.value = ""
    }

    fun deleteList(list: List<ThursdayClass?>) = uiScope.launch {
        list.forEach { thursdayClass->
            if (thursdayClass != null) {
                repository.deleteThursdayClass(thursdayClass)
            }
        }
    }

    fun deleteIconPressed() {
        _deleteThursdayClasses.value =
            Event(Unit)
    }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }
}

enum class ThursdayDataStatus {
    EMPTY, NOT_EMPTY
}
