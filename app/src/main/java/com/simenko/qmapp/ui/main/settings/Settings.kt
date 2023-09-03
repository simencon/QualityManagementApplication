package com.simenko.qmapp.ui.main.settings

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.simenko.qmapp.domain.EmptyString
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.repository.UnregisteredState
import com.simenko.qmapp.repository.UserError
import com.simenko.qmapp.ui.theme.QMAppTheme
import com.simenko.qmapp.repository.UserErrorState
import com.simenko.qmapp.repository.UserLoggedInState
import com.simenko.qmapp.ui.dialogs.ApproveAction

@Composable
fun Settings(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val settingsModel: SettingsViewModel = hiltViewModel()
    val userState by settingsModel.userState.collectAsStateWithLifecycle()

    var error by rememberSaveable { mutableStateOf("") }
    var msg by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(userState) {
        userState.let {
            if (it is UserErrorState) {
                error = it.error ?: UserError.UNKNOWN_ERROR.error
            } else if (it is UserLoggedInState) {
                msg = it.msg
            } else {
                onClick()
            }
        }
        settingsModel.clearLoadingState()
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
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(all = 0.dp)
            .verticalScroll(columnState)
    ) {
        if (msg != "")
            Text(
                text = msg,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 14.sp),
                modifier = Modifier
                    .padding(all = 5.dp),
                textAlign = TextAlign.Center
            )
        Spacer(modifier = Modifier.height(10.dp))

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .padding(all = 0.dp)
        ) {
            InfoLine(modifier = modifier.padding(start = 15.dp), title = "Full name", body = settingsModel.userLocalData.fullName)
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

        TextButton(
            modifier = Modifier.width(150.dp),
            onClick = {
                error = ""
                msg = ""
                settingsModel.getUserData()
            },
            content = {
                Text(
                    text = "Get user data",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 14.sp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .padding(top = 0.dp, start = 0.dp, end = 0.dp, bottom = 0.dp),
                )
            },
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
            shape = MaterialTheme.shapes.medium
        )
        TextButton(
            modifier = Modifier.width(150.dp),
            onClick = {
                error = ""
                msg = ""
                settingsModel.updateUserCompleteData()
            },
            content = {
                Text(
                    text = "Update user data",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 14.sp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .padding(top = 0.dp, start = 0.dp, end = 0.dp, bottom = 0.dp),
                )
            },
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
            shape = MaterialTheme.shapes.medium
        )
        TextButton(
            modifier = Modifier.width(150.dp),
            onClick = { settingsModel.logout() },
            content = {
                Text(
                    text = "Logout",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 14.sp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .padding(top = 0.dp, start = 20.dp, end = 20.dp, bottom = 0.dp),
                )
            },
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
            shape = MaterialTheme.shapes.medium
        )
        TextButton(
            modifier = Modifier.width(150.dp),
            onClick = {
                settingsModel.showActionApproveDialog()
            },
            content = {
                Text(
                    text = "Delete account",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 14.sp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .padding(all = 0.dp)
                )
            },
            colors = ButtonDefaults.textButtonColors(
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.error
            ),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onErrorContainer),
            shape = MaterialTheme.shapes.medium
        )
        Spacer(modifier = Modifier.height(10.dp))
        if (error != "")
            Text(
                text = error,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 14.sp, color = MaterialTheme.colorScheme.error),
                modifier = Modifier
                    .padding(all = 5.dp),
                textAlign = TextAlign.Center
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
