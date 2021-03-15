package com.example.android.mycampusapp.timetable.input

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.android.mycampusapp.R
import com.example.android.mycampusapp.data.CustomTime
import com.example.android.mycampusapp.data.Location
import com.example.android.mycampusapp.data.TimetableClass
import com.example.android.mycampusapp.util.*
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.functions.FirebaseFunctions
import timber.log.Timber
import java.util.*

class TimetableInputViewModel(
    private val timetableClass: TimetableClass?,
    private val app: Application,
    private val fridayFirestore: CollectionReference,
    private val functions: FirebaseFunctions
) : AndroidViewModel(app) {

    private val _displayNavigator = MutableLiveData<Event<Unit>>()
    val displayNavigator: LiveData<Event<Unit>>
        get() = _displayNavigator

    val textBoxSubject = MutableLiveData<String>(timetableClass?.subject)
    val textBoxTime = MutableLiveData(timetableClass?.let {
        formatTime(CustomTime(it.hour, it.minute))
    })
    val textBoxLocation = MutableLiveData<String>(timetableClass?.locationName)
    val textBoxRoom = MutableLiveData<String>(timetableClass?.room)
    private val id = timetableClass?.id
    private val alarmRequestCode = timetableClass?.alarmRequestCode
    private var location = timetableClass?.let { Location(it.locationName, it.locationCoordinates) }
    private val _timeSet: MutableLiveData<CustomTime> = MutableLiveData(
        timetableClass?.let {
            CustomTime(it.hour, it.minute)
        }
    )
    val timeSet: LiveData<CustomTime>
        get() = _timeSet

    private val cal: Calendar = Calendar.getInstance()

    private val _snackbarText = MutableLiveData<Event<Int>>()
    val snackbarText: LiveData<Event<Int>>
        get() = _snackbarText

    private val sharedPreferences = app.getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)
    private val courseId = sharedPreferences.getString(COURSE_ID, "")!!

    // Can only be tested through espresso
    fun save() {
        val currentSubject: String? = textBoxSubject.value
        val currentTime: CustomTime? = _timeSet.value
        val currentLocation: Location? = location
        val currentRoom: String? = textBoxRoom.value
        if (currentSubject.isNullOrBlank() || currentTime == null || currentLocation == null || currentRoom.isNullOrBlank()) {
            _snackbarText.value = Event(R.string.empty_message)
            return
        } else if (timetableClass == null) {
            //Create new class
            val timetableClass =
                TimetableClass(
                    subject = currentSubject,
                    hour = currentTime.hour,
                    minute = currentTime.minute,
                    locationName = currentLocation.name,
                    locationCoordinates = currentLocation.coordinates,
                    room = currentRoom
                )
            createNewClass(timetableClass)
        } else {
            //update present class
            val timetableClass =
                TimetableClass(
                    id!!,
                    currentSubject,
                    currentTime.hour,
                    currentTime.minute,
                    currentLocation.name,
                    currentLocation.coordinates,
                    alarmRequestCode!!,
                    currentRoom
                )
            updatePresentClass(timetableClass)
        }
    }

    private fun updatePresentClass(timetableClass: TimetableClass) {
        addFirestoreData(timetableClass)
        _snackbarText.value = Event(R.string.friday_updated)
        navigateToTimetable()
        val notificationMessage =
            "${timetableClass.subject} details have changed. It is set to start at ${
                formatTime(
                    _timeSet.value!!
                )
            } in ${timetableClass.locationName} Room ${timetableClass.room}"
        sendCloudMessage(notificationMessage, courseId)
    }

    private fun createNewClass(timetableClass: TimetableClass) {
        addFirestoreData(timetableClass)
        _snackbarText.value = Event(R.string.class_saved)
        navigateToTimetable()
        val notificationMessage =
            "${timetableClass.subject} class is set to start at ${
                formatTime(_timeSet.value!!)
            } in ${timetableClass.locationName} Room ${timetableClass.room}"
        sendCloudMessage(notificationMessage, courseId)
    }

    private fun addFirestoreData(fridayClass: TimetableClass) {
        fridayFirestore.document(fridayClass.id).set(fridayClass).addOnSuccessListener {
            Timber.i("Data was added successfully")
        }.addOnFailureListener { exception ->
            Timber.i("Data failed to add because of $exception")
        }
    }

    private fun navigateToTimetable() {
        _displayNavigator.value = Event(Unit)
    }

    fun setLocation(loc: Location) {
        location = loc
        textBoxLocation.value = loc.name
    }

    fun setTime(time: CustomTime) {
        textBoxTime.value = formatTime(time)
        _timeSet.value = time
    }

    private fun sendCloudMessage(message: String, courseId: String): Task<Unit> {
        val data = hashMapOf("message" to message, "courseId" to courseId.removeWhiteSpace())
        return functions.getHttpsCallable("sendMessage").call(data).continueWith {

        }
    }
}
