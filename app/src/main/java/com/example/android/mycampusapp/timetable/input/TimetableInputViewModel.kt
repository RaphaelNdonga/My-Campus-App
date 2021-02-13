package com.example.android.mycampusapp.timetable.input

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.android.mycampusapp.R
import com.example.android.mycampusapp.data.CustomTime
import com.example.android.mycampusapp.data.Location
import com.example.android.mycampusapp.data.TimetableClass
import com.example.android.mycampusapp.timetable.receiver.FridayClassReceiver
import com.example.android.mycampusapp.util.CalendarUtils
import com.example.android.mycampusapp.util.Event
import com.example.android.mycampusapp.util.RUN_DAILY
import com.example.android.mycampusapp.util.formatTime
import com.google.firebase.firestore.CollectionReference
import timber.log.Timber
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class TimetableInputViewModel(
    private val timetableClass: TimetableClass?,
    private val app: Application,
    private val fridayFirestore: CollectionReference
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
    private val _timeSet = MutableLiveData(
        timetableClass?.let {
            CustomTime(it.hour, it.minute)
        }
    )
    val timeSet: LiveData<CustomTime>
        get() = _timeSet

    private val cal: Calendar = Calendar.getInstance()
    private val hour = cal.get(Calendar.HOUR_OF_DAY)
    private val minute = cal.get(Calendar.MINUTE)
    private val day = cal.get(Calendar.DAY_OF_WEEK)
    private val friday = Calendar.FRIDAY

    private val _snackbarText = MutableLiveData<Event<Int>>()
    val snackbarText: LiveData<Event<Int>>
        get() = _snackbarText

    // Can only be tested through espresso
    fun save() {
        val currentSubject: String? = textBoxSubject.value
        val currentTime: CustomTime? = _timeSet.value
        val currentLocation: Location? = location
        val currentRoom: String? = textBoxRoom.value
        if (currentSubject.isNullOrBlank() || currentTime == null || currentLocation == null || currentRoom.isNullOrBlank()) {
            _snackbarText.value = Event(R.string.empty_message)
            return
        } else if (fridayClassIsNull()) {
            val fridayClass =
                TimetableClass(
                    subject = currentSubject,
                    hour = currentTime.hour,
                    minute = currentTime.minute,
                    locationName = currentLocation.name,
                    locationCoordinates = currentLocation.coordinates,
                    room = currentRoom
                )
            addFirestoreData(fridayClass)
            _snackbarText.value = Event(R.string.class_saved)
            startTimer(fridayClass)
            navigateToTimetable()

        } else if (!fridayClassIsNull()) {
            val fridayClass =
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
            addFirestoreData(fridayClass)
            _snackbarText.value = Event(R.string.friday_updated)
            startTimer(fridayClass)
            navigateToTimetable()
        }
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


    private fun fridayClassIsNull(): Boolean {
        if (timetableClass == null) {
            return true
        }
        return false
    }


    private fun startTimer(fridayClass: TimetableClass) {
        val time = try {
            SimpleDateFormat("hh:mm a", Locale.US).parse(textBoxTime.value!!)
        } catch (parseException: ParseException) {
            Timber.i("The exception caught is $parseException")
            SimpleDateFormat("HH:mm", Locale.UK).parse(textBoxTime.value!!)
        }
        val calendar = Calendar.getInstance()
        calendar.time = time!!
        CalendarUtils.initializeTimetableCalendar(calendar)

        if (calendar.timeInMillis <= System.currentTimeMillis() && day == friday
        ) {
            calendar.set(Calendar.WEEK_OF_MONTH, calendar.get(Calendar.WEEK_OF_MONTH + 1))
        }
        val triggerTime = calendar.timeInMillis

        val notifyIntent = Intent(app, FridayClassReceiver::class.java).apply {
            putExtra("fridaySubject", textBoxSubject.value)
            putExtra("fridayTime", textBoxTime.value)
        }
        val notifyPendingIntent = PendingIntent.getBroadcast(
            getApplication(),
            fridayClass.alarmRequestCode,
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

    fun setLocation(loc: Location) {
        location = loc
        textBoxLocation.value = loc.name
    }

    fun setTime(time: CustomTime) {
        textBoxTime.value = formatTime(time)
        _timeSet.value = time
    }
}
