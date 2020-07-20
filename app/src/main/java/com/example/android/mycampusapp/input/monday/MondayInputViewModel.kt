package com.example.android.mycampusapp.input.monday

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import androidx.core.app.AlarmManagerCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.android.mycampusapp.util.Event
import com.example.android.mycampusapp.R
import com.example.android.mycampusapp.data.MondayClass
import com.example.android.mycampusapp.data.timetable.local.TimetableDataSource
import com.example.android.mycampusapp.receiver.MondayClassReceiver
import com.example.android.mycampusapp.util.TimePickerValues
import kotlinx.coroutines.*
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

class MondayInputViewModel(
    private val timetableRepository: TimetableDataSource,
    private val mondayClass: MondayClass?,
    private val app: Application
) : AndroidViewModel(app) {

    private val mondayClassExtra = MutableLiveData<MondayClass>()
    private val _navigator = MutableLiveData<Event<Unit>>()
    val navigator: LiveData<Event<Unit>>
        get() = _navigator
    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + job)

    private val _timeSetByTimePicker = TimePickerValues.timeSetByTimePicker
    val timeSetByTimePicker: LiveData<String>
        get() = _timeSetByTimePicker

    val timePickerClockPosition = MutableLiveData<Event<List<Int>>>()

    val textBoxSubject = MutableLiveData<String>(mondayClass?.subject)
    val textBoxTime = MutableLiveData<String>(mondayClass?.time)
    val id = MutableLiveData<Long>(mondayClass?.id)

    private val cal: Calendar = Calendar.getInstance()
    private val hour = cal.get(Calendar.HOUR_OF_DAY)
    private val minute = cal.get(Calendar.MINUTE)
    private val day = cal.get(Calendar.DAY_OF_WEEK)
    private val monday = Calendar.MONDAY

    private val REQUEST_CODE = 0
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
        } else if (mondayClassIsNull()) {
            val mondayClass = MondayClass(subject = currentSubject,time = currentTime)
            addMondayClass(mondayClass)
            mondayClassExtra.value = mondayClass
            _snackbarText.value = Event(R.string.monday_updated)
            startTimer()
            navigateToTimetable()

        } else if (!mondayClassIsNull()) {
            val mondayClass = MondayClass(id.value!!, currentSubject, currentTime)
            updateMondayClass(mondayClass)
            mondayClassExtra.value = mondayClass
            _snackbarText.value = Event(R.string.monday_saved)
            startTimer()
            navigateToTimetable()
        }
    }

    fun updateMondayClass(mondayClass: MondayClass) = uiScope.launch {
        timetableRepository.updateMondayClass(mondayClass)
    }

    fun addMondayClass(mondayClass: MondayClass) = uiScope.launch {
        timetableRepository.addMondayClass(mondayClass)
    }

    fun navigateToTimetable() {
        _navigator.value = Event(Unit)
    }


    fun mondayClassIsNull(): Boolean {
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

    private fun startTimer() {
        val time = SimpleDateFormat("HH:mm", Locale.US).parse(textBoxTime.value!!)
        val calendar = Calendar.getInstance()
        calendar.time = time!!
        val hourSet = calendar.get(Calendar.HOUR_OF_DAY)
        val minuteSet = calendar.get(Calendar.MINUTE)
        val hourDifference = hourSet.minus(hour)
        val minuteDifference = minuteSet.minus(minute)
        val totalDifference = (hourDifference * 60).plus(minuteDifference)
        var dayDifference = day.minus(monday)
        if (dayDifference < 0 || (dayDifference == 0 && totalDifference < 0)) {
            dayDifference += 7
        }

        val dayDifferenceLong = dayDifference * dayLong
        val hourDifferenceLong = hourDifference * hourLong
        val minuteDifferenceLong = minuteDifference * minuteLong

        val differenceWithPresent = hourDifferenceLong + minuteDifferenceLong + dayDifferenceLong
        val triggerTime = SystemClock.elapsedRealtime() + 10_000L

        val notifyIntent = Intent(app, MondayClassReceiver::class.java).apply {
            putExtra("mondaySubject", mondayClassExtra.value?.subject)
            putExtra("mondayTime",mondayClassExtra.value?.time)
        }
        val notifyPendingIntent = PendingIntent.getBroadcast(
            getApplication(),
            REQUEST_CODE,
            notifyIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val alarmManager = app.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        AlarmManagerCompat.setExactAndAllowWhileIdle(
            alarmManager,
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            triggerTime,
            notifyPendingIntent
        )
    }
}
