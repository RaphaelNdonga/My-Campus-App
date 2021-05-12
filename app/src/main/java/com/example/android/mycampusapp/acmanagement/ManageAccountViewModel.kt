package com.example.android.mycampusapp.acmanagement

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.preference.PreferenceManager
import androidx.work.WorkManager
import com.example.android.mycampusapp.data.TimetableClass
import com.example.android.mycampusapp.timetable.receiver.TimetableAlarmReceiver
import com.example.android.mycampusapp.util.*
import com.example.android.mycampusapp.workers.DailyAlarmWorker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.messaging.FirebaseMessaging
import timber.log.Timber

class ManageAccountViewModel(
    private val app: Application,
    private val courseCollection: CollectionReference,
    private val firebaseMessaging: FirebaseMessaging,
    private val auth: FirebaseAuth
) :
    AndroidViewModel(app) {
    private val sharedPreferences =
        app.getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)

    private val settingsPreferences = PreferenceManager.getDefaultSharedPreferences(app)

    private var signOutOption: SignOutOptions? = null

    fun getEmail(): String = sharedPreferences.getString(USER_EMAIL, "")!!
    fun getCourseId(): String = sharedPreferences.getString(COURSE_ID, "")!!

    private fun cancelAllAlarms() {
        val alarmSet = sharedPreferences.getStringSet(ALARM_SET_COLLECTION, setOf())!!
        val todayCollection = alarmSet.elementAt(0)
        val tomorrowCollection = alarmSet.elementAt(1)

        val todayClasses =
            courseCollection.document(getCourseId()).collection(todayCollection).get()
        todayClasses.addOnSuccessListener {
            Timber.i("In today's loop")
            it.forEach { documentSnapshot ->
                val todayClass = documentSnapshot.toObject(TimetableClass::class.java)
                if (timetableClassIsLater(todayClass)) {
                    Timber.i("The cancelled class is: $todayClass")
                    val intent = Intent(app, TimetableAlarmReceiver::class.java)
                    val pendingIntent = PendingIntent.getBroadcast(
                        app,
                        todayClass.alarmRequestCode,
                        intent,
                        PendingIntent.FLAG_CANCEL_CURRENT
                    )
                    val alarmManager =
                        app.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                    alarmManager.cancel(pendingIntent)
                }
            }
        }.addOnFailureListener {
            Timber.i("$it")
        }

        val tomorrowClasses =
            courseCollection.document(getCourseId()).collection(tomorrowCollection).get()
        tomorrowClasses.addOnSuccessListener {
            Timber.i("In tomorow's loop")
            it.forEach { classDocument ->
                val timetableClass = classDocument.toObject(TimetableClass::class.java)
                Timber.i("The cancelled class is: $timetableClass")
                val intent = Intent(app, TimetableAlarmReceiver::class.java)
                val pendingIntent =
                    PendingIntent.getBroadcast(
                        app,
                        timetableClass.alarmRequestCode,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                    )
                val alarmManager = app.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                alarmManager.cancel(pendingIntent)
                when (signOutOption) {
                    SignOutOptions.LOG_OUT -> auth.signOut()
                    SignOutOptions.DELETE -> auth.currentUser?.delete()
                    null -> throw NullPointerException("Sign out option should never be null")
                }
            }
        }.addOnFailureListener {
            Timber.i("$it")
        }
    }

    private fun removeDailyAlarmWorker() {
        WorkManager.getInstance(app).cancelUniqueWork(DailyAlarmWorker.WORKER_NAME)
    }

    private fun unsubscribeFromTopic() {
        firebaseMessaging.unsubscribeFromTopic(getCourseId())
    }

    private fun removeSharedPreferences() {
        settingsPreferences.edit().clear().apply()
        sharedPreferences.edit().clear().apply()
    }

    fun performClearance() {
        cancelAllAlarms()
        removeDailyAlarmWorker()
        unsubscribeFromTopic()
        removeSharedPreferences()
    }

    fun logOut() {
        signOutOption = SignOutOptions.LOG_OUT
    }

    fun delete() {
        signOutOption = SignOutOptions.DELETE
    }
}

enum class SignOutOptions {
    LOG_OUT,
    DELETE
}