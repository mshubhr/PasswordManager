package com.example.passwordmanager.ui

import android.app.TimePickerDialog
import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import com.example.passwordmanager.notifications.scheduleNotifications
import java.util.*

@Composable
fun NotificationSettings(viewModel: SettingsViewModel = hiltViewModel()) {
    val context = LocalContext.current
    var time by remember { mutableStateOf(Calendar.getInstance()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = "Select Notification Time")
        Spacer(modifier = Modifier.height(20.dp))

        Button(onClick = {
            showTimePicker(context) { selectedTime ->
                time = selectedTime
            }
        }) {
            Text(text = "Select Time")
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(onClick = { viewModel.setNotificationTime(context, time) }) {
            Text(text = "Save")
        }
    }
}

fun showTimePicker(context: Context, onTimeSelected: (Calendar) -> Unit) {
    val currentTime = Calendar.getInstance()
    val hour = currentTime.get(Calendar.HOUR_OF_DAY)
    val minute = currentTime.get(Calendar.MINUTE)

    TimePickerDialog(context, { _, selectedHour, selectedMinute ->
        val selectedTime = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, selectedHour)
            set(Calendar.MINUTE, selectedMinute)
        }
        onTimeSelected(selectedTime)
    }, hour, minute, true).show()
}

class SettingsViewModel : ViewModel() {
    fun setNotificationTime(context: Context, time: Calendar) {
        val sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        sharedPreferences.edit().apply {
            putInt("notification_hour", time.get(Calendar.HOUR_OF_DAY))
            putInt("notification_minute", time.get(Calendar.MINUTE))
            apply()
        }
        scheduleNotifications(context, time)
    }
}