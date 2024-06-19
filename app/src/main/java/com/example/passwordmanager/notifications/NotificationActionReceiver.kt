package com.example.passwordmanager.notifications

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class NotificationActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            "TOAST_ACTION" -> {
                Toast.makeText(context, "App is open", Toast.LENGTH_SHORT).show()
            }
            "DISMISS_ACTION" -> {
                val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.cancel(1)
            }
        }
    }
}