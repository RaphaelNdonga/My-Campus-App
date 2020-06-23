package com.example.android.mycampusapp.classInput

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.android.mycampusapp.Event
import com.example.android.mycampusapp.data.MondayClass
import com.example.android.mycampusapp.data.timetable.local.TimetableDao
import com.example.android.mycampusapp.data.timetable.local.TimetableDataSource
import com.example.android.mycampusapp.data.timetable.local.TimetableLocalDataSource
import kotlinx.coroutines.*

class ClassInputViewModel(private val timetableRepository: TimetableLocalDataSource) : ViewModel() {

    enum class Days { MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY }

    val job = Job()
    val uiScope = CoroutineScope(Dispatchers.Main + job)

    val subject = MutableLiveData<String>()

    val time = MutableLiveData<String>()

    private val _status = MutableLiveData<Days>()
    val status: LiveData<Days>
        get() = _status

    //TODO change this to Event<int> once you're done testing stuff
    private val _snackbarText = MutableLiveData<Event<String>>()
    val snackbarText: LiveData<Event<String>>
        get() = _snackbarText


    fun save() {
        val currentSubject: String? = subject.value
        val currentTime: String? = time.value
        if (currentSubject.isNullOrBlank() || currentTime.isNullOrBlank()) {
            Log.i("ClassInputViewModel", "please fill in all the blanks")
            return
        } else {

        }
    }

    override fun onCleared() {
        super.onCleared()
        timetableRepository.job.cancel()
    }
}