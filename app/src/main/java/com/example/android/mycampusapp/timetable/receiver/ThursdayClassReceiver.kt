package com.example.android.mycampusapp.timetable.receiver

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.example.android.mycampusapp.util.sendNotification

class ThursdayClassReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val bundle = intent?.extras
        val thursdaySubject = bundle?.getString("thursdaySubject")
        val thursdayTime = bundle?.getString("thursdayTime")

        val notificationManager = ContextCompat.getSystemService(
            context,
            NotificationManager::class.java
        ) as NotificationManager

        notificationManager.sendNotification("$thursdaySubject at $thursdayTime",context)
    }
}