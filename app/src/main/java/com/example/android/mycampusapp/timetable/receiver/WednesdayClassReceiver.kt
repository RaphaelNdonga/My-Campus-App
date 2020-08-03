package com.example.android.mycampusapp.timetable.receiver

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.example.android.mycampusapp.util.sendNotification

class WednesdayClassReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        val bundle: Bundle? = intent?.extras
        val wednesdaySubject = bundle?.getString("wednesdaySubject")
        val wednesdayTime = bundle?.getString("wednesdayTime")
        val notificationManager =
            ContextCompat.getSystemService(
                context,
                NotificationManager::class.java
            ) as NotificationManager
        notificationManager.sendNotification(
            "$wednesdaySubject at $wednesdayTime",
            context
        )
    }
}