package com.example.passwordmanager.ui.appComponents

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.example.passwordmanager.ui.theme.roboto

@Composable
fun TextComponent(
    modifier: Modifier = Modifier,
    text: String,
    textColor: Color = Color.Black,
    textSize: TextUnit = 18.sp,
    fontFamily: FontFamily = roboto,
    fontWeight: FontWeight = FontWeight.Normal,
    textAlign: TextAlign = TextAlign.Start,
    maxLines: Int = 2,
) {
    Text(
        text = text,
        modifier = modifier,
        style = TextStyle(
            fontSize = textSize,
            fontWeight = fontWeight,
            fontFamily = fontFamily,
            color = textColor,
            textAlign = textAlign,
        ),
        maxLines = maxLines,
        softWrap = true,
        overflow = TextOverflow.Ellipsis
    )
}