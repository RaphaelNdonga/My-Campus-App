package com.example.android.mycampusapp.assessments.tests.display

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.android.mycampusapp.data.Assessment
import com.example.android.mycampusapp.data.DataStatus
import com.example.android.mycampusapp.util.Event
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ListenerRegistration
import timber.log.Timber

class TestsViewModel(private val testsCollection: CollectionReference) : ViewModel() {
    private val _tests = MutableLiveData<List<Assessment>>()
    val tests:LiveData<List<Assessment>>
        get() = _tests

    private val _inputNavigator = MutableLiveData<Event<Unit>>()
    val inputNavigator: LiveData<Event<Unit>>
        get() = _inputNavigator

    private val _openDetails = MutableLiveData<Event<Assessment>>()
    val openDetails:LiveData<Event<Assessment>>
        get() = _openDetails

    private val _deleteAssignments = MutableLiveData<Event<Unit>>()
    val deleteAssignments:LiveData<Event<Unit>>
        get() = _deleteAssignments

    private val _status = MutableLiveData<DataStatus>()
    val status:LiveData<DataStatus>
        get() = _status

    fun navigateToInput() {
        initializeEvent(_inputNavigator)
    }

    private fun initializeEvent(mutableLiveData: MutableLiveData<Event<Unit>>) {
        mutableLiveData.value = Event(Unit)
    }

    fun addSnapshotListener(): ListenerRegistration {
        _status.value = DataStatus.LOADING
        return testsCollection.addSnapshotListener { querySnapshot, error ->
            val mutableList = mutableListOf<Assessment>()
            querySnapshot?.documents?.forEach { document ->
                val test = document.toObject(Assessment::class.java)
                test?.let { mutableList.add(it) }
            }
            _tests.value = mutableList
            checkDataStatus()

            if(error!=null){
                Timber.i("Got an exception $error")
            }
        }
    }

    private fun checkDataStatus() {
        val tests = _tests.value
        try{
            if(tests.isNullOrEmpty()){
                throw NullPointerException()
            }
            _status.value = DataStatus.NOT_EMPTY
        }catch (npe:NullPointerException){
            _status.value = DataStatus.EMPTY
        }
    }

    fun displayDetails(assessment: Assessment) {
        _openDetails.value = Event(assessment)
    }

    fun deleteIconPressed(){
        _deleteAssignments.value = Event(Unit)
    }

    fun deleteList(list:List<Assessment>){
        list.forEach { test->
            testsCollection.document(test.id).delete()
        }
        checkDataStatus()
    }
}