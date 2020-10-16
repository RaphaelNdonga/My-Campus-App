package com.example.android.mycampusapp.timetable.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.android.mycampusapp.timetable.service.TimetableService
import com.example.android.mycampusapp.util.SUBJECT
import com.example.android.mycampusapp.util.TIME
import com.example.android.mycampusapp.util.isNotificationDay
import timber.log.Timber
import java.util.*

class ThursdayClassReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        val bundle = intent?.extras
        val thursdaySubject = bundle?.getString("thursdaySubject")
        val thursdayTime = bundle?.getString("thursdayTime")

        val calendar = Calendar.getInstance()
        Timber.i("notification day is ${isNotificationDay(calendar, Calendar.THURSDAY)}")

        if (isNotificationDay(calendar, Calendar.THURSDAY)) {
            val timetableServiceIntent = Intent(context, TimetableService::class.java)
            timetableServiceIntent.putExtra(SUBJECT, thursdaySubject)
            timetableServiceIntent.putExtra(TIME, thursdayTime)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(timetableServiceIntent)
                return
            }
            context.startService(timetableServiceIntent)
        }
    }
}