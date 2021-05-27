package com.mycampusapp.acmanagement

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.messaging.FirebaseMessaging

@Suppress("UNCHECKED_CAST")
class ManageAccountViewModelFactory(
    private val app: Application,
    private val collectionReference: CollectionReference,
    private val messaging: FirebaseMessaging,
    private val auth: FirebaseAuth
) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ManageAccountViewModel(app, collectionReference, messaging, auth) as T
    }
}