package com.example.android.mycampusapp.timetable.display.days.friday

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.android.mycampusapp.timetable.data.FridayClass
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

class FridayViewModel(courseDocument: DocumentReference) : ViewModel() {

    private val _fridayClasses2 = MutableLiveData<List<FridayClass>>()
    val fridayClasses2: LiveData<List<FridayClass>>
        get() = _fridayClasses2

    private val fridayFirestore = courseDocument.collection("friday")


    private val _status = MutableLiveData<FridayDataStatus>()
    val status: LiveData<FridayDataStatus> = _status

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

    private fun checkFridayDataStatus() = uiScope.launch {
        val fridayClasses = _fridayClasses2.value
        try {
            if (fridayClasses.isNullOrEmpty()) {
                throw NullPointerException()
            }
            _status.value = FridayDataStatus.NOT_EMPTY
        } catch (e: Exception) {
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
        list.forEach { fridayClass ->
            if (fridayClass != null) {
                fridayFirestore.document(fridayClass.id).delete()
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

    private fun updateData(mutableList: MutableList<FridayClass>) {
        _fridayClasses2.value = mutableList
        checkFridayDataStatus()
    }

    fun addSnapshotListener(): ListenerRegistration {
        _status.value = FridayDataStatus.LOADING
        return fridayFirestore.addSnapshotListener { querySnapshot: QuerySnapshot?, _: FirebaseFirestoreException? ->
            val mutableList: MutableList<FridayClass> = mutableListOf()
            querySnapshot?.documents?.forEach { document ->
                Timber.i("We are in the loop")
                val id = document.getString("id")
                val subject = document.getString("subject")
                val time = document.getString("time")
                if (id != null && subject != null && time != null) {
                    val fridayClass = FridayClass(id, subject, time)
                    mutableList.add(fridayClass)
                }
            }
            updateData(mutableList)
        }
    }
}

enum class FridayDataStatus {
    EMPTY, NOT_EMPTY, LOADING
}
