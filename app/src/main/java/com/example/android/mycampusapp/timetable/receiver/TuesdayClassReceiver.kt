package com.example.android.mycampusapp.timetable.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import com.example.android.mycampusapp.timetable.service.TimetableService
import com.example.android.mycampusapp.util.SUBJECT
import com.example.android.mycampusapp.util.TIME
import com.example.android.mycampusapp.util.isNotificationDay
import timber.log.Timber
import java.util.*

class TuesdayClassReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        val bundle: Bundle? = intent?.extras
        val tuesdaySubject = bundle?.getString("tuesdaySubject")
        val tuesdayTime = bundle?.getString("tuesdayTime")

        val calendar = Calendar.getInstance()
        Timber.i("${isNotificationDay(calendar,Calendar.TUESDAY)}")
        if (isNotificationDay(calendar,Calendar.TUESDAY)) {
            val timetableServiceIntent = Intent(context, TimetableService::class.java)
            timetableServiceIntent.putExtra(SUBJECT, tuesdaySubject)
            timetableServiceIntent.putExtra(TIME, tuesdayTime)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(timetableServiceIntent)
                return
            }
            context.startService(timetableServiceIntent)
        }
    }
}


