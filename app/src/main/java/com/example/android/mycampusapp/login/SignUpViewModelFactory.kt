package com.example.android.mycampusapp.login

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.functions.FirebaseFunctions

@Suppress("UNCHECKED_CAST")
class SignUpViewModelFactory(
    private val functions: FirebaseFunctions,
    private val auth: FirebaseAuth,
    private val studentStatus:StudentStatus,
    private val application: Application
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SignUpViewModel(functions,auth,studentStatus,application) as T
    }
}