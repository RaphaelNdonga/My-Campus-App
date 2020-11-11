package com.example.android.mycampusapp.timetable.display.days.monday

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.android.mycampusapp.timetable.data.TimetableClass
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

    private val _mondayClasses2 = MutableLiveData<List<TimetableClass>>()
    val mondayClasses2: LiveData<List<TimetableClass>>
        get() = _mondayClasses2

    private val mondayFirestore = courseId.collection("monday")

    private val _status = MutableLiveData<MondayDataStatus>()
    val status: LiveData<MondayDataStatus>
        get() = _status

    private val _addNewClass = MutableLiveData<Event<Unit>>()
    val addNewClass: LiveData<Event<Unit>> = _addNewClass

    private val _openMondayClass = MutableLiveData<Event<TimetableClass>>()
    val openMondayClass: LiveData<Event<TimetableClass>>
        get() = _openMondayClass

    private val _deleteMondayClasses = MutableLiveData<Event<Unit>>()
    val deleteMondayClasses: LiveData<Event<Unit>>
        get() = _deleteMondayClasses

    private val _hasPendingWrites = MutableLiveData<Event<Boolean>>()
    val hasPendingWrites:LiveData<Event<Boolean>>
        get() = _hasPendingWrites

    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + job)


    fun displayMondayClassDetails(mondayClass: TimetableClass) {
        _openMondayClass.value =
            Event(mondayClass)

        //Ensures that the timepickervalues object is updated before passing the arguments to mondayInput fragment
        TimePickerValues.timeSetByTimePicker.value = mondayClass.time
    }

    fun addNewClass() {
        _addNewClass.value = Event(Unit)
        TimePickerValues.timeSetByTimePicker.value = ""
    }

    fun deleteList(list: List<TimetableClass?>) = uiScope.launch {
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
            val mondayClasses: List<TimetableClass>? = _mondayClasses2.value
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

    private fun updateData(mutableList: MutableList<TimetableClass>) {
        _mondayClasses2.value = mutableList
        checkMondayDataStatus()
    }

    fun addSnapshotListener(): ListenerRegistration {
        _status.value = MondayDataStatus.LOADING
        return mondayFirestore.addSnapshotListener { querySnapshot: QuerySnapshot?, _: FirebaseFirestoreException? ->
            val mutableList: MutableList<TimetableClass> = mutableListOf()
            querySnapshot?.documents?.forEach { document ->
                _hasPendingWrites.value = Event(document.metadata.hasPendingWrites())

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

enum class MondayDataStatus {
    EMPTY, NOT_EMPTY, LOADING
}

