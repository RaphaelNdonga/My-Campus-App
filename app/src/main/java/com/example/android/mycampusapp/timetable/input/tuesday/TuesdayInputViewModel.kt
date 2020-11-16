package com.example.android.mycampusapp.timetable.input.tuesday

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
import com.example.android.mycampusapp.timetable.receiver.TuesdayClassReceiver
import com.example.android.mycampusapp.util.CalendarUtils
import com.example.android.mycampusapp.util.Event
import com.example.android.mycampusapp.util.RUN_DAILY
import com.example.android.mycampusapp.util.TimePickerValues
import com.google.firebase.firestore.DocumentReference
import timber.log.Timber
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class TuesdayInputViewModel(
    courseDocument: DocumentReference,
    private val tuesdayClass: TimetableClass?,
    private val app: Application
) : AndroidViewModel(app) {

    private val tuesdayFirestore = courseDocument.collection("tuesday")
    private val tuesdayClassExtra = MutableLiveData<TimetableClass>()
    private val _navigator = MutableLiveData<Event<Unit>>()
    val navigator: LiveData<Event<Unit>>
        get() = _navigator

    private val _timeSetByTimePicker = TimePickerValues.timeSetByTimePicker
    val timeSetByTimePicker: LiveData<String>
        get() = _timeSetByTimePicker

    val timePickerClockPosition = MutableLiveData<Event<List<Int>>>()

    val textBoxSubject = MutableLiveData<String>(tuesdayClass?.subject)
    val textBoxTime = MutableLiveData<String>(tuesdayClass?.time)
    val textBoxLocation = MutableLiveData<String>(tuesdayClass?.locationName)
    val textBoxRoom = MutableLiveData<String>(tuesdayClass?.room)
    private val id = tuesdayClass?.id
    private val alarmRequestCode = tuesdayClass?.alarmRequestCode
    private var location = tuesdayClass?.let { Location(it.locationName,it.locationCoordinates) }

    private val cal: Calendar = Calendar.getInstance()
    private val hour = cal.get(Calendar.HOUR_OF_DAY)
    private val minute = cal.get(Calendar.MINUTE)
    private val day = cal.get(Calendar.DAY_OF_WEEK)
    private val tuesday = Calendar.TUESDAY

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
        } else if (tuesdayClassIsNull()) {
            val tuesdayClass =
                TimetableClass(
                    subject = currentSubject,
                    time = currentTime,
                    locationName = currentLocation.name,
                    locationCoordinates = currentLocation.coordinates,
                    room = currentRoom
                )
            addFirestoreData(tuesdayClass)
            tuesdayClassExtra.value = tuesdayClass
            _snackbarText.value = Event(R.string.tuesday_saved)
            startTimer(tuesdayClass)
            navigateToTimetable()

        } else if (!tuesdayClassIsNull()) {
            val tuesdayClass =
                TimetableClass(
                    id!!,
                    currentSubject,
                    currentTime,
                    currentLocation.name,
                    currentLocation.coordinates,
                    alarmRequestCode!!,
                    currentRoom
                )
            addFirestoreData(tuesdayClass)
            tuesdayClassExtra.value = tuesdayClass
            _snackbarText.value = Event(R.string.tuesday_updated)
            startTimer(tuesdayClass)
            navigateToTimetable()
        }
    }

    private fun addFirestoreData(tuesdayClass: TimetableClass) {
        tuesdayFirestore.document(tuesdayClass.id).set(tuesdayClass)
    }

    private fun navigateToTimetable() {
        _navigator.value = Event(Unit)
    }


    private fun tuesdayClassIsNull(): Boolean {
        if (tuesdayClass == null) {
            return true
        }
        return false
    }

    fun setTimePickerClockPosition() {
        if (tuesdayClassIsNull()) {
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

    private fun startTimer(tuesdayClass: TimetableClass) {
        val time = try {
            SimpleDateFormat("hh:mm a", Locale.US).parse(textBoxTime.value!!)
        } catch (parseException: ParseException) {
            Timber.i("The exception is $parseException")
            SimpleDateFormat("HH:mm", Locale.UK).parse(textBoxTime.value!!)
        }
        Timber.i("The time set on tuesday is $time")
        val calendar = Calendar.getInstance()
        calendar.time = time!!
        CalendarUtils.initializeTimetableCalendar(calendar)
        Timber.i("The calendar time is ${calendar.time}")
        if (calendar.timeInMillis <= System.currentTimeMillis() && day == tuesday) {
            calendar.set(Calendar.WEEK_OF_MONTH, calendar.get(Calendar.WEEK_OF_MONTH + 1))
        }
        val triggerTime = calendar.timeInMillis

        val notifyIntent = Intent(app, TuesdayClassReceiver::class.java).apply {
            putExtra("tuesdaySubject", tuesdayClassExtra.value?.subject)
            putExtra("tuesdayTime", tuesdayClassExtra.value?.time)
        }
        val notifyPendingIntent = PendingIntent.getBroadcast(
            getApplication(),
            tuesdayClass.alarmRequestCode,
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
