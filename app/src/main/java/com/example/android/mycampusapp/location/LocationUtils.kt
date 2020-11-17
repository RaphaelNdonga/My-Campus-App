package com.example.android.mycampusapp.location

import com.example.android.mycampusapp.data.Location

object LocationUtils {
    fun getJkuatLocations(): List<Location> {
        return listOf(
            Location(
                "New Science Complex Lecture Theaters(NSC)",
                "geo:-1.1003381,37.0122877?q=New+Science+Complex+Lecture+Theaters(NSC)"
            ),
            Location(
                "Technology House, JKUAT",
                "geo:-1.0952965,37.0116225?q=Technology+House,+JKUAT"
            ),
            Location(
                "Swimming Pool Annex(SPA)",
                "geo:-1.0935708,37.0148197?q=Swimming+Pool+Annex(SPA)"
            ),
            Location(
                "ICSIT Computer Building(SCIT)",
                "geo:-1.0945107,37.0127597?q=ICSIT+Computer+Building"
            ),
            Location("Assembly Hall", "geo:-1.0953694,37.0144625?q=Discover+JKUAT+Assembly+Hall"),
            Location(
                "Common Lecture Building(CLB)",
                "geo:-1.0951335,37.0135683?q=Common+Lecture+Building(CLB)"
            ),
            Location(
                "Engineering Lab Building(ELB)",
                "geo:-1.0963041,37.0133645?q=Engineering+Lab+Building(ELB)"
            ),
            Location("B.E.E.D Building", "geo:-1.0937191,37.0136033?q=B.E.E.D+Building"),
            Location("IEET Block", "geo:-1.0938886,37.01199?q=IEET+Block"),
            Location("Food Science Building", "geo:-1.0947168,37.012475?q=Food+Science+Building"),
            Location("Pavillion Building", "geo:-1.0955253,37.0128953?q=Pavillion+Building"),
            Location("Photogrammetry Lab", "geo:-1.0955927,37.0122743?z=21&q=Photogrammetry+Lab+JKUAT"),
            Location(
                "Pan African University(PAU) ",
                "geo:-1.0984353,37.0143342?q=Pan+African+University+Institute+for+Basic+Sciences+Technology+and+Innovation+(PAUSTI)+Block+A"
            )
        )
    }
}