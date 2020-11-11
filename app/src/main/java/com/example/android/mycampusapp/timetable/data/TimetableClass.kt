package com.example.android.mycampusapp.timetable.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class TimetableClass(
    val id: String = UUID.randomUUID().toString(),
    val subject:String = "",
    val time: String = "",
    val locationName:String = "",
    val locationCoordinates:String = "",
    val alarmRequestCode:Int = Random().nextInt(Integer.MAX_VALUE)
) : Parcelable

/**
 * The values have to be initialized to make it possible for firebase to convert the document
 * to this object here
 **/