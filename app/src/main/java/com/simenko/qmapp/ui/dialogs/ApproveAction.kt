package com.simenko.qmapp.ui.dialogs

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.simenko.qmapp.ui.main.settings.SettingsViewModel
import com.simenko.qmapp.ui.theme.*

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ApproveAction(
    modifier: Modifier = Modifier,
    registrationViewModel: SettingsViewModel,
    msg: String,
    derivedPassword: String,
    onCanselClick: () -> Unit,
    onOkClick: (String) -> Unit
) {
    var error by rememberSaveable { mutableStateOf("") }

    var password by rememberSaveable { mutableStateOf("") }
    var passwordError by rememberSaveable { mutableStateOf(false) }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    var enableApprove by rememberSaveable { mutableStateOf(false) }

    val (focusRequesterPassword) = FocusRequester.createRefs()

    LaunchedEffect(Unit) { focusRequesterPassword.requestFocus() }

    Dialog(
        onDismissRequest = { registrationViewModel.hideActionApproveDialog() },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        val keyboardController = LocalSoftwareKeyboardController.current
        Card(
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.padding(10.dp, 10.dp, 10.dp, 10.dp),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier.background(MaterialTheme.colorScheme.onPrimary),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                //.......................................................................
                Image(
                    imageVector = Icons.Filled.Person,
                    contentDescription = null, // decorative
                    contentScale = ContentScale.Fit,
                    colorFilter = ColorFilter.tint(
                        color = MaterialTheme.colorScheme.surfaceTint
                    ),
                    modifier = Modifier
                        .padding(vertical = 20.dp)
                        .height(50.dp)
                        .fillMaxWidth(),
                )
                Text(
                    text = msg,
                    style = MaterialTheme.typography.labelLarge.copy(fontSize = 16.sp, color = MaterialTheme.colorScheme.primary),
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .padding(all = 5.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))
                Spacer(modifier = Modifier.height(10.dp))
                TextField(
                    value = password,
                    onValueChange = {
                        password = it
                        passwordError = false
                        error = ""
                        enableApprove = it.isNotEmpty()
                    },
                    leadingIcon = {
                        val tint = if (passwordError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.surfaceTint
                        Icon(imageVector = Icons.Default.Lock, contentDescription = "password", tint = tint)
                    },
                    label = { Text("Password *") },
                    placeholder = { Text(text = "Enter your password") },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                        val description = if (passwordVisible) "Hide password" else "Show password"
                        val tint = if (passwordError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.surfaceTint
                        IconButton(onClick = { passwordVisible = !passwordVisible }) { Icon(imageVector = image, description, tint = tint) }
                    },
                    isError = passwordError,
                    maxLines = 1,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
                    modifier = Modifier
                        .focusRequester(focusRequesterPassword)
                        .width(320.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = error,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 14.sp, color = MaterialTheme.colorScheme.error),
                    modifier = Modifier.padding(all = 5.dp),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(10.dp))
                //.......................................................................
                Row(
                    Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primary),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    TextButton(
                        modifier = Modifier.weight(1f),
                        onClick = {
                            registrationViewModel.hideActionApproveDialog()
                            onCanselClick()
                        },
                        content = {
                            Text(
                                "Cansel",
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.padding(top = 5.dp, bottom = 5.dp),
                                textAlign = TextAlign.Center
                            )
                        },
                        colors = ButtonDefaults.textButtonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                    )
                    Divider(modifier = modifier.width(1.dp).height(48.dp), color = MaterialTheme.colorScheme.onPrimary)
                    TextButton(
                        modifier = Modifier.weight(1f),
                        onClick = {
                            if (derivedPassword == password)
                                onOkClick(password)
                            else {
                                passwordError = true
                                error = "Incorrect password"
                            }
                        },
                        content = {
                            Text(
                                "Ok",
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.padding(top = 5.dp, bottom = 5.dp),
                                textAlign = TextAlign.Center
                            )
                        },
                        colors = ButtonDefaults.textButtonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        enabled = enableApprove
                    )
                }
            }
        }
    }
}