package com.simenko.qmapp.ui.main.team.forms.employee

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Work
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
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.simenko.qmapp.domain.EmptyString
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.repository.UserError
import com.simenko.qmapp.ui.common.RecordFieldItemWithMenu
import com.simenko.qmapp.ui.common.RecordFieldItem
import com.simenko.qmapp.ui.main.main.page.AddEditMode
import com.simenko.qmapp.ui.user.registration.enterdetails.FillInError
import com.simenko.qmapp.ui.user.registration.enterdetails.FillInInitialState
import com.simenko.qmapp.ui.user.registration.enterdetails.FillInSuccess

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun EmployeeForm(
    modifier: Modifier = Modifier,
    viewModel: EmployeeViewModel = hiltViewModel(),
    employeeId: Int
) {

    LaunchedEffect(employeeId) {
        if (employeeId == NoRecord.num) {
            viewModel.setupTopScreen(AddEditMode.ADD_EMPLOYEE, employeeId)
        } else {
            viewModel.setupTopScreen(AddEditMode.EDIT_EMPLOYEE, employeeId)
        }
    }

    val employee by viewModel.employee.collectAsStateWithLifecycle()
    val employeeErrors by viewModel.employeeErrors.collectAsStateWithLifecycle()

    val companies by viewModel.employeeCompanies.collectAsStateWithLifecycle()
    val departments by viewModel.employeeDepartments.collectAsStateWithLifecycle()
    val subDepartments by viewModel.employeeSubDepartments.collectAsStateWithLifecycle()
    val jobRoles by viewModel.employeeJobRoles.collectAsStateWithLifecycle()

    val fillInState by viewModel.fillInState.collectAsStateWithLifecycle()
    var error by rememberSaveable { mutableStateOf(UserError.NO_ERROR.error) }

    fillInState.let { state ->
        when (state) {
            is FillInSuccess -> viewModel.makeEmployee()
            is FillInError -> error = state.errorMsg
            is FillInInitialState -> error = UserError.NO_ERROR.error
        }
    }

    val (fullNameFR) = FocusRequester.createRefs()
    val (companyFR) = FocusRequester.createRefs()
    val (departmentFR) = FocusRequester.createRefs()
    val (subDepartmentFR) = FocusRequester.createRefs()
    val (jobRoleFR) = FocusRequester.createRefs()
    val (jobRoleDetailsFR) = FocusRequester.createRefs()
    val (emailFR) = FocusRequester.createRefs()
    val (passwordFR) = FocusRequester.createRefs()

    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .padding(all = 0.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(10.dp))
        RecordFieldItem(
            valueParam = Triple(employee.fullName, employeeErrors.fullNameError) { viewModel.setFullName(it) },
            keyboardNavigation = Pair(fullNameFR) { keyboardController?.hide() },
            keyBoardTypeAction = Pair(KeyboardType.Ascii, ImeAction.Done),
            contentDescription = Triple(Icons.Default.Person, "Full name", "Enter name and surname")
        )
        Spacer(modifier = Modifier.height(10.dp))
        RecordFieldItemWithMenu(
            options = companies,
            isError = employeeErrors.companyError,
            onDropdownMenuItemClick = { viewModel.setEmployeeCompany(it) },
            keyboardNavigation = Pair(companyFR) { companyFR.requestFocus() },
            keyBoardTypeAction = Pair(KeyboardType.Ascii, ImeAction.Done),
            contentDescription = Triple(Icons.Default.Apartment, "Company", "Select company"),
        )
        Spacer(modifier = Modifier.height(10.dp))
        RecordFieldItemWithMenu(
            options = departments,
            isError = employeeErrors.departmentError,
            onDropdownMenuItemClick = { viewModel.setEmployeeDepartment(it) },
            keyboardNavigation = Pair(departmentFR) { departmentFR.requestFocus() },
            keyBoardTypeAction = Pair(KeyboardType.Ascii, ImeAction.Done),
            contentDescription = Triple(Icons.Default.AccountBalance, "Department", "Select department"),
        )
        Spacer(modifier = Modifier.height(10.dp))
        RecordFieldItemWithMenu(
            options = subDepartments,
            isError = employeeErrors.subDepartmentError,
            onDropdownMenuItemClick = { viewModel.setEmployeeSubDepartment(it) },
            keyboardNavigation = Pair(subDepartmentFR) { subDepartmentFR.requestFocus() },
            keyBoardTypeAction = Pair(KeyboardType.Ascii, ImeAction.Done),
            contentDescription = Triple(Icons.Default.AccountTree, "Sub department", "Select only if applicable"),
            isMandatoryField = false
        )
        Spacer(modifier = Modifier.height(10.dp))
        RecordFieldItemWithMenu(
            options = jobRoles,
            isError = employeeErrors.jobRoleIdError,
            onDropdownMenuItemClick = { viewModel.setEmployeeJobRole(it) },
            keyboardNavigation = Pair(jobRoleFR) { jobRoleFR.requestFocus() },
            keyBoardTypeAction = Pair(KeyboardType.Ascii, ImeAction.Done),
            contentDescription = Triple(Icons.Default.Work, "Job role", "Enter job role / position")
        )
        Spacer(modifier = Modifier.height(10.dp))
        RecordFieldItem(
            valueParam = Triple(employee.jobRole, employeeErrors.jobRoleError) { viewModel.setJobRole(it) },
            keyboardNavigation = Pair(jobRoleDetailsFR) { keyboardController?.hide() },
            keyBoardTypeAction = Pair(KeyboardType.Ascii, ImeAction.Done),
            contentDescription = Triple(Icons.Default.Info, "Job role details", "Enter job role details")
        )
        Spacer(modifier = Modifier.height(10.dp))
        RecordFieldItem(
            valueParam = Triple(employee.email ?: EmptyString.str, employeeErrors.emailError) { viewModel.setEmail(it) },
            keyboardNavigation = Pair(emailFR) { keyboardController?.hide() },
            keyBoardTypeAction = Pair(KeyboardType.Email, ImeAction.Done),
            contentDescription = Triple(Icons.Default.Mail, "Email", "Enter email"),
            isMandatoryField = false
        )
        Spacer(modifier = Modifier.height(10.dp))
        var passwordVisible by rememberSaveable { mutableStateOf(false) }
        RecordFieldItem(
            valueParam = Triple(employee.passWord ?: EmptyString.str, employeeErrors.passwordError) { viewModel.setPassword(it) },
            keyboardNavigation = Pair(passwordFR) { keyboardController?.hide() },
            keyBoardTypeAction = Pair(KeyboardType.Email, ImeAction.Done),
            contentDescription = Triple(Icons.Default.Mail, "Password", "Enter password"),
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }, enabled = true) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = if (passwordVisible) "Hide password" else "Show password",
                        tint = if (employeeErrors.passwordError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.surfaceTint
                    )
                }
            },
            isMandatoryField = false
        )
        Spacer(modifier = Modifier.height(10.dp))
        if (error != UserError.NO_ERROR.error)
            Text(
                text = error,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 14.sp, color = MaterialTheme.colorScheme.error),
                modifier = Modifier.padding(all = 5.dp),
                textAlign = TextAlign.Center
            )
    }
}