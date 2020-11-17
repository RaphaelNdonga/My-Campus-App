package com.example.android.mycampusapp.assessments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.android.mycampusapp.R
import com.example.android.mycampusapp.data.Assignment
import com.example.android.mycampusapp.util.Event
import com.google.firebase.firestore.CollectionReference

class AssignmentInputViewModel(private val assignmentsCollection: CollectionReference) : ViewModel() {
    val textBoxSubject = MutableLiveData<String>()
    val textBoxDate = MutableLiveData<String>()

    private val _snackBarEvent = MutableLiveData<Event<Int>>()
    val snackBarEvent:LiveData<Event<Int>>
        get() = _snackBarEvent

    private val _displayNavigator = MutableLiveData<Event<Unit>>()
    val displayNavigator:LiveData<Event<Unit>>
        get() = _displayNavigator

    fun save(){
        val currentSubject = textBoxSubject.value
        val currentDate = textBoxDate.value

        if(currentDate.isNullOrBlank() || currentSubject.isNullOrBlank()){
            _snackBarEvent.value = Event(R.string.empty_message)
            return
        }
        val currentAssignment = Assignment(subject = currentSubject,date = currentDate)
        addFirestoreData(currentAssignment)
        navigateToDisplay()
    }

    private fun addFirestoreData(currentAssignment: Assignment) {
        assignmentsCollection.document(currentAssignment.id).set(currentAssignment)
    }
    private fun navigateToDisplay(){
        _displayNavigator.value = Event(Unit)
    }
}