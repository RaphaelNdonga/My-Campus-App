package com.example.android.mycampusapp

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.work.*
import com.example.android.mycampusapp.data.UserEmail
import com.example.android.mycampusapp.util.COURSE_ID
import com.example.android.mycampusapp.util.IS_ADMIN
import com.example.android.mycampusapp.util.USER_EMAIL
import com.example.android.mycampusapp.util.sharedPrefFile
import com.example.android.mycampusapp.workers.DailyAlarmWorker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GetTokenResult
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.concurrent.TimeUnit

class MainActivityViewModel(
    private val app: Application,
    private val messaging: FirebaseMessaging,
    private val auth: FirebaseAuth,
    private val courseCollection: CollectionReference
) : AndroidViewModel(app) {

    private val applicationScope = CoroutineScope(Dispatchers.Default)
    private val sharedPreferences = app.getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)
    private val courseId = sharedPreferences.getString(COURSE_ID, "")!!


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

    fun subscribeToTopic() {
        messaging.subscribeToTopic(courseId).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Timber.i("Subscribed successfully to topic $courseId")
            } else {
                Timber.i("Unsuccessful subscription due to ${task.exception?.message}")
            }
        }
    }

    /**
     * This method's main purpose is this. Every time a user starts the app, this method checks
     * whether the user is still an admin or regular user. Her status might have changed. She might
     * have been demoted or upgraded by another admin.
     */
    fun confirmAdminStatus() {
        val email = sharedPreferences.getString(USER_EMAIL, "")!!
        val sharedPrefEdit = sharedPreferences.edit()
        auth.currentUser?.getIdToken(true)
            ?.addOnSuccessListener { result: GetTokenResult? ->
                val isModerator: Boolean? = result?.claims?.get("admin") as Boolean?
                if (isModerator != null && isModerator) {
                    Timber.i("This user is an admin")
                    sharedPrefEdit.putBoolean(IS_ADMIN, isModerator)
                    sharedPrefEdit.apply()
                    Timber.i("$isModerator")
                    val adminEmail = UserEmail(email)
                    val adminCollection =
                        courseCollection.document(courseId).collection("admins")
                    adminCollection.document(adminEmail.email).set(adminEmail)
                } else {
                    Timber.i("This user is not an admin")
                    sharedPrefEdit.putBoolean(IS_ADMIN, false)
                    sharedPrefEdit.apply()
                    val regularEmail = UserEmail(email)
                    val regularCollection =
                        courseCollection.document(courseId).collection("regulars")
                    regularCollection.document(regularEmail.email).set(regularEmail)
                }
            }
    }

}
