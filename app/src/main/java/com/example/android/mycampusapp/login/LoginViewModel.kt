package com.example.android.mycampusapp.login

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.android.mycampusapp.R
import com.example.android.mycampusapp.util.COURSE_ID
import com.example.android.mycampusapp.util.Event
import com.example.android.mycampusapp.util.IS_ADMIN
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GetTokenResult
import timber.log.Timber

class LoginViewModel(private val auth:FirebaseAuth,private val sharedPreferences: SharedPreferences): ViewModel() {
    private val _adminNavigator = MutableLiveData<Event<Unit>>()
    val adminNavigator:LiveData<Event<Unit>>
        get() = _adminNavigator

    private val _regularNavigator = MutableLiveData<Event<Unit>>()
    val regularNavigator:LiveData<Event<Unit>>
        get() = _regularNavigator

    private val _mainNavigator = MutableLiveData<Event<Unit>>()
    val mainNavigator:LiveData<Event<Unit>>
        get() = _mainNavigator

    private val _loadStart = MutableLiveData<Event<Unit>>()
    val loadStart:LiveData<Event<Unit>>
        get() = _loadStart

    private val _loadFinish = MutableLiveData<Event<Unit>>()
    val loadFinish:LiveData<Event<Unit>>
        get() = _loadFinish

    private val _snackBarText = MutableLiveData<Event<Int>>()
    val snackBarText:LiveData<Event<Int>>
        get() = _snackBarText

    val username = MutableLiveData<String>()
    val email = MutableLiveData<String>()
    val password = MutableLiveData<String>()

    fun navigateToAdminSignUp(){
        _adminNavigator.value = Event(Unit)
    }
    fun navigateToRegularSignUp(){
        _regularNavigator.value = Event(Unit)
    }
    fun signInUser(email: String?, password: String?) {
        if (email.isNullOrEmpty() || password.isNullOrEmpty()) {
            Timber.i("values are null")
            return
        }
        initiateEvent(_loadStart)
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Timber.i("Signed in successfully with email and password")
                auth.currentUser?.getIdToken(false)
                    ?.addOnSuccessListener { result: GetTokenResult? ->
                        val sharedPrefEdit = sharedPreferences.edit()
                        val isModerator: Boolean? = result?.claims?.get("admin") as Boolean?
                        if (isModerator != null) {
                            Timber.i("This user is an admin")
                            sharedPrefEdit.putBoolean(IS_ADMIN, isModerator)
                        } else {
                            Timber.i("This user is not an admin")
                        }
                        val courseId: String? = result?.claims?.get("courseId") as String?
                        sharedPrefEdit.putString(COURSE_ID, courseId)
                        sharedPrefEdit.apply()
                        Timber.i("The course id is $courseId")
                        initiateEvent(_mainNavigator)
                        initiateEvent(_loadFinish)
                    }
                return@addOnCompleteListener
            }
            Timber.i("Sign in with email and password failed")
            Timber.i("$email and $password are the email and password put in")
            initiateEvent(_loadFinish)
            _snackBarText.value = Event(R.string.login_failure)

        }
    }
    private fun initiateEvent(event:MutableLiveData<Event<Unit>>){
        event.value = Event(Unit)
    }

}
