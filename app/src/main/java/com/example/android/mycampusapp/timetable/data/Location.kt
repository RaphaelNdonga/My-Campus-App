package com.example.android.mycampusapp.timetable.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Location(val name: String, val coordinates: String):Parcelable