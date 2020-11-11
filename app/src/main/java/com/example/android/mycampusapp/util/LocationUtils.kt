package com.example.android.mycampusapp.util

import com.example.android.mycampusapp.timetable.data.Location

object LocationUtils {
    fun getJkuatLocations(): List<Location> {
        return listOf(
            Location("New Science Complex Lecture Theaters(NSC)", "geo:-1.1003381,37.0122877"),
            Location("Technology House, JKUAT", "geo:-1.0952965,37.0116225"),
            Location("Swimming Pool Annex(SPA)", "geo:-1.0935708,37.0148197"),
            Location("ICSIT Computer Building(SCIT)", "geo:-1.0945107,37.0127597"),
            Location("Assembly Hall", "geo:-1.0953694,37.0144625"),
            Location("Common Lecture Building(CLB)", "geo:-1.0951335,37.0135683"),
            Location("Engineering Lab Building(ELB)", "geo:-1.0963041,37.0133645"),
            Location("B.E.E.D Building", "geo:-1.0937191,37.0136033"),
            Location("IEET Block", "geo:-1.0938886,37.01199"),
            Location("Food Science Building", "geo:-1.0947168,37.012475"),
            Location("Pavillion Building", "geo:-1.0955253,37.0128953"),
            Location("Photogrammetry Lab", "geo:-1.0955927,37.0122743"),
            Location("Pan African University(PAU) ","geo:-1.0984353,37.0143342")
        )
    }
}