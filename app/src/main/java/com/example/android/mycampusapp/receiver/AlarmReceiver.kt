package com.example.android.mycampusapp.receiver

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.android.mycampusapp.R
import com.example.android.mycampusapp.util.sendNotification

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
//        Toast.makeText(context,context?.getString(R.string.class_notification_title),Toast.LENGTH_LONG).show()
        val notificationManager =
            ContextCompat.getSystemService(
                context,
                NotificationManager::class.java
            ) as NotificationManager
        notificationManager.sendNotification("Class Time",context)
    }
}