package com.example.android.mycampusapp.classInput

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.android.mycampusapp.Event
import com.example.android.mycampusapp.R
import com.example.android.mycampusapp.data.MondayClass
import com.example.android.mycampusapp.data.timetable.local.TimetableDataSource
import com.example.android.mycampusapp.util.TimePickerValues
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*

class ClassInputViewModel(
    private val timetableRepository: TimetableDataSource,
    private val mondayClass: MondayClass?
) : ViewModel() {


    private val _navigator = MutableLiveData<Event<Unit>>()
    val navigator: LiveData<Event<Unit>>
        get() = _navigator
    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + job)

    private val _hourMinuteSet = TimePickerValues.hourMinuteSet
    val hourMinuteSet: LiveData<String>
        get() = _hourMinuteSet

    val hourMinuteDisplay = MutableLiveData<Event<List<Int>>>()

    val subject = MutableLiveData<String>(mondayClass?.subject)
    val time = MutableLiveData<String>(mondayClass?.time)
    val id = MutableLiveData<Long>(mondayClass?.id)

    private val cal: Calendar = Calendar.getInstance()
    private val hour = cal.get(Calendar.HOUR_OF_DAY)
    private val minute = cal.get(Calendar.MINUTE)

    private val _snackbarText = MutableLiveData<Event<Int>>()
    val snackbarText: LiveData<Event<Int>>
        get() = _snackbarText

    // Can only be tested through espresso
    fun save() {
        val currentSubject: String? = subject.value
        val currentTime: String? = time.value
        if (currentSubject.isNullOrBlank() || currentTime.isNullOrBlank()) {
            _snackbarText.value = Event(R.string.empty_message)
            return
        } else if (mondayClassIsNull()) {
            addMondayClass(currentSubject, currentTime)
            _snackbarText.value = Event(R.string.monday_saved)
            navigateToTimetable()

        } else if (!mondayClassIsNull()) {
            val mondayClass = MondayClass(id.value!!, currentSubject, currentTime)
            updateMondayClass(mondayClass)
            _snackbarText.value = Event(R.string.monday_updated)
            navigateToTimetable()
        }
    }

    fun updateMondayClass(mondayClass: MondayClass) = uiScope.launch {
        timetableRepository.updateMondayClass(mondayClass)
    }

    fun navigateToTimetable() {
        _navigator.value = Event(Unit)
    }

    fun addMondayClass(subject: String, time: String) = uiScope.launch {
        timetableRepository.addMondayClass(MondayClass(subject = subject, time = time))
    }

    fun mondayClassIsNull():Boolean {
        if (mondayClass == null) {
            return true
        }
        return false
    }

    fun setDialogBoxTime() {
        if (mondayClassIsNull()) {
            hourMinuteDisplay.value = Event(listOf(hour, minute))
        } else {
            val time = SimpleDateFormat("HH:mm", Locale.US).parse(mondayClass?.time!!)
            val calendar = Calendar.getInstance()
            calendar.time = time!!
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minutes = calendar.get(Calendar.MINUTE)
            hourMinuteDisplay.value = Event(listOf(hour, minutes))
        }
    }
}