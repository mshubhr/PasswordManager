package com.example.passwordmanager

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.passwordmanager.navigation.PasswordManagerApp
import com.example.passwordmanager.notifications.NotificationActionReceiver
import com.example.passwordmanager.ui.theme.PasswordManagerTheme
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class MainActivity : FragmentActivity() {

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            scheduleNotifications(this, getNotificationTime())
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createNotificationChannel(this)
        handlePermissions()
        enableEdgeToEdge()
        setContent {
            PasswordManagerTheme { PasswordManagerApp() }
        }
        checkBiometricSupport()
        showBiometricPrompt()
        showNotificationWithBigPicture()
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun handlePermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    private fun checkBiometricSupport() {
        val biometricManager = BiometricManager.from(this)
        when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {}
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE,
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE,
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED,
            BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED,
            BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED,
            BiometricManager.BIOMETRIC_STATUS_UNKNOWN -> TODO()
        }
    }

    private fun showBiometricPrompt() {
        val executor = ContextCompat.getMainExecutor(this)
        val biometricPrompt = BiometricPrompt(this, executor, object : BiometricPrompt.AuthenticationCallback() {})
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Unlock Lock Box")
            .setSubtitle("Unlock your screen with fingerprint")
            .setNegativeButtonText("Cancel")
            .build()
        biometricPrompt.authenticate(promptInfo)
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Password Manager Channel"
            val descriptionText = "Channel for password manager notifications"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("password_manager_channel", name, importance).apply {
                description = descriptionText
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun getNotificationTime(): Calendar {
        val sharedPreferences = getSharedPreferences("settings", Context.MODE_PRIVATE)
        val hour = sharedPreferences.getInt("notification_hour", 9)
        val minute = sharedPreferences.getInt("notification_minute", 0)
        return Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
        }
    }

    private fun showNotificationWithBigPicture() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.pass)

        val toastIntent = Intent(this, NotificationActionReceiver::class.java).apply {
            action = "TOAST_ACTION"
        }
        val toastPendingIntent: PendingIntent = PendingIntent.getBroadcast(this, 0, toastIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val dismissIntent = Intent(this, NotificationActionReceiver::class.java).apply {
            action = "DISMISS_ACTION"
        }
        val dismissPendingIntent: PendingIntent = PendingIntent.getBroadcast(this, 1, dismissIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val builder = NotificationCompat.Builder(this, "password_manager_channel")
            .setSmallIcon(R.drawable.ic_noti)
            .setContentTitle("Starting")
            .setContentText("A big picture notification")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setLargeIcon(bitmap)
            .setStyle(NotificationCompat.BigPictureStyle().bigPicture(bitmap).bigLargeIcon(null as Bitmap?))
            .addAction(R.drawable.ic_toast, "Toast Message", toastPendingIntent)
            .addAction(R.drawable.ic_dismiss, "Dismiss", dismissPendingIntent)

        notificationManager.notify(1, builder.build())
    }

    private fun scheduleNotifications(context: Context, time: Calendar) {
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
}