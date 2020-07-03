package com.example.android.mycampusapp.data

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "monday_table")
data class MondayClass(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    @ColumnInfo(name = "subject")
    var subject: String,
    @ColumnInfo(name = "time")
    var time: String
) : Parcelable