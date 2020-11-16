package com.example.android.mycampusapp.timetable.input.wednesday

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
import com.example.android.mycampusapp.timetable.receiver.WednesdayClassReceiver
import com.example.android.mycampusapp.util.CalendarUtils
import com.example.android.mycampusapp.util.Event
import com.example.android.mycampusapp.util.RUN_DAILY
import com.example.android.mycampusapp.util.TimePickerValues
import com.google.firebase.firestore.DocumentReference
import timber.log.Timber
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class WednesdayInputViewModel(
    courseDocument: DocumentReference,
    private val wednesdayClass: TimetableClass?,
    private val app: Application
) : AndroidViewModel(app) {

    private val wednesdayFirestore = courseDocument.collection("wednesday")

    private val wednesdayClassExtra = MutableLiveData<TimetableClass>()
    private val _navigator = MutableLiveData<Event<Unit>>()
    val navigator: LiveData<Event<Unit>>
        get() = _navigator

    private val _timeSetByTimePicker = TimePickerValues.timeSetByTimePicker
    val timeSetByTimePicker: LiveData<String>
        get() = _timeSetByTimePicker

    val timePickerClockPosition = MutableLiveData<Event<List<Int>>>()

    val textBoxSubject = MutableLiveData<String>(wednesdayClass?.subject)
    val textBoxTime = MutableLiveData<String>(wednesdayClass?.time)
    val textBoxLocation = MutableLiveData<String>(wednesdayClass?.locationName)
    val textBoxRoom = MutableLiveData<String>(wednesdayClass?.room)
    private val id = wednesdayClass?.id
    private val alarmRequestCode = wednesdayClass?.alarmRequestCode
    private var location = wednesdayClass?.let { Location(it.locationName,it.locationCoordinates) }

    private val cal: Calendar = Calendar.getInstance()
    private val hour = cal.get(Calendar.HOUR_OF_DAY)
    private val minute = cal.get(Calendar.MINUTE)
    private val day = cal.get(Calendar.DAY_OF_WEEK)
    private val wednesday = Calendar.WEDNESDAY

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
        } else if (wednesdayClassIsNull()) {
            val wednesdayClass =
                TimetableClass(
                    subject = currentSubject,
                    time = currentTime,
                    locationName = currentLocation.name,
                    locationCoordinates = currentLocation.coordinates,
                    room = currentRoom
                )
            addFirestoreData(wednesdayClass)
            wednesdayClassExtra.value = wednesdayClass
            _snackbarText.value = Event(R.string.wednesday_saved)
            startTimer(wednesdayClass)
            navigateToTimetable()

        } else if (!wednesdayClassIsNull()) {
            val wednesdayClass =
                TimetableClass(
                    id!!,
                    currentSubject,
                    currentTime,
                    currentLocation.name,
                    currentLocation.coordinates,
                    alarmRequestCode!!,
                    currentRoom
                )
            addFirestoreData(wednesdayClass)
            wednesdayClassExtra.value = wednesdayClass
            _snackbarText.value = Event(R.string.wednesday_updated)
            startTimer(wednesdayClass)
            navigateToTimetable()
        }
    }

    private fun addFirestoreData(wednesdayClass: TimetableClass) {
        wednesdayFirestore.document(wednesdayClass.id).set(wednesdayClass).addOnSuccessListener {
            Timber.i("Data was added successfully")
        }.addOnFailureListener { exception ->
            Timber.i("Data failed to upload because of $exception")
        }
    }

    private fun navigateToTimetable() {
        _navigator.value = Event(Unit)
    }


    private fun wednesdayClassIsNull(): Boolean {
        if (wednesdayClass == null) {
            return true
        }
        return false
    }

    fun setTimePickerClockPosition() {
        if (wednesdayClassIsNull()) {
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

    private fun startTimer(wednesdayClass: TimetableClass) {
        val time = try {
            SimpleDateFormat("hh:mm a", Locale.US).parse(textBoxTime.value!!)
        } catch (parseException: ParseException) {
            Timber.i("The exception caught is $parseException")
            SimpleDateFormat("HH:mm", Locale.UK).parse(textBoxTime.value!!)
        }
        Timber.i("the time in the textbox is $time")
        val calendar = Calendar.getInstance()
        calendar.time = time!!
        CalendarUtils.initializeTimetableCalendar(calendar)
        Timber.i("the time set is ${calendar.time}")

        if (calendar.timeInMillis <= System.currentTimeMillis() && day == wednesday) {
            calendar.set(Calendar.WEEK_OF_MONTH, calendar.get(Calendar.WEEK_OF_MONTH + 1))
        }

        val triggerTime = calendar.timeInMillis

        val notifyIntent = Intent(app, WednesdayClassReceiver::class.java).apply {
            putExtra("wednesdaySubject", wednesdayClassExtra.value?.subject)
            putExtra("wednesdayTime", wednesdayClassExtra.value?.time)
        }
        val notifyPendingIntent = PendingIntent.getBroadcast(
            getApplication(),
            wednesdayClass.alarmRequestCode,
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
