package com.example.android.mycampusapp.assessments.tests.display

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.firestore.CollectionReference

@Suppress("UNCHECKED_CAST")
class TestsViewModelFactory(private val testsCollection:CollectionReference):ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return TestsViewModel(testsCollection) as T
    }
}