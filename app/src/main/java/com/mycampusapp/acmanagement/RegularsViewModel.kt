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

class RegularsViewModel(
    private val regularsCollection: CollectionReference,
    private val functions: FirebaseFunctions
) : ViewModel() {
    private val _regularsList = MutableLiveData<List<UserEmail>>()
    val regularsList: LiveData<List<UserEmail>> = _regularsList

    private val _snackBarText = MutableLiveData<Event<String>>()
    val snackBarText: LiveData<Event<String>> = _snackBarText

    fun addSnapshotListener(): ListenerRegistration {
        return regularsCollection.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
            val mutableList = mutableListOf<UserEmail>()
            querySnapshot?.forEach { document ->
                Timber.i("In the loop")
                val email = document.data["email"] as? String
                email?.let {
                    Timber.i("Email obtained $email")
                    mutableList.add(UserEmail(email))
                }
            }
            _regularsList.value = mutableList
            firebaseFirestoreException?.let {
                Timber.i("An exception occurred $it")
            }
        }
    }

    fun upgradeToAdmins(userEmail: UserEmail, courseId: String): Task<String> {
        val data = hashMapOf("email" to userEmail.email, "courseId" to courseId)
        return functions.getHttpsCallable("upgradeToAdmin").call(data).continueWith { task ->
            val receivedHashMap = task.result?.data as HashMap<String, String>
            val result = receivedHashMap["result"]
            result!!
        }.addOnCompleteListener {
            if (it.isSuccessful) {
                deleteRegularsDocument(userEmail.email)
                setAdminsDocument(userEmail)
                _snackBarText.value = Event(it.result ?: "")
            } else {
                _snackBarText.value =
                    Event("Unable to upgrade user at this time. Please check your internet connection and try again.")
            }
        }
    }

    fun deleteRegularsDocument(email: String) {
        regularsCollection.document(email).delete()
    }

    fun setAdminsDocument(userEmail: UserEmail) {
        val adminCollection = regularsCollection.parent?.collection("admins")
        adminCollection?.document(userEmail.email)?.set(userEmail)
    }
}