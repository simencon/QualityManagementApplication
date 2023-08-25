package com.simenko.qmapp.ui.main.settings

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.simenko.qmapp.domain.EmptyString
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.ui.theme.QMAppTheme
import com.simenko.qmapp.ui.user.createLoginActivityIntent
import com.simenko.qmapp.repository.UserErrorState
import com.simenko.qmapp.repository.UserLoggedInState
import com.simenko.qmapp.ui.Screen
import com.simenko.qmapp.ui.dialogs.ApproveAction
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                QMAppTheme {
                    Settings(
                        modifier = Modifier
                            .padding(all = 0.dp)
                            .fillMaxWidth(),
                        onClick = { route ->
                            val intent = createLoginActivityIntent(requireActivity(), route)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                                    Intent.FLAG_ACTIVITY_CLEAR_TASK or
                                    Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun Settings(
    modifier: Modifier = Modifier,
    onClick: (String) -> Unit,
) {
    val settingsViewModel: SettingsViewModel = hiltViewModel()
    val userState by settingsViewModel.userState.collectAsStateWithLifecycle()

    var error by rememberSaveable { mutableStateOf("") }
    var msg by rememberSaveable { mutableStateOf("") }

    userState.getContentIfNotHandled()?.let {
        if (it is UserErrorState) {
            error = it.error ?: "unknown error"
        } else if (it is UserLoggedInState) {
            msg = it.msg
        }
    }

    val approveActionDialogVisibility by settingsViewModel.isApproveActionVisible.collectAsStateWithLifecycle()

    val onDenyLambda = remember<() -> Unit> {
        {
            settingsViewModel.hideActionApproveDialog()
        }
    }

    val onApproveLambda = remember<(String) -> Unit> {
        {
            settingsViewModel.deleteAccount(settingsViewModel.userLocalData.email, it)
            onClick(Screen.LoggedOut.Registration.route)
        }
    }
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        Box {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(all = 0.dp)
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
                    InfoLine(modifier = modifier.padding(start = 15.dp), title = "Full name", body = settingsViewModel.userLocalData.fullName)
                    InfoLine(modifier = modifier.padding(start = 15.dp), title = "Job role", body = settingsViewModel.userLocalData.jobRole)
                    InfoLine(
                        modifier = modifier.padding(start = 15.dp),
                        title = "Department",
                        body = settingsViewModel.userLocalData.department +
                                if (settingsViewModel.userLocalData.subDepartment == EmptyString.str) "" else "/${settingsViewModel.userLocalData.subDepartment}"
                    )
                    InfoLine(modifier = modifier.padding(start = 15.dp), title = "Company", body = settingsViewModel.userLocalData.company)
                    InfoLine(modifier = modifier.padding(start = 15.dp), title = "Email", body = settingsViewModel.userLocalData.email)
                    InfoLine(
                        modifier = modifier.padding(start = 15.dp),
                        title = "Phone number",
                        body = if (settingsViewModel.userLocalData.phoneNumber == NoRecord.num.toLong()) "-" else settingsViewModel.userLocalData.phoneNumber.toString()
                    )
                }

                TextButton(
                    modifier = Modifier.width(150.dp),
                    onClick = {
                        error = ""
                        msg = ""
                        settingsViewModel.getUserData()
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
                        settingsViewModel.updateUserCompleteData()
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
                    onClick = {
                        settingsViewModel.logout()
                        onClick(Screen.LoggedOut.LogIn.route)
                    },
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
                        settingsViewModel.showActionApproveDialog()
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
                    registrationViewModel = settingsViewModel,
                    msg = "Delete ${settingsViewModel.userLocalData.email}?",
                    derivedPassword = settingsViewModel.userLocalData.password,
                    onDenyClick = { onDenyLambda() },
                    onApproveClick = { p1 -> onApproveLambda(p1) }
                )
            }
        }
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
        style = MaterialTheme.typography.labelSmall.copy(fontSize = 14.sp),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier
    )
    Text(
        text = body,
        style = MaterialTheme.typography.labelLarge.copy(fontSize = 18.sp, color = MaterialTheme.colorScheme.primary),
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
