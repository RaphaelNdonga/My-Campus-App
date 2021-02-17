package com.example.android.mycampusapp.timetable.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import com.example.android.mycampusapp.timetable.service.TimetableService
import com.example.android.mycampusapp.util.SUBJECT
import com.example.android.mycampusapp.util.TIME
import com.example.android.mycampusapp.util.isNotificationDay
import java.util.*

class FridayClassReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        val bundle: Bundle? = intent?.extras
        val fridaySubject = bundle?.getString("fridaySubject")
        val fridayTime = bundle?.getString("fridayTime")
        Toast.makeText(context,"My Campus App Friday alarm Received",Toast.LENGTH_SHORT).show()


        val calendar = Calendar.getInstance()
        if (isNotificationDay(calendar, Calendar.FRIDAY)) {
            val timetableServiceIntent = Intent(context, TimetableService::class.java)
            timetableServiceIntent.putExtra(SUBJECT, fridaySubject)
            timetableServiceIntent.putExtra(TIME, fridayTime)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(timetableServiceIntent)
                return
            }
            context.startService(timetableServiceIntent)
        }
    }
}