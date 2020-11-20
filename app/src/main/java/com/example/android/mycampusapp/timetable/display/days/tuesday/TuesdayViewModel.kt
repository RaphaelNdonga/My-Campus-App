package com.example.android.mycampusapp.timetable.display.days.tuesday

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.android.mycampusapp.data.DataStatus
import com.example.android.mycampusapp.data.TimetableClass
import com.example.android.mycampusapp.timetable.receiver.TuesdayClassReceiver
import com.example.android.mycampusapp.util.Event
import com.example.android.mycampusapp.util.TimePickerValues
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot

class TuesdayViewModel(courseDocument: DocumentReference, private val app: Application) :
    AndroidViewModel(app) {

    private val _tuesdayClasses = MutableLiveData<List<TimetableClass>>()
    val tuesdayClasses: LiveData<List<TimetableClass>>
        get() = _tuesdayClasses

    private val _status = MutableLiveData<DataStatus>()
    val status: LiveData<DataStatus> = _status

    private val tuesdayFirestore = courseDocument.collection("tuesday")


    private val _addNewClass = MutableLiveData<Event<Unit>>()
    val addNewClass: LiveData<Event<Unit>> = _addNewClass

    private val _openTuesdayClass = MutableLiveData<Event<TimetableClass>>()
    val openTuesdayClass: LiveData<Event<TimetableClass>>
        get() = _openTuesdayClass

    private val _deleteTuesdayClasses = MutableLiveData<Event<Unit>>()
    val deleteTuesdayClasses: LiveData<Event<Unit>>
        get() = _deleteTuesdayClasses

    private val _isFromCache = MutableLiveData<Event<Unit>>()
    val isFromCache: LiveData<Event<Unit>>
        get() = _isFromCache


    private fun checkDataStatus() {
        val tuesdayClasses = _tuesdayClasses.value
        try {
            if (tuesdayClasses.isNullOrEmpty()) {
                throw NullPointerException()
            }
            _status.value =
                DataStatus.NOT_EMPTY
        } catch (e: Exception) {
            _status.value =
                DataStatus.EMPTY
        }

    }

    fun displayTuesdayClassDetails(tuesdayClass: TimetableClass) {
        _openTuesdayClass.value =
            Event(tuesdayClass)

        //Ensures that the timepickervalues object is updated before passing the arguments to tuesdayInput fragment
        TimePickerValues.timeSetByTimePicker.value = tuesdayClass.time
    }

    fun addNewClass() {
        _addNewClass.value = Event(Unit)
        TimePickerValues.timeSetByTimePicker.value = ""
    }

    fun deleteList(list: List<TimetableClass?>) {
        list.forEach { tuesdayClass ->
            if (tuesdayClass != null) {
                tuesdayFirestore.document(tuesdayClass.id).delete()
                cancelAlarm(tuesdayClass)
            }
        }
        checkDataStatus()
    }

    fun deleteIconPressed() {
        _deleteTuesdayClasses.value = Event(Unit)
    }

    private fun updateData(mutableList: MutableList<TimetableClass>) {
        _tuesdayClasses.value = mutableList
        checkDataStatus()
    }

    fun addSnapshotListener(): ListenerRegistration {
        _status.value = DataStatus.LOADING
        return tuesdayFirestore.addSnapshotListener { querySnapshot: QuerySnapshot?, _: FirebaseFirestoreException? ->
            val mutableList: MutableList<TimetableClass> = mutableListOf()
            querySnapshot?.documents?.forEach { document ->
                if (document.metadata.isFromCache) {
                    _isFromCache.value = Event(Unit)
                }

                val tuesdayClass = document.toObject(TimetableClass::class.java)
                tuesdayClass?.let {
                    mutableList.add(it)
                }
            }
            updateData(mutableList)
        }
    }

    private fun cancelAlarm(tuesdayClass: TimetableClass) {
        val alarmManager = app.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(app, TuesdayClassReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            app,
            tuesdayClass.alarmRequestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        alarmManager.cancel(pendingIntent)
    }
}
