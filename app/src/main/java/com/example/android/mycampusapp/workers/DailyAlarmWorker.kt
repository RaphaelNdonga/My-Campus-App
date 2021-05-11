package com.example.android.mycampusapp.workers

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.text.format.DateFormat
import androidx.hilt.work.HiltWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.android.mycampusapp.data.CustomTime
import com.example.android.mycampusapp.data.TimetableClass
import com.example.android.mycampusapp.timetable.receiver.TimetableAlarmReceiver
import com.example.android.mycampusapp.util.*
import com.google.firebase.firestore.CollectionReference
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import javax.inject.Inject

@HiltWorker
class DailyAlarmWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters
) : Worker(context, workerParams) {
    @Inject
    lateinit var coursesCollection: CollectionReference

    override fun doWork(): Result {
        val sharedPreferences =
            applicationContext.getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)
        val courseId = sharedPreferences.getString(COURSE_ID, "")!!

        /**
         * Why do we set two alarms?
         * Let's suppose the user installs the app at 9:00 pm.
         * The daily alarm will get today's classes, but since there are none, no alarms will be
         * set. Tomorrow, the worker will work at exactly 9:00 pm again, or thereabouts.
         * Therefore there will be an infinite loop of alarms not set if the alarm sets today's
         * classes.
         * That's why we set tomorrow's alarms also.
         * There is no overhead cost for overlaps in the alarm manager
         */

        val tomorrowCollection = getTomorrowEnumDay().name
        val todayCollection = getTodayEnumDay().name

        /**
         * Why do we need ALARM_SET_COLLECTION?
         * Let's say we use getTomorrowEnumDay.name to set the alarm and also to clear it through
         * the clearance worker.
         * If the alarm is set today and the clearance is done tomorrow before the next worker runs,
         * what happens?
         * The clearance will use tomorrow's collection instead of today's.
         * Therefore, it is necessary to keep track of which collection has been set by the
         * worker, mainly for clearance purposes. We don't want any leftover collections lying
         * around.
         */
        sharedPreferences.edit().putStringSet(
            ALARM_SET_COLLECTION, setOf(tomorrowCollection, todayCollection)
        ).apply()

        val todayClasses = coursesCollection.document(courseId).collection(todayCollection).get()

        todayClasses.addOnSuccessListener {
            it.forEach { documentSnapshot ->
                val todayClass = documentSnapshot.toObject(TimetableClass::class.java)
                if (isLater(todayClass)) {
                    val intent = Intent(applicationContext, TimetableAlarmReceiver::class.java)
                    val message = "${todayClass.subject} starts at ${
                        format24HourTime(
                            CustomTime(
                                todayClass.hour,
                                todayClass.minute
                            )
                        )
                    } in ${todayClass.locationName} room ${todayClass.room}"
                    intent.putExtra("message", message)
                    intent.putExtra("dayOfWeek", getTomorrowEnumDay().name)
                    val pendingIntent = PendingIntent.getBroadcast(
                        applicationContext,
                        todayClass.alarmRequestCode,
                        intent,
                        PendingIntent.FLAG_CANCEL_CURRENT
                    )
                    val alarmManager =
                        applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                    alarmManager.setExact(
                        AlarmManager.RTC_WAKEUP,
                        getTimetableCalendar(todayClass, getTodayEnumDay()).timeInMillis,
                        pendingIntent
                    )
                }
            }
        }

        val tomorrowTimetableClasses =
            coursesCollection.document(courseId).collection(tomorrowCollection).get()

        tomorrowTimetableClasses.addOnSuccessListener {
            it.forEach { documentSnapshot ->
                val tomorrowClass = documentSnapshot.toObject(TimetableClass::class.java)
                val intent = Intent(applicationContext, TimetableAlarmReceiver::class.java)
                val message = "${tomorrowClass.subject} starts at ${
                    format24HourTime(
                        CustomTime(
                            tomorrowClass.hour,
                            tomorrowClass.minute
                        )
                    )
                } in ${tomorrowClass.locationName} room ${tomorrowClass.room}"
                intent.putExtra("message", message)
                intent.putExtra("dayOfWeek", getTomorrowEnumDay().name)
                val pendingIntent = PendingIntent.getBroadcast(
                    applicationContext,
                    tomorrowClass.alarmRequestCode,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
                val tomorrowCalendar = getTomorrowTimetableCalendar(tomorrowClass)

                val alarmManager =
                    applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    tomorrowCalendar.timeInMillis,
                    pendingIntent
                )
            }
        }

        return Result.success()
    }

    private fun formatTime(customTime: CustomTime): String {
        return if (DateFormat.is24HourFormat(applicationContext)) {
            format24HourTime(customTime)
        } else {
            formatAmPmTime(customTime)
        }
    }

    companion object {
        const val WORKER_NAME = "DailyAlarmWorker"
    }
}