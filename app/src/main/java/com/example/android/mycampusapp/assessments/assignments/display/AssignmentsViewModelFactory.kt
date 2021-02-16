package com.example.android.mycampusapp.assessments.assignments.display

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.mycampusapp.assessments.AssessmentsViewModel
import com.google.firebase.firestore.CollectionReference

class AssignmentsViewModelFactory(private val collection:CollectionReference):ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return AssessmentsViewModel(collection) as T
    }
}