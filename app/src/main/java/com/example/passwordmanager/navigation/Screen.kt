package com.example.passwordmanager.navigation

sealed class Screen(val route: String) {
    data object AuthScreen : Screen("auth")
    data object PasswordList : Screen("passwordList")
    data object NotificationSettings : Screen("notificationSettings")
}