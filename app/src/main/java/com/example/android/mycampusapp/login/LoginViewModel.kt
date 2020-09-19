package com.example.android.mycampusapp.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.android.mycampusapp.util.Event

class LoginViewModel: ViewModel() {
    private val _navigator = MutableLiveData<Event<Unit>>()
    val navigator:LiveData<Event<Unit>>
        get() = _navigator

    val username = MutableLiveData<String>()
    val email = MutableLiveData<String>()
    val password = MutableLiveData<String>()

    fun navigateToSignUp(){
        _navigator.value = Event(Unit)
    }

}
