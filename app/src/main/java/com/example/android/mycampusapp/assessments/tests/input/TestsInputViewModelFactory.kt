package com.example.android.mycampusapp.assessments.tests.input

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.mycampusapp.data.Test
import com.google.firebase.firestore.CollectionReference

@Suppress("UNCHECKED_CAST")
class TestsInputViewModelFactory(private val test:Test?,private val testCollection:CollectionReference):ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return TestsInputViewModel(test,testCollection) as T
    }
}