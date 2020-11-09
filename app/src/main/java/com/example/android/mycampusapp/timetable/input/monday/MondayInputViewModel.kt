package com.example.android.mycampusapp.timetable.input.monday

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.android.mycampusapp.R
import com.example.android.mycampusapp.timetable.data.TimetableClass
import com.example.android.mycampusapp.timetable.receiver.MondayClassReceiver
import com.example.android.mycampusapp.util.CalendarUtils
import com.example.android.mycampusapp.util.Event
import com.example.android.mycampusapp.util.RUN_DAILY
import com.example.android.mycampusapp.util.TimePickerValues
import com.google.firebase.firestore.DocumentReference
import timber.log.Timber
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class MondayInputViewModel(
    coursesDocument: DocumentReference,
    private val mondayClass: TimetableClass?,
    private val app: Application
) : AndroidViewModel(app) {

    private val mondayFirestore = coursesDocument.collection("monday")

    private val mondayClassExtra = MutableLiveData<TimetableClass>()
    private val _displayNavigator = MutableLiveData<Event<Unit>>()
    val displayNavigator: LiveData<Event<Unit>>
        get() = _displayNavigator

    private val _timeSetByTimePicker = TimePickerValues.timeSetByTimePicker
    val timeSetByTimePicker: LiveData<String>
        get() = _timeSetByTimePicker

    val timePickerClockPosition = MutableLiveData<Event<List<Int>>>()

    val textBoxSubject = MutableLiveData<String>(mondayClass?.subject)
    val textBoxTime = MutableLiveData<String>(mondayClass?.time)
    val textBoxLocation = MutableLiveData<String>(mondayClass?.location)
    private val id = mondayClass?.id
    private val alarmRequestCode = mondayClass?.alarmRequestCode

    private val cal: Calendar = Calendar.getInstance()
    private val hour = cal.get(Calendar.HOUR_OF_DAY)
    private val minute = cal.get(Calendar.MINUTE)
    private val day = cal.get(Calendar.DAY_OF_WEEK)
    private val monday = Calendar.MONDAY

    private val _snackbarText = MutableLiveData<Event<Int>>()
    val snackbarText: LiveData<Event<Int>>
        get() = _snackbarText

    // Can only be tested through espresso
    fun save() {
        val currentSubject: String? = textBoxSubject.value
        val currentTime: String? = textBoxTime.value
        val currentLocation: String? = textBoxLocation.value
        if (currentSubject.isNullOrBlank() || currentTime.isNullOrBlank() || currentLocation.isNullOrBlank()) {
            _snackbarText.value = Event(R.string.empty_message)
            return
        } else if (mondayClassIsNull()) {
            val mondayClass =
                TimetableClass(
                    subject = currentSubject,
                    time = currentTime,
                    location = currentLocation
                )
            addFirestoreData(mondayClass)
            mondayClassExtra.value = mondayClass
            _snackbarText.value = Event(R.string.monday_saved)
            startTimer(mondayClass)
            navigateToTimetable()

        } else if (!mondayClassIsNull()) {
            val mondayClass =
                TimetableClass(
                    id!!,
                    currentSubject,
                    currentTime,
                    currentLocation,
                    alarmRequestCode!!
                )
            addFirestoreData(mondayClass)
            mondayClassExtra.value = mondayClass
            _snackbarText.value = Event(R.string.monday_updated)
            startTimer(mondayClass)
            navigateToTimetable()
        }
    }

    private fun addFirestoreData(mondayClass: TimetableClass) {
        mondayFirestore.document(mondayClass.id).set(mondayClass).addOnSuccessListener {
            Timber.i("Monday data was added successfully")
        }.addOnFailureListener { exception ->
            Timber.i("Monday data was not added because of $exception")
        }
    }

    private fun navigateToTimetable() {
        _displayNavigator.value = Event(Unit)
    }


    private fun mondayClassIsNull(): Boolean {
        if (mondayClass == null) {
            return true
        }
        return false
    }

    fun setTimePickerClockPosition() {
        if (mondayClassIsNull()) {
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

    private fun startTimer(mondayClass: TimetableClass) {
        val time = try {
            SimpleDateFormat("hh:mm a", Locale.US).parse(textBoxTime.value!!)
        } catch (parseException: ParseException) {
            Timber.i("The exception is $parseException")
            SimpleDateFormat("HH:mm", Locale.UK).parse(textBoxTime.value!!)
        }
        Timber.i("The timer reads the time as $time")
        val calendar = Calendar.getInstance()
        calendar.time = time!!
        CalendarUtils.initializeTimetableCalendar(calendar)
        if (calendar.timeInMillis <= System.currentTimeMillis() && day == monday
        ) {
            calendar.set(Calendar.WEEK_OF_MONTH, calendar.get(Calendar.WEEK_OF_MONTH + 1))
        }

        val triggerTime = calendar.timeInMillis

        val notifyIntent = Intent(app, MondayClassReceiver::class.java).apply {
            putExtra("mondaySubject", mondayClassExtra.value?.subject)
            putExtra("mondayTime", mondayClassExtra.value?.time)
        }
        val notifyPendingIntent = PendingIntent.getBroadcast(
            getApplication(),
            mondayClass.alarmRequestCode,
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
}
