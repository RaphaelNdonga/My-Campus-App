package com.example.android.mycampusapp.timetable.display.days.sunday

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.android.mycampusapp.timetable.data.SundayClass
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

class SundayViewModel(courseDocument: DocumentReference) : ViewModel() {

    private val sundayFirestore = courseDocument.collection("sunday")
    private val _sundayClasses2 = MutableLiveData<List<SundayClass>>()
    val sundayClasses2:LiveData<List<SundayClass>>
        get() = _sundayClasses2


    private val _status = MutableLiveData<SundayDataStatus>()
    val status:LiveData<SundayDataStatus> = _status

    private val _addNewClass = MutableLiveData<Event<Unit>>()
    val addNewClass: LiveData<Event<Unit>> = _addNewClass

    private val _openSundayClass = MutableLiveData<Event<SundayClass>>()
    val openSundayClass: LiveData<Event<SundayClass>>
        get() = _openSundayClass

    private val _deleteSundayClasses = MutableLiveData<Event<Unit>>()
    val deleteSundayClasses: LiveData<Event<Unit>>
        get() = _deleteSundayClasses

    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + job)

    private fun checkSundayDataStatus() = uiScope.launch {
        val sundayClasses = _sundayClasses2.value
        try {
            if(sundayClasses.isNullOrEmpty()){
                throw NullPointerException()
            }
            _status.value =
                SundayDataStatus.NOT_EMPTY
        }catch (e:Exception){
            _status.value =
                SundayDataStatus.EMPTY
        }
    }

    fun displaySundayClassDetails(sundayClass: SundayClass) {
        _openSundayClass.value =
            Event(sundayClass)

        //Ensures that the timepickervalues object is updated before passing the arguments to sundayInput fragment
        TimePickerValues.timeSetByTimePicker.value = sundayClass.time
    }

    fun addNewClass() {
        _addNewClass.value = Event(Unit)
        Timber.i("Should be navigating to sunday input")
        TimePickerValues.timeSetByTimePicker.value = ""
    }

    fun deleteList(list: List<SundayClass?>) = uiScope.launch {
        list.forEach { sundayClass->
            if (sundayClass != null) {
                sundayFirestore.document(sundayClass.id).delete()
            }
        }
        checkSundayDataStatus()
    }

    fun deleteIconPressed() {
        _deleteSundayClasses.value =
            Event(Unit)
    }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }

    private fun updateData(mutableList: MutableList<SundayClass>) {
        _sundayClasses2.value = mutableList
        checkSundayDataStatus()
    }

    fun addSnapshotListener(): ListenerRegistration {
        _status.value = SundayDataStatus.LOADING
        return sundayFirestore.addSnapshotListener { querySnapshot: QuerySnapshot?, _: FirebaseFirestoreException? ->
            val mutableList: MutableList<SundayClass> = mutableListOf()
            querySnapshot?.documents?.forEach { document ->
                val id = document.getString("id")
                val subject = document.getString("subject")
                val time = document.getString("time")
                if (id != null && subject != null && time != null) {
                    val sundayClass = SundayClass(id, subject, time)
                    mutableList.add(sundayClass)
                }
            }
            updateData(mutableList)
        }
    }
}
enum class SundayDataStatus {
    EMPTY, NOT_EMPTY, LOADING
}
