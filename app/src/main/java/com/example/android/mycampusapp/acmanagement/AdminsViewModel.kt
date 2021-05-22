package com.example.android.mycampusapp.acmanagement

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.android.mycampusapp.data.UserEmail
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ListenerRegistration
import timber.log.Timber

class AdminsViewModel(private val adminsCollection: CollectionReference) : ViewModel() {
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
}