package com.example.passwordmanager.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.passwordmanager.ui.FingerPrintAuth
import com.example.passwordmanager.ui.PasswordListScreen
import com.example.passwordmanager.ui.NotificationSettings

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun PasswordManagerApp() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.PasswordList.route) {
        composable(
            Screen.AuthScreen.route,
            enterTransition = { enterTransition() },
            exitTransition = { exitTransition() },
        ) {
            FingerPrintAuth(navController)
        }
        composable(Screen.PasswordList.route,
            enterTransition = { enterTransition() },
            exitTransition = { exitTransition() }
        ) {
            PasswordListScreen()
        }
        composable(Screen.NotificationSettings.route,
            enterTransition = { enterTransition() },
            exitTransition = { exitTransition() }
        ) {
            NotificationSettings()
        }
    }
}

private fun AnimatedContentTransitionScope<NavBackStackEntry>.exitTransition() =
    slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(800))

private fun AnimatedContentTransitionScope<NavBackStackEntry>.enterTransition() =
    slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start, tween(800))