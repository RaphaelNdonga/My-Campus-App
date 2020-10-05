package com.example.android.mycampusapp.login

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth

@Suppress("UNCHECKED_CAST")
class LoginViewModelFactory(
    private val auth: FirebaseAuth,
    private val sharedPreferences: SharedPreferences
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return LoginViewModel(auth, sharedPreferences) as T
    }
}