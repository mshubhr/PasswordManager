package com.example.passwordmanager.ui

import androidx.biometric.BiometricManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
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
        ShowUnavailableMessage()
        return
    }

    var message by remember { mutableStateOf("") }
    var id by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        authenticate(biometricAuthenticator, nav, { message = it }, { id = it })
    }

    if (id == -1) {
        RetryAuthentication(biometricAuthenticator, nav, message, { message = it }, { id = it })
    }
}

@Composable
fun ShowUnavailableMessage() {
    Column(
        Modifier
            .fillMaxSize()
            .background(Color.Black.copy(0.7f)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextComponent(text = "Biometric authentication not available or not set up", textColor = Color.White)
    }
}

@Composable
fun RetryAuthentication(
    biometricAuthenticator: BiometricAuthenticator,
    nav: NavHostController,
    message: String,
    setMessage: (String) -> Unit,
    setId: (Int) -> Unit
) {
    Column(
        Modifier
            .fillMaxSize()
            .background(Color.Black.copy(0.7f)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.background),
            onClick = { authenticate(biometricAuthenticator, nav, setMessage, setId) }
        ) {
            TextComponent(text = "Try Again", textColor = MaterialTheme.colorScheme.onBackground)
        }
        Spacer(modifier = Modifier.height(10.dp))
        TextComponent(text = "Current Status: $message", textColor = Color.White)
    }
}

fun authenticate(
    biometricAuthenticator: BiometricAuthenticator,
    nav: NavHostController,
    setMessage: (String) -> Unit,
    setId: (Int) -> Unit
) {
    biometricAuthenticator.promptBioMetricAuth(
        title = "Login",
        subTitle = "Use your fingerprint or face ID",
        negativeButtonText = "Cancel",
        onSuccess = {
            setMessage("Success")
            setId(1)
            nav.navigate(Screen.PasswordList.route)
        },
        onFailed = {
            setMessage("Wrong Fingerprint or Face ID")
            setId(-1)
        },
        onError = { _, error ->
            setMessage(error)
            setId(-1)
        }
    )
}