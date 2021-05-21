package com.example.android.mycampusapp.acmanagement

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.firestore.CollectionReference

class RegularViewModelFactory(private val regularCollection: CollectionReference) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return RegularsViewModel(regularCollection) as T
    }
}