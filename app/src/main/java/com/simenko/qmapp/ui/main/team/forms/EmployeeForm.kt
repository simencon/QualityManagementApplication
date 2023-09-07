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
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.simenko.qmapp.domain.EmptyString
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.domain.NoString
import com.simenko.qmapp.ui.common.DropdownMenu
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

    val employeeCompanies by viewModel.employeeCompanies.collectAsStateWithLifecycle()
    val employeeDepartments by viewModel.employeeDepartments.collectAsStateWithLifecycle()

    val (focusRequesterFullName) = FocusRequester.createRefs()
    val (focusRequesterDepartment) = FocusRequester.createRefs()

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
            keyboardNavigation = Pair(focusRequesterFullName) { focusRequesterDepartment.requestFocus() },
            keyBoardTypeAction = Pair(KeyboardType.Ascii, ImeAction.Next),
            contentDescription = Triple(Icons.Default.Person, "Full name", "Enter name and surname")
        )
        Spacer(modifier = Modifier.height(10.dp))
        val companies = mutableListOf<Pair<Int, String>>()
        var selectedCompany = EmptyString.str
        employeeCompanies.forEach {
            companies.add(Pair(it.id, it.companyName ?: NoString.str))
            if(employee.companyId == it.id) selectedCompany = it.companyName?: NoString.str
        }
        DropdownMenu(
            options = companies,
            onDropdownMenuItemClick = { viewModel.setEmployeeCompany(it) },
            selectedName = selectedCompany
        )
        Spacer(modifier = Modifier.height(10.dp))
        val departments = mutableListOf<Pair<Int, String>>()
        var selectedDepartment = EmptyString.str
        employeeDepartments.forEach {
            departments.add(Pair(it.id, it.depAbbr ?: NoString.str))
            if(employee.companyId == it.id) selectedDepartment = it.depAbbr?: NoString.str
        }
        DropdownMenu(
            options = departments,
            onDropdownMenuItemClick = { viewModel.setEmployeeDepartment(it) },
            selectedName = selectedDepartment
        )

    }
}