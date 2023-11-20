package com.maldEnz.ps.presentation.util

import android.content.Context
import com.maldEnz.ps.R
import org.threeten.bp.ZoneId
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object FunUtils {
    fun getDateTime(): String {
        val format = SimpleDateFormat("HH:mm dd-MM-yyyy", Locale.getDefault())
        val dateTime = Date()
        return format.format(dateTime)
    }

    fun unifyDateTime(dateTime: String, dateTimeZone: String): String {
        val zoneId = ZoneId.of(dateTimeZone).toString()

        val originalFormat = SimpleDateFormat("HH:mm dd-MM-yyyy", Locale.getDefault())
        originalFormat.timeZone = TimeZone.getTimeZone(zoneId)
        val originalDate = originalFormat.parse(dateTime)
        val targetFormat = SimpleDateFormat("HH:mm dd-MM-yyyy", Locale.getDefault())
        targetFormat.timeZone = TimeZone.getDefault()

        return targetFormat.format(originalDate!!)
    }

    fun setAppTheme(context: Context) {
        val sharedPreferences = context.getSharedPreferences("ThemePrefs", Context.MODE_PRIVATE)

        when (sharedPreferences.getString("selectedTheme", "2131886364")) {
            "Green Theme" -> context.setTheme(R.style.GreenTheme)
            "Pink Theme" -> context.setTheme(R.style.PinkTheme)
            "Brown Theme" -> context.setTheme(R.style.BrownTheme)

            else -> context.setTheme(R.style.DefaultTheme)
        }
    }
}
