package com.example.android.mycampusapp.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ClassRepViewModel : ViewModel() {
    val username = MutableLiveData<String>()
    val email = MutableLiveData<String>()
    val password = MutableLiveData<String>()


}