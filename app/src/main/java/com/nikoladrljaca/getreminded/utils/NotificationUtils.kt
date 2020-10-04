package com.nikoladrljaca.getreminded.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.nikoladrljaca.getreminded.MainActivity
import com.nikoladrljaca.getreminded.R

fun createNotificationChannel(context: Context, showBadge: Boolean) {
    //check that the OS version is greater than Oreo(api26)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        //create unique id and name, these are displayed in the notification settings
        val channelId = context.packageName
        val channelName = context.getString(R.string.get_reminded_notification_channel_name)
        val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
        channel.description = "App notification channel"
        channel.setShowBadge(showBadge)

        //create the channel using NotificationManager
        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }
}

fun sendNotification(context: Context) {
    val channelId = context.packageName

    val notificationBuilder = NotificationCompat.Builder(context, channelId).apply {
        setSmallIcon(R.drawable.ic_black_time_24)
        setContentTitle(context.getString(R.string.notification_title))
        setContentText(context.getString(R.string.notification_body))
        setStyle(NotificationCompat.BigTextStyle().bigText(context.getString(R.string.notification_body)))
        priority = NotificationCompat.PRIORITY_DEFAULT
        setAutoCancel(true)

        val intent = Intent(context, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)
        setContentIntent(pendingIntent)
    }

    val notificationManager = NotificationManagerCompat.from(context)
    notificationManager.notify(1, notificationBuilder.build())
}

