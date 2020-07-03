package com.example.android.mycampusapp.classInput

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.android.mycampusapp.Event
import com.example.android.mycampusapp.R
import com.example.android.mycampusapp.data.MondayClass
import com.example.android.mycampusapp.data.timetable.local.TimetableDataSource
import kotlinx.coroutines.*

class ClassInputViewModel(
    private val timetableRepository: TimetableDataSource,
    private val mondayClass: MondayClass?
) : ViewModel() {
    init {
        checkMondayClassIsNull()
    }

    enum class Days { MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY }

    private val _navigator = MutableLiveData<Event<Unit>>()
    val navigator: LiveData<Event<Unit>>
        get() = _navigator
    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + job)

    var mondayClassIsNull: Boolean? = null

    val subject = MutableLiveData<String>(mondayClass?.subject)

    val time = MutableLiveData<String>(mondayClass?.time)

    val id = MutableLiveData<Long>(mondayClass?.id)

    private val _status = MutableLiveData<Days>()
    val status: LiveData<Days>
        get() = _status

    private val _snackbarText = MutableLiveData<Event<Int>>()
    val snackbarText: LiveData<Event<Int>>
        get() = _snackbarText


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

    private fun updateMondayClass(mondayClass:MondayClass) = uiScope.launch {
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
}