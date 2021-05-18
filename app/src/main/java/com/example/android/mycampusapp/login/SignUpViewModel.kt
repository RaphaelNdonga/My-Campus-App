package com.example.android.mycampusapp.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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
    private val studentStatus: StudentStatus,
    private val app: Application
) : AndroidViewModel(app) {

    private val _snackBarText = MutableLiveData<Event<String>>()
    val snackBarText: LiveData<Event<String>>
        get() = _snackBarText

    private val _navigator = MutableLiveData<Event<Unit>>()
    val navigator: LiveData<Event<Unit>>
        get() = _navigator

    private val _finishLoading = MutableLiveData<Event<Unit>>()
    val finishLoading: LiveData<Event<Unit>>
        get() = _finishLoading

    private val _adminExists = MutableLiveData<Boolean>()
    val adminExists: LiveData<Boolean>
        get() = _adminExists

    val courseName = MutableLiveData<String>()
    val email = MutableLiveData<String>()
    val password = MutableLiveData<String>()
    val confirmPassword = MutableLiveData<String>()

    fun checkIfAdminExists(data: HashMap<String, String>): Task<Boolean> {
        return functions.getHttpsCallable("checkIfAdminExists").call(data).continueWith { task ->
            val receivedHashMap = task.result?.data as HashMap<String?, Boolean?>
            val result = receivedHashMap["result"]
            result!!
        }.addOnCompleteListener {
            if (it.isSuccessful) {
                Timber.i("The task was successful with message ${it.result}")
                _adminExists.value = it.result
            } else {
                Timber.i("The task was unsuccessful with exception ${it.exception} and message ${it.exception?.message}")
                stopLoading()
                val errorMessage = it.exception?.message
                if (errorMessage == "INTERNAL") {
                    _snackBarText.value = Event(app.getString(R.string.network_error_msg))
                }
            }
        }
    }

    private fun setCourseId(data: HashMap<String, String>): Task<String> {
        return functions.getHttpsCallable("addCourseId").call(data)
            .continueWith { task: Task<HttpsCallableResult> ->

                Timber.i("Setting course id")
                val receivedHashMap = task.result?.data as HashMap<String?, String?>
                if (receivedHashMap["result"] != null) {
                    _navigator.value = Event(Unit)
                    _snackBarText.value = Event(app.getString(R.string.successful_signup))
                }
                if (receivedHashMap["error"] != null) {
                    auth.currentUser?.delete()
                    _snackBarText.value = Event(app.getString(R.string.failed_signup))
                }
                val result = receivedHashMap["result"] ?: receivedHashMap["error"]
                result!!
            }.addOnCompleteListener {
                if (it.isSuccessful) {
                    Timber.i("The task was successful with message ${it.result}")
                } else {
                    Timber.i("The task was unsuccessful with exception ${it.exception}")
                }
            }
    }

    private fun setAdminCourseId(data: HashMap<String, String>): Task<String> {
        Timber.i("Making admin...")

        return functions.getHttpsCallable("addAdminCourseId").call(data)
            .continueWith { task: Task<HttpsCallableResult> ->
                // This continuation runs on either success or failure, but if the task
                // has failed then result will throw an Exception which will be
                // propagated down.
                val receivedHashMap: HashMap<String?, String?> =
                    task.result?.data as HashMap<String?, String?>
                if (receivedHashMap["result"] != null) {
                    _navigator.value = Event(Unit)
                    _snackBarText.value = Event(app.getString(R.string.successful_signup))
                }
                if (receivedHashMap["error"] != null) {
                    auth.currentUser?.delete()
                    _snackBarText.value = Event(app.getString(R.string.failed_signup))
                }
                val result = receivedHashMap["result"] ?: receivedHashMap["error"]
                result!!
            }.addOnCompleteListener {
                if (it.isSuccessful) {
                    Timber.i("The task was successful with message ${it.result}")
                } else {
                    Timber.i("The task was unsuccessful with exception ${it.exception}")
                }
            }
    }

    private fun checkStudentStatus(status: StudentStatus, data: HashMap<String, String>) {
        when (status) {
            StudentStatus.ADMIN -> setAdminCourseId(data)
            StudentStatus.REGULAR -> setCourseId(data)
            StudentStatus.UNDEFINED -> return
        }
    }

    fun createUser(data: HashMap<String, String>, password: String) {
        val email = data["email"]!!

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                checkStudentStatus(studentStatus, data)
                return@addOnCompleteListener
            }
            val exception = task.exception
            Timber.i("The error is $exception and the message is ${exception?.message}")
            stopLoading()
            exception?.message?.let { _snackBarText.value = Event(it) }
        }
    }

    private fun stopLoading() {
        _finishLoading.value = Event(Unit)
    }


}