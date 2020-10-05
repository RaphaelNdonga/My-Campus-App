package com.example.android.mycampusapp.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.android.mycampusapp.R
import com.example.android.mycampusapp.util.Event
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.HttpsCallableResult
import timber.log.Timber

class SignUpViewModel(
    private val functions: FirebaseFunctions,
    private val auth: FirebaseAuth,
    private val studentStatus: StudentStatus
) :
    ViewModel() {

    private val _snackBarText = MutableLiveData<Event<Int>>()
    val snackBarText: LiveData<Event<Int>>
        get() = _snackBarText

    private val _navigator = MutableLiveData<Event<Unit>>()
    val navigator:LiveData<Event<Unit>>
        get() = _navigator

    val courseName = MutableLiveData<String>()
    val email = MutableLiveData<String>()
    val password = MutableLiveData<String>()

    private fun setCourseId(data: HashMap<String?, String?>): Task<String> {
        return functions.getHttpsCallable("addCourseId").call(data)
            .continueWith { task: Task<HttpsCallableResult> ->

                Timber.i("Setting course id")
                val result = task.result?.data as String
                result
            }
    }

    private fun setAdminCourseId(data: HashMap<String?, String?>): Task<String> {
        Timber.i("Making admin...")

        return functions.getHttpsCallable("addAdminCourseId").call(data)
            .continueWith { task: Task<HttpsCallableResult> ->
                // This continuation runs on either success or failure, but if the task
                // has failed then result will throw an Exception which will be
                // propagated down.
                Timber.i("Made admin!")
                val result = task.result?.data as String
                result
            }
    }

    private fun checkStudentStatus(status: StudentStatus, data: HashMap<String?, String?>) {
        when (status) {
            StudentStatus.ADMIN -> setAdminCourseId(data)
            StudentStatus.REGULAR -> setCourseId(data)
            StudentStatus.UNDEFINED -> return
        }
    }

    fun createUser(data: HashMap<String?, String?>, password: String?) {
        val email = data["email"]

        if (email.isNullOrEmpty() || password.isNullOrEmpty()) {
            Timber.i("values are null")
            return
        }
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                _snackBarText.value = Event(R.string.successful_signup)
                _navigator.value = Event(Unit)
                checkStudentStatus(studentStatus, data)
                return@addOnCompleteListener
            }
            _snackBarText.value = Event(R.string.failed_signup)
        }
    }


}