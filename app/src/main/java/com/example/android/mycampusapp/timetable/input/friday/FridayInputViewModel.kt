package com.example.android.mycampusapp.timetable.input.friday

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.android.mycampusapp.R
import com.example.android.mycampusapp.timetable.data.FridayClass
import com.example.android.mycampusapp.timetable.receiver.FridayClassReceiver
import com.example.android.mycampusapp.util.Event
import com.example.android.mycampusapp.util.RUN_DAILY
import com.example.android.mycampusapp.util.TimePickerValues
import com.example.android.mycampusapp.util.initializeTimetableCalendar
import com.google.firebase.firestore.DocumentReference
import timber.log.Timber
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class FridayInputViewModel(
    private val fridayClass: FridayClass?,
    private val app: Application,
    courseDocument: DocumentReference
) : AndroidViewModel(app) {

    private val fridayFirestore = courseDocument.collection("friday")
    private val fridayClassExtra = MutableLiveData<FridayClass>()
    private val _navigator = MutableLiveData<Event<Unit>>()
    val navigator: LiveData<Event<Unit>>
        get() = _navigator

    private val _timeSetByTimePicker = TimePickerValues.timeSetByTimePicker
    val timeSetByTimePicker: LiveData<String>
        get() = _timeSetByTimePicker

    val timePickerClockPosition = MutableLiveData<Event<List<Int>>>()

    val textBoxSubject = MutableLiveData<String>(fridayClass?.subject)
    val textBoxTime = MutableLiveData<String>(fridayClass?.time)
    val id = MutableLiveData<String>(fridayClass?.id)

    private val cal: Calendar = Calendar.getInstance()
    private val hour = cal.get(Calendar.HOUR_OF_DAY)
    private val minute = cal.get(Calendar.MINUTE)
    private val day = cal.get(Calendar.DAY_OF_WEEK)
    private val friday = Calendar.FRIDAY

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
        } else if (fridayClassIsNull()) {
            val fridayClass =
                FridayClass(
                    subject = currentSubject,
                    time = currentTime
                )
            addFirestoreData(fridayClass)
            fridayClassExtra.value = fridayClass
            _snackbarText.value = Event(R.string.friday_saved)
            startTimer()
            navigateToTimetable()

        } else if (!fridayClassIsNull()) {
            val fridayClass =
                FridayClass(
                    id.value!!,
                    currentSubject,
                    currentTime
                )
            addFirestoreData(fridayClass)
            fridayClassExtra.value = fridayClass
            _snackbarText.value = Event(R.string.friday_updated)
            startTimer()
            navigateToTimetable()
        }
    }

    private fun addFirestoreData(fridayClass: FridayClass) {
        fridayFirestore.document(fridayClass.id).set(fridayClass).addOnSuccessListener {
            Timber.i("Data was added successfully")
        }.addOnFailureListener { exception ->
            Timber.i("Data failed to add because of $exception")
        }
    }

    private fun navigateToTimetable() {
        _navigator.value = Event(Unit)
    }


    private fun fridayClassIsNull(): Boolean {
        if (fridayClass == null) {
            return true
        }
        return false
    }

    fun setTimePickerClockPosition() {
        if (fridayClassIsNull()) {
            timePickerClockPosition.value =
                Event(listOf(hour, minute))
            return
        }
        val time = SimpleDateFormat("HH:mm", Locale.US).parse(textBoxTime.value!!)
        val calendar = Calendar.getInstance()
        calendar.time = time!!
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minutes = calendar.get(Calendar.MINUTE)
        timePickerClockPosition.value = Event(listOf(hour, minutes))
    }


    private fun startTimer() {
        val time = try {
            SimpleDateFormat("hh:mm a", Locale.US).parse(textBoxTime.value!!)
        }catch (parseException:ParseException){
            Timber.i("The exception caught is $parseException")
            SimpleDateFormat("HH:mm", Locale.UK).parse(textBoxTime.value!!)
        }
        val calendar = Calendar.getInstance()
        calendar.time = time!!
        initializeTimetableCalendar(calendar)

        if (calendar.timeInMillis <= System.currentTimeMillis()) {
            calendar.set(Calendar.DAY_OF_MONTH,calendar.get(Calendar.DAY_OF_MONTH + 1))
        }
        val triggerTime = calendar.timeInMillis

        val notifyIntent = Intent(app, FridayClassReceiver::class.java).apply {
            putExtra("fridaySubject", fridayClassExtra.value?.subject)
            putExtra("fridayTime", fridayClassExtra.value?.time)
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
