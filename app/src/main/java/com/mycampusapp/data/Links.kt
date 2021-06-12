package com.mycampusapp.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class Links(
    val id: String = UUID.randomUUID().toString(),
    val subject:String = "",
    val link:String = ""
):Parcelable
