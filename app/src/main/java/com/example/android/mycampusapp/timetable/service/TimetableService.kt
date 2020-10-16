package com.example.android.mycampusapp.timetable.service

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.android.mycampusapp.MainActivity
import com.example.android.mycampusapp.R
import com.example.android.mycampusapp.util.SUBJECT
import com.example.android.mycampusapp.util.TIME
import timber.log.Timber

class TimetableService : Service() {
    private var notificationId: Int = 0
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        notificationId = System.currentTimeMillis().toInt()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val subject = intent?.getStringExtra(SUBJECT)
        val time = intent?.getStringExtra(TIME)
        Timber.i("I have been summoned")

        val message = "$subject at $time"

        val appIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            notificationId,
            appIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notificationBuilder = NotificationCompat.Builder(
            this,
            getString(R.string.timetable_notification_channel_id)
        )
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.drawable.ic_open_book)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentTitle(getString(R.string.class_notification_title))
            .setContentText(message)

        startForeground(notificationId, notificationBuilder.build())

        return START_STICKY
    }
}
