package com.simenko.qmapp.presentation.ui.main.structure.forms.department

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FormatListNumbered
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Work
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.simenko.qmapp.domain.EmptyString
import com.simenko.qmapp.domain.FillInErrorState
import com.simenko.qmapp.domain.FillInInitialState
import com.simenko.qmapp.domain.FillInSuccessState
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.domain.entities.DomainDepartment
import com.simenko.qmapp.other.Constants.DEFAULT_SPACE
import com.simenko.qmapp.other.Constants.FAB_HEIGHT
import com.simenko.qmapp.presentation.ui.common.InfoLine
import com.simenko.qmapp.presentation.ui.common.RecordFieldItem
import com.simenko.qmapp.presentation.ui.common.RecordFieldItemWithMenu
import com.simenko.qmapp.navigation.Route
import com.simenko.qmapp.utils.StringUtils.concatTwoStrings1

@Composable
fun DepartmentForm(
    modifier: Modifier = Modifier,
    viewModel: DepartmentViewModel,
    route: Route.Main.CompanyStructure.DepartmentAddEdit,
) {
    val dComplete by viewModel.department.collectAsStateWithLifecycle(DomainDepartment.DomainDepartmentComplete())
    val companyEmployees by viewModel.companyEmployees.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) { viewModel.onEntered(route) }

    val fillInErrors by viewModel.fillInErrors.collectAsStateWithLifecycle()

    val fillInState by viewModel.fillInState.collectAsStateWithLifecycle()
    var error by rememberSaveable { mutableStateOf(EmptyString.str) }

    LaunchedEffect(fillInState) {
        fillInState.let { state ->
            when (state) {
                is FillInSuccessState -> viewModel.makeRecord()
                is FillInErrorState -> error = state.errorMsg
                is FillInInitialState -> error = EmptyString.str
            }
        }
    }

    val (orderFR) = FocusRequester.createRefs()
    val (abbreviationFR) = FocusRequester.createRefs()
    val (designationFR) = FocusRequester.createRefs()
    val (managerFR) = FocusRequester.createRefs()
    val (functionFR) = FocusRequester.createRefs()
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Bottom,
    ) {
        Spacer(modifier = Modifier.height(10.dp))
        InfoLine(modifier = modifier.padding(start = DEFAULT_SPACE.dp).fillMaxWidth(), title = "Company", body = concatTwoStrings1(dComplete.company.companyName, dComplete.company.companyIndustrialClassification))
        HorizontalDivider(modifier = Modifier.height(1.dp), color = MaterialTheme.colorScheme.secondary)
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(all = 0.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(10.dp))
            RecordFieldItem(
                modifier = Modifier.width(320.dp),
                valueParam = Triple(dComplete.department.depOrder.let { if (it == NoRecord.num.toInt()) EmptyString.str else it }.toString(), fillInErrors.departmentOrderError) {
                    viewModel.setDepartmentOrder(if (it == EmptyString.str) NoRecord.num.toInt() else it.toInt())
                },
                keyboardNavigation = Pair(orderFR) { abbreviationFR.requestFocus() },
                keyBoardTypeAction = Pair(KeyboardType.Number, ImeAction.Next),
                contentDescription = Triple(Icons.Outlined.FormatListNumbered, "Department order", "Enter order"),
            )
            Spacer(modifier = Modifier.height(10.dp))
            RecordFieldItem(
                modifier = Modifier.width(320.dp),
                valueParam = Triple(dComplete.department.depAbbr ?: EmptyString.str, fillInErrors.departmentAbbrError) { viewModel.setDepartmentAbbr(it) },
                keyboardNavigation = Pair(abbreviationFR) { designationFR.requestFocus() },
                keyBoardTypeAction = Pair(KeyboardType.Ascii, ImeAction.Next),
                contentDescription = Triple(Icons.Outlined.Info, "Department id", "Enter id"),
            )
            Spacer(modifier = Modifier.height(10.dp))
            RecordFieldItem(
                modifier = Modifier.width(320.dp),
                valueParam = Triple(dComplete.department.depName ?: EmptyString.str, fillInErrors.departmentDesignationError) { viewModel.setDepartmentDesignation(it) },
                keyboardNavigation = Pair(designationFR) { managerFR.requestFocus() },
                keyBoardTypeAction = Pair(KeyboardType.Ascii, ImeAction.Next),
                contentDescription = Triple(Icons.Outlined.Info, "Department complete name", "Enter complete name"),
            )

            Spacer(modifier = Modifier.height(10.dp))
            RecordFieldItemWithMenu(
                modifier = Modifier.width(320.dp),
                options = companyEmployees,
                isError = fillInErrors.departmentManagerError,
                onDropdownMenuItemClick = { viewModel.setDepartmentManager(it ?: NoRecord.num) },
                keyboardNavigation = Pair(managerFR) { managerFR.requestFocus() },
                keyBoardTypeAction = Pair(KeyboardType.Ascii, ImeAction.Next),
                contentDescription = Triple(Icons.Outlined.Person, "Department manager", "Select dep. manager"),
            )

            Spacer(modifier = Modifier.height(10.dp))
            RecordFieldItem(
                modifier = Modifier.width(320.dp),
                valueParam = Triple(dComplete.department.depOrganization ?: EmptyString.str, fillInErrors.departmentOrganizationError) { viewModel.setDepartmentFunction(it) },
                keyboardNavigation = Pair(functionFR) { keyboardController?.hide() },
                keyBoardTypeAction = Pair(KeyboardType.Ascii, ImeAction.Done),
                contentDescription = Triple(Icons.Outlined.Work, "Department function", "Enter function"),
            )
            Spacer(modifier = Modifier.height(10.dp))
            if (error != EmptyString.str)
                Text(
                    modifier = Modifier
                        .padding(all = 5.dp)
                        .width(320.dp),
                    text = error,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 14.sp, color = MaterialTheme.colorScheme.error),
                    textAlign = TextAlign.Center
                )
            Spacer(modifier = Modifier.height((FAB_HEIGHT + DEFAULT_SPACE).dp))
        }
    }
}