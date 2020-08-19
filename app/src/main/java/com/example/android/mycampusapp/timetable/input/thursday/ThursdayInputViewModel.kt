package com.example.android.mycampusapp.timetable.input.thursday

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import androidx.core.app.AlarmManagerCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.android.mycampusapp.R
import com.example.android.mycampusapp.timetable.data.ThursdayClass
import com.example.android.mycampusapp.timetable.receiver.ThursdayClassReceiver
import com.example.android.mycampusapp.util.Event
import com.example.android.mycampusapp.util.TimePickerValues
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

class ThursdayInputViewModel(
    private val firestore: FirebaseFirestore,
    private val thursdayClass: ThursdayClass?,
    private val app: Application
) : AndroidViewModel(app) {

    private val thursdayFirestore = firestore.collection("thursday")
    private val thursdayClassExtra = MutableLiveData<ThursdayClass>()
    private val _navigator = MutableLiveData<Event<Unit>>()
    val navigator: LiveData<Event<Unit>>
        get() = _navigator
    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + job)

    private val _timeSetByTimePicker = TimePickerValues.timeSetByTimePicker
    val timeSetByTimePicker: LiveData<String>
        get() = _timeSetByTimePicker

    val timePickerClockPosition = MutableLiveData<Event<List<Int>>>()

    val textBoxSubject = MutableLiveData<String>(thursdayClass?.subject)
    val textBoxTime = MutableLiveData<String>(thursdayClass?.time)
    val id = MutableLiveData<String>(thursdayClass?.id)

    private val cal: Calendar = Calendar.getInstance()
    private val hour = cal.get(Calendar.HOUR_OF_DAY)
    private val minute = cal.get(Calendar.MINUTE)
    private val day = cal.get(Calendar.DAY_OF_WEEK)
    private val thursday = Calendar.THURSDAY

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
        } else if (thursdayClassIsNull()) {
            val thursdayClass =
                ThursdayClass(
                    subject = currentSubject,
                    time = currentTime
                )
            addFirestoreData(thursdayClass)
            thursdayClassExtra.value = thursdayClass
            _snackbarText.value = Event(R.string.thursday_saved)
            startTimer()
            navigateToTimetable()

        } else if (!thursdayClassIsNull()) {
            val thursdayClass =
                ThursdayClass(
                    id.value!!,
                    currentSubject,
                    currentTime
                )
            addFirestoreData(thursdayClass)
            thursdayClassExtra.value = thursdayClass
            _snackbarText.value = Event(R.string.thursday_updated)
            startTimer()
            navigateToTimetable()
        }
    }

    private fun addFirestoreData(thursdayClass: ThursdayClass){
        thursdayFirestore.document(thursdayClass.id).set(thursdayClass).addOnSuccessListener {
            Timber.i("Data was added successfully")
        }.addOnFailureListener { exception: Exception ->
            Timber.i("Data failed to upload because of $exception")
        }
    }

    fun navigateToTimetable() {
        _navigator.value = Event(Unit)
    }


    fun thursdayClassIsNull(): Boolean {
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

    private fun startTimer() {

        val time = SimpleDateFormat("hh:mm a", Locale.US).parse(textBoxTime.value!!)
        val calendar = Calendar.getInstance()
        calendar.time = time!!
        val hourSet = calendar.get(Calendar.HOUR_OF_DAY)
        val minuteSet = calendar.get(Calendar.MINUTE)
        val hourDifference = hourSet.minus(hour)
        val minuteDifference = minuteSet.minus(minute)
        val totalDifference = (hourDifference * 60).plus(minuteDifference)
        var dayDifference = day.minus(thursday)
        if (dayDifference < 0 || (dayDifference == 0 && totalDifference < 0)) {
            dayDifference += 7
        }

        val dayDifferenceLong = dayDifference * dayLong
        val hourDifferenceLong = hourDifference * hourLong
        val minuteDifferenceLong = minuteDifference * minuteLong

        val differenceWithPresent = hourDifferenceLong + minuteDifferenceLong + dayDifferenceLong
        val triggerTime = SystemClock.elapsedRealtime() + differenceWithPresent

        val notifyIntent = Intent(app, ThursdayClassReceiver::class.java).apply {
            putExtra("thursdaySubject", thursdayClassExtra.value?.subject)
            putExtra("thursdayTime",thursdayClassExtra.value?.time)
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
