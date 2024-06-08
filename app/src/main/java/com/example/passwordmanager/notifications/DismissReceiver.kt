package com.example.passwordmanager.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat

class DismissReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent : Intent?) {
        context?.let {
            NotificationManagerCompat.from(it).cancel(1)
        }
    }

}