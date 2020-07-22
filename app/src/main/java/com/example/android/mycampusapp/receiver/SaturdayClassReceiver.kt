package com.example.android.mycampusapp.receiver

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.example.android.mycampusapp.data.SaturdayClass
import com.example.android.mycampusapp.util.sendNotification
import timber.log.Timber

class SaturdayClassReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        val bundle: Bundle? = intent?.extras
        val saturdaySubject = bundle?.getString("saturdaySubject")
        val saturdayTime = bundle?.getString("saturdayTime")
        val notificationManager =
            ContextCompat.getSystemService(
                context,
                NotificationManager::class.java
            ) as NotificationManager
        notificationManager.sendNotification(
            "$saturdaySubject at $saturdayTime",
            context
        )
    }
}