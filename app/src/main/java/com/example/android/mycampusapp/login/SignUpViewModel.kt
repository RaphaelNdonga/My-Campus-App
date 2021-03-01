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
    val navigator: LiveData<Event<Unit>>
        get() = _navigator

    val courseName = MutableLiveData<String>()
    val email = MutableLiveData<String>()
    val password = MutableLiveData<String>()

    private fun checkIfAdminExists(data: HashMap<String?,String?>): Task<Boolean> {
        return functions.getHttpsCallable("checkIfAdminExists").call(data).continueWith { task->
            val receivedHashMap = task.result?.data as HashMap<String?,Boolean?>
            val result = receivedHashMap["result"]
            result!!
        }.addOnCompleteListener {
            if (it.isSuccessful){
                Timber.i("The task was successful with message ${it.result}")
            }else{
                Timber.i("The task was unsuccessful with exception ${it.exception}")
            }
        }
    }

    private fun setCourseId(data: HashMap<String?, String?>): Task<String> {
        return functions.getHttpsCallable("addCourseId").call(data)
            .continueWith { task: Task<HttpsCallableResult> ->

                Timber.i("Setting course id")
                val receivedHashMap = task.result?.data as HashMap<String?,String?>
                if(receivedHashMap["result"] !=null){
                    _navigator.value = Event(Unit)
                    _snackBarText.value = Event(R.string.successful_signup)
                }
                if(receivedHashMap["error"]!=null){
                    auth.currentUser?.delete()
                    _snackBarText.value = Event(R.string.failed_signup)
                }
                val result = receivedHashMap["result"]?:receivedHashMap["error"]
                result!!
            }.addOnCompleteListener {
                if(it.isSuccessful){
                    Timber.i("The task was successful with message ${it.result}")
                }else{
                    Timber.i("The task was unsuccessful with exception ${it.exception}")
                }
            }
    }

    private fun setAdminCourseId(data: HashMap<String?, String?>): Task<String> {
        Timber.i("Making admin...")

        return functions.getHttpsCallable("addAdminCourseId").call(data)
            .continueWith { task: Task<HttpsCallableResult> ->
                // This continuation runs on either success or failure, but if the task
                // has failed then result will throw an Exception which will be
                // propagated down.
                val receivedHashMap:HashMap<String?,String?> = task.result?.data as HashMap<String?, String?>
                if(receivedHashMap["result"] !=null){
                    _navigator.value = Event(Unit)
                    _snackBarText.value = Event(R.string.successful_signup)
                }
                if(receivedHashMap["error"]!=null){
                    auth.currentUser?.delete()
                    _snackBarText.value = Event(R.string.failed_signup)
                }
                val result = receivedHashMap["result"]?:receivedHashMap["error"]
                result!!
            }.addOnCompleteListener {
                if(it.isSuccessful){
                    Timber.i("The task was successful with message ${it.result}")
                }
                else{
                    Timber.i("The task was unsuccessful with exception ${it.exception}")
                }
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
                checkStudentStatus(studentStatus, data)
                return@addOnCompleteListener
            }
        }
    }


}