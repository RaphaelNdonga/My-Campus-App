package com.example.android.mycampusapp.util

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.android.mycampusapp.MainActivity
import com.example.android.mycampusapp.R

private const val REQUEST_CODE = 0
private const val NOTIFICATION_ID = 0
fun NotificationManager.sendNotification(message: String, context: Context) {
    val intent = Intent(context, MainActivity::class.java)
    val pendingIntent =
        PendingIntent.getActivity(context, REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT)

    val notificationBuilder = NotificationCompat.Builder(
        context,
        context.getString(R.string.timetable_notification_channel_id))
        .setContentIntent(pendingIntent)
        .setContentText(message)
        .setContentTitle(context.getString(R.string.app_name))
        .setSmallIcon(R.drawable.ic_open_book)
        .setStyle(NotificationCompat.BigTextStyle().bigText(message))
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setAutoCancel(true)

    notify(NOTIFICATION_ID,notificationBuilder.build())
    }

fun NotificationManager.cancelNotification(){
    cancelAll()
}