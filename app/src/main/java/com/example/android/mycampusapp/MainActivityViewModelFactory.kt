package com.example.android.mycampusapp

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.messaging.FirebaseMessaging

@Suppress("UNCHECKED_CAST")
class MainActivityViewModelFactory(
    private val app: Application,
    private val messaging: FirebaseMessaging,
    private val auth: FirebaseAuth,
    private val courseCollection: CollectionReference
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MainActivityViewModel(app, messaging, auth, courseCollection) as T
    }
}