package com.simenko.qmapp.ui.auth.login

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.simenko.qmapp.ui.auth.registration.RegistrationActivity
import com.simenko.qmapp.ui.main.MainActivity
import com.simenko.qmapp.ui.theme.QMAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            QMAppTheme {
                LogIn(
                    modifier = Modifier
                        .padding(all = 0.dp)
                        .fillMaxWidth(),
                    logInSuccess = {
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    },
                    onClickUnregister = {
                        val intent = Intent(this, RegistrationActivity::class.java)
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


@Composable
fun LogIn(
    modifier: Modifier,
    loginViewModel: LoginViewModel = hiltViewModel(),
    logInSuccess: () -> Unit,
    onClickUnregister: () -> Unit
) {
    val userName = loginViewModel.getUsername()
    var passwordText by rememberSaveable { mutableStateOf("") }
    var errorVisibility by rememberSaveable { mutableStateOf(false) }

    val state by loginViewModel.loginState.observeAsState()

    when (state) {
        is LoginSuccess -> {
            logInSuccess()
        }

        is LoginError -> errorVisibility = true
        else -> {}
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
        TextField(value = userName, enabled = false, onValueChange = {})
        TextField(
            value = passwordText,
            onValueChange = {
                passwordText = it
                errorVisibility = false
            }
        )
        if (errorVisibility)
            Text(
                text = "Error logging you in. Try again.",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 14.sp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .padding(top = 0.dp, start = 0.dp, end = 0.dp, bottom = 0.dp)
            )
        TextButton(
            onClick = {
                loginViewModel.login(userName, passwordText)
            },
            content = {
                Text(
                    text = "Login",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 14.sp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .padding(top = 0.dp, start = 0.dp, end = 0.dp, bottom = 0.dp)
                )
            })
        TextButton(
            onClick = {
                loginViewModel.unregister()
                onClickUnregister()
            },
            content = {
                Text(
                    text = "Unregister",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 14.sp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .padding(top = 0.dp, start = 0.dp, end = 0.dp, bottom = 0.dp)
                )
            })
    }
}
