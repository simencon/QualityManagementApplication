package com.simenko.qmapp.ui.main.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.simenko.qmapp.domain.EmptyString
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.repository.NoState
import com.simenko.qmapp.repository.UnregisteredState
import com.simenko.qmapp.repository.UserAuthoritiesNotVerifiedState
import com.simenko.qmapp.repository.UserError
import com.simenko.qmapp.ui.theme.QMAppTheme
import com.simenko.qmapp.repository.UserErrorState
import com.simenko.qmapp.repository.UserLoggedInState
import com.simenko.qmapp.repository.UserLoggedOutState
import com.simenko.qmapp.repository.UserNeedToVerifyEmailState
import com.simenko.qmapp.ui.dialogs.ApproveAction
import com.simenko.qmapp.ui.user.registration.enterdetails.RecordActionTextBtn

@Composable
fun Settings(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val settingsModel: SettingsViewModel = hiltViewModel()
    val userState by settingsModel.userState.collectAsStateWithLifecycle()

    LaunchedEffect(userState) {
        userState.let {
            when (it) {
                is UserErrorState ->
                    if (it.error != UserError.NO_ERROR.error)
                        settingsModel.clearLoadingState(it.error ?: UserError.UNKNOWN_ERROR.error)
                    else
                        settingsModel.clearLoadingState()

                is UserLoggedOutState, is UnregisteredState, is UserNeedToVerifyEmailState, is UserAuthoritiesNotVerifiedState -> onClick()
                is UserLoggedInState, is NoState -> settingsModel.clearLoadingState()
            }
        }
    }

    val approveActionDialogVisibility by settingsModel.isApproveActionVisible.collectAsStateWithLifecycle()

    val onDenyLambda = remember { { settingsModel.hideActionApproveDialog() } }
    val onApproveLambda = remember<(String) -> String> {
        {
            if (it == settingsModel.userLocalData.password) {
                settingsModel.deleteAccount(settingsModel.userLocalData.email, it)
                EmptyString.str
            } else {
                UserError.WRONG_PASSWORD.error
            }
        }
    }

    val columnState = rememberScrollState()

    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(all = 0.dp)
            .verticalScroll(columnState)
    ) {
        Spacer(modifier = Modifier.height(10.dp))
        Image(
            painter = painterResource(settingsModel.userLocalData.logo),
            contentDescription = null,
            contentScale = ContentScale.FillHeight,
            modifier = Modifier.height(112.dp)
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = settingsModel.userLocalData.fullName,
            style = MaterialTheme.typography.titleMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = modifier,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(10.dp))
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .padding(all = 0.dp)
        ) {
            InfoLine(modifier = modifier.padding(start = 15.dp), title = "Job role", body = settingsModel.userLocalData.jobRole)
            InfoLine(
                modifier = modifier.padding(start = 15.dp),
                title = "Department",
                body = settingsModel.userLocalData.department +
                        if (settingsModel.userLocalData.subDepartment == EmptyString.str) "" else "/${settingsModel.userLocalData.subDepartment}"
            )
            InfoLine(modifier = modifier.padding(start = 15.dp), title = "Company", body = settingsModel.userLocalData.company)
            InfoLine(modifier = modifier.padding(start = 15.dp), title = "Email", body = settingsModel.userLocalData.email)
            InfoLine(
                modifier = modifier.padding(start = 15.dp),
                title = "Phone number",
                body = if (settingsModel.userLocalData.phoneNumber == NoRecord.num.toLong()) "-" else settingsModel.userLocalData.phoneNumber.toString()
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        RecordActionTextBtn(
            text = "Logout",
            onClick = { settingsModel.logout() },
            colors = Pair(ButtonDefaults.textButtonColors(), MaterialTheme.colorScheme.primary)
        )
        RecordActionTextBtn(
            text = "Edit user data",
            onClick = { settingsModel.editUserData() },
            colors = Pair(ButtonDefaults.textButtonColors(), MaterialTheme.colorScheme.primary)
        )
        RecordActionTextBtn(
            text = "Delete account",
            onClick = { settingsModel.showActionApproveDialog() },
            colors = Pair(
                ButtonDefaults.textButtonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.error
                ),
                MaterialTheme.colorScheme.onErrorContainer
            )
        )
    }

    if (approveActionDialogVisibility) {
        ApproveAction(
            actionTitle = "Are you sure you want to delete your account: ${settingsModel.userLocalData.email}?",
            onCanselClick = { onDenyLambda() },
            onOkClick = { password -> onApproveLambda(password) }
        )
    }
}

@Composable
fun InfoLine(
    modifier: Modifier,
    title: String,
    body: String
) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelMedium.copy(color = MaterialTheme.colorScheme.primary),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier
    )
    Text(
        text = body,
        style = MaterialTheme.typography.bodyMedium,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier
    )
    Spacer(modifier = Modifier.height(5.dp))
}

@Preview(name = "Lite Mode Settings", showBackground = true, widthDp = 360)
@Composable
fun SettingsPreview() {
    QMAppTheme {
        Settings(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 0.dp),
            onClick = {}
        )
    }
}
