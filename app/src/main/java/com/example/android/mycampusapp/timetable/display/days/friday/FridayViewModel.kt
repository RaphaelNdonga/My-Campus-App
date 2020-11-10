package com.example.android.mycampusapp.timetable.display.days.friday

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.android.mycampusapp.timetable.data.TimetableClass
import com.example.android.mycampusapp.timetable.receiver.FridayClassReceiver
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

class FridayViewModel(courseDocument: DocumentReference,private val app: Application) : AndroidViewModel(app) {

    private val _fridayClasses2 = MutableLiveData<List<TimetableClass>>()
    val fridayClasses2: LiveData<List<TimetableClass>>
        get() = _fridayClasses2

    private val fridayFirestore = courseDocument.collection("friday")


    private val _status = MutableLiveData<FridayDataStatus>()
    val status: LiveData<FridayDataStatus> = _status

    private val _addNewClass = MutableLiveData<Event<Unit>>()
    val addNewClass: LiveData<Event<Unit>> = _addNewClass

    private val _openFridayClass = MutableLiveData<Event<TimetableClass>>()
    val openFridayClass: LiveData<Event<TimetableClass>>
        get() = _openFridayClass

    private val _deleteFridayClasses = MutableLiveData<Event<Unit>>()
    val deleteFridayClasses: LiveData<Event<Unit>>
        get() = _deleteFridayClasses

    private val _hasPendingWrites = MutableLiveData<Event<Boolean>>()
    val hasPendingWrites:LiveData<Event<Boolean>>
        get() = _hasPendingWrites

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

    fun displayFridayClassDetails(fridayClass: TimetableClass) {
        _openFridayClass.value =
            Event(fridayClass)

        //Ensures that the timepickervalues object is updated before passing the arguments to fridayInput fragment
        TimePickerValues.timeSetByTimePicker.value = fridayClass.time
    }

    fun addNewClass() {
        _addNewClass.value = Event(Unit)
        TimePickerValues.timeSetByTimePicker.value = ""
    }

    fun deleteList(list: List<TimetableClass?>) = uiScope.launch {
        list.forEach { fridayClass ->
            if (fridayClass != null) {
                fridayFirestore.document(fridayClass.id).delete()
                cancelAlarm(fridayClass)
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

    private fun updateData(mutableList: MutableList<TimetableClass>) {
        _fridayClasses2.value = mutableList
        checkFridayDataStatus()
    }

    fun addSnapshotListener(): ListenerRegistration {
        _status.value = FridayDataStatus.LOADING
        return fridayFirestore.addSnapshotListener { querySnapshot: QuerySnapshot?, _: FirebaseFirestoreException? ->
            val mutableList: MutableList<TimetableClass> = mutableListOf()
            querySnapshot?.documents?.forEach { document ->
                _hasPendingWrites.value = Event(document.metadata.hasPendingWrites())
                Timber.i("We are in the loop")
                val id = document.getString("id")
                val subject = document.getString("subject")
                val time = document.getString("time")
                val location = document.getString("location")
                val alarmRequestCode = document.getLong("alarmRequestCode")?.toInt()
                if (id != null && subject != null && time != null && location!=null && alarmRequestCode!=null) {
                    val fridayClass = TimetableClass(id, subject, time,location,alarmRequestCode)
                    mutableList.add(fridayClass)
                }
            }
            updateData(mutableList)
        }
    }
    private fun cancelAlarm(fridayClass: TimetableClass) {
        val alarmManager = app.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(app, FridayClassReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            app,
            fridayClass.alarmRequestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        alarmManager.cancel(pendingIntent)
    }
}

enum class FridayDataStatus {
    EMPTY, NOT_EMPTY, LOADING
}
