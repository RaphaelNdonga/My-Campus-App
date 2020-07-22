package com.example.android.mycampusapp.display.days.sunday

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.android.mycampusapp.util.Event
import com.example.android.mycampusapp.data.SundayClass
import com.example.android.mycampusapp.data.timetable.local.TimetableDataSource
import com.example.android.mycampusapp.util.TimePickerValues
import kotlinx.coroutines.*
import timber.log.Timber

class SundayViewModel(private val repository: TimetableDataSource) : ViewModel() {

    val sundayClasses = repository.observeAllSundayClasses()

    private val _addNewClass = MutableLiveData<Event<Unit>>()
    val addNewClass: LiveData<Event<Unit>> = _addNewClass

    private val _openSundayClass = MutableLiveData<Event<SundayClass>>()
    val openSundayClass: LiveData<Event<SundayClass>>
        get() = _openSundayClass

    private val _deleteSundayClasses = MutableLiveData<Event<Unit>>()
    val deleteSundayClasses: LiveData<Event<Unit>>
        get() = _deleteSundayClasses

    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + job)

    init {
        Timber.i("sunday view model initialized")
    }
    fun displaySundayClassDetails(sundayClass: SundayClass) {
        _openSundayClass.value =
            Event(sundayClass)

        //Ensures that the timepickervalues object is updated before passing the arguments to sundayInput fragment
        TimePickerValues.timeSetByTimePicker.value = sundayClass.time
    }

    fun addNewClass() {
        _addNewClass.value = Event(Unit)
        Timber.i("Should be navigating to sunday input")
        TimePickerValues.timeSetByTimePicker.value = ""
    }

    fun deleteList(list: List<SundayClass?>) = uiScope.launch {
        list.forEach { sundayClass->
            if (sundayClass != null) {
                repository.deleteSundayClass(sundayClass)
            }
        }
    }

    fun deleteIconPressed() {
        _deleteSundayClasses.value =
            Event(Unit)
    }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }
}