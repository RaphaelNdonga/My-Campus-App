package com.example.android.mycampusapp.timetable.display.days.wednesday

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.android.mycampusapp.timetable.data.WednesdayClass
import com.example.android.mycampusapp.util.Event
import com.example.android.mycampusapp.util.TimePickerValues
import com.google.firebase.firestore.DocumentReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class WednesdayViewModel(private val courseDocument: DocumentReference) : ViewModel() {

    private val _wednesdayClasses2 = MutableLiveData<List<WednesdayClass>>()
    val wednesdayClasses2:LiveData<List<WednesdayClass>>
        get() = _wednesdayClasses2

    private val _status = MutableLiveData<WednesdayDataStatus>()
    val status: LiveData<WednesdayDataStatus> = _status

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

    init {
        checkWednesdayDataStatus()
    }

    fun checkWednesdayDataStatus() = uiScope.launch {
        val wednesdayClasses = _wednesdayClasses2.value
        try {
            if(wednesdayClasses.isNullOrEmpty()){
                throw NullPointerException()
            }
            _status.value =
                WednesdayDataStatus.NOT_EMPTY
        }catch (e:Exception){
            _status.value =
                WednesdayDataStatus.EMPTY
        }
    }

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
        list.forEach { wednesdayClass ->
            if (wednesdayClass != null) {
                val wednesdayFirestore = courseDocument.collection("wednesday")
                wednesdayFirestore.document(wednesdayClass.id).delete()
            }
        }
        checkWednesdayDataStatus()
    }

    fun deleteIconPressed() {
        _deleteWednesdayClasses.value =
            Event(Unit)
    }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }

    fun updateData(mutableList: MutableList<WednesdayClass>) {
        _wednesdayClasses2.value = mutableList
    }
}

enum class WednesdayDataStatus {
    EMPTY, NOT_EMPTY

}
