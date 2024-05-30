package com.example.passwordmanager.notifications

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.passwordmanager.R

class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            val notificationManager = NotificationManagerCompat.from(context)
            val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_noti)
                .setContentTitle("Password Manager")
                .setContentText("This is your repeated notification.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .build()
            notificationManager.notify(NOTIFICATION_ID, notification)
        }
    }

    companion object {
        const val CHANNEL_ID = "password_manager_channel"
        const val NOTIFICATION_ID = 2
    }
}
