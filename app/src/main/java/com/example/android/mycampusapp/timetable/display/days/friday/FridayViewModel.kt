package com.example.android.mycampusapp.timetable.display.days.friday

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
import com.example.android.mycampusapp.timetable.receiver.FridayClassReceiver
import com.example.android.mycampusapp.util.Event
import com.example.android.mycampusapp.util.TimePickerValues
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot
import timber.log.Timber

class FridayViewModel(courseDocument: DocumentReference,private val app: Application) : AndroidViewModel(app) {

    private val _fridayClasses = MutableLiveData<List<TimetableClass>>()
    val fridayClasses: LiveData<List<TimetableClass>>
        get() = _fridayClasses

    private val fridayFirestore = courseDocument.collection("friday")


    private val _status = MutableLiveData<DataStatus>()
    val status: LiveData<DataStatus> = _status

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

    private fun checkDataStatus(){
        val fridayClasses = _fridayClasses.value
        try {
            if (fridayClasses.isNullOrEmpty()) {
                throw NullPointerException()
            }
            _status.value = DataStatus.NOT_EMPTY
        } catch (e: Exception) {
            _status.value = DataStatus.EMPTY
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

    fun deleteList(list: List<TimetableClass?>){
        list.forEach { fridayClass ->
            if (fridayClass != null) {
                fridayFirestore.document(fridayClass.id).delete()
                cancelAlarm(fridayClass)
            }
        }
        checkDataStatus()
    }

    fun deleteIconPressed() {
        _deleteFridayClasses.value =
            Event(Unit)
    }

    private fun updateData(mutableList: MutableList<TimetableClass>) {
        _fridayClasses.value = mutableList
        checkDataStatus()
    }

    fun addSnapshotListener(): ListenerRegistration {
        _status.value = DataStatus.LOADING
        return fridayFirestore.addSnapshotListener { querySnapshot: QuerySnapshot?, _: FirebaseFirestoreException? ->
            val mutableList: MutableList<TimetableClass> = mutableListOf()
            querySnapshot?.documents?.forEach { document ->
                _hasPendingWrites.value = Event(document.metadata.hasPendingWrites())
                Timber.i("We are in the loop")
                val fridayClass = document.toObject(TimetableClass::class.java)
                fridayClass?.let {
                    mutableList.add(it)
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
