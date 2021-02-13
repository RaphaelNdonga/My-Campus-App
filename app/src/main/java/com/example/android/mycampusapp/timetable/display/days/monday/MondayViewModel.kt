package com.example.android.mycampusapp.timetable.display.days.monday

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
import com.example.android.mycampusapp.timetable.receiver.MondayClassReceiver
import com.example.android.mycampusapp.util.Event
import com.google.firebase.firestore.*

class MondayViewModel(courseId: DocumentReference, private val app: Application) :
    AndroidViewModel(app) {

    private val _mondayClasses = MutableLiveData<List<TimetableClass>>()
    val mondayClasses: LiveData<List<TimetableClass>>
        get() = _mondayClasses

    private val mondayFirestore = courseId.collection("monday")

    private val _status = MutableLiveData<DataStatus>()
    val status: LiveData<DataStatus>
        get() = _status

    private val _addNewClass = MutableLiveData<Event<Unit>>()
    val addNewClass: LiveData<Event<Unit>> = _addNewClass

    private val _openMondayClass = MutableLiveData<Event<TimetableClass>>()
    val openMondayClass: LiveData<Event<TimetableClass>>
        get() = _openMondayClass

    private val _deleteMondayClasses = MutableLiveData<Event<Unit>>()
    val deleteMondayClasses: LiveData<Event<Unit>>
        get() = _deleteMondayClasses

    private val _hasPendingWrites = MutableLiveData<Event<Unit>>()
    val hasPendingWrites: LiveData<Event<Unit>>
        get() = _hasPendingWrites


    fun displayMondayClassDetails(mondayClass: TimetableClass) {
        _openMondayClass.value =
            Event(mondayClass)
    }

    fun addNewClass() {
        _addNewClass.value = Event(Unit)
    }

    fun deleteList(list: List<TimetableClass?>) {
        list.forEach { mondayClass ->
            if (mondayClass != null) {
                mondayFirestore.document(mondayClass.id).delete()
                cancelAlarm(mondayClass)
            }
        }
        checkDataStatus()
    }

    fun deleteIconPressed() {
        _deleteMondayClasses.value =
            Event(Unit)
    }

    private fun checkDataStatus() {
        val mondayClasses: List<TimetableClass>? = _mondayClasses.value
        try {
            if (mondayClasses.isNullOrEmpty()) {
                throw NullPointerException()
            }
            _status.value =
                DataStatus.NOT_EMPTY
        } catch (npe: NullPointerException) {
            _status.value =
                DataStatus.EMPTY
        }
    }

    private fun updateData(mutableList: MutableList<TimetableClass>) {
        _mondayClasses.value = mutableList
        checkDataStatus()
    }

    fun addSnapshotListener(): ListenerRegistration {
        _status.value = DataStatus.LOADING
        return mondayFirestore.addSnapshotListener(MetadataChanges.INCLUDE) { querySnapshot: QuerySnapshot?, _: FirebaseFirestoreException? ->
            val mutableList: MutableList<TimetableClass> = mutableListOf()
            querySnapshot?.documents?.forEach { document ->
                if (document.metadata.hasPendingWrites()) {
                    _hasPendingWrites.value = Event(Unit)
                }

                val mondayClass = document.toObject(TimetableClass::class.java)
                mondayClass?.let {
                    mutableList.add(it)
                }
            }
            updateData(mutableList)
        }
    }

    private fun cancelAlarm(mondayClass: TimetableClass) {
        val alarmManager = app.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(app, MondayClassReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            app,
            mondayClass.alarmRequestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        alarmManager.cancel(pendingIntent)
    }
}

