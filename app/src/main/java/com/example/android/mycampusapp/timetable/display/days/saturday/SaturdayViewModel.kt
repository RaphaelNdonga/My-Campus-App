package com.example.android.mycampusapp.timetable.display.days.saturday

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.android.mycampusapp.timetable.data.SaturdayClass
import com.example.android.mycampusapp.timetable.data.timetable.local.TimetableDataSource
import com.example.android.mycampusapp.util.Event
import com.example.android.mycampusapp.util.TimePickerValues
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class SaturdayViewModel(private val repository: TimetableDataSource) : ViewModel() {

    val saturdayClasses = repository.observeAllSaturdayClasses()

    private val _status = MutableLiveData<SaturdayDataStatus>()
    val status:LiveData<SaturdayDataStatus> = _status

    private val _addNewClass = MutableLiveData<Event<Unit>>()
    val addNewClass: LiveData<Event<Unit>> = _addNewClass

    private val _openSaturdayClass = MutableLiveData<Event<SaturdayClass>>()
    val openSaturdayClass: LiveData<Event<SaturdayClass>>
        get() = _openSaturdayClass

    private val _deleteSaturdayClasses = MutableLiveData<Event<Unit>>()
    val deleteSaturdayClasses: LiveData<Event<Unit>>
        get() = _deleteSaturdayClasses

    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + job)

    init {
        checkSaturdayDataStatus()
    }

    private fun checkSaturdayDataStatus() = uiScope.launch {
        val saturdayClasses = repository.getAllSaturdayClasses()
        try {
            if(saturdayClasses.isNullOrEmpty()){
                throw NullPointerException()
            }
            _status.value =
                SaturdayDataStatus.NOT_EMPTY
        }catch (e:Exception){
            _status.value =
                SaturdayDataStatus.EMPTY
        }
    }

    fun displaySaturdayClassDetails(saturdayClass: SaturdayClass) {
        _openSaturdayClass.value =
            Event(saturdayClass)

        //Ensures that the timepickervalues object is updated before passing the arguments to saturdayInput fragment
        TimePickerValues.timeSetByTimePicker.value = saturdayClass.time
    }

    fun addNewClass() {
        _addNewClass.value = Event(Unit)
        TimePickerValues.timeSetByTimePicker.value = ""
    }

    fun deleteList(list: List<SaturdayClass?>) = uiScope.launch {
        list.forEach { saturdayClass->
            if (saturdayClass != null) {
                repository.deleteSaturdayClass(saturdayClass)
            }
        }
        checkSaturdayDataStatus()
    }

    fun deleteIconPressed() {
        _deleteSaturdayClasses.value =
            Event(Unit)
    }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }
}

enum class SaturdayDataStatus {
    EMPTY, NOT_EMPTY
}
