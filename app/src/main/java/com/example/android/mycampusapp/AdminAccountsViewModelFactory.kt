package com.example.android.mycampusapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.firestore.CollectionReference

@Suppress("UNCHECKED_CAST")
class AdminAccountsViewModelFactory(
    private val adminCollection: CollectionReference,
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return AdminAccountsViewModel(adminCollection) as T
    }
}