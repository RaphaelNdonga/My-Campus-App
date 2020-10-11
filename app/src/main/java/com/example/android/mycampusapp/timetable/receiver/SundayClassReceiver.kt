package com.example.android.mycampusapp.timetable.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.android.mycampusapp.timetable.service.TimetableService
import com.example.android.mycampusapp.util.SUBJECT
import com.example.android.mycampusapp.util.TIME

class SundayClassReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        val bundle = intent?.extras
        val sundaySubject = bundle?.getString("sundaySubject")
        val sundayTime = bundle?.getString("sundayTime")

        val timetableServiceIntent = Intent(context,TimetableService::class.java)
        timetableServiceIntent.putExtra(SUBJECT,sundaySubject)
        timetableServiceIntent.putExtra(TIME,sundayTime)

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            context.startForegroundService(timetableServiceIntent)
            return
        }
        context.startService(timetableServiceIntent)
    }
}