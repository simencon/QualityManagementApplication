package com.simenko.qmapp.ui.user.registration.termsandconditions

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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

@Composable
fun TermsAndConditions(
    regModel: RegistrationViewModel,
    user: String? = null,
    onChangeEmail: () -> Unit,
    onLogin: () -> Unit
) {
    val userState by regModel.userState.collectAsStateWithLifecycle()
    val userExistDialogVisibility by regModel.isUserExistDialogVisible.collectAsStateWithLifecycle()

    var error by rememberSaveable { mutableStateOf("") }
    var msg by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(userState) {
        userState.let { state ->
            if (state is UserErrorState) {
                if (state.error == UserError.USER_EXISTS.error)
                    regModel.showUserExistDialog()
                else
                    error = state.error ?: "Unknown error"
            }
        }
    }

    Box {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(all = 0.dp)
        ) {
            Text(
                text = "Hello, $user",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 14.sp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .padding(all = 5.dp)
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Terms and Conditions",
                style = MaterialTheme.typography.labelLarge.copy(fontSize = 18.sp, color = MaterialTheme.colorScheme.primary),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .padding(all = 5.dp)
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Here will be terms and conditions later ...",
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .padding(all = 5.dp)
            )
            Spacer(modifier = Modifier.height(20.dp))
            if (error != "")
                Text(
                    text = error,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 14.sp, color = MaterialTheme.colorScheme.error),
                    modifier = Modifier
                        .padding(all = 5.dp),
                    textAlign = TextAlign.Center
                )
            Spacer(modifier = Modifier.height(10.dp))
            TextButton(
                modifier = Modifier.width(150.dp),
                onClick = {
                    regModel.acceptTCs()
                    regModel.registerUser()
                },
                content = {
                    Text(
                        text = "Register",
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
        }

        if (userExistDialogVisibility) {
            UserExistDialog(registrationViewModel = regModel, msg = msg, onChangeEmail = onChangeEmail, onLoginClick = onLogin)
        }
    }


}

@Preview(name = "Lite Mode Terms and Conditions", showBackground = true, widthDp = 360)
@Composable
fun TermsAndConditionsPreview() {
    QMAppTheme {
        TermsAndConditions(
            regModel = hiltViewModel(),
            onChangeEmail = {},
            onLogin = {}
        )
    }
}
