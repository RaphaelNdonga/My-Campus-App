package com.example.android.mycampusapp.timetable.display.days.saturday

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.android.mycampusapp.timetable.data.SaturdayClass
import com.example.android.mycampusapp.util.Event
import com.example.android.mycampusapp.util.TimePickerValues
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class SaturdayViewModel(courseDocument: DocumentReference) : ViewModel() {

    private val _saturdayClasses2 = MutableLiveData<List<SaturdayClass>>()
    val saturdayClasses2:LiveData<List<SaturdayClass>>
        get() = _saturdayClasses2

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

    private val saturdayFirestore = courseDocument.collection("saturday")

    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + job)


    private fun checkSaturdayDataStatus() = uiScope.launch {
        val saturdayClasses = _saturdayClasses2.value
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
                saturdayFirestore.document(saturdayClass.id).delete()
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

    private fun updateData(mutableList: MutableList<SaturdayClass>) {
        _saturdayClasses2.value = mutableList
        checkSaturdayDataStatus()
    }

    fun addSnapshotListener(): ListenerRegistration {
        _status.value = SaturdayDataStatus.LOADING
        return saturdayFirestore.addSnapshotListener { querySnapshot: QuerySnapshot?, _: FirebaseFirestoreException? ->
            val mutableList: MutableList<SaturdayClass> = mutableListOf()
            querySnapshot?.documents?.forEach { document ->
                val id = document.getString("id")
                val subject = document.getString("subject")
                val time = document.getString("time")
                if (id != null && subject != null && time != null) {
                    val saturdayClass = SaturdayClass(id, subject, time)
                    mutableList.add(saturdayClass)
                }
            }
            updateData(mutableList)
        }
    }
}

enum class SaturdayDataStatus {
    EMPTY, NOT_EMPTY, LOADING
}
