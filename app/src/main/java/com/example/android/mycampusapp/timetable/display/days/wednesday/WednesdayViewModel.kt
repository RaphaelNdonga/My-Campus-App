package com.example.android.mycampusapp.timetable.display.days.wednesday

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
import com.example.android.mycampusapp.timetable.receiver.WednesdayClassReceiver
import com.example.android.mycampusapp.util.Event
import com.example.android.mycampusapp.util.TimePickerValues
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot


class WednesdayViewModel(
    courseDocument: DocumentReference,
    private val app: Application
) : AndroidViewModel(app) {

    private val _wednesdayClasses = MutableLiveData<List<TimetableClass>>()
    val wednesdayClasses: LiveData<List<TimetableClass>>
        get() = _wednesdayClasses

    private val _status = MutableLiveData<DataStatus>()
    val status: LiveData<DataStatus> = _status

    private val _addNewClass = MutableLiveData<Event<Unit>>()
    val addNewClass: LiveData<Event<Unit>> = _addNewClass

    private val _openWednesdayClass = MutableLiveData<Event<TimetableClass>>()
    val openWednesdayClass: LiveData<Event<TimetableClass>>
        get() = _openWednesdayClass

    private val _deleteWednesdayClasses = MutableLiveData<Event<Unit>>()
    val deleteWednesdayClasses: LiveData<Event<Unit>>
        get() = _deleteWednesdayClasses

    private val _hasPendingWrites = MutableLiveData<Event<Boolean>>()
    val hasPendingWrites:LiveData<Event<Boolean>>
        get() = _hasPendingWrites

    private val wednesdayFirestore = courseDocument.collection("wednesday")

    private fun checkDataStatus(){
        val wednesdayClasses = _wednesdayClasses.value
        try {
            if (wednesdayClasses.isNullOrEmpty()) {
                throw NullPointerException()
            }
            _status.value =
                DataStatus.NOT_EMPTY
        } catch (e: Exception) {
            _status.value =
                DataStatus.EMPTY
        }
    }

    fun displayWednesdayClassDetails(wednesdayClass: TimetableClass) {
        _openWednesdayClass.value =
            Event(wednesdayClass)

        //Ensures that the timepickervalues object is updated before passing the arguments to wednesdayInput fragment
        TimePickerValues.timeSetByTimePicker.value = wednesdayClass.time
    }

    fun addNewClass() {
        _addNewClass.value = Event(Unit)
        TimePickerValues.timeSetByTimePicker.value = ""
    }

    fun deleteList(list: List<TimetableClass?>){
        list.forEach { wednesdayClass ->
            if (wednesdayClass != null) {
                wednesdayFirestore.document(wednesdayClass.id).delete()
                cancelAlarm(wednesdayClass)
            }
        }
        checkDataStatus()
    }

    fun deleteIconPressed() {
        _deleteWednesdayClasses.value =
            Event(Unit)
    }

    private fun updateData(mutableList: MutableList<TimetableClass>) {
        _wednesdayClasses.value = mutableList
        checkDataStatus()
    }

    fun addSnapshotListener(): ListenerRegistration {
        _status.value = DataStatus.LOADING
        return wednesdayFirestore.addSnapshotListener { querySnapshot: QuerySnapshot?, _: FirebaseFirestoreException? ->
            val mutableList: MutableList<TimetableClass> = mutableListOf()
            querySnapshot?.documents?.forEach { document ->
                _hasPendingWrites.value = Event(document.metadata.hasPendingWrites())

                val wednesdayClass = document.toObject(TimetableClass::class.java)
                wednesdayClass?.let {
                    mutableList.add(it)
                }
            }
            updateData(mutableList)
        }
    }

    private fun cancelAlarm(wednesdayClass: TimetableClass) {
        val alarmManager = app.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(app, WednesdayClassReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            app,
            wednesdayClass.alarmRequestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        alarmManager.cancel(pendingIntent)
    }
}