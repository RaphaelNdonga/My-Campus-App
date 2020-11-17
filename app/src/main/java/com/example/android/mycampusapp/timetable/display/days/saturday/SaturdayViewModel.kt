package com.example.android.mycampusapp.timetable.display.days.saturday

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.android.mycampusapp.data.TimetableClass
import com.example.android.mycampusapp.timetable.receiver.SaturdayClassReceiver
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

class SaturdayViewModel(courseDocument: DocumentReference, private val app: Application) :
    AndroidViewModel(app) {

    private val _saturdayClasses2 = MutableLiveData<List<TimetableClass>>()
    val saturdayClasses2: LiveData<List<TimetableClass>>
        get() = _saturdayClasses2

    private val _status = MutableLiveData<SaturdayDataStatus>()
    val status: LiveData<SaturdayDataStatus> = _status

    private val _addNewClass = MutableLiveData<Event<Unit>>()
    val addNewClass: LiveData<Event<Unit>> = _addNewClass

    private val _openSaturdayClass = MutableLiveData<Event<TimetableClass>>()
    val openSaturdayClass: LiveData<Event<TimetableClass>>
        get() = _openSaturdayClass

    private val _deleteSaturdayClasses = MutableLiveData<Event<Unit>>()
    val deleteSaturdayClasses: LiveData<Event<Unit>>
        get() = _deleteSaturdayClasses

    private val _hasPendingWrites = MutableLiveData<Event<Boolean>>()
    val hasPendingWrites: LiveData<Event<Boolean>>
        get() = _hasPendingWrites

    private val saturdayFirestore = courseDocument.collection("saturday")

    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + job)


    private fun checkSaturdayDataStatus() = uiScope.launch {
        val saturdayClasses = _saturdayClasses2.value
        try {
            if (saturdayClasses.isNullOrEmpty()) {
                throw NullPointerException()
            }
            _status.value =
                SaturdayDataStatus.NOT_EMPTY
        } catch (e: Exception) {
            _status.value =
                SaturdayDataStatus.EMPTY
        }
    }

    fun displaySaturdayClassDetails(saturdayClass: TimetableClass) {
        _openSaturdayClass.value =
            Event(saturdayClass)

        //Ensures that the timepickervalues object is updated before passing the arguments to saturdayInput fragment
        TimePickerValues.timeSetByTimePicker.value = saturdayClass.time
    }

    fun addNewClass() {
        _addNewClass.value = Event(Unit)
        TimePickerValues.timeSetByTimePicker.value = ""
    }

    fun deleteList(list: List<TimetableClass?>) = uiScope.launch {
        list.forEach { saturdayClass ->
            if (saturdayClass != null) {
                saturdayFirestore.document(saturdayClass.id).delete()
                cancelAlarm(saturdayClass)
            }
        }
        checkSaturdayDataStatus()
    }

    fun deleteIconPressed() {
        _deleteSaturdayClasses.value =
            Event(Unit)
    }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }

    private fun updateData(mutableList: MutableList<TimetableClass>) {
        _saturdayClasses2.value = mutableList
        checkSaturdayDataStatus()
    }

    fun addSnapshotListener(): ListenerRegistration {
        _status.value = SaturdayDataStatus.LOADING
        return saturdayFirestore.addSnapshotListener { querySnapshot: QuerySnapshot?, _: FirebaseFirestoreException? ->
            val mutableList: MutableList<TimetableClass> = mutableListOf()
            querySnapshot?.documents?.forEach { document ->
                _hasPendingWrites.value = Event(document.metadata.hasPendingWrites())

                val saturdayClass = document.toObject(TimetableClass::class.java)
                saturdayClass?.let {
                    mutableList.add(it)
                }
            }
            updateData(mutableList)
        }
    }

    private fun cancelAlarm(saturdayClass: TimetableClass) {
        val alarmManager = app.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(app, SaturdayClassReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            app,
            saturdayClass.alarmRequestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        alarmManager.cancel(pendingIntent)
    }
}

enum class SaturdayDataStatus {
    EMPTY, NOT_EMPTY, LOADING
}
