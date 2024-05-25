package com.example.passwordmanager.ui.appComponents

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import com.example.passwordmanager.util.PasswordStrength

@Composable
fun PasswordStrengthIndicator(strength: PasswordStrength) {
    val color = when (strength) {
        PasswordStrength.WEAK -> Color.Red
        PasswordStrength.MEDIUM -> Color.Yellow
        PasswordStrength.STRONG -> Color.Green
    }

    val targetProgress = when (strength) {
        PasswordStrength.WEAK -> 0.33f
        PasswordStrength.MEDIUM -> 0.66f
        PasswordStrength.STRONG -> 1f
    }

    val progress by animateFloatAsState(
        targetValue = targetProgress,
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec, label = ""
    )

    LinearProgressIndicator(
        progress = { progress },
        strokeCap = StrokeCap.Round,
        modifier = Modifier
            .padding(horizontal = 14.dp)
            .fillMaxWidth()
            .height(8.dp),
        color = color,
        trackColor = Color.LightGray
    )
}