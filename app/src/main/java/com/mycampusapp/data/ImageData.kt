package com.mycampusapp.data

import java.util.*

data class ImageData(
    val id:String = System.currentTimeMillis().toString(),
    val imageUrl:String = ""
    )