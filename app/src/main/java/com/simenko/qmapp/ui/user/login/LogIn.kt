package com.simenko.qmapp.ui.user.login

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.simenko.qmapp.repository.UserErrorState
import com.simenko.qmapp.repository.UserLoggedOutState

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun LogIn() {
    val logInViewModel: LoginViewModel = hiltViewModel()
    val userState by logInViewModel.userState.collectAsStateWithLifecycle()

    var userEmail by rememberSaveable { mutableStateOf(logInViewModel.getUserEmail()) }
    var emailError by rememberSaveable { mutableStateOf(false) }

    var password by rememberSaveable { mutableStateOf("") }
    var passwordError by rememberSaveable { mutableStateOf(false) }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    var error by rememberSaveable { mutableStateOf("") }
    var msg by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(userState) {
        userState.let { state ->
            if (state is UserErrorState) {
                error = state.error ?: "Unknown error"
            } else if (state is UserLoggedOutState) {
                msg = state.msg
                error = ""
            }
        }
    }

    val (focusRequesterEmail) = FocusRequester.createRefs()
    val (focusRequesterPassword) = FocusRequester.createRefs()
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(key1 = Unit) {
        if (userEmail.isNotEmpty()) focusRequesterPassword.requestFocus() else focusRequesterEmail.requestFocus()
    }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(all = 0.dp)
    ) {
        Text(
            text = "Welcome to Quality Management",
            style = MaterialTheme.typography.labelLarge.copy(fontSize = 18.sp, color = MaterialTheme.colorScheme.primary),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .padding(all = 5.dp)
        )
        Spacer(modifier = Modifier.height(10.dp))
        TextField(
            value = userEmail,
            onValueChange = {
                userEmail = it
                emailError = false
            },
            leadingIcon = {
                val tint = if (emailError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.surfaceTint
                Icon(imageVector = Icons.Default.Mail, contentDescription = "email", tint = tint)
            },
            label = { Text("Email *") },
            isError = emailError,
            placeholder = { Text(text = "Enter your email") },
            maxLines = 1,
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { focusRequesterPassword.requestFocus() }),
            modifier = Modifier
                .focusRequester(focusRequesterEmail)
                .width(320.dp)
        )
        Spacer(modifier = Modifier.height(10.dp))
        TextField(
            value = password,
            onValueChange = {
                password = it
                passwordError = false
                error = ""
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

                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, description, tint = tint)
                }
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
        if (error != "")
            Text(
                text = error,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 14.sp, color = MaterialTheme.colorScheme.error),
                modifier = Modifier
                    .padding(all = 5.dp),
                textAlign = TextAlign.Center
            )
        else if (msg != "")
            Text(
                text = msg,
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .padding(all = 5.dp),
                textAlign = TextAlign.Center
            )
        Spacer(modifier = Modifier.height(10.dp))
        TextButton(
            modifier = Modifier.width(150.dp),
            onClick = {
                logInViewModel.login(userEmail, password)
            },
            content = {
                Text(
                    text = "Login",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 14.sp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .padding(top = 0.dp, start = 20.dp, end = 20.dp, bottom = 0.dp)
                )
            },
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
            shape = MaterialTheme.shapes.medium
        )
        TextButton(
            modifier = Modifier.width(150.dp),
            onClick = {
                logInViewModel.sendResetPasswordEmail(userEmail)
            },
            content = {
                Text(
                    text = "Reset password",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 14.sp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .padding(top = 0.dp, start = 0.dp, end = 0.dp, bottom = 0.dp)
                )
            },
            enabled = msg == "",
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
            shape = MaterialTheme.shapes.medium
        )
    }
}