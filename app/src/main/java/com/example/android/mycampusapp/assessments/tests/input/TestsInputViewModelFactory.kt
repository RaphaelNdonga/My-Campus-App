package com.example.android.mycampusapp.assessments.tests.input

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.mycampusapp.data.Assessment
import com.google.firebase.firestore.CollectionReference

@Suppress("UNCHECKED_CAST")
class TestsInputViewModelFactory(
    private val assessment: Assessment?,
    private val testCollection: CollectionReference,
    private val application: Application
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return TestsInputViewModel(assessment, testCollection, application) as T
    }
}