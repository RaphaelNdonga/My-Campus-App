package com.example.android.mycampusapp.assessments.assignments.input

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.android.mycampusapp.R
import com.example.android.mycampusapp.data.Assignment
import com.example.android.mycampusapp.util.Event
import com.google.firebase.firestore.CollectionReference

class AssignmentInputViewModel(
    private val assignmentsCollection: CollectionReference,
    private val assignment: Assignment?
) : ViewModel() {
    val textBoxSubject = MutableLiveData<String>(assignment?.subject)
    private val date = "${assignment?.day}/${assignment?.month}/${assignment?.year}"
    val textBoxDate = MutableLiveData<String>(date)
    val setDay = MutableLiveData(assignment?.day)
    val setMonth = MutableLiveData(assignment?.month)
    val setYear = MutableLiveData(assignment?.year)

    private val _snackBarEvent = MutableLiveData<Event<Int>>()
    val snackBarEvent: LiveData<Event<Int>>
        get() = _snackBarEvent

    private val _displayNavigator = MutableLiveData<Event<Unit>>()
    val displayNavigator: LiveData<Event<Unit>>
        get() = _displayNavigator

    fun save() {
        val currentSubject = textBoxSubject.value
        val currentDay = setDay.value
        val currentMonth = setMonth.value
        val currentYear = setYear.value

        if (currentDay == null || currentMonth == null || currentYear == null || currentSubject.isNullOrBlank()) {
            _snackBarEvent.value = Event(R.string.empty_message)
            return
        }
        val currentAssignment = Assignment(subject = currentSubject, day =currentDay,month = currentMonth,year = currentYear)
        addFirestoreData(currentAssignment)
        navigateToDisplay()
    }

    private fun addFirestoreData(currentAssignment: Assignment) {
        assignmentsCollection.document(currentAssignment.id).set(currentAssignment)
    }

    private fun navigateToDisplay() {
        _displayNavigator.value = Event(Unit)
    }
}