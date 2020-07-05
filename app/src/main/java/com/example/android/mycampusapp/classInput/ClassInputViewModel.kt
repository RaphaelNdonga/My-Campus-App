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

class ClassInputViewModel(
    private val timetableRepository: TimetableDataSource,
    private val mondayClass: MondayClass?
) : ViewModel() {
    init {
        checkMondayClassIsNull()
    }

    private val _navigator = MutableLiveData<Event<Unit>>()
    val navigator: LiveData<Event<Unit>>
        get() = _navigator
    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + job)

    val hourMinuteSet:LiveData<List<Int>>
        get() = TimePickerValues.hourMinuteSet

    var mondayClassIsNull: Boolean? = null

    val hourMinuteDisplay = MutableLiveData<Event<List<Int>>>()

    val subject = MutableLiveData<String>(mondayClass?.subject)

    val time = MutableLiveData<String>(mondayClass?.time)

    val id = MutableLiveData<Long>(mondayClass?.id)

    private val _snackbarText = MutableLiveData<Event<Int>>()
    val snackbarText: LiveData<Event<Int>>
        get() = _snackbarText

// Can only be tested through espresso
    fun save() {
        val currentSubject: String? = subject.value
        val currentTime: String? = time.value
        if (currentSubject.isNullOrBlank() || currentTime.isNullOrBlank()) {
            _snackbarText.value = Event(R.string.empty_message)

        } else if (mondayClassIsNull!!) {
            addMondayClass(currentSubject, currentTime)
            navigateToTimetable()

        } else if (!mondayClassIsNull!!) {
            val mondayClass = MondayClass(id.value!!,currentSubject,currentTime)
            updateMondayClass(mondayClass)
            navigateToTimetable()

        }
    }

    fun updateMondayClass(mondayClass:MondayClass) = uiScope.launch {
        timetableRepository.updateMondayClass(mondayClass)
        _snackbarText.value = Event(R.string.monday_updated)
    }

    fun navigateToTimetable() {
        _navigator.value = Event(Unit)
    }

    fun addMondayClass(subject: String, time: String) = uiScope.launch {
        timetableRepository.addMondayClass(MondayClass(subject = subject, time = time))
        _snackbarText.value = Event(R.string.monday_saved)
    }

    fun checkMondayClassIsNull() {
        if (mondayClass == null) {
            mondayClassIsNull = true
            return
        }
        mondayClassIsNull = false
    }
    fun setTime(){
        hourMinuteDisplay.value = Event(listOf(10,30))
    }
}