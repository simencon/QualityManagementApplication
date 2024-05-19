package com.simenko.qmapp.ui.user.registration.enterdetails

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.simenko.qmapp.domain.FillInErrorState
import com.simenko.qmapp.domain.FillInInitialState
import com.simenko.qmapp.domain.FillInSuccessState
import com.simenko.qmapp.repository.UserError
import com.simenko.qmapp.ui.common.RecordActionTextBtn
import com.simenko.qmapp.ui.common.RecordFieldItem

@Composable
fun EnterDetails(
    viewModel: EnterDetailsViewModel,
    editMode: Boolean = false
) {
    val rawPrinciple by viewModel.rawPrinciple.collectAsStateWithLifecycle()
    val rawPrincipleErrors by viewModel.rawPrincipleErrors.collectAsStateWithLifecycle()

    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    val fillInState by viewModel.fillInState.collectAsStateWithLifecycle()
    var error by rememberSaveable { mutableStateOf(UserError.NO_ERROR.error) }

    LaunchedEffect(fillInState) {
        fillInState.let { state ->
            println("EnterDetails - fillInState: $state")
            when (state) {
                is FillInSuccessState ->
                    if (!editMode) {
                        viewModel.onFillInSuccess(rawPrinciple.email)
                    } else {
                        viewModel.initRawUser()
                        viewModel.onSaveUserDataClick()
                    }

                is FillInErrorState -> error = state.errorMsg
                is FillInInitialState -> {}
            }
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
        viewModel.mainPageHandler.setupMainPage(0, editMode)
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
        if (!editMode)
            Text(
                text = "Register to Quality Management",
                style = MaterialTheme.typography.labelLarge.copy(fontSize = 18.sp, color = MaterialTheme.colorScheme.primary),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(all = 0.dp)
            )
        else
            Image(
                painter = painterResource(rawPrinciple.logo),
                contentDescription = null,
                contentScale = ContentScale.FillHeight,
                modifier = Modifier.height(112.dp)
            )
        Spacer(modifier = Modifier.height(20.dp))
        RecordFieldItem(
            modifier = Modifier.width(320.dp),
            valueParam = Triple(rawPrinciple.fullName, rawPrincipleErrors.fullNameError) { viewModel.setFullName(it) },
            keyboardNavigation = Pair(focusRequesterUserName) { if (!editMode) focusRequesterDepartment.requestFocus() else focusRequesterPhoneNumber.requestFocus() },
            keyBoardTypeAction = Pair(KeyboardType.Ascii, ImeAction.Next),
            contentDescription = Triple(Icons.Default.Person, "Full name", "Enter your name and surname")
        )
        Spacer(modifier = Modifier.height(10.dp))
        RecordFieldItem(
            modifier = Modifier.width(320.dp),
            valueParam = Triple(rawPrinciple.department, rawPrincipleErrors.departmentError) { viewModel.setDepartment(it) },
            keyboardNavigation = Pair(focusRequesterDepartment) { focusRequesterSubDepartment.requestFocus() },
            keyBoardTypeAction = Pair(KeyboardType.Ascii, ImeAction.Next),
            contentDescription = Triple(Icons.Default.AccountBalance, "Department", "Enter your department"),
            enabled = !editMode,
            isMandatoryField = !editMode
        )
        Spacer(modifier = Modifier.height(10.dp))
        RecordFieldItem(
            modifier = Modifier.width(320.dp),
            valueParam = Triple(rawPrinciple.subDepartment ?: "", false) { viewModel.setSubDepartment(it) },
            keyboardNavigation = Pair(focusRequesterSubDepartment) { focusRequesterJobRole.requestFocus() },
            keyBoardTypeAction = Pair(KeyboardType.Ascii, ImeAction.Next),
            contentDescription = Triple(Icons.Default.AccountTree, "Sub department", "Enter only if applicable"),
            isMandatoryField = false,
            enabled = !editMode
        )
        Spacer(modifier = Modifier.height(10.dp))
        RecordFieldItem(
            modifier = Modifier.width(320.dp),
            valueParam = Triple(rawPrinciple.jobRole, rawPrincipleErrors.jobRoleError) { viewModel.setJobRole(it) },
            keyboardNavigation = Pair(focusRequesterJobRole) { focusRequesterEmail.requestFocus() },
            keyBoardTypeAction = Pair(KeyboardType.Ascii, ImeAction.Next),
            contentDescription = Triple(Icons.Default.Work, "Job role", "Enter your job role / position"),
            enabled = !editMode,
            isMandatoryField = !editMode
        )
        Spacer(modifier = Modifier.height(10.dp))
        RecordFieldItem(
            modifier = Modifier.width(320.dp),
            valueParam = Triple(rawPrinciple.email, rawPrincipleErrors.emailError) { viewModel.setEmail(it) },
            keyboardNavigation = Pair(focusRequesterEmail) { focusRequesterPhoneNumber.requestFocus() },
            keyBoardTypeAction = Pair(KeyboardType.Email, ImeAction.Next),
            contentDescription = Triple(Icons.Default.Mail, "Email", "Enter your email"),
            enabled = !editMode,
            isMandatoryField = !editMode
        )
        Spacer(modifier = Modifier.height(10.dp))
        RecordFieldItem(
            modifier = Modifier.width(320.dp),
            valueParam = Triple(rawPrinciple.phoneNumber.phoneNumberToString(), false) { viewModel.setPhoneNumber(it.stringToPhoneNumber()) },
            keyboardNavigation = Pair(focusRequesterPhoneNumber) { if (!editMode) focusRequesterPassword.requestFocus() else keyboardController?.hide() },
            keyBoardTypeAction = Pair(KeyboardType.Phone, if (!editMode) ImeAction.Next else ImeAction.Done),
            contentDescription = Triple(Icons.Default.Phone, "Phone number", "Enter your phone number"),
            isMandatoryField = false,
        )
        Spacer(modifier = Modifier.height(10.dp))
        RecordFieldItem(
            modifier = Modifier.width(320.dp),
            valueParam = Triple(rawPrinciple.password, rawPrincipleErrors.passwordError) {
                viewModel.setPassword(it)
                error = UserError.NO_ERROR.error
            },
            keyboardNavigation = Pair(focusRequesterPassword) { keyboardController?.hide() },
            keyBoardTypeAction = Pair(KeyboardType.Password, ImeAction.Done),
            contentDescription = Triple(Icons.Default.Lock, "Password", "Enter your password"),
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }, enabled = !editMode) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = if (passwordVisible) "Hide password" else "Show password",
                        tint = if (rawPrincipleErrors.passwordError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.surfaceTint
                    )
                }
            },
            enabled = !editMode,
            isMandatoryField = !editMode
        )
        Spacer(modifier = Modifier.height(10.dp))
        if (error != UserError.NO_ERROR.error)
            Text(
                text = error,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 14.sp, color = MaterialTheme.colorScheme.error),
                modifier = Modifier.padding(all = 5.dp),
                textAlign = TextAlign.Center
            )
        Spacer(modifier = Modifier.height(10.dp))
        if (!editMode)
            RecordActionTextBtn(
                text = "Next",
                onClick = { viewModel.validateInput(rawPrinciple) },
                colors = Pair(ButtonDefaults.textButtonColors(), MaterialTheme.colorScheme.primary),
            )
        if (!editMode)
            RecordActionTextBtn(
                text = "Log in",
                onClick = { viewModel.onLogInClick() },
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