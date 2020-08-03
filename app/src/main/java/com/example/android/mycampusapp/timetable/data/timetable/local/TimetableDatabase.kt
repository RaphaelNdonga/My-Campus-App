package com.example.android.mycampusapp.timetable.data.timetable.local

import androidx.room.Database

import androidx.room.RoomDatabase
import com.example.android.mycampusapp.timetable.data.*

@Database(
    entities = [
        MondayClass::class,
        TuesdayClass::class,
        WednesdayClass::class,
        ThursdayClass::class,
        FridayClass::class,
        SaturdayClass::class,
        SundayClass::class],
    version = 1,
    exportSchema = false
)
abstract class TimetableDatabase : RoomDatabase() {
    abstract fun timetableDao(): TimetableDao
}