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
import androidx.compose.material.icons.filled.Apartment
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
import com.simenko.qmapp.domain.NoString
import com.simenko.qmapp.repository.UserError
import com.simenko.qmapp.ui.common.RecordFieldItemWithMenu
import com.simenko.qmapp.ui.main.settings.InfoLine
import com.simenko.qmapp.ui.user.registration.enterdetails.FillInError
import com.simenko.qmapp.ui.user.registration.enterdetails.FillInInitialState
import com.simenko.qmapp.ui.user.registration.enterdetails.FillInSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun UserForm(modifier: Modifier = Modifier, userId: String) {
    val viewModel: UserViewModel = hiltViewModel()

    LaunchedEffect(key1 = userId) {
        withContext(Dispatchers.Default) {
            if (userId != NoString.str)
                viewModel.loadUser(userId)
        }
    }

    val user by viewModel.user.collectAsStateWithLifecycle()
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

    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(all = 0.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(10.dp))
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .fillMaxSize()
                .padding(all = 0.dp)
        ) {
            InfoLine(modifier = modifier.padding(start = 15.dp), title = "Full name", body = user.fullName ?: NoString.str)
            InfoLine(modifier = modifier.padding(start = 15.dp), title = "Job role", body = user.jobRole ?: NoString.str)
            InfoLine(
                modifier = modifier.padding(start = 15.dp),
                title = "Department",
                body = user.department + if (user.subDepartment.isNullOrEmpty()) EmptyString.str else "/${user.subDepartment}"
            )
        }
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
        if (error != UserError.NO_ERROR.error)
            Text(
                text = error,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 14.sp, color = MaterialTheme.colorScheme.error),
                modifier = Modifier.padding(all = 5.dp),
                textAlign = TextAlign.Center
            )
    }
}