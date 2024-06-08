package com.example.passwordmanager.notifications

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.passwordmanager.R

@SuppressLint("MissingPermission")
fun createNotification(context: Context) {
    val toastIntent = Intent(context, ToastReceiver::class.java)
    val pendingIntent = PendingIntent.getBroadcast(context, 0, toastIntent, PendingIntent.FLAG_UPDATE_CURRENT)
    val dismissIntent = Intent(context, DismissReceiver::class.java)
    val pendingDismissIntent = PendingIntent.getBroadcast(context, 1, dismissIntent, PendingIntent.FLAG_UPDATE_CURRENT)

    val builder = NotificationCompat.Builder(context, "password_manager")
        .setSmallIcon(R.drawable.ic_noti)
        .setContentTitle("Password Manager")
        .setContentText("Choose an action")
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setAutoCancel(true)
        .addAction(R.drawable.ic_toast, "Show Toast", pendingIntent)
        .addAction(R.drawable.ic_dismiss, "Dismiss", pendingDismissIntent)

    with(NotificationManagerCompat.from(context)) {
        notify(1, builder.build())
    }
}