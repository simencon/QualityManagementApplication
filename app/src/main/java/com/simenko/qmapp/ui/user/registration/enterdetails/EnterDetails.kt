package com.simenko.qmapp.ui.user.registration.enterdetails

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.simenko.qmapp.ui.Screen
import com.simenko.qmapp.ui.user.registration.RegistrationViewModel
import com.simenko.qmapp.ui.theme.QMAppTheme

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun EnterDetails(
    navController: NavHostController = rememberNavController(),
) {
    val registrationViewModel: RegistrationViewModel = hiltViewModel()
    val enterDetailsViewModel: EnterDetailsViewModel = hiltViewModel()

    val stateEvent by enterDetailsViewModel.enterDetailsState.collectAsStateWithLifecycle()

    var userFullName by rememberSaveable { mutableStateOf("") }
    var fullNameError by rememberSaveable { mutableStateOf(false) }
    var userDepartment by rememberSaveable { mutableStateOf("") }
    var departmentError by rememberSaveable { mutableStateOf(false) }
    var userSubDepartment by rememberSaveable { mutableStateOf("") }
    var userJobRole by rememberSaveable { mutableStateOf("") }
    var jobRoleError by rememberSaveable { mutableStateOf(false) }
    var userEmail by rememberSaveable { mutableStateOf("") }
    var emailError by rememberSaveable { mutableStateOf(false) }

    var password by rememberSaveable { mutableStateOf("") }
    var passwordError by rememberSaveable { mutableStateOf(false) }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    var error by rememberSaveable { mutableStateOf("") }

    stateEvent.getContentIfNotHandled()?.let { state ->
        when (state) {
            is EnterDetailsSuccess -> {
                registrationViewModel.updateUserData(userFullName, userDepartment, userSubDepartment, userJobRole, userEmail, password)
                navController.navigate(Screen.LoggedOut.Registration.TermsAndConditions.withArgs(userEmail))
            }

            is EnterDetailsError -> {
                error = state.errorMsg
                fullNameError = state.errorTarget.fullNameError
                departmentError = state.errorTarget.departmentError
                jobRoleError = state.errorTarget.jobRoleError
                emailError = state.errorTarget.emailError
                passwordError = state.errorTarget.passwordError
            }

            is EnterDetailsInitialState -> {}
        }
    }

    val (focusRequesterUserName) = FocusRequester.createRefs()
    val (focusRequesterDepartment) = FocusRequester.createRefs()
    val (focusRequesterSubDepartment) = FocusRequester.createRefs()
    val (focusRequesterJobRole) = FocusRequester.createRefs()
    val (focusRequesterEmail) = FocusRequester.createRefs()
    val (focusRequesterPassword) = FocusRequester.createRefs()

    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        focusRequesterUserName.requestFocus()
    }

    val columnState = rememberScrollState()

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(all = 0.dp)
            .verticalScroll(columnState)
    ) {
        Text(
            text = "Register to Quality Management",
            style = MaterialTheme.typography.labelLarge.copy(fontSize = 18.sp, color = MaterialTheme.colorScheme.primary),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .padding(all = 0.dp)
        )
        Spacer(modifier = Modifier.height(20.dp))
        TextField(
            value = userFullName,
            onValueChange = {
                userFullName = it
                fullNameError = false
            },
            leadingIcon = {
                val tint = if (fullNameError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.surfaceTint
                Icon(imageVector = Icons.Default.Person, contentDescription = "fullName", tint = tint)
            },
            label = { Text("Full name *") },
            isError = fullNameError,
            placeholder = { Text(text = "Enter your name and surname") },
            maxLines = 1,
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii, imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { focusRequesterDepartment.requestFocus() }),
            modifier = Modifier
                .focusRequester(focusRequesterUserName)
                .width(320.dp)
        )
        Spacer(modifier = Modifier.height(10.dp))
        TextField(
            value = userDepartment,
            onValueChange = {
                userDepartment = it
                departmentError = false
            },
            leadingIcon = {
                val tint = if (departmentError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.surfaceTint
                Icon(imageVector = Icons.Default.AccountBalance, contentDescription = "department", tint = tint)
            },
            label = { Text("Department *") },
            isError = departmentError,
            placeholder = { Text(text = "Enter your department") },
            maxLines = 1,
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii, imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { focusRequesterSubDepartment.requestFocus() }),
            modifier = Modifier
                .focusRequester(focusRequesterDepartment)
                .width(320.dp)
        )
        Spacer(modifier = Modifier.height(10.dp))
        TextField(
            value = userSubDepartment,
            onValueChange = { userSubDepartment = it },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.AccountTree,
                    contentDescription = "subDepartment",
                    tint = MaterialTheme.colorScheme.surfaceTint
                )
            },
            label = { Text("Sub department") },
            placeholder = { Text(text = "Enter only if applicable", maxLines = 1, overflow = TextOverflow.Ellipsis) },
            maxLines = 1,
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii, imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { focusRequesterJobRole.requestFocus() }),
            modifier = Modifier
                .focusRequester(focusRequesterSubDepartment)
                .width(320.dp)
        )
        Spacer(modifier = Modifier.height(10.dp))
        TextField(
            value = userJobRole,
            onValueChange = {
                userJobRole = it
                jobRoleError = false
            },
            leadingIcon = {
                val tint = if (jobRoleError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.surfaceTint
                Icon(imageVector = Icons.Default.Work, contentDescription = "jobRole", tint = tint)
            },
            label = { Text("Job role *") },
            isError = jobRoleError,
            placeholder = { Text(text = "Enter your job role / position") },
            maxLines = 1,
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii, imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { focusRequesterEmail.requestFocus() }),
            modifier = Modifier
                .focusRequester(focusRequesterJobRole)
                .width(320.dp)
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
        Spacer(modifier = Modifier.height(10.dp))
        TextButton(
            modifier = Modifier.width(150.dp),
            onClick = {
                enterDetailsViewModel.validateInput(userFullName, userDepartment, userJobRole, userEmail, password)
            },
            content = {
                Text(
                    text = "Next",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 14.sp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .padding(all = 0.dp)
                )
            },
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
            shape = MaterialTheme.shapes.medium
        )
        TextButton(
            modifier = Modifier.width(150.dp),
            onClick = {
                navController.navigate(Screen.LoggedOut.LogIn.route)
            },
            content = {
                Text(
                    text = "Log in",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 14.sp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .padding(all = 0.dp)
                )
            },
            colors = ButtonDefaults.textButtonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.primary
            ),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
            shape = MaterialTheme.shapes.medium
        )
    }
}

@Preview(name = "Lite Mode Enter Details", showBackground = true, widthDp = 360)
@Composable
fun EnterDetailsPreview() {
    QMAppTheme {
        EnterDetails()
    }
}