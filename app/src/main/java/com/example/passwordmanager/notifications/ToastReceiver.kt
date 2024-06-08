package com.example.passwordmanager.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class ToastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        context?.let {
            Toast.makeText(it, "Think about Password", Toast.LENGTH_SHORT).show()
        }
    }
}