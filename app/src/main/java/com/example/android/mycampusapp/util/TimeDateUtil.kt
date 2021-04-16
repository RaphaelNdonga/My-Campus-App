package com.example.android.mycampusapp.util

import com.example.android.mycampusapp.data.CustomDate
import com.example.android.mycampusapp.data.CustomTime
import com.example.android.mycampusapp.data.TimetableClass
import java.util.*

fun getTomorrowTimetableCalendar(timetableClass: TimetableClass): Calendar {
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.HOUR_OF_DAY, timetableClass.hour)
    calendar.set(Calendar.MINUTE, timetableClass.minute)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    calendar.add(Calendar.DAY_OF_WEEK, 1)
    return calendar
}

fun getTodayTimetableCalendar(timetableClass: TimetableClass): Calendar {
    return Calendar.getInstance().also {
        it.set(Calendar.HOUR_OF_DAY, timetableClass.hour)
        it.set(Calendar.MINUTE, timetableClass.minute)
        it.set(Calendar.SECOND, 0)
        it.set(Calendar.MILLISECOND, 0)
    }
}

fun getTimetableCalendar(timetableClass: TimetableClass, dayOfWeek: DayOfWeek): Calendar {
    return Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, timetableClass.hour)
        set(Calendar.MINUTE, timetableClass.minute)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
        set(Calendar.DAY_OF_WEEK, getDayOfWeekFromEnum(dayOfWeek))
    }
}

fun isLater(timetableClass: TimetableClass): Boolean {
    val now = getCustomTimeNow()
    if (timetableClass.hour > now.hour) {
        return true
    }
    if ((timetableClass.minute > now.minute) && (timetableClass.hour == now.hour)) {
        return true
    }
    return false
}

fun getCustomTimeNow(): CustomTime {
    val calendar = Calendar.getInstance()
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val minute = calendar.get(Calendar.MINUTE)
    return CustomTime(hour, minute)
}

fun getTimetableCustomTime(timetableClass: TimetableClass): CustomTime {
    return CustomTime(timetableClass.hour, timetableClass.minute)
}

fun formatTime(timeSet: CustomTime): String {
    val hourText = "${timeSet.hour}"
    val minuteText = addZeroText(timeSet.minute)
    return "$hourText:$minuteText"
}

fun formatDate(dateSet: CustomDate): String {
    val dayText = dateSet.day
    val monthText = addMonthText(dateSet.month)
    val yearText = dateSet.year

    return "$dayText/$monthText/$yearText"
}

fun addZeroText(int: Int): String {
    return if (int < 10) {
        "0$int"
    } else {
        "$int"
    }
}

fun addMonthText(int: Int): String {
    return when (int) {
        0 -> {
            "January"
        }
        1 -> {
            "February"
        }
        2 -> {
            "March"
        }
        3 -> {
            "April"
        }
        4 -> {
            "May"
        }
        5 -> {
            "June"
        }
        6 -> {
            "July"
        }
        7 -> {
            "August"
        }
        8 -> {
            "September"
        }
        9 -> {
            "October"
        }
        10 -> {
            "November"
        }
        11 -> {
            "December"
        }
        else -> throw IllegalArgumentException("No such month input should be obtained")
    }
}

fun getTodayEnumDay(): DayOfWeek {
    val today = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
    return when (today) {
        Calendar.SUNDAY -> DayOfWeek.SUNDAY
        Calendar.MONDAY -> DayOfWeek.MONDAY
        Calendar.TUESDAY -> DayOfWeek.TUESDAY
        Calendar.WEDNESDAY -> DayOfWeek.WEDNESDAY
        Calendar.THURSDAY -> DayOfWeek.THURSDAY
        Calendar.FRIDAY -> DayOfWeek.FRIDAY
        Calendar.SATURDAY -> DayOfWeek.SATURDAY
        else -> DayOfWeek.SUNDAY
    }
}

fun getTomorrowEnumDay(): DayOfWeek {
    val tomorrow = Calendar.getInstance().get(Calendar.DAY_OF_WEEK).plus(1)
    return when (tomorrow) {
        Calendar.SUNDAY -> DayOfWeek.SUNDAY
        Calendar.MONDAY -> DayOfWeek.MONDAY
        Calendar.TUESDAY -> DayOfWeek.TUESDAY
        Calendar.WEDNESDAY -> DayOfWeek.WEDNESDAY
        Calendar.THURSDAY -> DayOfWeek.THURSDAY
        Calendar.FRIDAY -> DayOfWeek.FRIDAY
        Calendar.SATURDAY -> DayOfWeek.SATURDAY
        else -> DayOfWeek.SUNDAY
    }
}

fun getDayOfWeekFromEnum(dayOfWeek: DayOfWeek): Int {
    return when (dayOfWeek) {
        DayOfWeek.MONDAY -> Calendar.MONDAY
        DayOfWeek.TUESDAY -> Calendar.TUESDAY
        DayOfWeek.WEDNESDAY -> Calendar.WEDNESDAY
        DayOfWeek.THURSDAY -> Calendar.THURSDAY
        DayOfWeek.FRIDAY -> Calendar.FRIDAY
        DayOfWeek.SATURDAY -> Calendar.SATURDAY
        DayOfWeek.SUNDAY -> Calendar.SUNDAY
    }
}


enum class DayOfWeek {
    MONDAY,
    TUESDAY,
    WEDNESDAY,
    THURSDAY,
    FRIDAY,
    SATURDAY,
    SUNDAY
}