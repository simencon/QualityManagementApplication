package com.simenko.qmapp.ui.main.team.forms.user

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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.simenko.qmapp.domain.EmptyString
import com.simenko.qmapp.domain.NoRecordStr
import com.simenko.qmapp.domain.NoString
import com.simenko.qmapp.domain.SelectedString
import com.simenko.qmapp.other.Constants
import com.simenko.qmapp.repository.UserError
import com.simenko.qmapp.ui.common.RecordFieldItemWithMenu
import com.simenko.qmapp.ui.main.settings.InfoLine
import com.simenko.qmapp.ui.main.team.forms.user.subforms.role.AddRole
import com.simenko.qmapp.ui.main.team.forms.user.subforms.RolesHeader
import com.simenko.qmapp.ui.main.team.forms.user.subforms.TrueFalseField
import com.simenko.qmapp.ui.user.registration.enterdetails.FillInError
import com.simenko.qmapp.ui.user.registration.enterdetails.FillInInitialState
import com.simenko.qmapp.ui.user.registration.enterdetails.FillInSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun UserForm(modifier: Modifier = Modifier, userId: String) {
    val viewModel: UserViewModel = hiltViewModel()

    val user by viewModel.user.collectAsStateWithLifecycle()
    LaunchedEffect(key1 = userId) {
        if (user.email == NoRecordStr.str)
            withContext(Dispatchers.Default) {
                if (userId != NoRecordStr.str) {
                    viewModel.clearNotificationIfExists(userId)
                    viewModel.loadUser(userId)
                }
            }
    }

    val userRoles by viewModel.userRoles.collectAsStateWithLifecycle()
    val userErrors by viewModel.userErrors.collectAsStateWithLifecycle()

    val userEmployees by viewModel.userEmployees.collectAsStateWithLifecycle()

    val fillInState by viewModel.fillInState.collectAsStateWithLifecycle()
    var error by rememberSaveable { mutableStateOf(UserError.NO_ERROR.error) }

    fillInState.let { state ->
        when (state) {
            is FillInSuccess -> viewModel.makeUser(user)
            is FillInError -> error = state.errorMsg
            is FillInInitialState -> error = UserError.NO_ERROR.error
        }
    }

    val (userEmployeeFR) = FocusRequester.createRefs()

    val isAddRoleDialogVisible by viewModel.isAddRoleDialogVisible.collectAsStateWithLifecycle()

    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(all = 0.dp)
            .verticalScroll(rememberScrollState())
    ) {
        if (isAddRoleDialogVisible)
            AddRole(userModel = viewModel)

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.padding(all = 0.dp)
        ) {
            Spacer(modifier = Modifier.height(10.dp))
            InfoLine(modifier = modifier.padding(start = 0.dp), title = "User full name", body = user.fullName ?: NoString.str)
            InfoLine(modifier = modifier.padding(start = 0.dp), title = "User job role", body = user.jobRole ?: NoString.str)
            InfoLine(
                modifier = modifier.padding(start = 0.dp),
                title = "User department",
                body = user.department + if (user.subDepartment.isNullOrEmpty()) EmptyString.str else "/${user.subDepartment}"
            )
            Spacer(modifier = Modifier.height(10.dp))
            RecordFieldItemWithMenu(
                options = userEmployees,
                isError = userErrors.teamMemberError,
                onDropdownMenuItemClick = { viewModel.setUserEmployee(it) },
                keyboardNavigation = Pair(userEmployeeFR) { userEmployeeFR.requestFocus() },
                keyBoardTypeAction = Pair(KeyboardType.Ascii, ImeAction.Done),
                contentDescription = Triple(Icons.Default.Person, "Company employee", "Select company employee"),
            )
            Spacer(modifier = Modifier.height(10.dp))
            RolesHeader(
                modifier = Modifier.padding(Constants.CARDS_PADDING),
                userRoles = userRoles,
                userRolesError = userErrors.rolesError,
                onClickActions = { viewModel.setCurrentUserRoleVisibility(aId = SelectedString(it)) },
                onClickDelete = { viewModel.deleteUserRole(it) },
                onClickAdd = { viewModel.setAddRoleDialogVisibility(true) }
            )
            Spacer(modifier = Modifier.height(10.dp))
            TrueFalseField(
                modifier = Modifier.padding(Constants.CARDS_PADDING),
                user = user,
                onSwitch = { viewModel.setUserIsEnabled(it) },
                isError = userErrors.enabledError
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
        }
    }
}