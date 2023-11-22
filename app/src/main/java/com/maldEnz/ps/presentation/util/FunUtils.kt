package com.maldEnz.ps.presentation.util

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
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

    @SuppressLint("MissingPermission")
    fun createNotification(
        messageContent: String,
        context: Context,
        senderName: String,
    ) {
        /*val futureTarget = Glide.with(context)
            .asBitmap()
            .load(image)
            .submit()

        val senderIcon: Bitmap = futureTarget.get()*/

        val notificationBuilder = NotificationCompat.Builder(context, "YOUR_CHANNEL_ID")
            .setSmallIcon(R.drawable.baseline_camera_alt_24)
            .setContentTitle(senderName)
            .setContentText(messageContent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        val notificationManager = NotificationManagerCompat.from(context)

        // Crear un canal de notificación para versiones >= Oreo
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "YOUR_CHANNEL_ID",
                "Your_Channel_Name",
                NotificationManager.IMPORTANCE_DEFAULT,
            )
            notificationManager.createNotificationChannel(channel)
        }

        // Mostrar la notificación
        val notificationId = 1 // Identificador único para la notificación
        notificationManager.notify(notificationId, notificationBuilder.build())
    }
}
