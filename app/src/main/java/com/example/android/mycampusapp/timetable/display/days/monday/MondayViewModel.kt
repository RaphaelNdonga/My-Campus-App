package com.example.android.mycampusapp.timetable.display.days.monday

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.android.mycampusapp.timetable.data.MondayClass
import com.example.android.mycampusapp.timetable.receiver.MondayClassReceiver
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

class MondayViewModel(courseId: DocumentReference, private val app: Application) :
    AndroidViewModel(app) {

    private val _mondayClasses2 = MutableLiveData<List<MondayClass>>()
    val mondayClasses2: LiveData<List<MondayClass>>
        get() = _mondayClasses2

    private val mondayFirestore = courseId.collection("monday")

    private val _status = MutableLiveData<MondayDataStatus>()
    val status: LiveData<MondayDataStatus>
        get() = _status

    private val _addNewClass = MutableLiveData<Event<Unit>>()
    val addNewClass: LiveData<Event<Unit>> = _addNewClass

    private val _openMondayClass = MutableLiveData<Event<MondayClass>>()
    val openMondayClass: LiveData<Event<MondayClass>>
        get() = _openMondayClass

    private val _deleteMondayClasses = MutableLiveData<Event<Unit>>()
    val deleteMondayClasses: LiveData<Event<Unit>>
        get() = _deleteMondayClasses

    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + job)


    fun displayMondayClassDetails(mondayClass: MondayClass) {
        _openMondayClass.value =
            Event(mondayClass)

        //Ensures that the timepickervalues object is updated before passing the arguments to mondayInput fragment
        TimePickerValues.timeSetByTimePicker.value = mondayClass.time
    }

    fun addNewClass() {
        _addNewClass.value = Event(Unit)
        TimePickerValues.timeSetByTimePicker.value = ""
    }

    fun deleteList(list: List<MondayClass?>) = uiScope.launch {
        list.forEach { mondayClass ->
            if (mondayClass != null) {
                mondayFirestore.document(mondayClass.id).delete()
                cancelAlarm(mondayClass)
            }
        }
        checkMondayDataStatus()
    }

    fun deleteIconPressed() {
        _deleteMondayClasses.value =
            Event(Unit)
    }

    private fun checkMondayDataStatus() {
        uiScope.launch {
            val mondayClasses: List<MondayClass>? = _mondayClasses2.value
            try {
                if (mondayClasses.isNullOrEmpty()) {
                    throw NullPointerException()
                }
                _status.value =
                    MondayDataStatus.NOT_EMPTY
            } catch (e: Exception) {
                _status.value =
                    MondayDataStatus.EMPTY
            }
        }
    }


    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }

    private fun updateData(mutableList: MutableList<MondayClass>) {
        _mondayClasses2.value = mutableList
        checkMondayDataStatus()
    }

    fun addSnapshotListener(): ListenerRegistration {
        _status.value = MondayDataStatus.LOADING
        return mondayFirestore.addSnapshotListener { querySnapshot: QuerySnapshot?, _: FirebaseFirestoreException? ->
            val mutableList: MutableList<MondayClass> = mutableListOf()
            querySnapshot?.documents?.forEach { document ->
                val id = document.getString("id")
                val subject = document.getString("subject")
                val time = document.getString("time")
                if (id != null && subject != null && time != null) {
                    val mondayClass = MondayClass(id, subject, time)
                    mutableList.add(mondayClass)
                }
            }
            updateData(mutableList)
        }
    }

    private fun cancelAlarm(mondayClass: MondayClass) {
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

enum class MondayDataStatus {
    EMPTY, NOT_EMPTY, LOADING
}

