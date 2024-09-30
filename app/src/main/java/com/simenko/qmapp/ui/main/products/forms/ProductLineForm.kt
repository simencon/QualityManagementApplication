package com.simenko.qmapp.ui.main.products.forms

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Person
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
import com.simenko.qmapp.other.Constants.DEFAULT_SPACE
import com.simenko.qmapp.other.Constants.FAB_HEIGHT
import com.simenko.qmapp.ui.common.InfoLine
import com.simenko.qmapp.ui.common.RecordFieldItem
import com.simenko.qmapp.ui.common.RecordFieldItemWithMenu
import com.simenko.qmapp.ui.navigation.Route
import com.simenko.qmapp.utils.StringUtils.concatTwoStrings1

@Composable
fun ProductLineForm(
    modifier: Modifier = Modifier,
    viewModel: ProductLineViewModel,
    route: Route.Main.ProductLines.AddEditProductLine,
) {
    val productLine by viewModel.productLine.collectAsStateWithLifecycle()
    val availableDesignDepartments by viewModel.availableDepartments.collectAsStateWithLifecycle()
    val availableEmployees by viewModel.availableEmployees.collectAsStateWithLifecycle()
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

    val (designationFR) = FocusRequester.createRefs()
    val (descriptionFR) = FocusRequester.createRefs()
    val (designDepartmentFR) = FocusRequester.createRefs()
    val (designManagerFR) = FocusRequester.createRefs()
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(all = 0.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.padding(all = 0.dp)
        ) {
            Spacer(modifier = Modifier.height(10.dp))
            InfoLine(modifier = modifier.padding(start = 0.dp), title = "Company", body = concatTwoStrings1(productLine.company.companyName, productLine.company.companyIndustrialClassification))
            Spacer(modifier = Modifier.height(10.dp))
            RecordFieldItem(
                modifier = Modifier.width(320.dp),
                valueParam = Triple(productLine.manufacturingProject.pfmeaNum ?: EmptyString.str, fillInErrors.productLineDesignationError) { viewModel.onSetDesignation(it) },
                keyboardNavigation = Pair(designationFR) { descriptionFR.requestFocus() },
                keyBoardTypeAction = Pair(KeyboardType.Ascii, ImeAction.Next),
                contentDescription = Triple(Icons.Outlined.Info, "Designation", "Enter designation"),
            )
            Spacer(modifier = Modifier.height(10.dp))
            RecordFieldItem(
                modifier = Modifier.width(320.dp),
                valueParam = Triple(productLine.manufacturingProject.projectSubject ?: EmptyString.str, fillInErrors.productLineDescriptionError) { viewModel.onSetDescription(it) },
                keyboardNavigation = Pair(descriptionFR) { designDepartmentFR.requestFocus() },
                keyBoardTypeAction = Pair(KeyboardType.Ascii, ImeAction.Next),
                contentDescription = Triple(Icons.Outlined.Info, "Description", "Enter description"),
            )

            Spacer(modifier = Modifier.height(10.dp))
            RecordFieldItemWithMenu(
                modifier = Modifier.width(320.dp),
                options = availableDesignDepartments,
                isError = fillInErrors.designDepartmentError,
                onDropdownMenuItemClick = { viewModel.onSetDesignDepartment(it ?: NoRecord.num) },
                keyboardNavigation = Pair(designDepartmentFR) { designManagerFR.requestFocus() },
                keyBoardTypeAction = Pair(KeyboardType.Ascii, ImeAction.Next),
                contentDescription = Triple(Icons.Outlined.AccountBalance, "Design department", "Select design dep."),
            )

            Spacer(modifier = Modifier.height(10.dp))
            RecordFieldItemWithMenu(
                modifier = Modifier.width(320.dp),
                options = availableEmployees,
                isError = fillInErrors.designManagerError,
                onDropdownMenuItemClick = { viewModel.onSetDesignManager(it ?: NoRecord.num) },
                keyboardNavigation = Pair(designManagerFR) { keyboardController?.hide() },
                keyBoardTypeAction = Pair(KeyboardType.Ascii, ImeAction.Next),
                contentDescription = Triple(Icons.Outlined.Person, "Design manager", "Select design manager"),
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