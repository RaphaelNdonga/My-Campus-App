package com.example.android.mycampusapp.timetable.data

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class TuesdayClass(
    @PrimaryKey
    var id: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "subject")
    var subject: String,
    @ColumnInfo(name = "time")
    var time: String
) : Parcelable