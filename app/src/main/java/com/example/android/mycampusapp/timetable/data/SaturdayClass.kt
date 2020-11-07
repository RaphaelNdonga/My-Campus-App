package com.example.android.mycampusapp.timetable.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class SaturdayClass(
    val id: String = UUID.randomUUID().toString(),
    val subject: String,
    val time: String,
    val alarmRequestCode:Int = Random().nextInt(Integer.MAX_VALUE)
):Parcelable