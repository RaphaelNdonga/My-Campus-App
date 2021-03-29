package com.example.android.mycampusapp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.work.*
import com.example.android.mycampusapp.data.AdminEmail
import com.example.android.mycampusapp.workers.DailyAlarmWorker
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.concurrent.TimeUnit

class MainActivityViewModel(
    private val adminCollection: CollectionReference,
    private val app: Application
) : AndroidViewModel(app) {

    private val applicationScope = CoroutineScope(Dispatchers.Default)

    private val _adminList = MutableLiveData<List<AdminEmail>>()
    val adminList: LiveData<List<AdminEmail>>
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

    fun setupRecurringWork() {
        applicationScope.launch {
            val constraints =
                Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()

            val periodicWorkRequest = PeriodicWorkRequestBuilder<DailyAlarmWorker>(
                1, TimeUnit.DAYS
            ).setConstraints(constraints).build()

            WorkManager.getInstance(app).enqueueUniquePeriodicWork(
                DailyAlarmWorker.WORKER_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                periodicWorkRequest
            )
        }
    }

}
