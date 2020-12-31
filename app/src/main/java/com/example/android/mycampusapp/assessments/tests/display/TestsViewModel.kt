package com.example.android.mycampusapp.assessments.tests.display

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.android.mycampusapp.data.Test
import com.example.android.mycampusapp.util.Event
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ListenerRegistration

class TestsViewModel(private val testsCollection: CollectionReference) : ViewModel() {
    private val _tests = MutableLiveData<List<Test>>()
    val tests:LiveData<List<Test>>
        get() = _tests

    private val _inputNavigator = MutableLiveData<Event<Unit>>()
    val inputNavigator: LiveData<Event<Unit>>
        get() = _inputNavigator

    fun navigateToInput() {
        initializeEvent(_inputNavigator)
    }

    private fun initializeEvent(mutableLiveData: MutableLiveData<Event<Unit>>) {
        mutableLiveData.value = Event(Unit)
    }

    fun addSnapshotListener(): ListenerRegistration {
        return testsCollection.addSnapshotListener { querySnapshot, error ->
            val mutableList = mutableListOf<Test>()
            querySnapshot?.documents?.forEach { document ->
                val test = document.toObject(Test::class.java)
                test?.let { mutableList.add(it) }
            }
            _tests.value = mutableList
        }
    }
}