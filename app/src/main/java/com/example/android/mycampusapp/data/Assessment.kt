package com.example.android.mycampusapp.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*
@Parcelize
data class Assessment(
    val id:String = UUID.randomUUID().toString(),
    val hour:Int = 0,
    val minute:Int = 0,
    val day:Int = 0,
    val month:Int = 0,
    val year:Int = 0,
    val subject:String = "",
    val locationCoordinates:String = "",
    val locationName:String = "",
    val room:String = "",
    val alarmRequestCode:Int = Random().nextInt(Integer.MAX_VALUE)
):Parcelable
