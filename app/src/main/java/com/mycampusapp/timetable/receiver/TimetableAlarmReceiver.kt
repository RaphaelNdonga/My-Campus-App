package com.mycampusapp.timetable.receiver

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.mycampusapp.util.sendNotification
import timber.log.Timber

class TimetableAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        Timber.i("The receiver has been called")
        val notificationManager = ContextCompat.getSystemService(
            context,
            NotificationManager::class.java
        ) as NotificationManager

        Timber.i("The extras are:${intent?.extras}")
        val message = intent?.getStringExtra("message")
        Timber.i("The extra message is $message")
        val dayOfWeek = intent?.getStringExtra("dayOfWeek")
        Timber.i("The extra dayOfWeek is $dayOfWeek")
        val assessmentType = intent?.getStringExtra("assessmentType")
        Timber.i("The assessment type is $assessmentType")

        if (!message.isNullOrEmpty()) {
            notificationManager.sendNotification(
                message = message,
                dayOfWeekString = dayOfWeek,
                assessmentTypeString = assessmentType,
                context = context
            )
        }
    }
}