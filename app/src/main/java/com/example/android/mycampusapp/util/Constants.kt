package com.example.android.mycampusapp.util

import java.util.*

const val USER_EMAIL = "email"
const val IS_ADMIN = "admin"
const val COURSE_ID: String = "courseId"
const val sharedPrefFile = "com.example.android.mycampusapp"
const val SUBJECT = "SUBJECT"
const val TIME = "TIME"
const val RUN_DAILY:Long = 24*60*60*1000


fun initializeTimetableCalendar(calendar: Calendar){
    val todayCalendar = Calendar.getInstance()
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    calendar.set(Calendar.YEAR,todayCalendar.get(Calendar.YEAR))
    calendar.set(Calendar.MONTH,todayCalendar.get(Calendar.MONTH))
    calendar.set(Calendar.DAY_OF_MONTH,todayCalendar.get(Calendar.DAY_OF_MONTH))
}

fun isNotificationDay(calendar:Calendar,day:Int):Boolean{
    return calendar.get(Calendar.DAY_OF_WEEK) == day
}