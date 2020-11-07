package com.example.android.mycampusapp.timetable.display.days.wednesday

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.android.mycampusapp.timetable.data.WednesdayClass
import com.example.android.mycampusapp.timetable.receiver.WednesdayClassReceiver
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

class WednesdayViewModel(
    courseDocument: DocumentReference,
    private val app: Application
) : AndroidViewModel(app) {

    private val _wednesdayClasses2 = MutableLiveData<List<WednesdayClass>>()
    val wednesdayClasses2: LiveData<List<WednesdayClass>>
        get() = _wednesdayClasses2

    private val _status = MutableLiveData<WednesdayDataStatus>()
    val status: LiveData<WednesdayDataStatus> = _status

    private val _addNewClass = MutableLiveData<Event<Unit>>()
    val addNewClass: LiveData<Event<Unit>> = _addNewClass

    private val _openWednesdayClass = MutableLiveData<Event<WednesdayClass>>()
    val openWednesdayClass: LiveData<Event<WednesdayClass>>
        get() = _openWednesdayClass

    private val _deleteWednesdayClasses = MutableLiveData<Event<Unit>>()
    val deleteWednesdayClasses: LiveData<Event<Unit>>
        get() = _deleteWednesdayClasses

    private val wednesdayFirestore = courseDocument.collection("wednesday")

    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + job)

    private fun checkWednesdayDataStatus() = uiScope.launch {
        val wednesdayClasses = _wednesdayClasses2.value
        try {
            if (wednesdayClasses.isNullOrEmpty()) {
                throw NullPointerException()
            }
            _status.value =
                WednesdayDataStatus.NOT_EMPTY
        } catch (e: Exception) {
            _status.value =
                WednesdayDataStatus.EMPTY
        }
    }

    fun displayWednesdayClassDetails(wednesdayClass: WednesdayClass) {
        _openWednesdayClass.value =
            Event(wednesdayClass)

        //Ensures that the timepickervalues object is updated before passing the arguments to wednesdayInput fragment
        TimePickerValues.timeSetByTimePicker.value = wednesdayClass.time
    }

    fun addNewClass() {
        _addNewClass.value = Event(Unit)
        TimePickerValues.timeSetByTimePicker.value = ""
    }

    fun deleteList(list: List<WednesdayClass?>) = uiScope.launch {
        list.forEach { wednesdayClass ->
            if (wednesdayClass != null) {
                wednesdayFirestore.document(wednesdayClass.id).delete()
                cancelAlarm(wednesdayClass)
            }
        }
        checkWednesdayDataStatus()
    }

    fun deleteIconPressed() {
        _deleteWednesdayClasses.value =
            Event(Unit)
    }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }

    private fun updateData(mutableList: MutableList<WednesdayClass>) {
        _wednesdayClasses2.value = mutableList
        checkWednesdayDataStatus()
    }

    fun addSnapshotListener(): ListenerRegistration {
        _status.value = WednesdayDataStatus.LOADING
        return wednesdayFirestore.addSnapshotListener { querySnapshot: QuerySnapshot?, _: FirebaseFirestoreException? ->
            val mutableList: MutableList<WednesdayClass> = mutableListOf()
            querySnapshot?.documents?.forEach { document ->
                val id = document.getString("id")
                val subject = document.getString("subject")
                val time = document.getString("time")
                if (id != null && subject != null && time != null) {
                    val wednesdayClass = WednesdayClass(id, subject, time)
                    mutableList.add(wednesdayClass)
                }
            }
            updateData(mutableList)
        }
    }

    private fun cancelAlarm(wednesdayClass: WednesdayClass) {
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

enum class WednesdayDataStatus {
    EMPTY, NOT_EMPTY, LOADING

}
