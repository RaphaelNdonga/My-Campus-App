package com.example.android.mycampusapp.timetable.input

import android.app.Application
import android.content.Context
import android.text.format.DateFormat
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

    private val _classType = MutableLiveData<ClassType>()
    val classType: LiveData<ClassType> = _classType

    private val _displayNavigator = MutableLiveData<Event<Unit>>()
    val displayNavigator: LiveData<Event<Unit>>
        get() = _displayNavigator

    val textBoxSubject = MutableLiveData<String>(previousClass?.subject)
    val textBoxTime = MutableLiveData(previousClass?.let {
        formatTime(CustomTime(it.hour, it.minute))
    })
    val textBoxLocation = MutableLiveData<String>(previousClass?.locationNameOrLink)
    val textBoxRoom = MutableLiveData<String>(previousClass?.room)
    private val id = previousClass?.id
    private val alarmRequestCode = previousClass?.alarmRequestCode
    private var location =
        previousClass?.let { Location(it.locationNameOrLink, it.locationCoordinates) }
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
        val locationOrLink: String? = textBoxLocation.value
        val currentRoom: String? = textBoxRoom.value
        if (currentSubject.isNullOrBlank() || currentTime == null || locationOrLink.isNullOrBlank() || currentRoom.isNullOrBlank()) {
            _snackbarText.value = Event(app.getString(R.string.empty_message))
            return
        }
        /**
         * If the class is online, check if the link is a valid url
         */
        if (_classType.value == ClassType.ONLINE && !locationOrLink.isValidUrl()) {
            _snackbarText.value = Event("Please enter a valid url")
            return
        }
        /**
         * The following logic will be used to construct new classes:
         * If the location has not been set, just save the text(which will most probably be a link)
         * and set the coordinates to an empty string
         */
        if (previousClass == null) {
            //Create new class
            val newClass =
                TimetableClass(
                    subject = currentSubject,
                    hour = currentTime.hour,
                    minute = currentTime.minute,
                    locationNameOrLink = location?.name ?: locationOrLink,
                    locationCoordinates = location?.coordinates ?: "",
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
                location?.name ?: locationOrLink,
                location?.coordinates ?: "",
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

        val currentClassIsLater = timetableClassIsLater(timetableClass)
        val previousClassWasLater = timetableClassIsLater(previousClass!!)

        //Do this if the class is set for later today
        if (getTodayEnumDay() == dayOfWeek && currentClassIsLater) {
            updateData(timetableClass.id, getTodayEnumDay(), courseId)

        } else if (!currentClassIsLater && previousClassWasLater) {
            cancelData(
                timetableClass.alarmRequestCode.toString(),
                timetableClass.subject,
                getTodayEnumDay(),
                courseId
            )
        }
        //Do this if the class is set for tomorrow.
        else if (getTomorrowEnumDay() == dayOfWeek) {
            updateData(timetableClass.id, getTomorrowEnumDay(), courseId)
        }
    }

    private fun createNewClass(timetableClass: TimetableClass) {
        addFirestoreData(timetableClass)
        _snackbarText.value = Event("${timetableClass.subject} has been saved")
        navigateToTimetable()

        val isLater = timetableClassIsLater(timetableClass)

        //Do this if the class is set for later today
        if (getTodayEnumDay() == dayOfWeek && isLater) {
            updateData(timetableClass.id, getTodayEnumDay(), courseId)
        }
        //Do this if the class is set for tomorrow.
        else if (getTomorrowEnumDay() == dayOfWeek) {
            updateData(timetableClass.id, getTomorrowEnumDay(), courseId)
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

    /**
     * Is necessary when the class is online. Since upon saving, the user might change their minds
     * after saving a location. Therefore if the class is online, nullify the location
     */
    fun nullifyLocation() {
        location = null
    }

    fun setTime(time: CustomTime) {
        textBoxTime.value = formatTime(time)
        _timeSet.value = time
    }

    private fun updateData(
        timetableId: String,
        dayOfWeek: DayOfWeek,
        courseId: String
    ): Task<Unit> {
        val data = hashMapOf(
            "timetableId" to timetableId,
            "dayOfWeek" to dayOfWeek.name,
            "courseId" to courseId
        )
        return functions.getHttpsCallable("updateData").call(data).continueWith {

        }
    }

    private fun cancelData(
        requestCode: String,
        subject: String,
        dayOfWeek: DayOfWeek,
        courseId: String
    ): Task<Unit> {
        val data =
            hashMapOf(
                "requestCode" to requestCode,
                "subject" to subject,
                "dayOfWeek" to dayOfWeek.name,
                "courseId" to courseId
            )
        return functions.getHttpsCallable("cancelData").call(data).continueWith { }
    }

    private fun formatTime(customTime: CustomTime): String {
        return if (DateFormat.is24HourFormat(getApplication())) {
            format24HourTime(customTime)
        } else {
            formatAmPmTime(customTime)
        }
    }

    fun setClassType(classType: ClassType) {
        _classType.value = classType
    }
}
