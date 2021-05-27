package com.mycampusapp.assessments

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.functions.FirebaseFunctions
import com.mycampusapp.data.Assessment

class AssessmentInputViewModelFactory(
    private val assignmentsCollection: CollectionReference,
    private val assignment: Assessment?,
    private val functions: FirebaseFunctions,
    private val assessmentType: AssessmentType,
    private val application: Application
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return AssessmentInputViewModel(
            assignmentsCollection,
            assignment,
            functions,
            assessmentType,
            application
        ) as T
    }
}