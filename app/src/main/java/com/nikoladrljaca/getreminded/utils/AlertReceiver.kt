package com.nikoladrljaca.getreminded.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class AlertReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, p1: Intent?) {
        //this method is called when the alarm is fired
        //meaning send a notification
        createNotificationChannel(context, true)
        sendNotification(context)
    }
}