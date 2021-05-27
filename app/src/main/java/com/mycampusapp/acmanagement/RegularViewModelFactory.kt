package com.mycampusapp.acmanagement

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.functions.FirebaseFunctions

class RegularViewModelFactory(
    private val regularCollection: CollectionReference,
    private val functions: FirebaseFunctions
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return RegularsViewModel(regularCollection, functions) as T
    }
}