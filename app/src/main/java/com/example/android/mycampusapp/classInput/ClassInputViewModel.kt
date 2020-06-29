package com.example.android.mycampusapp.classInput

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.android.mycampusapp.Event
import com.example.android.mycampusapp.R
import com.example.android.mycampusapp.data.MondayClass
import com.example.android.mycampusapp.data.timetable.local.TimetableDataSource
import kotlinx.coroutines.*

class ClassInputViewModel(private val timetableRepository: TimetableDataSource) : ViewModel() {

    enum class Days { MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY }

    private val _navigator = MutableLiveData<Event<Unit>>()
    val navigator:LiveData<Event<Unit>>
        get() = _navigator
    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + job)

    val subject = MutableLiveData<String>()

    val time = MutableLiveData<String>()

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
        } else {
            //TODO add all the other weekday classes in a when statement
            addMondayClass(currentSubject,currentTime)
            navigateToTimetable()
        }
    }

    fun navigateToTimetable() {
        _navigator.value = Event(Unit)
    }

    fun addMondayClass(subject: String, time: String) = uiScope.launch {
        timetableRepository.addMondayClass(MondayClass(subject = subject,time = time))
        _snackbarText.value = Event(R.string.monday_saved)
    }
    //TODO add all the other weekday classes and their functions
}