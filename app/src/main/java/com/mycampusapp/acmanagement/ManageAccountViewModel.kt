package com.mycampusapp.acmanagement

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.preference.PreferenceManager
import androidx.work.WorkManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.messaging.FirebaseMessaging
import com.mycampusapp.assessments.AssessmentType
import com.mycampusapp.data.Assessment
import com.mycampusapp.data.TimetableClass
import com.mycampusapp.timetable.receiver.TimetableAlarmReceiver
import com.mycampusapp.util.*
import com.mycampusapp.workers.DailyAlarmWorker
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
    private val alarmSet = sharedPreferences.getStringSet(ALARM_SET_COLLECTION, setOf())!!
    private val alarmManager = app.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    private var signOutOption: SignOutOptions? = null

    fun getEmail(): String = sharedPreferences.getString(USER_EMAIL, "")!!
    fun getCourseId(): String = sharedPreferences.getString(COURSE_ID, "")!!
    fun isAdmin(): Boolean = sharedPreferences.getBoolean(IS_ADMIN, false)

    private fun cancelTodayAlarms() {
        val todayCollection = alarmSet.elementAt(0)

        val todayClasses =
            courseCollection.document(getCourseId()).collection(todayCollection).get()
        todayClasses.addOnSuccessListener {
            Timber.i("In today's loop")
            it.forEach { documentSnapshot ->
                val todayClass = documentSnapshot.toObject(TimetableClass::class.java)
                if (timetableClassIsLater(todayClass)) {
                    Timber.i("The cancelled today class is: $todayClass")
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
    }

    private fun cancelTomorrowAndSignOut() {
        val tomorrowCollection = alarmSet.elementAt(1)
        val tomorrowClasses =
            courseCollection.document(getCourseId()).collection(tomorrowCollection).get()
        tomorrowClasses.addOnSuccessListener {
            Timber.i("In tomorow's loop")
            it.forEach { classDocument ->
                val timetableClass = classDocument.toObject(TimetableClass::class.java)
                Timber.i("The cancelled tomorrow class is: $timetableClass")
                val intent = Intent(app, TimetableAlarmReceiver::class.java)
                val pendingIntent =
                    PendingIntent.getBroadcast(
                        app,
                        timetableClass.alarmRequestCode,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                    )
                alarmManager.cancel(pendingIntent)
            }
            when (signOutOption) {
                SignOutOptions.LOG_OUT -> {
                    auth.signOut()
                    Timber.i("Logging out")
                }
                SignOutOptions.DELETE -> {
                    auth.currentUser?.delete()
                    Timber.i("Deleting account")
                }
                null -> throw NullPointerException("Sign out option should never be null")
            }
        }.addOnFailureListener {
            Timber.i("$it")
        }
    }

    private fun cancelTestAlarms() {
        val testCollection = AssessmentType.TEST.name
        courseCollection.document(getCourseId()).collection(testCollection).get()
            .addOnSuccessListener {
                it.forEach { document ->
                    val test = document.toObject(Assessment::class.java)
                    Timber.i("The cancelled test is $test")
                    val intent = Intent(app, TimetableAlarmReceiver::class.java)
                    val pendingIntent = PendingIntent.getBroadcast(
                        app,
                        test.alarmRequestCode,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                    )
                    alarmManager.cancel(pendingIntent)
                }
            }.addOnFailureListener {
                Timber.i("$it")
            }
    }

    private fun cancelAssignmentAlarms() {
        val assignmentCollection = AssessmentType.ASSIGNMENT.name
        courseCollection.document(getCourseId()).collection(assignmentCollection).get()
            .addOnSuccessListener {
                it.forEach { document ->
                    val assignment = document.toObject(Assessment::class.java)
                    Timber.i("the cancelled alarm is assignment $assignment")
                    val intent = Intent(app, TimetableAlarmReceiver::class.java)
                    val pendingIntent = PendingIntent.getBroadcast(
                        app,
                        assignment.alarmRequestCode,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                    )
                    alarmManager.cancel(pendingIntent)
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

    private fun deleteAdminCollection() {
        val adminCollection = courseCollection.document(getCourseId()).collection("admins")
        adminCollection.document(getEmail()).delete()
    }

    private fun deleteRegularCollection() {
        val regularCollection = courseCollection.document(getCourseId()).collection("regulars")
        regularCollection.document(getEmail()).delete()
    }

    fun performClearance() {
        if (signOutOption == SignOutOptions.DELETE) {
            val isAdmin = sharedPreferences.getBoolean(IS_ADMIN, false)
            if (isAdmin) {
                deleteAdminCollection()
            } else {
                deleteRegularCollection()
            }
        }
        cancelAssignmentAlarms()
        cancelTestAlarms()
        cancelTodayAlarms()
        /**
         * Cancel tomorrow alarms is conducted only after all the other alarms have been cancelled.
         * This is because it also has the responsibility of signing out the user. It has that
         * responsibility because we need to signout the user in the same thread that is cancelling
         * the alarms. Otherwise, if signing out is handled in the main thread, the user will be
         * signed out before the alarms are obtained and cancelled.
         */
        cancelTomorrowAndSignOut()
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