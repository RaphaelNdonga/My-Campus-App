package com.example.android.mycampusapp.timetable.display.days.tuesday

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.android.mycampusapp.timetable.data.TimetableClass
import com.example.android.mycampusapp.timetable.receiver.TuesdayClassReceiver
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

class TuesdayViewModel(courseDocument: DocumentReference, private val app: Application) :
    AndroidViewModel(app) {

    private val _tuesdayClasses2 = MutableLiveData<List<TimetableClass>>()
    val tuesdayClasses2: LiveData<List<TimetableClass>>
        get() = _tuesdayClasses2

    private val _status = MutableLiveData<TuesdayDataStatus>()
    val status: LiveData<TuesdayDataStatus> = _status

    private val tuesdayFirestore = courseDocument.collection("tuesday")


    private val _addNewClass = MutableLiveData<Event<Unit>>()
    val addNewClass: LiveData<Event<Unit>> = _addNewClass

    private val _openTuesdayClass = MutableLiveData<Event<TimetableClass>>()
    val openTuesdayClass: LiveData<Event<TimetableClass>>
        get() = _openTuesdayClass

    private val _deleteTuesdayClasses = MutableLiveData<Event<Unit>>()
    val deleteTuesdayClasses: LiveData<Event<Unit>>
        get() = _deleteTuesdayClasses

    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + job)


    private fun checkTuesdayDataStatus() = uiScope.launch {
        val tuesdayClasses = _tuesdayClasses2.value
        try {
            if (tuesdayClasses.isNullOrEmpty()) {
                throw NullPointerException()
            }
            _status.value =
                TuesdayDataStatus.NOT_EMPTY
        } catch (e: Exception) {
            _status.value =
                TuesdayDataStatus.EMPTY
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

    fun deleteList(list: List<TimetableClass?>) = uiScope.launch {
        list.forEach { tuesdayClass ->
            if (tuesdayClass != null) {
                tuesdayFirestore.document(tuesdayClass.id).delete()
                cancelAlarm(tuesdayClass)
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

    private fun updateData(mutableList: MutableList<TimetableClass>) {
        _tuesdayClasses2.value = mutableList
        checkTuesdayDataStatus()
    }

    fun addSnapshotListener(): ListenerRegistration {
        _status.value = TuesdayDataStatus.LOADING
        return tuesdayFirestore.addSnapshotListener { querySnapshot: QuerySnapshot?, _: FirebaseFirestoreException? ->
            val mutableList: MutableList<TimetableClass> = mutableListOf()
            querySnapshot?.documents?.forEach { document ->
                val id = document.getString("id")
                val subject = document.getString("subject")
                val time = document.getString("time")
                val location = document.getString("location")
                val alarmRequestCode = document.getLong("alarmRequestCode")?.toInt()
                if (id != null && subject != null && time != null && location !=null && alarmRequestCode!=null) {
                    val tuesdayClass = TimetableClass(id, subject, time,location,alarmRequestCode)
                    mutableList.add(tuesdayClass)
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

enum class TuesdayDataStatus { EMPTY, NOT_EMPTY, LOADING }
