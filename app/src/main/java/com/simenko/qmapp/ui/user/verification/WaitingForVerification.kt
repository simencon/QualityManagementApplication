package com.simenko.qmapp.ui.user.verification

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
import com.simenko.qmapp.ui.theme.QMAppTheme
import com.simenko.qmapp.repository.UserNeedToVerifyEmailState
import com.simenko.qmapp.repository.UserErrorState
import com.simenko.qmapp.repository.UserAuthoritiesNotVerifiedState

@Composable
fun WaitingForVerification(message: String? = null) {
    val waitingForVerificationViewModel: WaitingForVerificationViewModel = hiltViewModel()
    val userState by waitingForVerificationViewModel.userState.collectAsStateWithLifecycle()

    var error by rememberSaveable { mutableStateOf("") }
    var msg by rememberSaveable { mutableStateOf("Please check your email box") }

    LaunchedEffect(userState) {
        userState.let { state ->
            if (state is UserErrorState) {
                error = state.error ?: "Unknown error"
            } else if (state is UserNeedToVerifyEmailState) {
                msg = state.msg
                error = ""
            } else if (state is UserAuthoritiesNotVerifiedState) {
                msg = state.msg
                error = ""
            }
        }
    }

    LaunchedEffect(key1 = Unit, block = { message?.let { msg = message } })
    Box {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(all = 0.dp)
        ) {
            Text(
                text = "Verification",
                style = MaterialTheme.typography.labelLarge.copy(fontSize = 18.sp, color = MaterialTheme.colorScheme.primary),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .padding(all = 5.dp)
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = msg,
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .padding(all = 5.dp),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(10.dp))
            TextButton(
                modifier = Modifier.width(150.dp),
                onClick = {
                    msg = ""
                    waitingForVerificationViewModel.resendVerificationEmail()
                },
                content = {
                    Text(
                        text = "Resend email",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 14.sp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .padding(all = 0.dp),
                    )
                },
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
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
    }
}

@Preview(name = "Lite Mode Waiting For Verification", showBackground = true, widthDp = 360)
@Composable
fun WaitingForVerificationPreview() {
    QMAppTheme {
        WaitingForVerification()
    }
}
