package com.example.android.mycampusapp.assessments.assignments.display

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.android.mycampusapp.data.Assignment
import com.example.android.mycampusapp.data.DataStatus
import com.example.android.mycampusapp.util.Event
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import timber.log.Timber

class AssignmentsViewModel(private val assignmentsFirestore: CollectionReference) : ViewModel() {
    private val _assignments = MutableLiveData<List<Assignment>>()
    val assignments: LiveData<List<Assignment>>
        get() = _assignments

    private val _inputNavigator = MutableLiveData<Event<Unit>>()
    val inputNavigator: LiveData<Event<Unit>>
        get() = _inputNavigator

    private val _openDetails = MutableLiveData<Event<Assignment>>()
    val openDetails: LiveData<Event<Assignment>>
        get() = _openDetails

    private val _deleteAssignments = MutableLiveData<Event<Unit>>()
    val deleteAssignments: LiveData<Event<Unit>>
        get() = _deleteAssignments

    private val _status = MutableLiveData<DataStatus>()
    val status: LiveData<DataStatus>
        get() = _status

    private val _hasPendingWrites = MutableLiveData<Event<Unit>>()
    val hasPendingWrites: LiveData<Event<Unit>>
        get() = _hasPendingWrites


    fun addSnapshotListener(): ListenerRegistration {
        return assignmentsFirestore
            .orderBy("year", Query.Direction.ASCENDING)
            .orderBy("month", Query.Direction.ASCENDING)
            .orderBy(
                "day", Query.Direction
                    .ASCENDING
            )
            .addSnapshotListener { querySnapshot, firebaseException ->
                querySnapshot?.let {
                    val mutableList = mutableListOf<Assignment>()

                    querySnapshot.documents.forEach { document ->
                        val assignment = document.toObject(Assignment::class.java)
                        assignment?.let { mutableList.add(it) }
                    }
                    updateData(mutableList)
                }
                if (firebaseException != null) {
                    Timber.i("Got an exception $firebaseException ")
                }
            }
    }

    private fun updateData(mutableList: List<Assignment>) {
        _assignments.value = mutableList
        checkDataStatus()
    }

    private fun checkDataStatus() {
        val assignments = assignments.value
        try {
            if (assignments.isNullOrEmpty()) {
                throw NullPointerException()
            }
            _status.value = DataStatus.NOT_EMPTY
        } catch (npe: NullPointerException) {
            _status.value = DataStatus.EMPTY
        }
    }

    fun navigateToInput() {
        _inputNavigator.value = Event(Unit)
        Timber.i("navigate to input")
    }

    fun displayDetails(assignment: Assignment) {
        _openDetails.value = Event(assignment)
    }

    fun deleteIconPressed() {
        _deleteAssignments.value = Event(Unit)
    }

    fun deleteList(list: List<Assignment>) {
        list.forEach { assignment ->
            assignmentsFirestore.document(assignment.id).delete()
        }
        checkDataStatus()
    }

}