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

class RegularsViewModel(
    private val regularsCollection: CollectionReference,
    private val functions: FirebaseFunctions
) : ViewModel() {
    private val _regularsList = MutableLiveData<List<UserEmail>>()
    val regularsList: LiveData<List<UserEmail>> = _regularsList

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

    fun upgradeToAdmins(email: String, courseId: String): Task<Unit> {
        val data = hashMapOf("email" to email, "courseId" to courseId)
        return functions.getHttpsCallable("upgradeToAdmin").call(data).continueWith { }
    }

    fun deleteRegularsDocument(email: String) {
        regularsCollection.document(email).delete()
    }

    fun setAdminsDocument(userEmail: UserEmail) {
        val adminCollection = regularsCollection.parent?.collection("admins")
        adminCollection?.document(userEmail.email)?.set(userEmail)
    }
}