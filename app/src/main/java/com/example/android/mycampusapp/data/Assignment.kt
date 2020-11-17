package com.example.android.mycampusapp.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*
@Parcelize
data class Assignment(
    val id:String = UUID.randomUUID().toString(),
    val subject:String = "",
    val date:String = "",
    val alarmRequestCode:Int = Random().nextInt(Integer.MAX_VALUE)
) : Parcelable