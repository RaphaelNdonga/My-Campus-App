package com.example.android.mycampusapp.timetable.display

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.android.mycampusapp.data.CustomTime
import com.example.android.mycampusapp.data.DataStatus
import com.example.android.mycampusapp.data.TimetableClass
import com.example.android.mycampusapp.util.*
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*
import com.google.firebase.functions.FirebaseFunctions
import timber.log.Timber

class TimetableViewModel(
    private val timetableFirestore: CollectionReference,
    private val functions: FirebaseFunctions,
    private val dayOfWeek: DayOfWeek,
    private val app: Application
) :
    AndroidViewModel(app) {

    private val _timetableClasses = MutableLiveData<List<TimetableClass>>()
    val timetableClasses: LiveData<List<TimetableClass>>
        get() = _timetableClasses

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

    private val _hasPendingWrites = MutableLiveData<Event<Unit>>()
    val hasPendingWrites: LiveData<Event<Unit>>
        get() = _hasPendingWrites

    private fun checkDataStatus() {
        val timetableClasses = _timetableClasses.value
        try {
            if (timetableClasses.isNullOrEmpty()) {
                throw NullPointerException()
            }
            _status.value = DataStatus.NOT_EMPTY
        } catch (e: Exception) {
            _status.value = DataStatus.EMPTY
        }
    }

    fun displayFridayClassDetails(timetableClass: TimetableClass) {
        _openFridayClass.value =
            Event(timetableClass)
    }

    fun addNewClass() {
        _addNewClass.value = Event(Unit)
    }

    fun deleteList(list: List<TimetableClass?>) {
        val sharedPreferences = app.getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)
        val courseId = sharedPreferences.getString(COURSE_ID, "")!!
        list.forEach { timetableClass ->
            if (timetableClass != null) {
                timetableFirestore.document(timetableClass.id).delete()
                val isLater = compareCustomTime(
                    CustomTime(timetableClass.hour, timetableClass.minute),
                    getCustomTimeNow()
                )
                if (getTodayEnumDay() == dayOfWeek && isLater) {
                    cancelTodayAlarm(
                        timetableClass.alarmRequestCode.toString(),
                        timetableClass.subject,
                        courseId
                    )
                }
                if (getTomorrowEnumDay() == dayOfWeek) {
                    cancelTomorrowAlarm(
                        timetableClass.alarmRequestCode.toString(),
                        timetableClass.subject,
                        courseId
                    )
                }
            }
        }
        checkDataStatus()
    }

    fun deleteIconPressed() {
        _deleteFridayClasses.value =
            Event(Unit)
    }

    fun addSnapshotListener(): ListenerRegistration {
        _status.value = DataStatus.LOADING
        return timetableFirestore
            .orderBy("hour", Query.Direction.ASCENDING)
            .orderBy("minute", Query.Direction.ASCENDING)
            .addSnapshotListener { querySnapshot: QuerySnapshot?, firestoreException: FirebaseFirestoreException? ->
                _timetableClasses.value = querySnapshot?.toObjects(TimetableClass::class.java)
                checkDataStatus()

                if (firestoreException != null) {
                    Timber.i("Got an error $firestoreException")
                }
            }
    }

    private fun cancelTodayAlarm(
        requestCode: String,
        subject: String,
        courseId: String
    ): Task<Unit> {
        val data =
            hashMapOf("requestCode" to requestCode, "subject" to subject, "courseId" to courseId)
        return functions.getHttpsCallable("cancelTodayAlarm").call(data).continueWith { }
    }

    private fun cancelTomorrowAlarm(
        requestCode: String,
        subject: String,
        courseId: String
    ): Task<Unit> {
        val data =
            hashMapOf("requestCode" to requestCode, "subject" to subject, "courseId" to courseId)
        return functions.getHttpsCallable("cancelTomorrowAlarm").call(data).continueWith { }
    }
}
