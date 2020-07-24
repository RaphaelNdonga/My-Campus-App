package com.example.android.mycampusapp.display.days.friday

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.android.mycampusapp.util.Event
import com.example.android.mycampusapp.data.FridayClass
import com.example.android.mycampusapp.data.timetable.local.TimetableDataSource
import com.example.android.mycampusapp.util.TimePickerValues
import kotlinx.coroutines.*
import java.lang.Exception
import java.lang.NullPointerException

class FridayViewModel(private val repository: TimetableDataSource) : ViewModel() {

    val fridayClasses = repository.observeAllFridayClasses()

    private val _status = MutableLiveData<FridayDataStatus>()
    val status:LiveData<FridayDataStatus> = _status

    private val _addNewClass = MutableLiveData<Event<Unit>>()
    val addNewClass: LiveData<Event<Unit>> = _addNewClass

    private val _openFridayClass = MutableLiveData<Event<FridayClass>>()
    val openFridayClass: LiveData<Event<FridayClass>>
        get() = _openFridayClass

    private val _deleteFridayClasses = MutableLiveData<Event<Unit>>()
    val deleteFridayClasses: LiveData<Event<Unit>>
        get() = _deleteFridayClasses

    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + job)

    init {
        checkFridayDataStatus()
    }

    private fun checkFridayDataStatus() = uiScope.launch{
        val fridayClasses = repository.getAllFridayClasses()
        try {
            if(fridayClasses.isNullOrEmpty()){
                throw NullPointerException()
            }
            _status.value = FridayDataStatus.NOT_EMPTY
        }catch (e:Exception){
            _status.value = FridayDataStatus.EMPTY
        }
    }

    fun displayFridayClassDetails(fridayClass: FridayClass) {
        _openFridayClass.value =
            Event(fridayClass)

        //Ensures that the timepickervalues object is updated before passing the arguments to fridayInput fragment
        TimePickerValues.timeSetByTimePicker.value = fridayClass.time
    }

    fun addNewClass() {
        _addNewClass.value = Event(Unit)
        TimePickerValues.timeSetByTimePicker.value = ""
    }

    fun deleteList(list: List<FridayClass?>) = uiScope.launch {
        list.forEach { fridayClass->
            if (fridayClass != null) {
                repository.deleteFridayClass(fridayClass)
            }
        }
        checkFridayDataStatus()
    }

    fun deleteIconPressed() {
        _deleteFridayClasses.value =
            Event(Unit)
    }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }
}

enum class FridayDataStatus {
    EMPTY, NOT_EMPTY
}
