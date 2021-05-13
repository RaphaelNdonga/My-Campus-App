package com.example.android.mycampusapp.assessments

import android.app.Application
import android.text.format.DateFormat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.android.mycampusapp.R
import com.example.android.mycampusapp.data.Assessment
import com.example.android.mycampusapp.data.CustomDate
import com.example.android.mycampusapp.data.CustomTime
import com.example.android.mycampusapp.data.Location
import com.example.android.mycampusapp.util.Event
import com.example.android.mycampusapp.util.format24HourTime
import com.example.android.mycampusapp.util.formatAmPmTime
import com.example.android.mycampusapp.util.formatDate
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.functions.FirebaseFunctions

class AssessmentInputViewModel(
    private val assignmentsCollection: CollectionReference,
    private val assessment: Assessment?,
    private val functions: FirebaseFunctions,
    private val assessmentType: AssessmentType,
    application: Application
) : AndroidViewModel(application) {

    // acquire the date values and save them as integers
    private val _dateSet = MutableLiveData<CustomDate>(assessment?.let {
        CustomDate(assessment.year, assessment.month, assessment.day)
    })
    val dateSet: LiveData<CustomDate>
        get() = _dateSet
    private val _timeSet = MutableLiveData<CustomTime>(assessment?.let {
        CustomTime(assessment.hour, assessment.minute)
    })
    val timeSet: LiveData<CustomTime>
        get() = _timeSet

    private val dateText = _dateSet.value?.let { date ->
        formatDate(date)
    }
    private val timeText = _timeSet.value?.let { time ->
        formatTime(time)
    }
    private val _snackBarEvent = MutableLiveData<Event<Int>>()
    val snackBarEvent: LiveData<Event<Int>>
        get() = _snackBarEvent

    private val _displayNavigator = MutableLiveData<Event<Unit>>()
    val displayNavigator: LiveData<Event<Unit>>
        get() = _displayNavigator

    // The immutable variables below are connected to the xml file through a two-way dataBinding.
    val textBoxSubject = MutableLiveData<String>(assessment?.subject)
    val textBoxDate = MutableLiveData<String>(dateText)
    val textBoxTime = MutableLiveData<String>(timeText)
    val textBoxLocation = MutableLiveData<String>(assessment?.locationName)
    val textBoxRoom = MutableLiveData<String>(assessment?.room)

    private var location = assessment?.let {
        Location(
            it.locationName,
            it.locationCoordinates
        )
    }


    fun save() {
        val currentSubject = textBoxSubject.value
        val currentDate = _dateSet.value
        val currentTime = _timeSet.value
        val currentLocation = location
        val currentRoom = textBoxRoom.value

        if (currentDate == null || currentSubject.isNullOrBlank() || currentRoom.isNullOrBlank() ||
            currentTime == null || currentLocation == null
        ) {
            _snackBarEvent.value = Event(R.string.empty_message)
            return
        }
        if (assessment == null) {
            val currentAssignment = Assessment(
                subject = currentSubject,
                day = currentDate.day,
                month = currentDate.month,
                year = currentDate.year,
                hour = currentTime.hour,
                minute = currentTime.minute,
                locationName = currentLocation.name,
                locationCoordinates = currentLocation.coordinates,
                room = currentRoom
            )
            addFirestoreData(currentAssignment)
            navigateToDisplay()
        } else {
            val currentAssignment = Assessment(
                id = assessment.id,
                subject = currentSubject,
                day = currentDate.day,
                month = currentDate.month,
                year = currentDate.year,
                hour = currentTime.hour,
                minute = currentTime.minute,
                locationName = currentLocation.name,
                locationCoordinates = currentLocation.coordinates,
                alarmRequestCode = assessment.alarmRequestCode,
                room = currentRoom
            )
            addFirestoreData(currentAssignment)
            navigateToDisplay()
        }
    }

    private fun addFirestoreData(currentAssignment: Assessment) {
        assignmentsCollection.document(currentAssignment.id).set(currentAssignment)
    }

    private fun navigateToDisplay() {
        _displayNavigator.value = Event(Unit)
    }

    fun setDateFromDatePicker(date: CustomDate) {
        val dateText = formatDate(date)
        textBoxDate.value = dateText
        _dateSet.value = date
    }

    fun setTimeFromTimePicker(customTime: CustomTime) {
        val timeText = formatTime(customTime)
        textBoxTime.value = timeText
        _timeSet.value = customTime
    }

    fun setLocation(location: Location) {
        this.location = location
        textBoxLocation.value = location.name
    }

    private fun formatTime(time: CustomTime): String {
        return if (DateFormat.is24HourFormat(getApplication())) {
            format24HourTime(time)
        } else {
            formatAmPmTime(time)
        }
    }

}