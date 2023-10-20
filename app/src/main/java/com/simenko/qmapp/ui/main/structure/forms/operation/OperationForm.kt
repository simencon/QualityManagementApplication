package com.simenko.qmapp.ui.main.structure.forms.operation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Construction
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.Info
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.simenko.qmapp.domain.EmptyString
import com.simenko.qmapp.domain.FillInError
import com.simenko.qmapp.domain.FillInInitialState
import com.simenko.qmapp.domain.FillInSuccess
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.domain.NoString
import com.simenko.qmapp.domain.SelectedString
import com.simenko.qmapp.repository.UserError
import com.simenko.qmapp.ui.common.InfoLine
import com.simenko.qmapp.ui.common.RecordFieldItem
import com.simenko.qmapp.ui.common.RecordFieldItemWithMenu
import com.simenko.qmapp.ui.main.team.forms.user.subforms.RolesHeader
import com.simenko.qmapp.ui.main.team.forms.user.subforms.TrueFalseField
import com.simenko.qmapp.utils.StringUtils.concatTwoStrings

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun OperationForm(
    modifier: Modifier = Modifier,
    viewModel: OperationViewModel
) {
    val operation by viewModel.operation.collectAsStateWithLifecycle()
    LaunchedEffect(operation) {
        viewModel.mainPageHandler?.setupMainPage?.invoke(0, true)
    }

    val fillInErrors by viewModel.fillInErrors.collectAsStateWithLifecycle()

    val fillInState by viewModel.fillInState.collectAsStateWithLifecycle()
    var error by rememberSaveable { mutableStateOf(EmptyString.str) }

    LaunchedEffect(fillInState) {
        fillInState.let { state ->
            when (state) {
                is FillInSuccess -> viewModel.makeRecord()
                is FillInError -> error = state.errorMsg
                is FillInInitialState -> error = EmptyString.str
            }
        }
    }

    val (orderFR) = FocusRequester.createRefs()
    val (abbreviationFR) = FocusRequester.createRefs()
    val (designationFR) = FocusRequester.createRefs()
    val (equipmentFR) = FocusRequester.createRefs()
    val keyboardController = LocalSoftwareKeyboardController.current

    val isAddPreviousOperationDialogVisible by viewModel.isAddPreviousOperationDialogVisible.collectAsStateWithLifecycle()

    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(all = 0.dp)
            .verticalScroll(rememberScrollState())
    ) {
//        if (isAddPreviousOperationDialogVisible)
//            AddRole(userModel = viewModel)

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.padding(all = 0.dp)
        ) {
            Spacer(modifier = Modifier.height(10.dp))
            InfoLine(modifier = modifier.padding(start = 0.dp), title = "Department", body = concatTwoStrings(operation.lineComplete.depAbbr, operation.lineComplete.depName))
            InfoLine(modifier = modifier.padding(start = 0.dp), title = "Sub department", body = concatTwoStrings(operation.lineComplete.subDepAbbr, operation.lineComplete.subDepDesignation))
            InfoLine(modifier = modifier.padding(start = 0.dp), title = "Channel", body = concatTwoStrings(operation.lineComplete.channelAbbr, operation.lineComplete.channelDesignation))
            InfoLine(modifier = modifier.padding(start = 0.dp), title = "Line", body = concatTwoStrings(operation.lineComplete.lineAbbr, operation.lineComplete.lineDesignation))
            Spacer(modifier = Modifier.height(10.dp))
            RecordFieldItem(
                valueParam = Triple(operation.operation.operationOrder.let { if (it == NoRecord.num) EmptyString.str else it }.toString(), fillInErrors.operationOrderError) {
                    viewModel.setOperationOrder(it.toInt())
                },
                keyboardNavigation = Pair(orderFR) { keyboardController?.hide() },
                keyBoardTypeAction = Pair(KeyboardType.Number, ImeAction.Done),
                contentDescription = Triple(Icons.Default.FormatListNumbered, "Operation order", "Enter operation order"),
            )
            Spacer(modifier = Modifier.height(10.dp))
            RecordFieldItem(
                valueParam = Triple(operation.operation.operationAbbr, fillInErrors.operationAbbrError) { viewModel.setOperationAbbr(it) },
                keyboardNavigation = Pair(abbreviationFR) { keyboardController?.hide() },
                keyBoardTypeAction = Pair(KeyboardType.Text, ImeAction.Done),
                contentDescription = Triple(Icons.Default.Fingerprint, "Operation id", "Enter operation id"),
            )
            Spacer(modifier = Modifier.height(10.dp))
            RecordFieldItem(
                valueParam = Triple(operation.operation.operationDesignation, fillInErrors.operationDesignationError) { viewModel.setOperationDesignation(it) },
                keyboardNavigation = Pair(designationFR) { keyboardController?.hide() },
                keyBoardTypeAction = Pair(KeyboardType.Text, ImeAction.Done),
                contentDescription = Triple(Icons.Default.Info, "Operation complete name", "Enter operation complete name"),
            )
            Spacer(modifier = Modifier.height(10.dp))
            RecordFieldItem(
                valueParam = Triple(operation.operation.equipment ?: EmptyString.str, fillInErrors.operationEquipmentError) { viewModel.setOperationEquipment(it) },
                keyboardNavigation = Pair(equipmentFR) { keyboardController?.hide() },
                keyBoardTypeAction = Pair(KeyboardType.Text, ImeAction.Done),
                contentDescription = Triple(Icons.Default.Construction, "Operation equipment", "Enter operation equipment")
            )
            /*RecordFieldItemWithMenu(
                options = userEmployees,
                isError = fillInErrors.teamMemberError,
                onDropdownMenuItemClick = { viewModel.setUserEmployee(it) },
                keyboardNavigation = Pair(userEmployeeFR) { userEmployeeFR.requestFocus() },
                keyBoardTypeAction = Pair(KeyboardType.Ascii, ImeAction.Done),
                contentDescription = Triple(Icons.Default.Person, "Company employee", "Select company employee"),
            )
            Spacer(modifier = Modifier.height(10.dp))
            RolesHeader(
                userRoles = userRoles,
                userRolesError = fillInErrors.rolesError,
                onClickActions = { viewModel.setCurrentUserRoleVisibility(aId = SelectedString(it)) },
                onClickDelete = { viewModel.deleteUserRole(it) },
                onClickAdd = { viewModel.setAddRoleDialogVisibility(true) }
            )
            Spacer(modifier = Modifier.height(10.dp))
            TrueFalseField(
                user = operation,
                onSwitch = { viewModel.setUserIsEnabled(it) },
                isError = fillInErrors.enabledError
            )
            Spacer(modifier = Modifier.height(10.dp))
            if (error != UserError.NO_ERROR.error)
                Text(
                    text = error,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 14.sp, color = MaterialTheme.colorScheme.error),
                    modifier = Modifier.padding(all = 5.dp),
                    textAlign = TextAlign.Center
                )
            Spacer(modifier = Modifier.height(10.dp))*/
        }
    }
}