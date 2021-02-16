package com.example.android.mycampusapp.assessments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.android.mycampusapp.data.Assessment
import com.example.android.mycampusapp.data.DataStatus
import com.example.android.mycampusapp.util.Event
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import timber.log.Timber

class AssessmentsViewModel(private val assessmentsFirestore: CollectionReference) : ViewModel() {
    private val _assessments = MutableLiveData<List<Assessment>>()
    val assessments: LiveData<List<Assessment>>
        get() = _assessments

    private val _inputNavigator = MutableLiveData<Event<Unit>>()
    val inputNavigator: LiveData<Event<Unit>>
        get() = _inputNavigator

    private val _openDetails = MutableLiveData<Event<Assessment>>()
    val openDetails: LiveData<Event<Assessment>>
        get() = _openDetails

    private val _deleteAssessments = MutableLiveData<Event<Unit>>()
    val deleteAssignments: LiveData<Event<Unit>>
        get() = _deleteAssessments

    private val _status = MutableLiveData<DataStatus>()
    val status: LiveData<DataStatus>
        get() = _status

    private val _hasPendingWrites = MutableLiveData<Event<Unit>>()
    val hasPendingWrites: LiveData<Event<Unit>>
        get() = _hasPendingWrites


    fun addSnapshotListener(): ListenerRegistration {
        _status.value = DataStatus.LOADING
        return assessmentsFirestore
            .orderBy("year", Query.Direction.ASCENDING)
            .orderBy("month", Query.Direction.ASCENDING)
            .orderBy("day", Query.Direction.ASCENDING)
            .orderBy("hour",Query.Direction.ASCENDING)
            .orderBy("minute",Query.Direction.ASCENDING)
            .addSnapshotListener { querySnapshot, firebaseException ->
                querySnapshot?.let {
                    val mutableList = mutableListOf<Assessment>()

                    querySnapshot.documents.forEach { document ->
                        val assessment = document.toObject(Assessment::class.java)
                        assessment?.let { mutableList.add(it) }
                    }
                    updateData(mutableList)
                }
                if (firebaseException != null) {
                    Timber.i("Got an exception $firebaseException ")
                }
            }
    }

    private fun updateData(mutableList: List<Assessment>) {
        _assessments.value = mutableList
        checkDataStatus()
    }

    private fun checkDataStatus() {
        val assessments = _assessments.value
        try {
            if (assessments.isNullOrEmpty()) {
                Timber.i("The data status should be empty")
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

    fun displayDetails(assessment: Assessment) {
        _openDetails.value = Event(assessment)
    }

    fun deleteIconPressed() {
        _deleteAssessments.value = Event(Unit)
    }

    fun deleteList(list: List<Assessment?>) {
        list.forEach { assessment ->
            if (assessment != null)
                assessmentsFirestore.document(assessment.id).delete()
        }
        checkDataStatus()
    }

}