package com.example.android.mycampusapp.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "friday_table")
data class FridayClass(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    @ColumnInfo(name = "subject")
    var subject:String,
    @ColumnInfo(name = "time")
    var time: String
)