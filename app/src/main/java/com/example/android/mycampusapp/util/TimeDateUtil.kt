package com.example.android.mycampusapp.util

import com.example.android.mycampusapp.data.CustomDate
import com.example.android.mycampusapp.data.CustomTime


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

fun addZeroText(int: Int):String{
    return if(int<10){
        "0$int"
    }else{
        "$int"
    }
}
fun addMonthText(int:Int):String{
    return when(int){
        0->{
            "January"
        }
        1->{
            "February"
        }
        2->{
            "March"
        }
        3->{
            "April"
        }
        4->{
            "May"
        }
        5->{
            "June"
        }
        6->{
            "July"
        }
        7->{
            "August"
        }
        8->{
            "September"
        }
        9->{
            "October"
        }
        10->{
            "November"
        }
        11->{
            "December"
        }
        else -> throw IllegalArgumentException("No such month input should be obtained")
    }
}