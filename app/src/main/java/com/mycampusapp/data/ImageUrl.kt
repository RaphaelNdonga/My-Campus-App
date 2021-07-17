package com.mycampusapp.data

import java.util.*

data class ImageUrl(
    val id:String = UUID.randomUUID().toString(),
    val imageUrl:String = ""
    )