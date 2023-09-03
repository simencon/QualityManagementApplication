package com.simenko.qmapp.ui.user.login

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import com.simenko.qmapp.ui.user.registration.enterdetails.RecordFieldItem

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun LogIn() {
    val viewModel: LoginViewModel = hiltViewModel()
    val userState by viewModel.userState.collectAsStateWithLifecycle()

    val principle by viewModel.loggedOutPrinciple.collectAsStateWithLifecycle()
    val principleErrors by viewModel.loggedOutPrincipleErrors.collectAsStateWithLifecycle()

    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    var error by rememberSaveable { mutableStateOf("") }
    var msg by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(userState) {
        userState.let { state ->
            if (state is UserErrorState) {
                msg = ""
                error = state.error ?: "Unknown error"
            } else if (state is UserLoggedOutState) {
                msg = state.msg
                error = ""
            }
            viewModel.updateLoadingState(Pair(false, null))
        }
    }

    val (focusRequesterEmail) = FocusRequester.createRefs()
    val (focusRequesterPassword) = FocusRequester.createRefs()
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(key1 = Unit) {
        if (principle.email.isNotEmpty()) focusRequesterPassword.requestFocus() else focusRequesterEmail.requestFocus()
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
        RecordFieldItem(
            valueParam = Triple(principle.email, principleErrors.emailError) { viewModel.setEmail(it) },
            keyboardNavigation = Pair(focusRequesterEmail) { focusRequesterPassword.requestFocus() },
            keyBoardTypeAction = Pair(KeyboardType.Email, ImeAction.Next),
            contentDescription = Triple(Icons.Default.Mail, "Email", "Enter your email")
        )
        Spacer(modifier = Modifier.height(10.dp))
        RecordFieldItem(
            valueParam = Triple(principle.email, principleErrors.passwordError) {
                viewModel.setPassword(it)
                error = ""
            },
            keyboardNavigation = Pair(focusRequesterPassword) { keyboardController?.hide() },
            keyBoardTypeAction = Pair(KeyboardType.Password, ImeAction.Done),
            contentDescription = Triple(Icons.Default.Lock, "Password", "Enter your password"),
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = if (passwordVisible) "Hide password" else "Show password",
                        tint = if (principleErrors.emailError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.surfaceTint
                    )
                }
            }
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
            onClick = { viewModel.login(principle.email, principle.password) },
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
                viewModel.sendResetPasswordEmail(principle.email)
                viewModel.updateLoadingState(Pair(true, null))
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