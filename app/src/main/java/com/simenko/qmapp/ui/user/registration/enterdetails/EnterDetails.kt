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
import androidx.compose.ui.graphics.vector.ImageVector
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
    val viewModel: EnterDetailsViewModel = hiltViewModel()

    val stateEvent by viewModel.enterDetailsState.collectAsStateWithLifecycle()

    val rawPrinciple by viewModel.rawPrinciple.collectAsStateWithLifecycle()
    val rawPrincipleErrors by viewModel.rawPrincipleErrors.collectAsStateWithLifecycle()

    var error by rememberSaveable { mutableStateOf("") }

    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    stateEvent.getContentIfNotHandled()?.let { state ->
        when (state) {
            is EnterDetailsSuccess -> {
                registrationViewModel.updateUserData(rawPrinciple)
                navController.navigate(Screen.LoggedOut.Registration.TermsAndConditions.withArgs(rawPrinciple.email))
            }

            is EnterDetailsError -> error = state.errorMsg
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
            value = rawPrinciple.fullName,
            onValueChange = {
                viewModel.setFullName(it)
            },
            leadingIcon = {
                val tint = if (rawPrincipleErrors.fullNameError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.surfaceTint
                Icon(imageVector = Icons.Default.Person, contentDescription = "fullName", tint = tint)
            },
            label = { Text("Full name *") },
            isError = rawPrincipleErrors.fullNameError,
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
            value = rawPrinciple.department,
            onValueChange = {
                viewModel.setDepartment(it)
            },
            leadingIcon = {
                val tint = if (rawPrincipleErrors.departmentError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.surfaceTint
                Icon(imageVector = Icons.Default.AccountBalance, contentDescription = "department", tint = tint)
            },
            label = { Text("Department *") },
            isError = rawPrincipleErrors.departmentError,
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
            value = rawPrinciple.subDepartment ?: "",
            onValueChange = { viewModel.setSubDepartment(it) },
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
            value = rawPrinciple.jobRole,
            onValueChange = {
                viewModel.setJobRole(it)
            },
            leadingIcon = {
                val tint = if (rawPrincipleErrors.jobRoleError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.surfaceTint
                Icon(imageVector = Icons.Default.Work, contentDescription = "jobRole", tint = tint)
            },
            label = { Text("Job role *") },
            isError = rawPrincipleErrors.jobRoleError,
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
            value = rawPrinciple.email,
            onValueChange = { viewModel.setEmail(it) },
            leadingIcon = {
                val tint = if (rawPrincipleErrors.emailError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.surfaceTint
                Icon(imageVector = Icons.Default.Mail, contentDescription = "email", tint = tint)
            },
            label = { Text("Email *") },
            isError = rawPrincipleErrors.emailError,
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
            value = rawPrinciple.password,
            onValueChange = {
                viewModel.setPassword(it)
                error = ""
            },
            leadingIcon = {
                val tint = if (rawPrincipleErrors.passwordError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.surfaceTint
                Icon(imageVector = Icons.Default.Lock, contentDescription = "password", tint = tint)
            },
            label = { Text("Password *") },
            placeholder = { Text(text = "Enter your password") },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff

                val description = if (passwordVisible) "Hide password" else "Show password"

                val tint = if (rawPrincipleErrors.passwordError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.surfaceTint

                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, description, tint = tint)
                }
            },
            isError = rawPrincipleErrors.passwordError,
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
                viewModel.validateInput(rawPrinciple)
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

@Composable
fun RecordFieldItem(
    valueParam: Triple<String, Boolean, (String) -> Unit>,
    keyboardNavigation: Pair<FocusRequester, () -> Unit>,
    keyBoardTypeAction: Pair<KeyboardType, ImeAction>,
    contentDescription: Triple<ImageVector, String, String>,
    isMandatoryField: Boolean = true,
    enabled: Boolean = true
) {
    TextField(
        value = valueParam.first,
        onValueChange = valueParam.third,
        leadingIcon = {
            val tint = if (valueParam.second) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.surfaceTint
            Icon(imageVector = contentDescription.first, contentDescription = contentDescription.second, tint = tint)
        },
        label = { Text("${contentDescription.second} + ${if (isMandatoryField) " *" else ""}") },
        isError = valueParam.second,
        placeholder = { Text(text = contentDescription.third) },
        maxLines = 1,
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyBoardTypeAction.first, imeAction = keyBoardTypeAction.second),
        keyboardActions = KeyboardActions(onNext = { keyboardNavigation.second() }),
        enabled = enabled,
        modifier = Modifier
            .focusRequester(keyboardNavigation.first)
            .width(320.dp)
    )
}

@Preview(name = "Lite Mode Enter Details", showBackground = true, widthDp = 360)
@Composable
fun EnterDetailsPreview() {
    QMAppTheme {
        EnterDetails()
    }
}