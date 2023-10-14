package com.maldEnz.ps.presentation.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object Util {
    fun getDateTime(): String {
        val format = SimpleDateFormat("HH:mm dd-MM-yyyy", Locale.getDefault())
        val dateTime = Date()
        return format.format(dateTime)
    }
}
