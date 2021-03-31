package com.example.android.mycampusapp.workers

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.hilt.work.HiltWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.android.mycampusapp.data.TimetableClass
import com.example.android.mycampusapp.timetable.receiver.TimetableAlarmReceiver
import com.example.android.mycampusapp.util.getTomorrowEnumDay
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
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MINUTE, 5)

        Timber.i("The minute is ${calendar.get(Calendar.MINUTE)}")

        val intent = Intent(applicationContext, TimetableAlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val alarmManager =
            applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)

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