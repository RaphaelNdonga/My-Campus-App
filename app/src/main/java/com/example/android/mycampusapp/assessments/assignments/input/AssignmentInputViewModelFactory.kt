package com.example.android.mycampusapp.assessments.assignments.input

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.mycampusapp.data.Assignment
import com.google.firebase.firestore.CollectionReference

class AssignmentInputViewModelFactory(private val assignmentsCollection:CollectionReference,private val assignment:Assignment?):ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return AssignmentInputViewModel(assignmentsCollection,assignment) as T
    }
}