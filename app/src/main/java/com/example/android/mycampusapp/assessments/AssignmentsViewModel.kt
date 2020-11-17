package com.example.android.mycampusapp.assessments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.android.mycampusapp.data.Assignment
import com.example.android.mycampusapp.util.Event
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ListenerRegistration
import timber.log.Timber

class AssignmentsViewModel(private val assignmentsFirestore: CollectionReference) : ViewModel() {
    private val _assignments = MutableLiveData<List<Assignment>>()
    val assignments: LiveData<List<Assignment>>
        get() = _assignments

    private val _inputNavigator = MutableLiveData<Event<Unit>>()
    val inputNavigator:LiveData<Event<Unit>>
        get() = _inputNavigator


    fun addSnapshotListener():ListenerRegistration {
        return assignmentsFirestore.addSnapshotListener { querySnapshot, error ->
            val mutableList = mutableListOf<Assignment>()
            querySnapshot?.documents?.forEach { document ->
                val assignment = document.toObject(Assignment::class.java)
                assignment?.let { mutableList.add(it) }
            }
            _assignments.value = mutableList
        }
    }
    fun navigateToInput(){
        _inputNavigator.value = Event(Unit)
        Timber.i("navigate to input")
    }

}