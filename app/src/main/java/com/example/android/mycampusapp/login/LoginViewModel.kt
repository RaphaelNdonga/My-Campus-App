package com.example.android.mycampusapp.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.android.mycampusapp.util.Event

class LoginViewModel: ViewModel() {
    private val _adminNavigator = MutableLiveData<Event<Unit>>()
    val adminNavigator:LiveData<Event<Unit>>
        get() = _adminNavigator

    private val _regularNavigator = MutableLiveData<Event<Unit>>()
    val regularNavigator:LiveData<Event<Unit>>
        get() = _regularNavigator

    val username = MutableLiveData<String>()
    val email = MutableLiveData<String>()
    val password = MutableLiveData<String>()

    fun navigateToAdminSignUp(){
        _adminNavigator.value = Event(Unit)
    }
    fun navigateToRegularSignUp(){
        _regularNavigator.value = Event(Unit)
    }

}
