package com.example.android.mycampusapp.receiver

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.example.android.mycampusapp.data.FridayClass
import com.example.android.mycampusapp.util.sendNotification
import timber.log.Timber

class FridayClassReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        val bundle: Bundle? = intent?.extras
        val fridaySubject = bundle?.getString("fridaySubject")
        val fridayTime = bundle?.getString("fridayTime")
        val notificationManager =
            ContextCompat.getSystemService(
                context,
                NotificationManager::class.java
            ) as NotificationManager
        notificationManager.sendNotification(
            "$fridaySubject at $fridayTime",
            context
        )
    }
}