package com.example.android.mycampusapp.timetable.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import com.example.android.mycampusapp.timetable.service.TimetableService
import com.example.android.mycampusapp.util.CalendarUtils
import com.example.android.mycampusapp.util.SUBJECT
import com.example.android.mycampusapp.util.TIME
import java.util.*

class ThursdayClassReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        val bundle = intent?.extras
        val thursdaySubject = bundle?.getString("thursdaySubject")
        val thursdayTime = bundle?.getString("thursdayTime")
        Toast.makeText(context,"My Campus App Thursday alarm Received", Toast.LENGTH_SHORT).show()

        val calendar = Calendar.getInstance()
        if (CalendarUtils.isNotificationDay(calendar, Calendar.THURSDAY)) {
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