package com.example.android.mycampusapp.assessments

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.mycampusapp.data.Assessment
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.functions.FirebaseFunctions

class AssessmentInputViewModelFactory(
    private val assignmentsCollection: CollectionReference,
    private val assignment: Assessment?,
    private val functions: FirebaseFunctions,
    private val application: Application
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return AssessmentInputViewModel(
            assignmentsCollection,
            assignment,
            functions,
            application
        ) as T
    }
}