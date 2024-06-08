package com.example.passwordmanager.notifications

import android.content.Context
import androidx.work.*
import java.util.concurrent.TimeUnit
import java.util.*

fun scheduleNotifications(context: Context, time: Calendar) {
    createNotification(context)
    val currentTime = Calendar.getInstance()
    val delay = time.timeInMillis - currentTime.timeInMillis
    val initialDelay = if (delay > 0) delay else delay + 24 * 60 * 60 * 1000

    val notificationWork = OneTimeWorkRequestBuilder<NotificationWorker>()
        .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
        .build()

    WorkManager.getInstance(context).enqueue(notificationWork)
}

class NotificationWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
    override fun doWork(): Result {
        return Result.success()
    }
}