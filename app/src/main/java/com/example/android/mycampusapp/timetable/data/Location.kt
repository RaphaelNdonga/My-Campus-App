package com.example.android.mycampusapp.timetable.data

data class Location(val name: String, val coordinates: String){
    override fun toString(): String {
        return name
    }
}