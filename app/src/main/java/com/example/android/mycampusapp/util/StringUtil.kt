package com.example.android.mycampusapp.util

import android.util.Patterns

fun String?.isValidEmail() = !isNullOrEmpty()&& Patterns.EMAIL_ADDRESS.matcher(this).matches()
fun String.removeWhiteSpace() = replace("\\s".toRegex(),"")