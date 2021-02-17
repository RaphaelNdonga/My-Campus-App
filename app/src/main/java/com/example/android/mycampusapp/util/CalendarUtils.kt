package com.example.android.mycampusapp.util

import java.util.*

fun initializeTimetableCalendar(calendar: Calendar) {
    val todayCalendar = Calendar.getInstance()
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    calendar.set(Calendar.YEAR, todayCalendar.get(Calendar.YEAR))
    calendar.set(Calendar.MONTH, todayCalendar.get(Calendar.MONTH))
    calendar.set(Calendar.DAY_OF_MONTH, todayCalendar.get(Calendar.DAY_OF_MONTH))
}

fun isNotificationDay(calendar: Calendar, day: Int): Boolean {
    return calendar.get(Calendar.DAY_OF_WEEK) == day
}