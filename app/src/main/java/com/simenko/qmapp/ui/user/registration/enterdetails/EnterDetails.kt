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
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.ButtonColors
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
import androidx.compose.ui.graphics.Color
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
                registrationViewModel.initPrincipleToRegister(rawPrinciple)
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
    val (focusRequesterPhoneNumber) = FocusRequester.createRefs()
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
        RecordFieldItem(
            valueParam = Triple(rawPrinciple.fullName, rawPrincipleErrors.fullNameError) { viewModel.setFullName(it) },
            keyboardNavigation = Pair(focusRequesterUserName) { focusRequesterDepartment.requestFocus() },
            keyBoardTypeAction = Pair(KeyboardType.Ascii, ImeAction.Next),
            contentDescription = Triple(Icons.Default.Person, "Full name", "Enter your name and surname")
        )
        Spacer(modifier = Modifier.height(10.dp))
        RecordFieldItem(
            valueParam = Triple(rawPrinciple.department, rawPrincipleErrors.departmentError) { viewModel.setDepartment(it) },
            keyboardNavigation = Pair(focusRequesterDepartment) { focusRequesterSubDepartment.requestFocus() },
            keyBoardTypeAction = Pair(KeyboardType.Ascii, ImeAction.Next),
            contentDescription = Triple(Icons.Default.AccountBalance, "Department", "Enter your department")
        )
        Spacer(modifier = Modifier.height(10.dp))
        RecordFieldItem(
            valueParam = Triple(rawPrinciple.subDepartment ?: "", false) { viewModel.setSubDepartment(it) },
            keyboardNavigation = Pair(focusRequesterSubDepartment) { focusRequesterJobRole.requestFocus() },
            keyBoardTypeAction = Pair(KeyboardType.Ascii, ImeAction.Next),
            contentDescription = Triple(Icons.Default.AccountTree, "Sub department", "Enter only if applicable"),
            isMandatoryField = false
        )
        Spacer(modifier = Modifier.height(10.dp))
        RecordFieldItem(
            valueParam = Triple(rawPrinciple.jobRole, rawPrincipleErrors.jobRoleError) { viewModel.setJobRole(it) },
            keyboardNavigation = Pair(focusRequesterJobRole) { focusRequesterEmail.requestFocus() },
            keyBoardTypeAction = Pair(KeyboardType.Ascii, ImeAction.Next),
            contentDescription = Triple(Icons.Default.Work, "Job role", "Enter your job role / position")
        )
        Spacer(modifier = Modifier.height(10.dp))
        RecordFieldItem(
            valueParam = Triple(rawPrinciple.email, rawPrincipleErrors.emailError) { viewModel.setEmail(it) },
            keyboardNavigation = Pair(focusRequesterEmail) { focusRequesterPhoneNumber.requestFocus() },
            keyBoardTypeAction = Pair(KeyboardType.Email, ImeAction.Next),
            contentDescription = Triple(Icons.Default.Mail, "Email", "Enter your email")
        )
        Spacer(modifier = Modifier.height(10.dp))
        RecordFieldItem(
            valueParam = Triple(rawPrinciple.phoneNumber.phoneNumberToString(), false) { viewModel.setPhoneNumber(it.stringToPhoneNumber()) },
            keyboardNavigation = Pair(focusRequesterPhoneNumber) { focusRequesterPassword.requestFocus() },
            keyBoardTypeAction = Pair(KeyboardType.Phone, ImeAction.Next),
            contentDescription = Triple(Icons.Default.Phone, "Phone number", "Enter your phone number"),
            isMandatoryField = false,
        )
        Spacer(modifier = Modifier.height(10.dp))
        RecordFieldItem(
            valueParam = Triple(rawPrinciple.password, rawPrincipleErrors.passwordError) {
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
                        tint = if (rawPrincipleErrors.passwordError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.surfaceTint
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
        Spacer(modifier = Modifier.height(10.dp))
        RecordActionTextBtn(
            text = "Next",
            onClick = { viewModel.validateInput(rawPrinciple) },
            colors = Pair(ButtonDefaults.textButtonColors(), MaterialTheme.colorScheme.primary),
        )
        RecordActionTextBtn(
            text = "Log in",
            onClick = { navController.navigate(Screen.LoggedOut.LogIn.route) },
            colors = Pair(
                ButtonDefaults.textButtonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.primary
                ),
                MaterialTheme.colorScheme.primary
            ),
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
    enabled: Boolean = true,
    trailingIcon: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
) {
    TextField(
        value = valueParam.first,
        onValueChange = valueParam.third,
        leadingIcon = {
            val tint = if (valueParam.second) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.surfaceTint
            Icon(imageVector = contentDescription.first, contentDescription = contentDescription.second, tint = tint)
        },
        label = { Text(text = "${contentDescription.second}${if (isMandatoryField) " *" else ""}") },
        isError = valueParam.second,
        placeholder = { Text(text = "${contentDescription.third}${if (isMandatoryField) " *" else ""}") },
        maxLines = 1,
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyBoardTypeAction.first, imeAction = keyBoardTypeAction.second),
        keyboardActions = KeyboardActions(onNext = { keyboardNavigation.second() }),
        enabled = enabled,
        trailingIcon = trailingIcon,
        visualTransformation = visualTransformation,
        modifier = Modifier
            .focusRequester(keyboardNavigation.first)
            .width(320.dp)
    )
}

@Composable
fun RecordActionTextBtn(
    text: String,
    onClick: () -> Unit,
    colors: Pair<ButtonColors, Color>,
    enabled: Boolean = true
) {
    TextButton(
        modifier = Modifier.width(150.dp),
        onClick = onClick,
        content = {
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 14.sp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .padding(all = 0.dp)
            )
        },
        colors = colors.first,
        border = BorderStroke(1.dp, colors.second),
        shape = MaterialTheme.shapes.medium,
        enabled = enabled
    )
}

@Preview(name = "Lite Mode Enter Details", showBackground = true, widthDp = 360)
@Composable
fun EnterDetailsPreview() {
    QMAppTheme {
        EnterDetails()
    }
}