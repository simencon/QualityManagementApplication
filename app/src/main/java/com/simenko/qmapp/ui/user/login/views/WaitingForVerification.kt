package com.simenko.qmapp.ui.user.login.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.simenko.qmapp.ui.user.registration.RegistrationActivity
import com.simenko.qmapp.ui.theme.QMAppTheme
import com.simenko.qmapp.ui.user.login.LoginViewModel
import com.simenko.qmapp.ui.user.login.Screen
import com.simenko.qmapp.ui.user.repository.UserNeedToVerifyEmailState
import com.simenko.qmapp.ui.user.repository.UserErrorState
import com.simenko.qmapp.ui.user.repository.UserInitialState
import com.simenko.qmapp.ui.user.repository.UserLoggedInState
import com.simenko.qmapp.ui.user.repository.UserNeedToVerifiedByOrganisationState
import com.simenko.qmapp.ui.user.repository.UserRegisteredState

@Composable
fun WaitingForEmailVerification(
    modifier: Modifier,
    navController: NavController = rememberNavController()
) {
    val loginViewModel: LoginViewModel = hiltViewModel()
    val context = LocalContext.current
    val stateEvent by loginViewModel.userState.collectAsStateWithLifecycle()

    var error by rememberSaveable { mutableStateOf("") }
    var msg by rememberSaveable { mutableStateOf("") }

    stateEvent.getContentIfNotHandled()?.let { state ->
        when (state) {
            is UserInitialState -> {}
            is UserRegisteredState -> {}
            is UserNeedToVerifyEmailState -> msg = state.msg ?: "Unknown reason"
            is UserNeedToVerifiedByOrganisationState -> msg = state.msg ?: "Unknown reason"
            is UserLoggedInState -> navController.navigate(Screen.LogIn.route)
            is UserErrorState -> error = state.error ?: "Unknown error"
        }
    }

    LaunchedEffect(Unit) {
        loginViewModel.refreshUserState()
    }

    Box {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(all = 0.dp)
        ) {
            Text(
                text = "Please check your email box",
                style = MaterialTheme.typography.labelLarge.copy(fontSize = 18.sp, color = MaterialTheme.colorScheme.primary),
                modifier = Modifier
                    .padding(all = 5.dp)
            )
        }
    }


}

@Preview(name = "Lite Mode Waiting For Verification", showBackground = true, widthDp = 360)
@Composable
fun WaitingForVerificationPreview() {
    QMAppTheme {
        WaitingForEmailVerification(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 0.dp)
        )
    }
}
