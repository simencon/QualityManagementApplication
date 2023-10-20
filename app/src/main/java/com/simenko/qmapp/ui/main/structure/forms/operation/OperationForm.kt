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
import androidx.compose.material.icons.filled.Person
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
import com.simenko.qmapp.domain.NoString
import com.simenko.qmapp.domain.SelectedString
import com.simenko.qmapp.repository.UserError
import com.simenko.qmapp.ui.common.InfoLine
import com.simenko.qmapp.ui.common.RecordFieldItemWithMenu
import com.simenko.qmapp.ui.main.team.forms.user.subforms.RolesHeader
import com.simenko.qmapp.ui.main.team.forms.user.subforms.TrueFalseField

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

    val (userEmployeeFR) = FocusRequester.createRefs()

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
            InfoLine(modifier = modifier.padding(start = 0.dp), title = "Department", body = operation.lineComplete.depName ?: NoString.str)
            InfoLine(modifier = modifier.padding(start = 0.dp), title = "Sub department", body = operation.lineComplete.subDepDesignation ?: NoString.str)
            InfoLine(modifier = modifier.padding(start = 0.dp), title = "Channel", body = operation.lineComplete.channelDesignation ?: NoString.str)
            InfoLine(modifier = modifier.padding(start = 0.dp), title = "Line", body = operation.lineComplete.lineDesignation)


            Spacer(modifier = Modifier.height(10.dp))
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