package com.example.passwordmanager

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.example.passwordmanager.navigation.PasswordManagerApp
import com.example.passwordmanager.notifications.scheduleNotifications
import com.example.passwordmanager.ui.theme.PasswordManagerTheme
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class MainActivity : FragmentActivity() {

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
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
        setContent { PasswordManagerTheme { PasswordManagerApp() } }
        checkBiometricSupport()
        showBiometricPrompt()
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun handlePermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED -> { }
                else -> requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun checkBiometricSupport() {
        val biometricManager = BiometricManager.from(this)
        when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {}
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {}
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {}
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {}
            BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED -> TODO()
            BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED -> TODO()
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
}