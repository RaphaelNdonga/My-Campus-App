package com.example.android.mycampusapp.acmanagement

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.android.mycampusapp.data.UserEmail
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.functions.FirebaseFunctions
import timber.log.Timber

class AdminsViewModel(
    private val adminsCollection: CollectionReference,
    private val functions: FirebaseFunctions
) : ViewModel() {
    private val _adminsList = MutableLiveData<List<UserEmail>>()
    val adminsList: LiveData<List<UserEmail>> = _adminsList

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

    fun demoteToRegular(email: String, courseId: String): Task<Unit> {
        val data = hashMapOf("email" to email, "courseId" to courseId)
        return functions.getHttpsCallable("demoteToRegular").call(data).continueWith { }
    }

    fun deleteAdminsDocument(email: String) {
        adminsCollection.document(email).delete()
    }

    fun createRegularsDocument(userEmail: UserEmail) {
        val regularsCollection = adminsCollection.parent?.collection("regulars")
        regularsCollection?.document(userEmail.email)?.set(userEmail)
    }
}