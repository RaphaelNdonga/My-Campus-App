package com.example.android.mycampusapp

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.firestore.CollectionReference

@Suppress("UNCHECKED_CAST")
class MainActivityViewModelFactory(
    private val adminCollection: CollectionReference,
    private val app: Application
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MainActivityViewModel(adminCollection, app) as T
    }
}