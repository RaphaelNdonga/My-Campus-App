package com.example.android.mycampusapp.assessments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.firestore.CollectionReference

class AssessmentsViewModelFactory(private val collection:CollectionReference):ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return AssessmentsViewModel(collection) as T
    }
}