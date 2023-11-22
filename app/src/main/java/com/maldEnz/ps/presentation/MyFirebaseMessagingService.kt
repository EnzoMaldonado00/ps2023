package com.maldEnz.ps.presentation

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.maldEnz.ps.R

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("New Token", "Refreshed token: $token")
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val data = remoteMessage.data
        showCustomNotification(data)
    }

    @SuppressLint("RemoteViewLayout")
    private fun showCustomNotification(data: Map<String, String>) {
        val customLayout = RemoteViews(packageName, R.layout.notification)

        val title = data["title"]
        val message = data["body"]
        val profilePicture = data["profilePicture"]

        customLayout.setTextViewText(R.id.title_notification, title)
        customLayout.setTextViewText(R.id.msg_notification, message)
        customLayout.setImageViewResource(R.id.profile_picture_notification, R.drawable.ic_default_acc)
        Glide.with(this)
            .asBitmap()
            .load(profilePicture)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    customLayout.setImageViewBitmap(R.id.profile_picture_notification, resource)

                    // Crear la notificación usando NotificationCompat.Builder
                    val notification = NotificationCompat.Builder(baseContext, "channel_id")
                        .setSmallIcon(R.drawable.fire_0)
                        .setStyle(NotificationCompat.DecoratedCustomViewStyle())
                        .setCustomContentView(customLayout)
                        .build()

                    // Mostrar la notificación
                    val notificationManager =
                        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    notificationManager.notify(1, notification)
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    Log.e("Notification", "Error loading image")
                }
            })
    }
}
