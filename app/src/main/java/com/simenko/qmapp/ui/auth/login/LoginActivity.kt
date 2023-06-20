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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Observer
import com.simenko.qmapp.ui.auth.registration.RegistrationActivity
import com.simenko.qmapp.ui.main.MainActivity
import com.simenko.qmapp.ui.theme.QMAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var errorVisibility = false

        setContent {
            QMAppTheme {
                LogIn(
                    modifier = Modifier
                        .padding(all = 0.dp)
                        .fillMaxWidth(),
                    userName = loginViewModel.getUsername(),
                    errorVisibility = errorVisibility,
                    onClickLogIn = { p1, p2 -> loginViewModel.login(p1, p2) },
                    onClickUnregister = {
                        loginViewModel.unregister()
                        val intent = Intent(this, RegistrationActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                                Intent.FLAG_ACTIVITY_CLEAR_TASK or
                                Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                    },
                    errorMessage = "Error logging you in. Try again."
                )
            }
        }

        loginViewModel.loginState.observe(this, Observer<LoginViewState> { state ->
            when (state) {
                is LoginSuccess -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }

                is LoginError -> errorVisibility = true
            }
        })
    }
}


@Composable
fun LogIn(
    modifier: Modifier,
    userName: String,
    errorVisibility: Boolean,
    onClickLogIn: (String, String) -> Unit,
    onClickUnregister: () -> Unit,
    errorMessage: String
) {
    Column(
        modifier = Modifier
            .padding(all = 0.dp)
    ) {
        var passwordText by rememberSaveable {
            mutableStateOf("-")
        }

        var errorVisibilityRS by rememberSaveable {
            mutableStateOf(errorVisibility)
        }

        Text(
            text = "Register to Quality Management",
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 18.sp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .padding(top = 0.dp, start = 0.dp, end = 0.dp, bottom = 0.dp)
        )
        TextField(value = userName, enabled = false, onValueChange = {})
        TextField(value = passwordText, onValueChange = {
            passwordText = it
            errorVisibilityRS = false
        })
        if (errorVisibilityRS)
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 14.sp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .padding(top = 0.dp, start = 0.dp, end = 0.dp, bottom = 0.dp)
            )
        TextButton(onClick = { onClickLogIn(userName, errorMessage) }, content = {
            Text(
                text = "Login",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 14.sp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .padding(top = 0.dp, start = 0.dp, end = 0.dp, bottom = 0.dp)
            )
        })
        TextButton(onClick = { onClickUnregister() }, content = {
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
