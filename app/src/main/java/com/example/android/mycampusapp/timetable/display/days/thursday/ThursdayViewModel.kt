package com.example.android.mycampusapp.timetable.display.days.thursday

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
import com.example.android.mycampusapp.timetable.receiver.ThursdayClassReceiver
import com.example.android.mycampusapp.util.Event
import com.google.firebase.firestore.*

class ThursdayViewModel(courseDocument: DocumentReference, private val app: Application) :
    AndroidViewModel(app) {

    private val _thursdayClasses = MutableLiveData<List<TimetableClass>>()
    val thursdayClasses: LiveData<List<TimetableClass>>
        get() = _thursdayClasses

    private val _status = MutableLiveData<DataStatus>()
    val status: LiveData<DataStatus> = _status

    private val _addNewClass = MutableLiveData<Event<Unit>>()
    val addNewClass: LiveData<Event<Unit>> = _addNewClass

    private val _openThursdayClass = MutableLiveData<Event<TimetableClass>>()
    val openThursdayClass: LiveData<Event<TimetableClass>>
        get() = _openThursdayClass

    private val _hasPendingWrites = MutableLiveData<Event<Unit>>()
    val hasPendingWrites: LiveData<Event<Unit>>
        get() = _hasPendingWrites
    private val _deleteThursdayClasses = MutableLiveData<Event<Unit>>()
    val deleteThursdayClasses: LiveData<Event<Unit>>
        get() = _deleteThursdayClasses

    private val thursdayFirestore = courseDocument.collection("thursday")


    private fun checkThursdayDataStatus() {
        val thursdayClasses = _thursdayClasses.value
        try {
            if (thursdayClasses.isNullOrEmpty()) {
                throw NullPointerException()
            }
            _status.value = DataStatus.NOT_EMPTY
        } catch (e: Exception) {
            _status.value = DataStatus.EMPTY
        }
    }

    fun displayThursdayClassDetails(thursdayClass: TimetableClass) {
        _openThursdayClass.value =
            Event(thursdayClass)

        //Ensures that the timepickervalues object is updated before passing the arguments to thursdayInput fragment
    }

    fun addNewClass() {
        _addNewClass.value = Event(Unit)
    }

    fun deleteList(list: List<TimetableClass?>) {
        list.forEach { thursdayClass ->
            if (thursdayClass != null) {
                thursdayFirestore.document(thursdayClass.id).delete()
                cancelAlarm(thursdayClass)
            }
        }
        checkThursdayDataStatus()
    }

    fun deleteIconPressed() {
        _deleteThursdayClasses.value =
            Event(Unit)
    }

    private fun update(mutableList: MutableList<TimetableClass>) {
        _thursdayClasses.value = mutableList
        checkThursdayDataStatus()
    }

    fun addSnapshotListener(): ListenerRegistration {
        _status.value = DataStatus.LOADING
        return thursdayFirestore.addSnapshotListener(MetadataChanges.INCLUDE) { querySnapshot: QuerySnapshot?, _: FirebaseFirestoreException? ->
            val mutableList: MutableList<TimetableClass> = mutableListOf()
            querySnapshot?.documents?.forEach { document ->
                if (document.metadata.hasPendingWrites()) {
                    _hasPendingWrites.value = Event(Unit)
                }

                val thursdayClass = document.toObject(TimetableClass::class.java)
                thursdayClass?.let {
                    mutableList.add(it)
                }
            }
            update(mutableList)
        }
    }

    private fun cancelAlarm(thursdayClass: TimetableClass) {
        val alarmManager = app.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(app, ThursdayClassReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            app,
            thursdayClass.alarmRequestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        alarmManager.cancel(pendingIntent)
    }
}
