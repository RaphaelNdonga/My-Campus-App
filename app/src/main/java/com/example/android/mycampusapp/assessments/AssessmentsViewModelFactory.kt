package com.example.android.mycampusapp.assessments

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.functions.FirebaseFunctions

class AssessmentsViewModelFactory(
    private val collection: CollectionReference,
    private val app: Application,
    private val functions: FirebaseFunctions
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return AssessmentsViewModel(collection, app, functions) as T
    }
}