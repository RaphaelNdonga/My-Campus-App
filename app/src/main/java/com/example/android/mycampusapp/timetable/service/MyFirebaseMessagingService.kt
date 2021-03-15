package com.example.android.mycampusapp.timetable.service

import android.app.NotificationManager
import androidx.core.content.ContextCompat
import com.example.android.mycampusapp.util.sendNotification
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import timber.log.Timber

class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Timber.i("A new message has been received from ${remoteMessage.from}")
        Timber.i("The message is ${remoteMessage.data}")
        Timber.i("The notification is ${remoteMessage.data["message"]}")
        val notificationMessage = remoteMessage.data["message"]
        notificationMessage?.let { sendNotification(it) }
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
        notificationManager.sendNotification(message, applicationContext)
    }
}