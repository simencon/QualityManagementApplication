package com.simenko.qmapp.presentation.ui.main.structure.forms.sub_department

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
import com.simenko.qmapp.domain.entities.DomainSubDepartment
import com.simenko.qmapp.other.Constants.DEFAULT_SPACE
import com.simenko.qmapp.other.Constants.FAB_HEIGHT
import com.simenko.qmapp.presentation.ui.common.InfoLine
import com.simenko.qmapp.presentation.ui.common.RecordFieldItem
import com.simenko.qmapp.navigation.Route
import com.simenko.qmapp.utils.StringUtils.concatTwoStrings

@Composable
fun SubDepartmentForm(
    modifier: Modifier = Modifier,
    viewModel: SubDepartmentViewModel,
    route: Route.Main.CompanyStructure.SubDepartmentAddEdit
) {
    val sdComplete by viewModel.subDepartment.collectAsStateWithLifecycle(DomainSubDepartment.DomainSubDepartmentComplete())
    LaunchedEffect(Unit) { viewModel.onEntered(route = route) }

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
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(horizontalAlignment = Alignment.Start, verticalArrangement = Arrangement.Bottom) {
        Spacer(modifier = Modifier.height(10.dp))
        InfoLine(modifier = modifier.padding(start = DEFAULT_SPACE.dp).fillMaxWidth(), title = "Department", body = concatTwoStrings(sdComplete.department.depAbbr, sdComplete.department.depName))
        HorizontalDivider(modifier = Modifier.height(1.dp), color = MaterialTheme.colorScheme.secondary)
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
                modifier = Modifier.width(320.dp),
                valueParam = Triple(sdComplete.subDepartment.subDepOrder.let { if (it == NoRecord.num.toInt()) EmptyString.str else it }.toString(), fillInErrors.subDepartmentOrderError) {
                    viewModel.setSubDepartmentOrder(if (it == EmptyString.str) NoRecord.num.toInt() else it.toInt())
                },
                keyboardNavigation = Pair(orderFR) { abbreviationFR.requestFocus() },
                keyBoardTypeAction = Pair(KeyboardType.Number, ImeAction.Next),
                contentDescription = Triple(Icons.Outlined.FormatListNumbered, "Sub department order", "Enter order"),
            )
            Spacer(modifier = Modifier.height(10.dp))
            RecordFieldItem(
                modifier = Modifier.width(320.dp),
                valueParam = Triple(sdComplete.subDepartment.subDepAbbr ?: EmptyString.str, fillInErrors.subDepartmentAbbrError) { viewModel.setSubDepartmentAbbr(it) },
                keyboardNavigation = Pair(abbreviationFR) { designationFR.requestFocus() },
                keyBoardTypeAction = Pair(KeyboardType.Ascii, ImeAction.Next),
                contentDescription = Triple(Icons.Outlined.Info, "Sub department id", "Enter id"),
            )
            Spacer(modifier = Modifier.height(10.dp))
            RecordFieldItem(
                modifier = Modifier.width(320.dp),
                valueParam = Triple(sdComplete.subDepartment.subDepDesignation ?: EmptyString.str, fillInErrors.subDepartmentDesignationError) { viewModel.setSubDepartmentDesignation(it) },
                keyboardNavigation = Pair(designationFR) { keyboardController?.hide() },
                keyBoardTypeAction = Pair(KeyboardType.Ascii, ImeAction.Done),
                contentDescription = Triple(Icons.Outlined.Info, "Sub dep. complete name", "Enter complete name"),
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