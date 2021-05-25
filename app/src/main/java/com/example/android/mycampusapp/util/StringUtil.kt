package com.example.android.mycampusapp.util

import android.util.Patterns

fun String.isValidEmail() = Patterns.EMAIL_ADDRESS.matcher(this).matches()
fun String.isValidMessagingTopic() = contains("[^a-zA-Z0-9-_.~%]".toRegex())
fun String.isValidUrl() = Patterns.WEB_URL.matcher(this).matches()