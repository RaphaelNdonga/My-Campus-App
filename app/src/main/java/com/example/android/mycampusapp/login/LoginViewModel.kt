package com.example.android.mycampusapp.login

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.android.mycampusapp.data.UserEmail
import com.example.android.mycampusapp.util.COURSE_ID
import com.example.android.mycampusapp.util.Event
import com.example.android.mycampusapp.util.IS_ADMIN
import com.example.android.mycampusapp.util.USER_EMAIL
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GetTokenResult
import com.google.firebase.firestore.CollectionReference
import timber.log.Timber

class LoginViewModel(
    private val auth: FirebaseAuth,
    private val sharedPreferences: SharedPreferences,
    private val courseCollection: CollectionReference
) : ViewModel() {
    private val _adminNavigator = MutableLiveData<Event<Unit>>()
    val adminNavigator: LiveData<Event<Unit>>
        get() = _adminNavigator

    private val _regularNavigator = MutableLiveData<Event<Unit>>()
    val regularNavigator: LiveData<Event<Unit>>
        get() = _regularNavigator

    private val _mainNavigator = MutableLiveData<Event<Unit>>()
    val mainNavigator: LiveData<Event<Unit>>
        get() = _mainNavigator

    private val _loadStart = MutableLiveData<Event<Unit>>()
    val loadStart: LiveData<Event<Unit>>
        get() = _loadStart

    private val _loadFinish = MutableLiveData<Event<Unit>>()
    val loadFinish: LiveData<Event<Unit>>
        get() = _loadFinish

    private val _snackBarText = MutableLiveData<Event<String?>>()
    val snackBarText: LiveData<Event<String?>> = _snackBarText

    val email = MutableLiveData<String>()
    val password = MutableLiveData<String>()

    fun navigateToAdminSignUp() {
        _adminNavigator.value = Event(Unit)
    }

    fun navigateToRegularSignUp() {
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
                val sharedPrefEdit = sharedPreferences.edit()
                sharedPrefEdit.putString(USER_EMAIL, email)

                Timber.i("Signed in successfully with email and password")
                auth.currentUser?.getIdToken(true)
                    ?.addOnSuccessListener { result: GetTokenResult? ->
                        val courseId: String? = result?.claims?.get("courseId") as String?
                        if (courseId.isNullOrEmpty()) {
                            _snackBarText.value =
                                Event("There was a problem saving your details. Please try again")
                            initiateEvent(_loadFinish)
                            return@addOnSuccessListener
                        }
                        sharedPrefEdit.putString(COURSE_ID, courseId)
                        sharedPrefEdit.apply()
                        Timber.i("The course id is $courseId")
                        val isModerator: Boolean? = result?.claims?.get("admin") as Boolean?
                        if (isModerator != null && isModerator) {
                            Timber.i("This user is an admin")
                            sharedPrefEdit.putBoolean(IS_ADMIN, isModerator)
                            sharedPrefEdit.apply()
                            Timber.i("$isModerator")
                            val adminEmail = UserEmail(email)
                            val adminCollection =
                                courseCollection.document(courseId).collection("admins")
                            adminCollection.document(adminEmail.email).set(adminEmail)
                        } else {
                            Timber.i("This user is not an admin")
                            sharedPrefEdit.putBoolean(IS_ADMIN, false)
                            sharedPrefEdit.apply()
                            val regularEmail = UserEmail(email)
                            val regularCollection =
                                courseCollection.document(courseId).collection("regulars")
                            regularCollection.document(regularEmail.email).set(regularEmail)
                        }
                        val sharedPrefBol = sharedPreferences.getBoolean(IS_ADMIN, false)
                        Timber.i("The shared preferences boolean is $sharedPrefBol")
                        initiateEvent(_mainNavigator)
                    }

                return@addOnCompleteListener
            }
            Timber.i("Sign in with email and password failed")
            Timber.i("The exception is ${task.exception} and the message is ${task.exception?.message}")
            initiateEvent(_loadFinish)
            _snackBarText.value = Event(task.exception?.message)
        }
    }

    private fun initiateEvent(event: MutableLiveData<Event<Unit>>) {
        event.value = Event(Unit)
    }

}
