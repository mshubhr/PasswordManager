package com.example.passwordmanager.ui.appComponents

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.sp

@Composable
fun PasswordText(password: String) {
    Text(
        buildAnnotatedString {
            repeat(password.length) {
                append('●')
            }
        },
        color = Color(0xffc6c6c6),
        fontSize = 18.sp
    )
}