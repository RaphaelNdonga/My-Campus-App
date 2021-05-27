package com.mycampusapp.login

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference

@Suppress("UNCHECKED_CAST")
class LoginViewModelFactory(
    private val auth: FirebaseAuth,
    private val sharedPreferences: SharedPreferences,
    private val courseCollection: CollectionReference
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return LoginViewModel(auth, sharedPreferences, courseCollection) as T
    }
}