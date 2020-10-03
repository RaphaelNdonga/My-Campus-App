package com.example.android.mycampusapp.util

import androidx.lifecycle.MutableLiveData

object CustomClaims {
    private val admin = MutableLiveData<Boolean>()
    private val courseId = MutableLiveData<String>()

    fun setAsAdmin(){
        admin.value = true
    }
    fun setCourseId(id:String){
        courseId.value = id
    }
    fun isAdmin():Boolean?{
        return admin.value
    }
    fun getCourseId():String?{
        return courseId.value
    }
}