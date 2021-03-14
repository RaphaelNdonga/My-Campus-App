package com.example.android.mycampusapp.timetable.service

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import timber.log.Timber

class MyFirebaseMessagingService:FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Timber.i("A new message has been received from ${remoteMessage.from}")
        Timber.i("The message is ${remoteMessage.data}")
    }
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Timber.i("A new token has been received $token")
    }
}