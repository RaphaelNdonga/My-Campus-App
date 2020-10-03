package com.example.android.mycampusapp.timetable.display.days.monday

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.android.mycampusapp.timetable.data.MondayClass
import com.example.android.mycampusapp.util.Event
import com.example.android.mycampusapp.util.TimePickerValues
import com.google.firebase.firestore.DocumentReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MondayViewModel(courseId: DocumentReference) : ViewModel() {

    private val _mondayClasses2 = MutableLiveData<List<MondayClass>>()
    val mondayClasses2:LiveData<List<MondayClass>>
        get() = _mondayClasses2

    private val mondayFirestore = courseId.collection("monday")

    private val _status = MutableLiveData<MondayDataStatus>()
    val status: LiveData<MondayDataStatus>
        get() = _status

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
        checkMondayDataStatus()
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
                mondayFirestore.document(mondayClass.id).delete()
            }
        }
        checkMondayDataStatus()
    }

    fun deleteIconPressed() {
        _deleteMondayClasses.value =
            Event(Unit)
    }

    fun checkMondayDataStatus() {
        uiScope.launch {
            val mondayClasses: List<MondayClass>? = _mondayClasses2.value
            try {
                if (mondayClasses.isNullOrEmpty()) {
                    throw NullPointerException()
                }
                _status.value =
                    MondayDataStatus.NOT_EMPTY
            } catch (e: Exception) {
                _status.value =
                    MondayDataStatus.EMPTY
            }
        }
    }


    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }

    fun updateData(mutableList: MutableList<MondayClass>) {
        _mondayClasses2.value = mutableList
    }
}

enum class MondayDataStatus {
    EMPTY, NOT_EMPTY
}

