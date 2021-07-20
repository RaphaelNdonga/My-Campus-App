package com.mycampusapp.data

data class DocumentData(
    val id:String = System.currentTimeMillis().toString(),
    val url:String = "",
    val fileName:String = ""
)
