package com.mycampusapp.acmanagement

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.functions.FirebaseFunctions
import com.mycampusapp.data.UserEmail
import com.mycampusapp.util.Event
import timber.log.Timber

class AdminsViewModel(
    private val adminsCollection: CollectionReference,
    private val functions: FirebaseFunctions
) : ViewModel() {
    private val _adminsList = MutableLiveData<List<UserEmail>>()
    val adminsList: LiveData<List<UserEmail>> = _adminsList

    private val _snackBarText = MutableLiveData<Event<String>>()
    val snackBarText: LiveData<Event<String>> = _snackBarText

    fun addSnapshotListener(): ListenerRegistration {
        return adminsCollection.addSnapshotListener { querySnapshot, firebaseException ->
            val mutableList = mutableListOf<UserEmail>()
            querySnapshot?.forEach { document ->
                Timber.i("In the loop")
                val email = document.data["email"] as? String
                email?.let {
                    mutableList.add(UserEmail(it))
                    Timber.i("Email obtained $it")
                }
            }
            _adminsList.value = mutableList
            firebaseException?.let {
                Timber.i("error occurred $it")
            }
        }
    }

    fun demoteToRegular(userEmail: UserEmail, courseId: String): Task<String> {
        val data = hashMapOf("email" to userEmail.email, "courseId" to courseId)
        return functions.getHttpsCallable("demoteToRegular").call(data).continueWith { task ->
            val receivedHashMap = task.result?.data as HashMap<String?, String?>
            val result = receivedHashMap["result"]
            result!!
        }.addOnCompleteListener {
            if (it.isSuccessful) {
                deleteAdminsDocument(userEmail.email)
                createRegularsDocument(userEmail)
                _snackBarText.value = Event(it.result ?: "")
            } else {
                _snackBarText.value =
                    Event("Unable to demote at this time. Please check your internet connection and try again.")
            }
        }
    }

    fun deleteAdminsDocument(email: String) {
        adminsCollection.document(email).delete()
    }

    fun createRegularsDocument(userEmail: UserEmail) {
        val regularsCollection = adminsCollection.parent?.collection("regulars")
        regularsCollection?.document(userEmail.email)?.set(userEmail)
    }
}