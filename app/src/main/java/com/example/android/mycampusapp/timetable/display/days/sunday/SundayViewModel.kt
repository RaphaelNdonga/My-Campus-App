package com.example.android.mycampusapp.timetable.display.days.sunday

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.android.mycampusapp.timetable.data.TimetableClass
import com.example.android.mycampusapp.timetable.receiver.SundayClassReceiver
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

class SundayViewModel(courseDocument: DocumentReference, private val app: Application) :
    AndroidViewModel(app) {

    private val sundayFirestore = courseDocument.collection("sunday")
    private val _sundayClasses2 = MutableLiveData<List<TimetableClass>>()
    val sundayClasses2: LiveData<List<TimetableClass>>
        get() = _sundayClasses2


    private val _status = MutableLiveData<SundayDataStatus>()
    val status: LiveData<SundayDataStatus> = _status

    private val _addNewClass = MutableLiveData<Event<Unit>>()
    val addNewClass: LiveData<Event<Unit>> = _addNewClass

    private val _openSundayClass = MutableLiveData<Event<TimetableClass>>()
    val openSundayClass: LiveData<Event<TimetableClass>>
        get() = _openSundayClass

    private val _deleteSundayClasses = MutableLiveData<Event<Unit>>()
    val deleteSundayClasses: LiveData<Event<Unit>>
        get() = _deleteSundayClasses

    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + job)

    private fun checkSundayDataStatus() = uiScope.launch {
        val sundayClasses = _sundayClasses2.value
        try {
            if (sundayClasses.isNullOrEmpty()) {
                throw NullPointerException()
            }
            _status.value =
                SundayDataStatus.NOT_EMPTY
        } catch (e: Exception) {
            _status.value =
                SundayDataStatus.EMPTY
        }
    }

    fun displaySundayClassDetails(sundayClass: TimetableClass) {
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

    fun deleteList(list: List<TimetableClass?>) = uiScope.launch {
        list.forEach { sundayClass ->
            if (sundayClass != null) {
                sundayFirestore.document(sundayClass.id).delete()
                cancelAlarm(sundayClass)
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

    private fun updateData(mutableList: MutableList<TimetableClass>) {
        _sundayClasses2.value = mutableList
        checkSundayDataStatus()
    }

    fun addSnapshotListener(): ListenerRegistration {
        _status.value = SundayDataStatus.LOADING
        return sundayFirestore.addSnapshotListener { querySnapshot: QuerySnapshot?, _: FirebaseFirestoreException? ->
            val mutableList: MutableList<TimetableClass> = mutableListOf()
            querySnapshot?.documents?.forEach { document ->
                val id = document.getString("id")
                val subject = document.getString("subject")
                val time = document.getString("time")
                val location = document.getString("location")
                val alarmRequestCode = document.getLong("alarmRequestCode")?.toInt()
                if (id != null && subject != null && time != null && location!=null && alarmRequestCode!=null) {
                    val sundayClass = TimetableClass(id, subject, time,location,alarmRequestCode)
                    mutableList.add(sundayClass)
                }
            }
            updateData(mutableList)
        }
    }

    private fun cancelAlarm(sundayClass: TimetableClass) {
        val alarmManager = app.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(app, SundayClassReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            app,
            sundayClass.alarmRequestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        alarmManager.cancel(pendingIntent)
    }
}

enum class SundayDataStatus {
    EMPTY, NOT_EMPTY, LOADING
}
