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

class TimetableInputViewModel(
    private val previousClass: TimetableClass?,
    private val app: Application,
    courseCollection: CollectionReference,
    private val functions: FirebaseFunctions,
    private val dayOfWeek: DayOfWeek
) : AndroidViewModel(app) {

    private val _displayNavigator = MutableLiveData<Event<Unit>>()
    val displayNavigator: LiveData<Event<Unit>>
        get() = _displayNavigator

    val textBoxSubject = MutableLiveData<String>(previousClass?.subject)
    val textBoxTime = MutableLiveData(previousClass?.let {
        formatTime(CustomTime(it.hour, it.minute))
    })
    val textBoxLocation = MutableLiveData<String>(previousClass?.locationName)
    val textBoxRoom = MutableLiveData<String>(previousClass?.room)
    private val id = previousClass?.id
    private val alarmRequestCode = previousClass?.alarmRequestCode
    private var location = previousClass?.let { Location(it.locationName, it.locationCoordinates) }
    private val _timeSet: MutableLiveData<CustomTime> = MutableLiveData(
        previousClass?.let {
            CustomTime(it.hour, it.minute)
        }
    )
    val timeSet: LiveData<CustomTime>
        get() = _timeSet

    private val _snackbarText = MutableLiveData<Event<String>>()
    val snackbarText: LiveData<Event<String>> = _snackbarText

    private val sharedPreferences = app.getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)
    private val courseId = sharedPreferences.getString(COURSE_ID, "")!!

    private val dayFirestore = courseCollection.document(courseId).collection(dayOfWeek.name)


    // Can only be tested through espresso
    fun save() {
        val currentSubject: String? = textBoxSubject.value
        val currentTime: CustomTime? = _timeSet.value
        val currentLocation: Location? = location
        val currentRoom: String? = textBoxRoom.value
        if (currentSubject.isNullOrBlank() || currentTime == null || currentLocation == null || currentRoom.isNullOrBlank()) {
            _snackbarText.value = Event(app.getString(R.string.empty_message))
            return
        }
        if (previousClass == null) {
            //Create new class
            val newClass =
                TimetableClass(
                    subject = currentSubject,
                    hour = currentTime.hour,
                    minute = currentTime.minute,
                    locationName = currentLocation.name,
                    locationCoordinates = currentLocation.coordinates,
                    room = currentRoom
                )
            createNewClass(newClass)
            return
        }
        //update present class
        val updatedClass =
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
        if (updatedClass == previousClass) {
            _snackbarText.value =
                Event("${updatedClass.subject} details have not been changed")
            navigateToTimetable()
        } else {
            updatePresentClass(updatedClass)
        }
    }

    private fun updatePresentClass(timetableClass: TimetableClass) {
        addFirestoreData(timetableClass)
        _snackbarText.value = Event("${timetableClass.subject} has been updated.")
        navigateToTimetable()

        val currentClassIsLater =
            compareCustomTime(getTimetableCustomTime(timetableClass), getCustomTimeNow())

        val previousClassWasLater =
            compareCustomTime(getTimetableCustomTime(previousClass!!), getCustomTimeNow())

        //Do this if the class is set for later today
        if (getTodayEnumDay() == dayOfWeek && currentClassIsLater) {
            val notificationMessage =
                "**TODAY** ${timetableClass.subject} will start at ${
                    formatTime(_timeSet.value!!)
                } in ${timetableClass.locationName} Room ${timetableClass.room}"
            sendCloudMessage(notificationMessage, courseId)
            sendNotificationId(timetableClass.alarmRequestCode.toString(), courseId)

        } else if (!currentClassIsLater && previousClassWasLater) {
            val notificationMessage = "**TODAY** ${timetableClass.subject} will not be happening"
            sendCloudMessage(notificationMessage, courseId)
            cancelAlarm(timetableClass.alarmRequestCode.toString(), courseId)
        }
        //Do this if the class is set for tomorrow.
        else if (getTomorrowEnumDay() == dayOfWeek) {

            val notificationMessage = "**TOMORROW** ${timetableClass.subject} will start at ${
                formatTime(_timeSet.value!!)
            } in ${timetableClass.locationName} Room ${timetableClass.room}"
            sendCloudMessage(notificationMessage, courseId)
            sendNotificationId(timetableClass.alarmRequestCode.toString(), courseId)
        }
    }

    private fun createNewClass(timetableClass: TimetableClass) {
        addFirestoreData(timetableClass)
        _snackbarText.value = Event("${timetableClass.subject} has been saved")
        navigateToTimetable()

        val isLater = compareCustomTime(getTimetableCustomTime(timetableClass), getCustomTimeNow())

        //Do this if the class is set for later today
        if (getTodayEnumDay() == dayOfWeek && isLater) {
            val notificationMessage =
                "**TODAY** ${timetableClass.subject} will start at ${
                    formatTime(_timeSet.value!!)
                } in ${timetableClass.locationName} Room ${timetableClass.room}"
            sendCloudMessage(notificationMessage, courseId)
            sendNotificationId(timetableClass.alarmRequestCode.toString(), courseId)

        }
        //Do this if the class is set for tomorrow.
        else if (getTomorrowEnumDay() == dayOfWeek) {

            val notificationMessage = "**TOMORROW** ${timetableClass.subject} will start at ${
                formatTime(_timeSet.value!!)
            } in ${timetableClass.locationName} Room ${timetableClass.room}"
            sendCloudMessage(notificationMessage, courseId)
            sendNotificationId(timetableClass.alarmRequestCode.toString(), courseId)
        }
    }

    private fun addFirestoreData(fridayClass: TimetableClass) {
        dayFirestore.document(fridayClass.id).set(fridayClass).addOnSuccessListener {
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
        val data = hashMapOf("message" to message, "courseId" to courseId)
        return functions.getHttpsCallable("sendMessage").call(data).continueWith {

        }
    }

    private fun sendNotificationId(notificationId: String, courseId: String): Task<Unit> {
        val data = hashMapOf("notificationId" to notificationId, "courseId" to courseId)
        return functions.getHttpsCallable("sendNotificationId").call(data).continueWith {

        }
    }

    private fun cancelAlarm(cancelAlarmId: String, courseId: String): Task<Unit> {
        val data = hashMapOf("cancelAlarmId" to cancelAlarmId, "courseId" to courseId)
        return functions.getHttpsCallable("cancelAlarm").call(data).continueWith { }
    }
}
