package com.example.android.mycampusapp.timetable.display.days.tuesday

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.android.mycampusapp.timetable.data.TuesdayClass
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

class TuesdayViewModel(private val courseDocument:DocumentReference) : ViewModel() {

    private val _tuesdayClasses2 = MutableLiveData<List<TuesdayClass>>()
    val tuesdayClasses2:LiveData<List<TuesdayClass>>
        get() = _tuesdayClasses2

    private val _status = MutableLiveData<TuesdayDataStatus>()
    val status:LiveData<TuesdayDataStatus> = _status

    private val tuesdayFirestore = courseDocument.collection("tuesday")


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


    private fun checkTuesdayDataStatus() = uiScope.launch {
        val tuesdayClasses = _tuesdayClasses2.value
        try {
            if(tuesdayClasses.isNullOrEmpty()){
                throw NullPointerException()
            }
            _status.value =
                TuesdayDataStatus.NOT_EMPTY
        }
        catch (e:Exception){
            _status.value =
                TuesdayDataStatus.EMPTY
        }

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
                tuesdayFirestore.document(tuesdayClass.id).delete()
            }
        }
        checkTuesdayDataStatus()
    }

    fun deleteIconPressed() {
        _deleteTuesdayClasses.value =
            Event(Unit)
    }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }

    private fun updateData(mutableList: MutableList<TuesdayClass>) {
        _tuesdayClasses2.value = mutableList
        checkTuesdayDataStatus()
    }
    fun addSnapshotListener():ListenerRegistration{
        _status.value = TuesdayDataStatus.LOADING
        return  tuesdayFirestore.addSnapshotListener { querySnapshot: QuerySnapshot?, _: FirebaseFirestoreException? ->
                val mutableList: MutableList<TuesdayClass> = mutableListOf()
                querySnapshot?.documents?.forEach { document ->
                    val id = document.getString("id")
                    val subject = document.getString("subject")
                    val time = document.getString("time")
                    if (id != null && subject != null && time != null) {
                        val tuesdayClass = TuesdayClass(id, subject, time)
                        mutableList.add(tuesdayClass)
                    }
                }
                updateData(mutableList)
            }
    }
}

enum class TuesdayDataStatus {EMPTY, NOT_EMPTY, LOADING}
