package com.example.android.mycampusapp.display.days.tuesday

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.android.mycampusapp.util.Event
import com.example.android.mycampusapp.data.TuesdayClass
import com.example.android.mycampusapp.data.timetable.local.TimetableDataSource
import com.example.android.mycampusapp.util.TimePickerValues
import kotlinx.coroutines.*

class TuesdayViewModel(private val repository: TimetableDataSource) : ViewModel() {

    val tuesdayClasses = repository.observeAllTuesdayClasses()

    private val _status = MutableLiveData<TuesdayDataStatus>()
    val status:LiveData<TuesdayDataStatus> = _status

    private val _addNewClass = MutableLiveData<Event<Unit>>()
    val addNewClass: LiveData<Event<Unit>> = _addNewClass

    private val _openTuesdayClass = MutableLiveData<Event<TuesdayClass>>()
    val openTuesdayClass: LiveData<Event<TuesdayClass>>
        get() = _openTuesdayClass

    private val _deleteTuesdayClasses = MutableLiveData<Event<Unit>>()
    val deleteTuesdayClasses: LiveData<Event<Unit>>
        get() = _deleteTuesdayClasses

    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + job)

    init {
        _status.value = TuesdayDataStatus.EMPTY
    }

    fun displayTuesdayClassDetails(tuesdayClass: TuesdayClass) {
        _openTuesdayClass.value =
            Event(tuesdayClass)

        //Ensures that the timepickervalues object is updated before passing the arguments to tuesdayInput fragment
        TimePickerValues.timeSetByTimePicker.value = tuesdayClass.time
    }

    fun addNewClass() {
        _addNewClass.value = Event(Unit)
        TimePickerValues.timeSetByTimePicker.value = ""
    }

    fun deleteList(list: List<TuesdayClass?>) = uiScope.launch {
        list.forEach { tuesdayClass->
            if (tuesdayClass != null) {
                repository.deleteTuesdayClass(tuesdayClass)
            }
        }
    }

    fun deleteIconPressed() {
        _deleteTuesdayClasses.value =
            Event(Unit)
    }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }
}

enum class TuesdayDataStatus {EMPTY, NOT_EMPTY}
