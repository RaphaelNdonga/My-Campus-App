package com.example.android.mycampusapp.workers

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.hilt.work.HiltWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.android.mycampusapp.data.CustomDate
import com.example.android.mycampusapp.data.CustomTime
import com.example.android.mycampusapp.data.TimetableClass
import com.example.android.mycampusapp.timetable.receiver.TimetableAlarmReceiver
import com.example.android.mycampusapp.util.*
import com.google.firebase.firestore.CollectionReference
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import timber.log.Timber
import java.util.*
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
        val dayCollection = getTomorrowEnumDay().name
        sharedPreferences.edit().putString(ALARM_SET_COLLECTION, dayCollection).apply()
        val tomorrowTimetableClasses =
            coursesCollection.document(courseId).collection(dayCollection).get()

        tomorrowTimetableClasses.addOnSuccessListener {
            it.forEach { documentSnapshot ->
                val tomorrowClass = documentSnapshot.toObject(TimetableClass::class.java)
                val intent = Intent(applicationContext, TimetableAlarmReceiver::class.java)
                val message = "${tomorrowClass.subject} starts at ${
                    formatTime(
                        CustomTime(
                            tomorrowClass.hour,
                            tomorrowClass.minute
                        )
                    )
                } in ${tomorrowClass.locationName} room ${tomorrowClass.room}"
                intent.putExtra("message", message)
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
                val tomorrowTime = CustomTime(
                    tomorrowCalendar.get(Calendar.HOUR_OF_DAY),
                    tomorrowCalendar.get(Calendar.MINUTE)
                )
                val tomorrowDate = CustomDate(
                    tomorrowCalendar.get(Calendar.YEAR),
                    tomorrowCalendar.get(Calendar.MONTH),
                    tomorrowCalendar.get(Calendar.DAY_OF_MONTH)
                )
                Timber.i(
                    "Alarm set for date ${formatDate(tomorrowDate)} at time ${
                        formatTime(tomorrowTime)
                    }  "
                )
            }
        }

        return Result.success()
    }

    companion object {
        const val WORKER_NAME = "DailyAlarmWorker"
    }

    private fun addFirestoreData(timetableClass: TimetableClass, courseId: String) {
        coursesCollection.document(courseId).collection(getTomorrowEnumDay().name)
            .document(timetableClass.id).set(timetableClass)
    }
}