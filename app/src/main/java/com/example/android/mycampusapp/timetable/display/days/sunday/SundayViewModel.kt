package com.example.android.mycampusapp.timetable.display.days.sunday

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
import com.example.android.mycampusapp.timetable.receiver.SundayClassReceiver
import com.example.android.mycampusapp.util.Event
import com.google.firebase.firestore.*

class SundayViewModel(courseDocument: DocumentReference, private val app: Application) :
    AndroidViewModel(app) {

    private val sundayFirestore = courseDocument.collection("sunday")
    private val _sundayClasses = MutableLiveData<List<TimetableClass>>()
    val sundayClasses: LiveData<List<TimetableClass>>
        get() = _sundayClasses


    private val _status = MutableLiveData<DataStatus>()
    val status: LiveData<DataStatus> = _status

    private val _addNewClass = MutableLiveData<Event<Unit>>()
    val addNewClass: LiveData<Event<Unit>> = _addNewClass

    private val _openSundayClass = MutableLiveData<Event<TimetableClass>>()
    val openSundayClass: LiveData<Event<TimetableClass>>
        get() = _openSundayClass

    private val _deleteSundayClasses = MutableLiveData<Event<Unit>>()
    val deleteSundayClasses: LiveData<Event<Unit>>
        get() = _deleteSundayClasses

    private val _hasPendingWrites = MutableLiveData<Event<Unit>>()
    val hasPendingWrites: LiveData<Event<Unit>>
        get() = _hasPendingWrites

    private fun checkDataStatus() {
        val sundayClasses = _sundayClasses.value
        try {
            if (sundayClasses.isNullOrEmpty()) {
                throw NullPointerException()
            }
            _status.value =
                DataStatus.NOT_EMPTY
        } catch (e: Exception) {
            _status.value =
                DataStatus.EMPTY
        }
    }

    fun displaySundayClassDetails(sundayClass: TimetableClass) {
        _openSundayClass.value =
            Event(sundayClass)
    }

    fun addNewClass() {
        _addNewClass.value = Event(Unit)
    }

    fun deleteList(list: List<TimetableClass?>) {
        list.forEach { sundayClass ->
            if (sundayClass != null) {
                sundayFirestore.document(sundayClass.id).delete()
                cancelAlarm(sundayClass)
            }
        }
        checkDataStatus()
    }

    fun deleteIconPressed() {
        _deleteSundayClasses.value =
            Event(Unit)
    }

    private fun updateData(mutableList: MutableList<TimetableClass>) {
        _sundayClasses.value = mutableList
        checkDataStatus()
    }

    fun addSnapshotListener(): ListenerRegistration {
        _status.value = DataStatus.LOADING
        return sundayFirestore.addSnapshotListener(MetadataChanges.INCLUDE) { querySnapshot: QuerySnapshot?, _: FirebaseFirestoreException? ->
            val mutableList: MutableList<TimetableClass> = mutableListOf()
            querySnapshot?.documents?.forEach { document ->
                if (document.metadata.hasPendingWrites()) {
                    _hasPendingWrites.value = Event(Unit)
                }

                val sundayClass = document.toObject(TimetableClass::class.java)
                sundayClass?.let {
                    mutableList.add(it)
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
