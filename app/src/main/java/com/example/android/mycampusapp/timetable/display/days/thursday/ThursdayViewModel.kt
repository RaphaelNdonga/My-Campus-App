package com.example.android.mycampusapp.timetable.display.days.thursday

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.android.mycampusapp.timetable.data.ThursdayClass
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
import timber.log.Timber

class ThursdayViewModel(courseDocument: DocumentReference) : ViewModel() {

    private val _thursdayClasses2 = MutableLiveData<List<ThursdayClass>>()
    val thursdayClasses2:LiveData<List<ThursdayClass>>
        get() = _thursdayClasses2

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

    private val thursdayFirestore = courseDocument.collection("thursday")

    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + job)


    private fun checkThursdayDataStatus() = uiScope.launch {
        val thursdayClasses = _thursdayClasses2.value
        try {
            if(thursdayClasses.isNullOrEmpty()){
                throw NullPointerException()
            }
            _status.value =
                ThursdayDataStatus.NOT_EMPTY
        }catch (e:Exception){
            _status.value =
                ThursdayDataStatus.EMPTY
        }
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
                thursdayFirestore.document(thursdayClass.id).delete()
            }
        }
        checkThursdayDataStatus()
    }

    fun deleteIconPressed() {
        _deleteThursdayClasses.value =
            Event(Unit)
    }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }

    private fun update(mutableList: MutableList<ThursdayClass>) {
        _thursdayClasses2.value = mutableList
        checkThursdayDataStatus()
    }

    fun addSnapshotListener(): ListenerRegistration {
        _status.value = ThursdayDataStatus.LOADING
        return thursdayFirestore.addSnapshotListener { querySnapshot: QuerySnapshot?, _: FirebaseFirestoreException? ->
            val mutableList: MutableList<ThursdayClass> = mutableListOf()
            querySnapshot?.documents?.forEach { document ->
                val id = document.getString("id")
                val subject = document.getString("subject")
                val time = document.getString("time")
                if (id != null && subject != null && time != null) {
                    val thursdayClass = ThursdayClass(id, subject, time)
                    mutableList.add(thursdayClass)
                }
            }
            update(mutableList)
        }
    }
}

enum class ThursdayDataStatus {
    EMPTY, NOT_EMPTY, LOADING
}
