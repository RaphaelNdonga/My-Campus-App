package com.example.android.mycampusapp.workers

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.hilt.work.HiltWorker
import androidx.preference.PreferenceManager
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.android.mycampusapp.data.TimetableClass
import com.example.android.mycampusapp.timetable.receiver.TimetableAlarmReceiver
import com.example.android.mycampusapp.util.ALARM_SET_COLLECTION
import com.example.android.mycampusapp.util.COURSE_ID
import com.example.android.mycampusapp.util.sharedPrefFile
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.messaging.FirebaseMessaging
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import timber.log.Timber
import javax.inject.Inject

/**
 * Clears the shared preferences and settings preferences
 * Clears all the alarms set
 * Removes the dailyAlarmWorker
 */

@HiltWorker
class ClearanceWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters
) : Worker(context, workerParams) {
    @Inject
    lateinit var course: CollectionReference

    @Inject
    lateinit var firebaseMessaging: FirebaseMessaging
    override fun doWork(): Result {
        val settingsPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val sharedPreferences =
            applicationContext.getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)
        val courseId = sharedPreferences.getString(COURSE_ID, "")!!
        Timber.i("The courseId is $courseId")
        val dayCollection = sharedPreferences.getString(ALARM_SET_COLLECTION, "")!!
        val tomorrowClasses = course.document(courseId).collection(dayCollection).get()

        val alarmManager =
            applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        tomorrowClasses.addOnSuccessListener {
            it.forEach { classDocument ->
                val timetableClass = classDocument.toObject(TimetableClass::class.java)
                val intent = Intent(applicationContext, TimetableAlarmReceiver::class.java)
                val pendingIntent =
                    PendingIntent.getBroadcast(
                        applicationContext,
                        timetableClass.alarmRequestCode,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                    )
                alarmManager.cancel(pendingIntent)
            }
        }

        WorkManager.getInstance(applicationContext).cancelUniqueWork(DailyAlarmWorker.WORKER_NAME)
        settingsPreferences.edit().clear().apply()
        sharedPreferences.edit().clear().apply()
        firebaseMessaging.unsubscribeFromTopic(courseId)

        return Result.success()
    }

    companion object {
        const val WORKER_NAME = "ClearanceWorker"
    }
}