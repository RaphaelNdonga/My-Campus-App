package com.example.android.mycampusapp.display.days.monday

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.android.mycampusapp.util.Event
import com.example.android.mycampusapp.data.MondayClass
import com.example.android.mycampusapp.data.timetable.local.TimetableDataSource
import com.example.android.mycampusapp.util.TimePickerValues
import kotlinx.coroutines.*
import timber.log.Timber

class MondayViewModel(private val repository: TimetableDataSource) : ViewModel() {

    val mondayClasses: LiveData<List<MondayClass>> = repository.observeAllMondayClasses()

    private  var mondayClassesList: List<MondayClass>? = null

    private val _status = MutableLiveData<MondayDataStatus>()
    val status: LiveData<MondayDataStatus> = _status

    private val _addNewClass = MutableLiveData<Event<Unit>>()
    val addNewClass: LiveData<Event<Unit>> = _addNewClass

    private val _openMondayClass = MutableLiveData<Event<MondayClass>>()
    val openMondayClass: LiveData<Event<MondayClass>>
        get() = _openMondayClass

    private val _deleteMondayClasses = MutableLiveData<Event<Unit>>()
    val deleteMondayClasses: LiveData<Event<Unit>>
        get() = _deleteMondayClasses

    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + job)

    init {
        refreshMondayClasses()
        checkMondayDataStatus()
    }

    private fun checkMondayDataStatus() {
//        if(mondayClassesList.isNullOrEmpty()){
//            _status.value = MondayDataStatus.NOT_EMPTY
//            return
//        }
        _status.value = MondayDataStatus.EMPTY
    }


    fun displayMondayClassDetails(mondayClass: MondayClass) {
        _openMondayClass.value =
            Event(mondayClass)

        //Ensures that the timepickervalues object is updated before passing the arguments to mondayInput fragment
        TimePickerValues.timeSetByTimePicker.value = mondayClass.time
    }

    fun addNewClass() {
        _addNewClass.value = Event(Unit)
        TimePickerValues.timeSetByTimePicker.value = ""
    }

    fun deleteList(list: List<MondayClass?>) = uiScope.launch {
        list.forEach { mondayClass ->
            if (mondayClass != null) {
                repository.deleteMondayClass(mondayClass)
            }
        }
        refreshMondayClasses()
        checkMondayDataStatus()
    }

    fun deleteIconPressed() {
        _deleteMondayClasses.value =
            Event(Unit)
    }
    private fun refreshMondayClasses(){
        uiScope.launch {
            mondayClassesList = repository.getAllMondayClasses()
        }
    }


    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }
}

enum class MondayDataStatus {
    EMPTY, NOT_EMPTY
}

