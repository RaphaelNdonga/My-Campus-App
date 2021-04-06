package com.example.android.mycampusapp.acmanagement

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.messaging.FirebaseMessaging

@Suppress("UNCHECKED_CAST")
class ManageAccountViewModelFactory(
    private val app: Application,
    private val firebaseMessaging: FirebaseMessaging
) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ManageAccountViewModel(app, firebaseMessaging) as T
    }
}