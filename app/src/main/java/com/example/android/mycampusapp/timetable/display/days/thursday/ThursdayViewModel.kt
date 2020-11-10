package com.example.android.mycampusapp.timetable.display.days.thursday

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.android.mycampusapp.timetable.data.TimetableClass
import com.example.android.mycampusapp.timetable.receiver.ThursdayClassReceiver
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

class ThursdayViewModel(courseDocument: DocumentReference, private val app: Application) :
    AndroidViewModel(app) {

    private val _thursdayClasses2 = MutableLiveData<List<TimetableClass>>()
    val thursdayClasses2: LiveData<List<TimetableClass>>
        get() = _thursdayClasses2

    private val _status = MutableLiveData<ThursdayDataStatus>()
    val status: LiveData<ThursdayDataStatus> = _status

    private val _addNewClass = MutableLiveData<Event<Unit>>()
    val addNewClass: LiveData<Event<Unit>> = _addNewClass

    private val _openThursdayClass = MutableLiveData<Event<TimetableClass>>()
    val openThursdayClass: LiveData<Event<TimetableClass>>
        get() = _openThursdayClass

    private val _hasPendingWrites = MutableLiveData<Event<Boolean>>()
    val hasPendingWrites: LiveData<Event<Boolean>>
        get() = _hasPendingWrites
    private val _deleteThursdayClasses = MutableLiveData<Event<Unit>>()
    val deleteThursdayClasses: LiveData<Event<Unit>>
        get() = _deleteThursdayClasses

    private val thursdayFirestore = courseDocument.collection("thursday")

    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + job)


    private fun checkThursdayDataStatus() = uiScope.launch {
        val thursdayClasses = _thursdayClasses2.value
        try {
            if (thursdayClasses.isNullOrEmpty()) {
                throw NullPointerException()
            }
            _status.value =
                ThursdayDataStatus.NOT_EMPTY
        } catch (e: Exception) {
            _status.value =
                ThursdayDataStatus.EMPTY
        }
    }

    fun displayThursdayClassDetails(thursdayClass: TimetableClass) {
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

    fun deleteList(list: List<TimetableClass?>) = uiScope.launch {
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

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }

    private fun update(mutableList: MutableList<TimetableClass>) {
        _thursdayClasses2.value = mutableList
        checkThursdayDataStatus()
    }

    fun addSnapshotListener(): ListenerRegistration {
        _status.value = ThursdayDataStatus.LOADING
        return thursdayFirestore.addSnapshotListener { querySnapshot: QuerySnapshot?, _: FirebaseFirestoreException? ->
            val mutableList: MutableList<TimetableClass> = mutableListOf()
            querySnapshot?.documents?.forEach { document ->
                _hasPendingWrites.value = Event(document.metadata.hasPendingWrites())
                val id = document.getString("id")
                val subject = document.getString("subject")
                val time = document.getString("time")
                val location = document.getString("location")
                val alarmRequestCode = document.getLong("alarmRequestCode")?.toInt()
                if (id != null && subject != null && time != null && location != null && alarmRequestCode != null) {
                    val thursdayClass =
                        TimetableClass(id, subject, time, location, alarmRequestCode)
                    mutableList.add(thursdayClass)
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

enum class ThursdayDataStatus {
    EMPTY, NOT_EMPTY, LOADING
}
