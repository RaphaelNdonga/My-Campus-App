package com.example.android.mycampusapp.util

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.android.mycampusapp.MainActivity
import com.example.android.mycampusapp.R

private val NOTIFICATION_ID = 0
fun NotificationManager.sendNotification(message: String, applicationContext: Context) {
    val contentIntent = Intent(applicationContext, MainActivity::class.java)
    val contentPendingIntent = PendingIntent.getActivity(
        applicationContext,
        NOTIFICATION_ID,
        contentIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )
    val builder = NotificationCompat.Builder(
        applicationContext,
        applicationContext.getString(R.string.timetable_notification_channel_id)
    )
        .setSmallIcon(R.drawable.ic_open_book)
        .setAutoCancel(true)
        .setContentTitle(applicationContext.getString(R.string.class_notification_title))
        .setContentText(message)
        .setContentIntent(contentPendingIntent)
        .setPriority(NotificationCompat.PRIORITY_HIGH)

    notify(NOTIFICATION_ID,builder.build())
}
fun NotificationManager.cancelNotification(){
    cancelAll()
}