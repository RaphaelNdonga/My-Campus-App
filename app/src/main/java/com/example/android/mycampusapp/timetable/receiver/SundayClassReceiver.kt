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

class SundayClassReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        val bundle = intent?.extras
        val sundaySubject = bundle?.getString("sundaySubject")
        val sundayTime = bundle?.getString("sundayTime")
        Toast.makeText(context,"My Campus App Sunday alarm Received", Toast.LENGTH_SHORT).show()

        val calendar = Calendar.getInstance()
        if (CalendarUtils.isNotificationDay(calendar, Calendar.SUNDAY)) {
            val timetableServiceIntent = Intent(context, TimetableService::class.java)
            timetableServiceIntent.putExtra(SUBJECT, sundaySubject)
            timetableServiceIntent.putExtra(TIME, sundayTime)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(timetableServiceIntent)
                return
            }
            context.startService(timetableServiceIntent)
        }
    }
}