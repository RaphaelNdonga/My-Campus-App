package com.example.android.mycampusapp.timetable.service

import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.content.ContextCompat
import com.example.android.mycampusapp.util.SUBJECT
import com.example.android.mycampusapp.util.TIME
import com.example.android.mycampusapp.util.sendNotification
import timber.log.Timber

class TimetableService : Service() {
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            val subject = intent.getStringExtra(SUBJECT)
            val time = intent.getStringExtra(TIME)
            Timber.i("I have been summoned")

            val notificationManager = ContextCompat.getSystemService(
                this,
                NotificationManager::class.java
            ) as NotificationManager
            notificationManager.sendNotification("$subject at $time",this)
        }
        return START_STICKY
    }
}
