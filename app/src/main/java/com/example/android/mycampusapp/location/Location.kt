package com.example.android.mycampusapp.location

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Location(val name: String, val coordinates: String) : Parcelable {
    override fun toString(): String {
        return name
    }
}