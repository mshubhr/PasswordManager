package com.example.passwordmanager.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.util.Calendar

fun scheduleNotifications(context: Context) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val pendingIntent = PendingIntent.getBroadcast(context, 0, Intent(context, NotificationReceiver::class.java), PendingIntent.FLAG_UPDATE_CURRENT)

    val calender = Calendar.getInstance().apply {
        timeInMillis = System.currentTimeMillis()
        set(Calendar.HOUR_OF_DAY, 8)
        set(Calendar.MINUTE, 0)
    }

    alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calender.timeInMillis, AlarmManager.INTERVAL_DAY, pendingIntent)
}