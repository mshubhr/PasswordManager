package com.example.passwordmanager.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.passwordmanager.R
import com.example.passwordmanager.data.Password
import com.example.passwordmanager.data.PasswordSaver
import com.example.passwordmanager.data.PasswordViewModel
import com.example.passwordmanager.notifications.NotificationReceiver
import com.example.passwordmanager.ui.appComponents.PasswordFieldComponent
import com.example.passwordmanager.ui.appComponents.TextComponent
import com.example.passwordmanager.ui.appComponents.TextFieldComponent
import com.example.passwordmanager.ui.appComponents.PasswordStrengthIndicator
import com.example.passwordmanager.ui.appComponents.PasswordText
import com.example.passwordmanager.util.PasswordStrength
import com.example.passwordmanager.util.Utils
import kotlinx.coroutines.launch

private const val CHANNEL_ID = "password_manager_channel"

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun PasswordListScreen(vm: PasswordViewModel = hiltViewModel()) {
    var openBottomSheet by rememberSaveable { mutableStateOf(false) }
    var openDetailsBottomSheet by rememberSaveable { mutableStateOf(false) }
    var selectedAuthData by rememberSaveable { mutableStateOf<Password?>(null) }
    var isEditMode by rememberSaveable { mutableStateOf(false) } // New state for edit mode

    val allData = vm.passwords.observeAsState(initial = emptyList())

    LaunchedEffect(true) {
        vm.loadPasswords()
    }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    androidx.compose.material3.Scaffold(
        snackbarHost = { androidx.compose.material3.SnackbarHost(snackbarHostState) },
    ) { paddingValues ->
        Column(
            Modifier
                .background(Color(0xfff3f5fa))
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            TextComponent(
                text = "Password Manager",
                modifier = Modifier
                    .padding(start = 18.dp, bottom = 10.dp)
                    .statusBarsPadding(),
                fontWeight = FontWeight.SemiBold,
                textSize = 20.sp
            )
            HorizontalDivider(color = Color(0xffe8e8e8), thickness = 3.dp)

            AnimatedVisibility(visible = allData.value.isEmpty(), modifier = Modifier.weight(1f)) {
                Box(modifier = Modifier.weight(1f)) {
                    TextComponent(
                        text = "Add your password by clicking + button",
                        modifier = Modifier.align(Alignment.Center),
                        textSize = 22.sp
                    )
                }
            }

            AnimatedVisibility(
                visible = allData.value.isNotEmpty(),
                modifier = Modifier.weight(1f)
            ) {
                Box(Modifier.weight(1f)) {
                    LazyColumn(
                        Modifier
                            .padding(top = 15.dp)
                            .fillMaxSize()
                            .align(Alignment.TopCenter),
                        verticalArrangement = Arrangement.spacedBy(15.dp)
                    ) {
                        items(allData.value) { authData ->
                            CardItem(authData = authData) {
                                selectedAuthData = it
                                openBottomSheet = false
                                openDetailsBottomSheet = true
                            }
                        }
                    }
                }
            }

            Box(Modifier.align(Alignment.End)) {
                androidx.compose.material3.FloatingActionButton(
                    onClick = { openBottomSheet = !openBottomSheet },
                    containerColor = Color(0xff3F7DE3),
                    contentColor = Color.White,
                    shape = RoundedCornerShape(10.dp),
                    elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 10.dp),
                    modifier = Modifier.padding(horizontal = 18.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }

        AnimatedVisibility(
            openBottomSheet,
            enter = fadeIn() + slideInVertically { it },
            exit = fadeOut() + slideOutVertically { it },
        ) {
            // Edit functionality added here
            PasswordBottomSheet(
                isEditMode = isEditMode,
                selectedAuthData = selectedAuthData,
                onDismiss = { openBottomSheet = false; isEditMode = false },
                onSave = { accountName, email, password ->
                    if (accountName.trim().isEmpty() || email.trim().isEmpty() || password.trim().isEmpty()) {
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                message = "Please fill all the fields !",
                                duration = SnackbarDuration.Short,
                            )
                        }
                    } else {
                        if (isEditMode) {
                            vm.updatePassword(
                                Password(
                                    id = selectedAuthData?.id ?: 0,
                                    accountType = accountName,
                                    username = email,
                                    encryptedPassword = Utils.encrypt(password)
                                )
                            )
                        } else {
                            vm.insertPassword(
                                Password(
                                    accountType = accountName,
                                    username = email,
                                    encryptedPassword = Utils.encrypt(password)
                                )
                            )
                        }
                        openBottomSheet = false
                        isEditMode = false
                    }
                }
            )
        }

        AnimatedVisibility(
            visible = openDetailsBottomSheet,
            enter = fadeIn() + slideInVertically { it },
            exit = fadeOut() + slideOutVertically { it },
        ) {
            Box(
                Modifier
                    .clickable {
                        openDetailsBottomSheet = false
                    }
                    .fillMaxSize()
                    .background(Color(0x4D000000))) {
                Column(
                    Modifier
                        .background(
                            Color(0xfff9f9f9),
                            shape = RoundedCornerShape(topEnd = 20.dp, topStart = 20.dp)
                        )
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .align(Alignment.BottomCenter)
                ) {
                    HorizontalDivider(
                        Modifier
                            .padding(top = 12.dp)
                            .align(Alignment.CenterHorizontally)
                            .width(80.dp)
                            .clip(RoundedCornerShape(50)),
                        color = Color(0xffe3e3e3),
                        thickness = 5.dp
                    )

                    TextComponent(
                        text = "Account Details",
                        modifier = Modifier
                            .padding(start = 14.dp, end = 14.dp, top = 12.dp, bottom = 25.dp)
                            .fillMaxWidth(),
                        textColor = Color(0xff3F7DE3),
                        fontWeight = FontWeight.SemiBold
                    )

                    TextComponent(
                        text = "Account Type",
                        textColor = Color(0xffcccccc),
                        textSize = 13.sp,
                        modifier = Modifier.padding(horizontal = 14.dp)
                    )
                    Spacer(modifier = Modifier.height(5.dp))

                    selectedAuthData?.let {
                        TextComponent(
                            text = it.accountType,
                            modifier = Modifier
                                .padding(horizontal = 14.dp)
                                .fillMaxWidth(),
                        )
                    }
                    Spacer(modifier = Modifier.height(15.dp))

                    TextComponent(
                        text = "UserName/ Email",
                        textColor = Color(0xffcccccc),
                        textSize = 13.sp,
                        modifier = Modifier.padding(horizontal = 14.dp)
                    )
                    Spacer(modifier = Modifier.height(5.dp))

                    selectedAuthData?.let {
                        TextComponent(
                            text = it.username,
                            modifier = Modifier
                                .padding(horizontal = 14.dp)
                                .fillMaxWidth(),
                        )
                    }
                    Spacer(modifier = Modifier.height(15.dp))

                    TextComponent(
                        text = "Password",
                        textColor = Color(0xffcccccc),
                        textSize = 13.sp,
                        modifier = Modifier.padding(horizontal = 14.dp)
                    )

                    val passwordVisible = remember {
                        mutableStateOf(false)
                    }
                    val iconImage =
                        if (passwordVisible.value)
                            R.drawable.ic_show
                        else
                            R.drawable.ic_hide

                    Row(
                        Modifier
                            .padding(horizontal = 14.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        val orgPassword = selectedAuthData?.let { Utils.decrypt(it.encryptedPassword) }

                        if (!passwordVisible.value) {
                            orgPassword?.let { it1 -> PasswordText(it1)
                            }
                        } else {
                            orgPassword?.let {
                                TextComponent(
                                    text = it,
                                )
                            }
                        }

                        Icon(
                            imageVector = ImageVector.vectorResource(id = iconImage),
                            contentDescription = null,
                            tint = Color(0xffd7d7d7),
                            modifier = Modifier.clickable {
                                passwordVisible.value = !passwordVisible.value
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(25.dp))

                    Row(
                        Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                    ) {
                        androidx.compose.material3.Button(
                            onClick = {
                                openBottomSheet = true
                                isEditMode = true
                                openDetailsBottomSheet = false
                            },
                            modifier = Modifier
                                .padding(horizontal = 14.dp)
                                .weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xff2c2c2c)
                            )
                        ) {
                            TextComponent(
                                text = "Edit",
                                textColor = Color.White,
                                modifier = Modifier.padding(vertical = 2.dp),
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        androidx.compose.material3.Button(
                            onClick = {
                                vm.deletePassword(selectedAuthData!!)
                                openDetailsBottomSheet = false
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        message = "Password deleted successfully ",
                                        duration = SnackbarDuration.Short,
                                    )
                                }
                            },
                            modifier = Modifier
                                .padding(horizontal = 14.dp)
                                .weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xff2c2c2c)
                            )
                        ) {
                            TextComponent(
                                text = "Delete",
                                textColor = Color.White,
                                modifier = Modifier.padding(vertical = 2.dp),
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun PasswordBottomSheet(
    isEditMode: Boolean = false,
    selectedAuthData: Password? = null,
    onDismiss: () -> Unit,
    onSave: (String, String, String) -> Unit
) {
    val accountNameState = rememberSaveable { mutableStateOf(selectedAuthData?.accountType ?: "") }
    val emailState = rememberSaveable { mutableStateOf(selectedAuthData?.username ?: "") }
    val passwordState = rememberSaveable(stateSaver = PasswordSaver) { mutableStateOf(selectedAuthData?: Password(0, "", "", "")) }
    val passwordStrengthState = remember { mutableStateOf(Utils.getPasswordStrength(passwordState.value.encryptedPassword)) }

    var openBottomSheet by rememberSaveable { mutableStateOf(false) }

    val context = LocalContext.current
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                createNotificationChannel(context)
                notification(context)
            } else {
                // Handle permission denied case
            }
        }
    )

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(passwordState.value.encryptedPassword) {
        passwordStrengthState.value = Utils.getPasswordStrength(passwordState.value.encryptedPassword)
    }

    Box(
        Modifier
            .clickable {
                openBottomSheet = false
            }
            .fillMaxSize()
            .background(Color(0x4D000000))) {

        Column(
            Modifier
                .background(
                    Color(0xfff9f9f9),
                    shape = RoundedCornerShape(topEnd = 20.dp, topStart = 20.dp)
                )
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            HorizontalDivider(
                Modifier
                    .padding(top = 12.dp)
                    .align(Alignment.CenterHorizontally)
                    .width(80.dp)
                    .clip(RoundedCornerShape(50)),
                color = Color(0xffe3e3e3),
                thickness = 5.dp
            )
            TextComponent(
                text = if (isEditMode) "Edit Password" else "Add Password",
                modifier = Modifier
                    .padding(start = 14.dp, end = 14.dp, top = 12.dp, bottom = 25.dp)
                    .fillMaxWidth(),
                textColor = Color(0xff3F7DE3),
                fontWeight = FontWeight.SemiBold
            )

            TextFieldComponent(
                value = accountNameState.value,
                onValueChange = { accountNameState.value = it },
                modifier = Modifier
                    .padding(start = 14.dp, end = 14.dp, top = 12.dp)
                    .fillMaxWidth(),
                labelValue = "Account Type"
            )

            TextFieldComponent(
                value = emailState.value,
                onValueChange = { emailState.value = it },
                modifier = Modifier
                    .padding(horizontal = 14.dp, vertical = 10.dp)
                    .fillMaxWidth(),
                labelValue = "Email/ Username"
            )

            PasswordFieldComponent(
                value = passwordState.value.encryptedPassword,
                onValueChange = { newValue ->
                    passwordState.value = passwordState.value.copy(encryptedPassword = newValue)
                },
                modifier = Modifier
                    .padding(start = 14.dp, end = 14.dp)
                    .fillMaxWidth(),
                labelValue = "Password",
                keyboardActions = KeyboardActions(
                    onDone = {
                        keyboardController?.hide()
                        focusManager.clearFocus()
                    }
                )
            )

            TextButton(
                modifier = Modifier.align(Alignment.End),
                onClick = {
                    passwordState.value =
                        passwordState.value.copy(encryptedPassword = Utils.generateStrongPassword())
                }) {
                Text(text = "Generate a password")
            }

            Spacer(modifier = Modifier.height(8.dp))

            AnimatedVisibility(
                visible = passwordState.value.encryptedPassword.trim().isNotEmpty()
            ) {
                Column {
                    PasswordStrengthIndicator(strength = passwordStrengthState.value)
                    Spacer(modifier = Modifier.height(5.dp))
                    val text = "Your password strength is ${passwordStrengthState.value}"
                    val color = when (passwordStrengthState.value) {
                        PasswordStrength.WEAK -> Color.Red
                        PasswordStrength.MEDIUM -> Color.Blue
                        PasswordStrength.STRONG -> Color.Black
                    }
                    TextComponent(
                        text = text,
                        textSize = 13.sp,
                        modifier = Modifier.padding(horizontal = 14.dp),
                        textColor = color
                    )
                }
            }

            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(14.dp)
            ) {
                androidx.compose.material3.Button(
                    onClick = {
                        onDismiss()
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xff2c2c2c)
                    )
                ) {
                    TextComponent(
                        text = "Cancel",
                        textColor = Color.White,
                        modifier = Modifier.padding(vertical = 2.dp),
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                androidx.compose.material3.Button(
                    onClick = {
                        onSave(
                            accountNameState.value,
                            emailState.value,
                            passwordState.value.encryptedPassword
                        )
                        if (ContextCompat.checkSelfPermission(
                                context, Manifest.permission.POST_NOTIFICATIONS
                            ) == PackageManager.PERMISSION_GRANTED) {
                            createNotificationChannel(context)
                            notification(context)
                        } else {
                            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xff3F7DE3)
                    )
                ) {
                    TextComponent(
                        text = "Save",
                        textColor = Color.White,
                        modifier = Modifier.padding(vertical = 2.dp),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            Spacer(modifier = Modifier.windowInsetsBottomHeight(WindowInsets.captionBar))
        }
    }
}

fun createNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name = "Password Manager Channel"
        val descriptionText = "Channel for password manager notifications"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(NotificationReceiver.CHANNEL_ID, name, importance).apply {
            description = descriptionText
        }
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}

@SuppressLint("MissingPermission")
private fun notification(context: Context) {
    val builder = NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_noti)
        .setContentTitle("Password Manager")
        .setContentText("Password added successfully")
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setAutoCancel(true)

    val notificationManagerCompat = NotificationManagerCompat.from(context)
     notificationManagerCompat.notify(1, builder.build())
}

@Composable
fun CardItem(authData: Password, onclick: (Password) -> Unit) {
    Surface(
        border = BorderStroke(.8.dp, Color(0xffededed)),
        onClick = {
            onclick.invoke(authData)
        },
        color = Color.White,
        modifier = Modifier
            .padding(horizontal = 15.dp)
            .fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        shadowElevation = 5.dp
    ) {
        Row(
            Modifier
                .padding(vertical = 18.dp, horizontal = 14.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TextComponent(
                text = Utils.capitalizeFirstLetter(authData.accountType),
                fontWeight = FontWeight.SemiBold,
                textSize = 20.sp
            )

            Spacer(modifier = Modifier.width(15.dp))

            PasswordText(password = authData.encryptedPassword.take(8))
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = null
            )
        }
    }
}