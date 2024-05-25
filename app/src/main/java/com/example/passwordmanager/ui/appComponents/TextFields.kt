package com.example.passwordmanager.ui.appComponents

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.passwordmanager.R
import com.example.passwordmanager.ui.theme.roboto

@Composable
fun TextFieldComponent(
    modifier: Modifier = Modifier,
    value: String,
    labelValue: String? = null,
    onValueChange: (String) -> Unit,
    imeAction: ImeAction = ImeAction.Next,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    keyboardType: KeyboardType = KeyboardType.Text,
    readyOnly: Boolean = false,
    labelColor: Color = Color(0xffd7d7d7),
    shape: Shape = RoundedCornerShape(8.dp),
    fontFamily: FontFamily = roboto,
    fontSize: TextUnit = 17.sp,
    enabled: Boolean = true,

    ) {
    OutlinedTextField(
        value = value,
        onValueChange = {
            onValueChange.invoke(it)
        },
        placeholder = {
            if (labelValue != null)
                Text(
                    text = labelValue,
                    color = labelColor,
                    style = TextStyle(
                        fontFamily = fontFamily,
                        fontSize = fontSize,
                    )
                )
        },
        shape = shape,
        modifier = modifier,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White.copy(0.9f),
            focusedBorderColor = Color(0xffd7d7d7),
            unfocusedBorderColor = Color(0xffd7d7d7).copy(0.85f),
            cursorColor = Color.Black.copy(0.7f),
        ),

        keyboardActions = keyboardActions,
        keyboardOptions = KeyboardOptions(
            autoCorrectEnabled = false,
            keyboardType = keyboardType,
            imeAction = imeAction
        ),

        maxLines = 1,
        singleLine = true,
        readOnly = readyOnly,
        textStyle = TextStyle(
            fontFamily = fontFamily,
            fontSize = fontSize,
            color = Color.Black.copy(.7f),
        ),

        enabled = enabled
    )
}

@Composable
fun PasswordFieldComponent(
    modifier: Modifier = Modifier,
    value: String,
    labelValue: String? = null,
    onValueChange: (String) -> Unit,
    imeAction: ImeAction = ImeAction.Done,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    keyboardType: KeyboardType = KeyboardType.Password,
    readyOnly: Boolean = false,
    labelColor: Color = Color(0xffd7d7d7),
    shape: Shape = RoundedCornerShape(8.dp),
    fontFamily: FontFamily = roboto,
    fontSize: TextUnit = 17.sp,
    enabled: Boolean = true,

    ) {
    val passwordVisible = remember {
        mutableStateOf(false)
    }

    OutlinedTextField(
        value = value,
        onValueChange = {
            onValueChange.invoke(it)
        },

        placeholder = {
            if (labelValue != null)
                Text(
                    text = labelValue,
                    color = labelColor,
                    style = TextStyle(
                        fontFamily = fontFamily,
                        fontSize = fontSize,
                    )
                )
        },

        shape = shape,
        modifier = modifier,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White.copy(0.9f),
            focusedBorderColor = Color(0xffd7d7d7),
            unfocusedBorderColor = Color(0xffd7d7d7).copy(0.85f),
            cursorColor = Color.Black.copy(0.7f),
        ),

        keyboardActions = keyboardActions,
        keyboardOptions = KeyboardOptions(
            autoCorrectEnabled = false,
            keyboardType = keyboardType,
            imeAction = imeAction
        ),

        maxLines = 1,
        singleLine = true,
        readOnly = readyOnly,
        textStyle = TextStyle(
            fontFamily = fontFamily,
            fontSize = fontSize,
            color = Color.Black.copy(.7f),
        ),

        enabled = enabled,
        trailingIcon = {
            val iconImage =
                if (passwordVisible.value)
                    R.drawable.ic_show
                else
                    R.drawable.ic_hide

            val description = if (passwordVisible.value)
                "Hide Password"
            else
                "Show Password"

            IconButton(
                onClick = {
                    passwordVisible.value = !passwordVisible.value
                }
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = iconImage),
                    contentDescription = description,
                    tint = Color(0xffd7d7d7)
                )
            }
        },

        visualTransformation =
        if (passwordVisible.value)
            VisualTransformation.None
        else
            PasswordVisualTransformation('●'),
        )
}