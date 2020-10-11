package com.example.android.mycampusapp.timetable.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import com.example.android.mycampusapp.timetable.service.TimetableService
import com.example.android.mycampusapp.util.SUBJECT

class SaturdayClassReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        val bundle: Bundle? = intent?.extras
        val saturdaySubject = bundle?.getString("saturdaySubject")
        val saturdayTime = bundle?.getString("saturdayTime")

        val timetableServiceIntent = Intent(context,TimetableService::class.java)
        timetableServiceIntent.putExtra(SUBJECT,saturdaySubject)
        timetableServiceIntent.putExtra(SUBJECT,saturdayTime)

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            context.startForegroundService(timetableServiceIntent)
            return
        }
        context.startService(timetableServiceIntent)
    }
}