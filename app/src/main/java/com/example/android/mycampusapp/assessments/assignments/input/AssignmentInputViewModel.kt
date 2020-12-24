package com.example.android.mycampusapp.assessments.assignments.input

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.android.mycampusapp.R
import com.example.android.mycampusapp.data.Assignment
import com.example.android.mycampusapp.data.CustomDate
import com.example.android.mycampusapp.util.Event
import com.google.firebase.firestore.CollectionReference

class AssignmentInputViewModel(
    private val assignmentsCollection: CollectionReference,
    private val assignment: Assignment?
) : ViewModel() {
    // The vals below are connected to the xml file through a two-way databinding.
    val textBoxSubject = MutableLiveData<String>(assignment?.subject)

    //fills the textbox with values if editing is being done instead of adding a new class
    private val date =
        assignment?.let {
            "${assignment.day}/${assignment.month}/${assignment.year}"
        }
    val textBoxDate = MutableLiveData<String>(date)

    // acquire the date values and save them as integers
    val setDate = MutableLiveData<CustomDate>(assignment?.let {
        CustomDate(assignment.year, assignment.month, assignment.day)
    })

    private val _snackBarEvent = MutableLiveData<Event<Int>>()
    val snackBarEvent: LiveData<Event<Int>>
        get() = _snackBarEvent

    private val _displayNavigator = MutableLiveData<Event<Unit>>()
    val displayNavigator: LiveData<Event<Unit>>
        get() = _displayNavigator

    fun save() {
        val currentSubject = textBoxSubject.value
        val currentDate = setDate.value

        if (currentDate == null || currentSubject.isNullOrBlank()) {
            _snackBarEvent.value = Event(R.string.empty_message)
            return
        }
        if (assignment == null) {
            val currentAssignment = Assignment(
                subject = currentSubject,
                day = currentDate.day,
                month = currentDate.month,
                year = currentDate.year
            )
            addFirestoreData(currentAssignment)
            navigateToDisplay()
        } else {
            val currentAssignment = Assignment(
                id = assignment.id,
                subject = currentSubject,
                day = currentDate.day,
                month = currentDate.month,
                year = currentDate.year,
                alarmRequestCode = assignment.alarmRequestCode
            )
            addFirestoreData(currentAssignment)
            navigateToDisplay()
        }
    }

    private fun addFirestoreData(currentAssignment: Assignment) {
        assignmentsCollection.document(currentAssignment.id).set(currentAssignment)
    }

    private fun navigateToDisplay() {
        _displayNavigator.value = Event(Unit)
    }
}