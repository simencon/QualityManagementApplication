package com.simenko.qmapp.ui.user.registration.termsandconditions

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.simenko.qmapp.ui.dialogs.UserExistDialog
import com.simenko.qmapp.ui.user.registration.RegistrationViewModel
import com.simenko.qmapp.ui.theme.QMAppTheme
import com.simenko.qmapp.ui.user.Screen
import com.simenko.qmapp.repository.UserNeedToVerifyEmailState
import com.simenko.qmapp.repository.UserErrorState
import com.simenko.qmapp.repository.UserInitialState
import com.simenko.qmapp.repository.UserLoggedOutState
import com.simenko.qmapp.repository.UserLoggedInState
import com.simenko.qmapp.repository.UserNeedToVerifiedByOrganisationState
import com.simenko.qmapp.repository.UserRegisteredState

@Composable
fun TermsAndConditions(
    modifier: Modifier,
    registrationViewModel: RegistrationViewModel = hiltViewModel(),
    navController: NavController = rememberNavController(),
    user: String? = null
) {
    val stateEvent by registrationViewModel.userState.collectAsStateWithLifecycle()
    val userExistDialogVisibility by registrationViewModel.isUserExistDialogVisible.collectAsStateWithLifecycle()

    var error by rememberSaveable { mutableStateOf("") }
    var msg by rememberSaveable { mutableStateOf("") }

    val onRegisterUnderAnotherEmailLambda = remember<() -> Unit> {
        {
            navController.popBackStack()
        }
    }

    val onLoginLambda = remember<(String) -> Unit> {
        {
            navController.navigate(Screen.LogIn.route)
        }
    }

    stateEvent.getContentIfNotHandled()?.let { state ->
        when (state) {
            is UserInitialState -> {}
            is UserRegisteredState -> {
                msg = state.msg
                registrationViewModel.showUserExistDialog()
            }

            is UserNeedToVerifyEmailState -> navController.navigate(Screen.WaitingForEmailVerification.withArgs(state.msg)) {
                popUpTo(Screen.Registration.route) {
                    inclusive = true
                }
            }

            is UserNeedToVerifiedByOrganisationState -> navController.navigate(Screen.WaitingForEmailVerification.route) {
                popUpTo(Screen.Registration.route) {
                    inclusive = true
                }
            }

            is UserLoggedInState -> {}
            is UserLoggedOutState -> {}
            is UserErrorState -> error = state.error ?: "Unknown error"
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
                    registrationViewModel.acceptTCs()
                    registrationViewModel.registerUser()
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
            UserExistDialog(
                registrationViewModel = registrationViewModel,
                msg = msg,
                onRegisterUnderAnotherEmailClick = { onRegisterUnderAnotherEmailLambda() },
                onLoginClick = { p1 -> onLoginLambda(p1) }
            )
        }
    }


}

@Preview(name = "Lite Mode Terms and Conditions", showBackground = true, widthDp = 360)
@Composable
fun TermsAndConditionsPreview() {
    QMAppTheme {
        TermsAndConditions(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 0.dp)
        )
    }
}
