package com.simenko.qmapp.ui.user.registration.termsandconditions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.simenko.qmapp.repository.UserError
import com.simenko.qmapp.ui.dialogs.UserExistDialog
import com.simenko.qmapp.ui.user.registration.RegistrationViewModel
import com.simenko.qmapp.ui.theme.QMAppTheme
import com.simenko.qmapp.repository.UserErrorState
import com.simenko.qmapp.ui.common.RecordActionTextBtn

@Composable
fun TermsAndConditions(
    viewModel: RegistrationViewModel,
    user: String? = null,
    onLoadingStateChanged: (Pair<Boolean, String?>) -> Unit,
    onDismiss: () -> Unit,
    onChangeEmail: () -> Unit,
    onLogin: () -> Unit
) {
    val loadingState by viewModel.loadingState.collectAsStateWithLifecycle()
    LaunchedEffect(key1 = loadingState, block = { onLoadingStateChanged(loadingState) })

    val userState by viewModel.userState.collectAsStateWithLifecycle()
    val userExistDialogVisibility by viewModel.isUserExistDialogVisible.collectAsStateWithLifecycle()

    var error by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(userState) {
        userState.let { state ->
            if (state is UserErrorState) {
                error = state.error ?: UserError.UNKNOWN_ERROR.error
                if (state.error == UserError.USER_EXISTS.error) {
                    viewModel.showUserExistDialog()
                }
            }
        }
        viewModel.updateLoadingState(Pair(false, null))
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(all = 0.dp)
        ) {
            Text(
                text = "Hello, $user",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 14.sp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(all = 5.dp)
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Terms and Conditions",
                style = MaterialTheme.typography.labelLarge.copy(fontSize = 18.sp, color = MaterialTheme.colorScheme.primary),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(all = 5.dp)
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Here will be terms and conditions later ...",
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(all = 5.dp)
            )
            Spacer(modifier = Modifier.height(20.dp))
            if (error != UserError.NO_ERROR.error)
                Text(
                    text = error,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 14.sp, color = MaterialTheme.colorScheme.error),
                    modifier = Modifier.padding(all = 5.dp),
                    textAlign = TextAlign.Center
                )
            Spacer(modifier = Modifier.height(10.dp))
            RecordActionTextBtn(
                text = "Register",
                onClick = { viewModel.registerUser() },
                colors = Pair(ButtonDefaults.textButtonColors(), MaterialTheme.colorScheme.primary),
            )
        }

        if (userExistDialogVisibility) {
            UserExistDialog(msg = error, btn = Pair("Change email", "Login"), onCancel = onChangeEmail, onOk = onLogin, onDismiss = onDismiss)
        }
    }


}

@Preview(name = "Lite Mode Terms and Conditions", showBackground = true, widthDp = 360)
@Composable
fun TermsAndConditionsPreview() {
    QMAppTheme {
        TermsAndConditions(
            viewModel = hiltViewModel(),
            onLoadingStateChanged = {},
            onDismiss = {},
            onChangeEmail = {},
            onLogin = {}
        )
    }
}
