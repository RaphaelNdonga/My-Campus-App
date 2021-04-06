package com.example.android.mycampusapp.acmanagement

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.work.*
import com.example.android.mycampusapp.util.COURSE_ID
import com.example.android.mycampusapp.util.USER_EMAIL
import com.example.android.mycampusapp.util.sharedPrefFile
import com.example.android.mycampusapp.workers.ClearanceWorker
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ManageAccountViewModel(
    private val app: Application,
    private val firebaseMessaging: FirebaseMessaging
) :
    AndroidViewModel(app) {
    private val applicationScope = CoroutineScope(Dispatchers.Default)
    private var sharedPreferences =
        app.getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)

    fun getEmail(): String = sharedPreferences.getString(USER_EMAIL, "")!!
    fun getCourseId(): String = sharedPreferences.getString(COURSE_ID, "")!!

    fun performClearance() {
        applicationScope.launch {
            val constraints =
                Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
            val workRequest =
                OneTimeWorkRequest.Builder(ClearanceWorker::class.java).setConstraints(constraints)
                    .build()
            WorkManager.getInstance(app)
                .enqueueUniqueWork(
                    ClearanceWorker.WORKER_NAME,
                    ExistingWorkPolicy.APPEND,
                    workRequest
                )
        }
    }
}