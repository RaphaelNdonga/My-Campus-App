package com.example.android.mycampusapp.acmanagement

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.firestore.CollectionReference

class AdminsViewModelFactory(private val adminsCollection: CollectionReference) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return AdminsViewModel(adminsCollection) as T
    }
}