package com.example.android.mycampusapp.timetable.receiver

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.example.android.mycampusapp.util.sendNotification

class SundayClassReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val bundle = intent?.extras
        val sundaySubject = bundle?.getString("sundaySubject")
        val sundayTime = bundle?.getString("sundayTime")

        val notificationManager = ContextCompat.getSystemService(
            context,
            NotificationManager::class.java
        ) as NotificationManager

        notificationManager.sendNotification("$sundaySubject at $sundayTime",context)
    }
}