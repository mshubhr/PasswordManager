package com.example.passwordmanager

import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavBackStackEntry
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.passwordmanager.navigation.Screen
import com.example.passwordmanager.ui.*
import com.example.passwordmanager.ui.theme.PasswordManagerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT
            ),
            navigationBarStyle = SystemBarStyle.light(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT
            ),
        )
        setContent {
            PasswordManagerTheme {
                PasswordManagerApp()
            }
        }
    }
}

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
    }
}

private fun AnimatedContentTransitionScope<NavBackStackEntry>.exitTransition() =
    slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(800))


private fun AnimatedContentTransitionScope<NavBackStackEntry>.enterTransition() =
    slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start, tween(800))

//@Preview(showBackground = true)
//@Composable
//fun DefaultPreview() {
//    PasswordManagerTheme {
//        PasswordManagerApp()
//    }
//}