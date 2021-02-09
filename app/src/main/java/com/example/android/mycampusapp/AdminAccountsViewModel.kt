package com.example.android.mycampusapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.android.mycampusapp.data.AdminEmail
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ListenerRegistration
import timber.log.Timber

class AdminAccountsViewModel(
    private val adminCollection: CollectionReference,
) : ViewModel() {

    private val _adminList = MutableLiveData<List<AdminEmail>>()
    val adminList:LiveData<List<AdminEmail>>
        get() = _adminList

    fun addSnapshotListener(): ListenerRegistration {
        val mutableAdminList: MutableList<AdminEmail> = mutableListOf()
        return adminCollection.addSnapshotListener { collection, error ->
            collection?.forEach { documentSnapshot ->
                val adminEmail = documentSnapshot.toString()
                Timber.i("The documentSnapshot string is $adminEmail")
                mutableAdminList.add(AdminEmail(adminEmail))
            }
            _adminList.value = mutableAdminList
            if (error != null) {
                Timber.i("Got an error $error")
            }
        }
    }

    fun checkAndAddEmail(list: List<AdminEmail>, userEmail: AdminEmail) {
        if (list.contains(userEmail))
            return
        // add the email to the collection
        adminCollection.document(userEmail.email).set(userEmail)
    }
}
