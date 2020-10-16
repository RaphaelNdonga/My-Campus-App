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
import com.example.android.mycampusapp.timetable.data.WednesdayClass
import com.example.android.mycampusapp.timetable.receiver.WednesdayClassReceiver
import com.example.android.mycampusapp.util.Event
import com.example.android.mycampusapp.util.RUN_DAILY
import com.example.android.mycampusapp.util.TimePickerValues
import com.example.android.mycampusapp.util.initializeTimetableCalendar
import com.google.firebase.firestore.DocumentReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

class WednesdayInputViewModel(
    private val courseDocument: DocumentReference,
    private val wednesdayClass: WednesdayClass?,
    private val app: Application
) : AndroidViewModel(app) {

    private val wednesdayFirestore = courseDocument.collection("wednesday")

    private val wednesdayClassExtra = MutableLiveData<WednesdayClass>()
    private val _navigator = MutableLiveData<Event<Unit>>()
    val navigator: LiveData<Event<Unit>>
        get() = _navigator
    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + job)

    private val _timeSetByTimePicker = TimePickerValues.timeSetByTimePicker
    val timeSetByTimePicker: LiveData<String>
        get() = _timeSetByTimePicker

    val timePickerClockPosition = MutableLiveData<Event<List<Int>>>()

    val textBoxSubject = MutableLiveData<String>(wednesdayClass?.subject)
    val textBoxTime = MutableLiveData<String>(wednesdayClass?.time)
    val id = MutableLiveData<String>(wednesdayClass?.id)

    private val cal: Calendar = Calendar.getInstance()
    private val hour = cal.get(Calendar.HOUR_OF_DAY)
    private val minute = cal.get(Calendar.MINUTE)
    private val day = cal.get(Calendar.DAY_OF_WEEK)
    private val wednesday = Calendar.WEDNESDAY

    private val REQUEST_CODE = Random().nextInt(Integer.MAX_VALUE)
    private val minuteLong = 60_000L
    private val hourLong = minuteLong * 60
    private val dayLong = hourLong * 24
    private val priorAlertTime = minuteLong * 5

    private val _snackbarText = MutableLiveData<Event<Int>>()
    val snackbarText: LiveData<Event<Int>>
        get() = _snackbarText

    // Can only be tested through espresso
    fun save() {
        val currentSubject: String? = textBoxSubject.value
        val currentTime: String? = textBoxTime.value
        if (currentSubject.isNullOrBlank() || currentTime.isNullOrBlank()) {
            _snackbarText.value = Event(R.string.empty_message)
            return
        } else if (wednesdayClassIsNull()) {
            val wednesdayClass =
                WednesdayClass(
                    subject = currentSubject,
                    time = currentTime
                )
            addFirestoreData(wednesdayClass)
            wednesdayClassExtra.value = wednesdayClass
            _snackbarText.value = Event(R.string.wednesday_saved)
            startTimer()
            navigateToTimetable()

        } else if (!wednesdayClassIsNull()) {
            val wednesdayClass =
                WednesdayClass(
                    id.value!!,
                    currentSubject,
                    currentTime
                )
            addFirestoreData(wednesdayClass)
            wednesdayClassExtra.value = wednesdayClass
            _snackbarText.value = Event(R.string.wednesday_updated)
            startTimer()
            navigateToTimetable()
        }
    }

    private fun addFirestoreData(wednesdayClass: WednesdayClass) {
        wednesdayFirestore.document(wednesdayClass.id).set(wednesdayClass).addOnSuccessListener {
            Timber.i("Data was added successfully")
        }.addOnFailureListener { exception ->
            Timber.i("Data failed to upload because of $exception")
        }
    }

    fun navigateToTimetable() {
        _navigator.value = Event(Unit)
    }


    fun wednesdayClassIsNull(): Boolean {
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

    private fun startTimer() {
        val time = SimpleDateFormat("hh:mm a", Locale.US).parse(textBoxTime.value!!)
        Timber.i("the time in the textbox is $time")
        val calendar = Calendar.getInstance()
        calendar.time = time!!
        initializeTimetableCalendar(calendar)
        Timber.i("the time set is ${calendar.time}")

        if (calendar.timeInMillis <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        val triggerTime = calendar.timeInMillis

        val notifyIntent = Intent(app, WednesdayClassReceiver::class.java).apply {
            putExtra("wednesdaySubject", wednesdayClassExtra.value?.subject)
            putExtra("wednesdayTime", wednesdayClassExtra.value?.time)
        }
        val notifyPendingIntent = PendingIntent.getBroadcast(
            getApplication(),
            REQUEST_CODE,
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
