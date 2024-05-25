package com.example.passwordmanager.ui

import androidx.biometric.BiometricManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavHostController
import com.example.passwordmanager.auth.BiometricAuthenticator
import com.example.passwordmanager.navigation.Screen
import com.example.passwordmanager.ui.appComponents.TextComponent

@Composable
fun FingerPrintAuth(nav: NavHostController) {
    val activity = LocalContext.current as FragmentActivity
    val biometricAuthenticator = BiometricAuthenticator(activity)
    val biometricManager = BiometricManager.from(activity)
    val canAuthenticate = biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)

    if (canAuthenticate != BiometricManager.BIOMETRIC_SUCCESS) {
        // Handle cases where biometric authentication is not available
        Column(
            Modifier
                .fillMaxSize()
                .background(Color.Black.copy(0.7f)),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextComponent(text = "Biometric authentication not available or not set up", textColor = Color.White)
        }
        return
    }

    var message by remember { mutableStateOf("") }
    var id by remember { mutableIntStateOf(0) }

    LaunchedEffect(key1 = true) {
        biometricAuthenticator.promptBioMetricAuth(
            title = "Login",
            subTitle = "Use your fingerprint or face ID",
            negativeButtonText = "Cancel",
            onSuccess = {
                message = "Success"
                id = 1
                nav.navigate(Screen.PasswordList.route)
            },
            onFailed = {
                message = "Wrong Fingerprint or Face ID"
                id = -1
                android.util.Log.d("BiometricAuth", "Authentication failed.")
            },
            onError = { code, error ->
                message = error
                id = -1
                android.util.Log.d("BiometricAuth", "Error code: $code, message: $error")
            }
        )
    }

    if (id == -1) {
        Column(
            Modifier
                .fillMaxSize()
                .background(Color.Black.copy(0.7f)),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.background),
                onClick = {
                    biometricAuthenticator.promptBioMetricAuth(
                        title = "Login",
                        subTitle = "Use your fingerprint or face ID",
                        negativeButtonText = "Cancel",
                        onSuccess = {
                            message = "Success"
                            id = 1
                            nav.navigate(Screen.PasswordList.route)
                        },
                        onFailed = {
                            message = "Wrong Fingerprint or Face ID"
                            id = -1
                            android.util.Log.d("BiometricAuth", "Authentication failed.")
                        },
                        onError = { code, error ->
                            message = error
                            id = -1
                            android.util.Log.d("BiometricAuth", "Error code: $code, message: $error")
                        }
                    )
                }) {
                TextComponent(
                    text = "Try Again",
                    textColor = MaterialTheme.colorScheme.onBackground
                )
            }
            Spacer(modifier = Modifier.height(10.dp))

            TextComponent(text = "Current Status: $message", textColor = Color.White)
        }
    }
}