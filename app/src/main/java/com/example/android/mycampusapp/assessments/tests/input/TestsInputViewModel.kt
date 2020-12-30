package com.example.android.mycampusapp.assessments.tests.input

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.android.mycampusapp.data.CustomDate
import com.example.android.mycampusapp.data.CustomTime
import com.example.android.mycampusapp.data.Location
import com.example.android.mycampusapp.data.Test
import com.example.android.mycampusapp.util.Event
import com.google.firebase.firestore.CollectionReference
import timber.log.Timber

class TestsInputViewModel(
    private val test: Test?,
    private val testCollection: CollectionReference
) : ViewModel() {

    val textBoxSubject = MutableLiveData<String>()
    val textBoxTime = MutableLiveData<String>()
    val textBoxDate = MutableLiveData<String>()
    val textBoxLocation = MutableLiveData<String>()
    val textBoxRoom = MutableLiveData<String>()

    private val _dateSet =
        MutableLiveData<CustomDate>(test?.let { CustomDate(test.year, test.month, test.day) })
    val dateSet: LiveData<CustomDate>
        get() = _dateSet

    private val _timeSet =
        MutableLiveData<CustomTime>(test?.let { CustomTime(test.hour, test.minute) })
    val timeSet: LiveData<CustomTime>
        get() = _timeSet

    private val _location =
        MutableLiveData<Location>(test?.let {
            Location(
                test.locationName,
                test.locationCoordinates
            )
        })

    private val _displayNavigator = MutableLiveData<Event<Unit>>()
    val displayNavigator:LiveData<Event<Unit>>
        get() = _displayNavigator


    fun setDateFromDatePicker(dateSet: CustomDate) {
        val dateText = "${dateSet.day}/${dateSet.month.plus(1)}/${dateSet.year}"
        textBoxDate.value = dateText
        _dateSet.value = dateSet
    }

    fun setTimeFromTimePicker(timeset: CustomTime) {
        val timeText = "${timeset.hour}:${timeset.minute}"
        textBoxTime.value = timeText
        _timeSet.value = timeset
    }

    fun save() {
        val dateToSave = _dateSet.value
        val timeToSave = _timeSet.value
        val subjectToSave = textBoxSubject.value
        val locationToSave = _location.value
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
            val testToSave = Test(
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
            val testToSave = Test(
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

    private fun saveFirestoreTest(test: Test) {
        testCollection.document(test.id).set(test)
        Timber.i("The test has been saved!")
    }

    fun setLocation(location: Location) {
        _location.value = location
        textBoxLocation.value = location.name
    }
    private fun navigateToDisplay(){
        initializeEvent(_displayNavigator)
    }

    private fun initializeEvent(mutableLiveData: MutableLiveData<Event<Unit>>) {
        mutableLiveData.value = Event(Unit)
    }
}