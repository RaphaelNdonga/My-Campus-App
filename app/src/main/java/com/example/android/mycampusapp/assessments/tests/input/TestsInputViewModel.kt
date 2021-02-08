package com.example.android.mycampusapp.assessments.tests.input

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.android.mycampusapp.data.Assessment
import com.example.android.mycampusapp.data.CustomDate
import com.example.android.mycampusapp.data.CustomTime
import com.example.android.mycampusapp.data.Location
import com.example.android.mycampusapp.util.Event
import com.example.android.mycampusapp.util.formatDate
import com.example.android.mycampusapp.util.formatTime
import com.google.firebase.firestore.CollectionReference
import timber.log.Timber

class TestsInputViewModel(
    private val test: Assessment?,
    private val testCollection: CollectionReference
) : ViewModel() {
    private val _dateSet =
        MutableLiveData<CustomDate>(test?.let { CustomDate(test.year, test.month, test.day) })
    val dateSet: LiveData<CustomDate>
        get() = _dateSet

    private val _timeSet =
        MutableLiveData<CustomTime>(test?.let { CustomTime(test.hour, test.minute) })
    val timeSet: LiveData<CustomTime>
        get() = _timeSet

    private var dateText = _dateSet.value?.let { date->
        formatDate(date)
    }?:""
    private var timeText = _timeSet.value?.let { time->
        formatTime(time)
    }?:""

    val textBoxSubject = MutableLiveData<String>(test?.subject)
    val textBoxTime = MutableLiveData(timeText)
    val textBoxDate = MutableLiveData(dateText)
    val textBoxLocation = MutableLiveData<String>(test?.locationName)
    val textBoxRoom = MutableLiveData<String>(test?.room)

    private var location = test?.let {
            Location(
                test.locationName,
                test.locationCoordinates
            )
        }

    private val _displayNavigator = MutableLiveData<Event<Unit>>()
    val displayNavigator:LiveData<Event<Unit>>
        get() = _displayNavigator


    fun setDateFromDatePicker(dateSet: CustomDate) {
        dateText = formatDate(dateSet)
        textBoxDate.value = dateText
        _dateSet.value = dateSet
    }

    fun setTimeFromTimePicker(time: CustomTime) {
        val timeText = formatTime(time)
        textBoxTime.value = timeText
        _timeSet.value = time
    }

    fun save() {
        val dateToSave = _dateSet.value
        val timeToSave = _timeSet.value
        val subjectToSave = textBoxSubject.value
        val locationToSave = location
        val roomToSave = textBoxRoom.value
        if (dateToSave == null ||
            timeToSave == null ||
            locationToSave==null||
            subjectToSave.isNullOrBlank() ||
            roomToSave.isNullOrBlank()
        ) {
            return
        }
        if (test == null) {
            val testToSave = Assessment(
                hour = timeToSave.hour,
                minute = timeToSave.minute,
                year = dateToSave.year,
                month = dateToSave.month,
                day = dateToSave.day,
                subject = subjectToSave,
                locationCoordinates = locationToSave.coordinates,
                locationName = locationToSave.name,
                room = roomToSave
            )
            saveFirestoreTest(testToSave)
            navigateToDisplay()
        }else{
            val testToSave = Assessment(
                id = test.id,
                hour = timeToSave.hour,
                minute = timeToSave.minute,
                year = dateToSave.year,
                month = dateToSave.month,
                day = dateToSave.day,
                subject = subjectToSave,
                locationCoordinates = locationToSave.coordinates,
                locationName = locationToSave.name,
                room = roomToSave,
                alarmRequestCode = test.alarmRequestCode
            )
            saveFirestoreTest(testToSave)
            navigateToDisplay()
        }
    }

    private fun saveFirestoreTest(assessment: Assessment) {
        testCollection.document(assessment.id).set(assessment)
        Timber.i("The assessment has been saved!")
    }

    fun setLocation(location: Location) {
        this.location = location
        textBoxLocation.value = location.name
    }
    private fun navigateToDisplay(){
        initializeEvent(_displayNavigator)
    }

    private fun initializeEvent(mutableLiveData: MutableLiveData<Event<Unit>>) {
        mutableLiveData.value = Event(Unit)
    }
}