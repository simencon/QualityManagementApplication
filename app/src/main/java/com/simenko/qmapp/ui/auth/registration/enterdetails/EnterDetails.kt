package com.simenko.qmapp.ui.auth.registration.enterdetails

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.simenko.qmapp.ui.auth.Screen
import com.simenko.qmapp.ui.auth.registration.RegistrationViewModel
import com.simenko.qmapp.ui.theme.QMAppTheme

@Composable
fun EnterDetails(
    modifier: Modifier,
    navController: NavHostController = rememberNavController(),
) {
    val registrationViewModel: RegistrationViewModel = hiltViewModel()
    val enterDetailsViewModel: EnterDetailsViewModel = hiltViewModel()

    val stateEvent by enterDetailsViewModel.enterDetailsState.collectAsStateWithLifecycle()

    var userNameText by rememberSaveable { mutableStateOf("") }
    var passwordText by rememberSaveable { mutableStateOf("") }
    var errorText by rememberSaveable { mutableStateOf("") }

    stateEvent.getContentIfNotHandled()?.let { state ->
        when (state) {
            is EnterDetailsSuccess -> {
                registrationViewModel.updateUserData(userNameText, passwordText)
                navController.navigate(Screen.TermsAndConditions.withArgs(userNameText))
            }

            is EnterDetailsError -> {
                errorText = state.error
            }

            is EnterDetailsInitialState -> {}
        }
    }

    Column(
        modifier = Modifier
            .padding(all = 0.dp)
    ) {
        Text(
            text = "Register to Quality Management",
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 18.sp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .padding(top = 0.dp, start = 0.dp, end = 0.dp, bottom = 0.dp)
        )
        TextField(value = userNameText, onValueChange = { userNameText = it })
        TextField(value = passwordText, onValueChange = { passwordText = it })
        if (errorText != "")
            Text(
                text = errorText,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 14.sp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .padding(top = 0.dp, start = 0.dp, end = 0.dp, bottom = 0.dp)
            )
        TextButton(
            onClick = {
                enterDetailsViewModel.validateInput(userNameText, passwordText)
            },
            content = {
                Text(
                    text = "Next",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 14.sp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .padding(top = 0.dp, start = 0.dp, end = 0.dp, bottom = 0.dp)
                )
            })
    }
}

@Preview(name = "Lite Mode Enter Details", showBackground = true, widthDp = 360)
@Composable
fun EnterDetailsPreview() {
    QMAppTheme {
        EnterDetails(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 0.dp)
        )
    }
}