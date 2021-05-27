package com.mycampusapp.acmanagement

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.functions.FirebaseFunctions

class AdminsViewModelFactory(
    private val adminsCollection: CollectionReference,
    private val functions: FirebaseFunctions
) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return AdminsViewModel(adminsCollection, functions) as T
    }
}