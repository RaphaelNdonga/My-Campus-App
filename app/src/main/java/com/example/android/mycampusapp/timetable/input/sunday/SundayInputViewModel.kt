package com.example.android.mycampusapp.timetable.input.sunday

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
import com.example.android.mycampusapp.timetable.receiver.SundayClassReceiver
import com.example.android.mycampusapp.util.CalendarUtils
import com.example.android.mycampusapp.util.Event
import com.example.android.mycampusapp.util.RUN_DAILY
import com.example.android.mycampusapp.util.TimePickerValues
import com.google.firebase.firestore.DocumentReference
import timber.log.Timber
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class SundayInputViewModel(
    courseDocument: DocumentReference,
    private val sundayClass: TimetableClass?,
    private val app: Application
) : AndroidViewModel(app) {

    private val sundayFirestore = courseDocument.collection("sunday")
    private val sundayClassExtra = MutableLiveData<TimetableClass>()
    private val _navigator = MutableLiveData<Event<Unit>>()
    val navigator: LiveData<Event<Unit>>
        get() = _navigator

    private val _timeSetByTimePicker = TimePickerValues.timeSetByTimePicker
    val timeSetByTimePicker: LiveData<String>
        get() = _timeSetByTimePicker

    val timePickerClockPosition = MutableLiveData<Event<List<Int>>>()

    val textBoxSubject = MutableLiveData<String>(sundayClass?.subject)
    val textBoxTime = MutableLiveData<String>(sundayClass?.time)
    val textBoxLocation = MutableLiveData<String>(sundayClass?.location)
    private val id = sundayClass?.id
    private val alarmRequestCode = sundayClass?.alarmRequestCode

    private val cal: Calendar = Calendar.getInstance()
    private val hour = cal.get(Calendar.HOUR_OF_DAY)
    private val minute = cal.get(Calendar.MINUTE)
    private val day = cal.get(Calendar.DAY_OF_WEEK)
    private val sunday = Calendar.SUNDAY

    private val _snackbarText = MutableLiveData<Event<Int>>()
    val snackbarText: LiveData<Event<Int>>
        get() = _snackbarText

    // Can only be tested through espresso
    fun save() {
        val currentSubject: String? = textBoxSubject.value
        val currentTime: String? = textBoxTime.value
        val currentLocation:String? = textBoxLocation.value
        if (currentSubject.isNullOrBlank() || currentTime.isNullOrBlank() || currentLocation.isNullOrBlank()) {
            _snackbarText.value = Event(R.string.empty_message)
            return
        } else if (sundayClassIsNull()) {
            val sundayClass =
                TimetableClass(
                    subject = currentSubject,
                    time = currentTime,
                    location = currentLocation
                )
            addFirestoreData(sundayClass)
            sundayClassExtra.value = sundayClass
            _snackbarText.value = Event(R.string.sunday_saved)
            startTimer(sundayClass)
            navigateToTimetable()

        } else if (!sundayClassIsNull()) {
            val sundayClass =
                TimetableClass(
                    id!!,
                    currentSubject,
                    currentTime,
                    currentLocation,
                    alarmRequestCode!!
                )
            addFirestoreData(sundayClass)
            sundayClassExtra.value = sundayClass
            _snackbarText.value = Event(R.string.sunday_updated)
            startTimer(sundayClass)
            navigateToTimetable()
        }
    }

    private fun addFirestoreData(sundayClass: TimetableClass) {
        sundayFirestore.document(sundayClass.id).set(sundayClass).addOnSuccessListener {
            Timber.i("Data added successfully")
        }.addOnFailureListener { exception ->
            Timber.i("Data failed to upload because of $exception")
        }
    }

    private fun navigateToTimetable() {
        _navigator.value = Event(Unit)
    }


    private fun sundayClassIsNull(): Boolean {
        if (sundayClass == null) {
            return true
        }
        return false
    }

    fun setTimePickerClockPosition() {
        if (sundayClassIsNull()) {
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

    private fun startTimer(sundayClass: TimetableClass) {
        val time = try {
            SimpleDateFormat("hh:mm a", Locale.US).parse(textBoxTime.value!!)
        } catch (parseException: ParseException) {
            Timber.i("The exception is $parseException")
            SimpleDateFormat("HH:mm", Locale.UK).parse(textBoxTime.value!!)
        }
        val calendar = Calendar.getInstance()
        calendar.time = time!!
        CalendarUtils.initializeTimetableCalendar(calendar)

        if (calendar.timeInMillis >= System.currentTimeMillis()&&day == sunday) {
            calendar.set(Calendar.WEEK_OF_MONTH,calendar.get(Calendar.WEEK_OF_MONTH + 1))
        }

        val triggerTime = calendar.timeInMillis

        val notifyIntent = Intent(app, SundayClassReceiver::class.java).apply {
            putExtra("sundaySubject", sundayClassExtra.value?.subject)
            putExtra("sundayTime", sundayClassExtra.value?.time)
        }
        val notifyPendingIntent = PendingIntent.getBroadcast(
            getApplication(),
            sundayClass.alarmRequestCode,
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
