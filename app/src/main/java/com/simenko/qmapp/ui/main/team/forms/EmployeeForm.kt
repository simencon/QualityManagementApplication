package com.simenko.qmapp.ui.main.team.forms

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
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.ui.common.RecordFieldItemWithMenu
import com.simenko.qmapp.ui.common.RecordFieldItem

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun EmployeeForm(modifier: Modifier = Modifier, employeeId: Int) {
    val viewModel: EmployeeViewModel = hiltViewModel()

    LaunchedEffect(key1 = employeeId) {
        if (employeeId != NoRecord.num)
            viewModel.loadEmployee(employeeId)
    }

    val employee by viewModel.employee.collectAsStateWithLifecycle()
    val employeeErrors by viewModel.employeeErrors.collectAsStateWithLifecycle()

    val companies by viewModel.employeeCompanies.collectAsStateWithLifecycle()
    val departments by viewModel.employeeDepartments.collectAsStateWithLifecycle()
    val subDepartments by viewModel.employeeSubDepartments.collectAsStateWithLifecycle()

    val (focusRequesterFullName) = FocusRequester.createRefs()
    val (focusRequesterCompany) = FocusRequester.createRefs()
    val (focusRequesterDepartment) = FocusRequester.createRefs()
    val (focusRequesterSubDepartment) = FocusRequester.createRefs()

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
            keyboardNavigation = Pair(focusRequesterFullName) { keyboardController?.hide() },
            keyBoardTypeAction = Pair(KeyboardType.Ascii, ImeAction.Next),
            contentDescription = Triple(Icons.Default.Person, "Full name", "Enter name and surname")
        )
        Spacer(modifier = Modifier.height(10.dp))
        RecordFieldItemWithMenu(
            options = companies,
            isError = employeeErrors.companyError,
            onDropdownMenuItemClick = { viewModel.setEmployeeCompany(it) },
            keyboardNavigation = Pair(focusRequesterCompany) { focusRequesterCompany.requestFocus() },
            keyBoardTypeAction = Pair(KeyboardType.Ascii, ImeAction.Next),
            contentDescription = Triple(Icons.Default.Apartment, "Company", "Select company"),
        )
        Spacer(modifier = Modifier.height(10.dp))
        RecordFieldItemWithMenu(
            options = departments,
            isError = employeeErrors.departmentError,
            onDropdownMenuItemClick = { viewModel.setEmployeeDepartment(it) },
            keyboardNavigation = Pair(focusRequesterDepartment) { focusRequesterDepartment.requestFocus()},
            keyBoardTypeAction = Pair(KeyboardType.Ascii, ImeAction.Next),
            contentDescription = Triple(Icons.Default.AccountBalance, "Department", "Select department"),
        )
        Spacer(modifier = Modifier.height(10.dp))
        RecordFieldItemWithMenu(
            options = subDepartments,
            isError = employeeErrors.subDepartmentError,
            onDropdownMenuItemClick = { viewModel.setEmployeeSubDepartment(it) },
            keyboardNavigation = Pair(focusRequesterSubDepartment) { focusRequesterSubDepartment.requestFocus() },
            keyBoardTypeAction = Pair(KeyboardType.Ascii, ImeAction.Next),
            contentDescription = Triple(Icons.Default.AccountTree, "Sub department", "Select only if applicable"),
            isMandatoryField = false
        )
    }
}