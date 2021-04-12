package com.example.android.mycampusapp.timetable.service

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import androidx.core.content.ContextCompat
import com.example.android.mycampusapp.timetable.receiver.TimetableAlarmReceiver
import com.example.android.mycampusapp.util.getTodayEnumDay
import com.example.android.mycampusapp.util.sendNotification
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import timber.log.Timber

class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Timber.i("A new message has been received from ${remoteMessage.from}")
        Timber.i("The message is ${remoteMessage.data}")
        val notificationMessage = remoteMessage.data["message"]
        val setAlarmId = remoteMessage.data["setAlarmId"]
        val cancelAlarmId = remoteMessage.data["cancelAlarmId"]
        notificationMessage?.let { sendNotification(it) }
        val intent = Intent(applicationContext, TimetableAlarmReceiver::class.java)
        setAlarmId?.let { requestCode ->
            Timber.i("The notification id is ${requestCode.toInt()}")
        }
        cancelAlarmId?.let { requestCode ->
            val pendingIntent = PendingIntent.getBroadcast(
                applicationContext,
                requestCode.toInt(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
            val alarmManager = applicationContext.getSystemService(ALARM_SERVICE) as AlarmManager
            alarmManager.cancel(pendingIntent)
            Timber.i("The cancel alarm id is ${requestCode.toInt()}")
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Timber.i("A new token has been received $token")
    }

    private fun sendNotification(message: String) {
        val notificationManager = ContextCompat.getSystemService(
            applicationContext,
            NotificationManager::class.java
        ) as NotificationManager
        notificationManager.sendNotification(message, getTodayEnumDay(), applicationContext)
    }
}