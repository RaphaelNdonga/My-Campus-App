package com.example.android.mycampusapp.timetable.input.thursday

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.android.mycampusapp.R
import com.example.android.mycampusapp.location.Location
import com.example.android.mycampusapp.timetable.data.TimetableClass
import com.example.android.mycampusapp.timetable.receiver.ThursdayClassReceiver
import com.example.android.mycampusapp.util.CalendarUtils
import com.example.android.mycampusapp.util.Event
import com.example.android.mycampusapp.util.RUN_DAILY
import com.example.android.mycampusapp.util.TimePickerValues
import com.google.firebase.firestore.DocumentReference
import timber.log.Timber
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class ThursdayInputViewModel(
    courseDocument: DocumentReference,
    private val thursdayClass: TimetableClass?,
    private val app: Application
) : AndroidViewModel(app) {

    private val thursdayFirestore = courseDocument.collection("thursday")
    private val thursdayClassExtra = MutableLiveData<TimetableClass>()
    private val _navigator = MutableLiveData<Event<Unit>>()
    val navigator: LiveData<Event<Unit>>
        get() = _navigator

    private val _timeSetByTimePicker = TimePickerValues.timeSetByTimePicker
    val timeSetByTimePicker: LiveData<String>
        get() = _timeSetByTimePicker

    val timePickerClockPosition = MutableLiveData<Event<List<Int>>>()

    val textBoxSubject = MutableLiveData<String>(thursdayClass?.subject)
    val textBoxTime = MutableLiveData<String>(thursdayClass?.time)
    val textBoxLocation = MutableLiveData<String>(thursdayClass?.locationName)
    val textBoxRoom = MutableLiveData<String>(thursdayClass?.room)
    private val id = thursdayClass?.id
    private val alarmRequestCode = thursdayClass?.alarmRequestCode
    private var location = thursdayClass?.let { Location(it.locationName,it.locationCoordinates) }

    private val cal: Calendar = Calendar.getInstance()
    private val hour = cal.get(Calendar.HOUR_OF_DAY)
    private val minute = cal.get(Calendar.MINUTE)
    private val day = cal.get(Calendar.DAY_OF_WEEK)
    private val thursday = Calendar.THURSDAY

    private val _snackbarText = MutableLiveData<Event<Int>>()
    val snackbarText: LiveData<Event<Int>>
        get() = _snackbarText

    // Can only be tested through espresso
    fun save() {
        val currentSubject: String? = textBoxSubject.value
        val currentTime: String? = textBoxTime.value
        val currentLocation: Location? = location
        val currentRoom:String? = textBoxRoom.value
        if (currentSubject.isNullOrBlank() || currentTime.isNullOrBlank() || currentLocation == null || currentRoom.isNullOrBlank()) {
            _snackbarText.value = Event(R.string.empty_message)
            return
        } else if (thursdayClassIsNull()) {
            val thursdayClass =
                TimetableClass(
                    subject = currentSubject,
                    time = currentTime,
                    locationName = currentLocation.name,
                    locationCoordinates = currentLocation.coordinates,
                    room = currentRoom
                )
            addFirestoreData(thursdayClass)
            thursdayClassExtra.value = thursdayClass
            _snackbarText.value = Event(R.string.thursday_saved)
            startTimer(thursdayClass)
            navigateToTimetable()

        } else if (!thursdayClassIsNull()) {
            val thursdayClass =
                TimetableClass(
                    id!!,
                    currentSubject,
                    currentTime,
                    currentLocation.name,
                    currentLocation.coordinates,
                    alarmRequestCode!!,
                    currentRoom
                )
            addFirestoreData(thursdayClass)
            thursdayClassExtra.value = thursdayClass
            _snackbarText.value = Event(R.string.thursday_updated)
            startTimer(thursdayClass)
            navigateToTimetable()
        }
    }

    private fun addFirestoreData(thursdayClass: TimetableClass) {
        thursdayFirestore.document(thursdayClass.id).set(thursdayClass).addOnSuccessListener {
            Timber.i("Data was added successfully")
        }.addOnFailureListener { exception: Exception ->
            Timber.i("Data failed to upload because of $exception")
        }
    }

    private fun navigateToTimetable() {
        _navigator.value = Event(Unit)
    }


    private fun thursdayClassIsNull(): Boolean {
        if (thursdayClass == null) {
            return true
        }
        return false
    }

    fun setTimePickerClockPosition() {
        if (thursdayClassIsNull()) {
            timePickerClockPosition.value =
                Event(listOf(hour, minute))
        } else {
            val time = SimpleDateFormat("HH:mm", Locale.US).parse(textBoxTime.value!!)
            val calendar = Calendar.getInstance()
            calendar.time = time!!
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minutes = calendar.get(Calendar.MINUTE)
            timePickerClockPosition.value = Event(listOf(hour, minutes))
        }
    }

    private fun startTimer(thursdayClass: TimetableClass) {
        val time = try {
            SimpleDateFormat("hh:mm a", Locale.US).parse(textBoxTime.value!!)
        } catch (parseException: ParseException) {
            Timber.i("The exception is $parseException")
            SimpleDateFormat("HH:mm", Locale.UK).parse(textBoxTime.value!!)
        }
        val calendar = Calendar.getInstance()
        calendar.time = time!!
        CalendarUtils.initializeTimetableCalendar(calendar)

        if (calendar.timeInMillis <= System.currentTimeMillis() && day == thursday) {
            calendar.set(Calendar.WEEK_OF_MONTH, calendar.get(Calendar.WEEK_OF_MONTH + 1))
        }
        val triggerTime = calendar.timeInMillis

        val notifyIntent = Intent(app, ThursdayClassReceiver::class.java).apply {
            putExtra("thursdaySubject", thursdayClassExtra.value?.subject)
            putExtra("thursdayTime", thursdayClassExtra.value?.time)
        }
        val notifyPendingIntent = PendingIntent.getBroadcast(
            getApplication(),
            thursdayClass.alarmRequestCode,
            notifyIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val alarmManager = app.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            triggerTime,
            RUN_DAILY,
            notifyPendingIntent
        )
    }
    fun setLocation(loc: Location){
        location = loc
        textBoxLocation.value = loc.name
    }
}
