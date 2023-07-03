package com.simenko.qmapp.ui.user.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.simenko.qmapp.ui.user.registration.RegistrationActivity
import com.simenko.qmapp.ui.main.MainActivity
import com.simenko.qmapp.ui.theme.QMAppTheme
import com.simenko.qmapp.ui.user.repository.UserRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject


internal const val INITIAL_ROUTE = "INITIATED_ROUTE"
fun createLoginActivityIntent(
    context: Context,
    initiateRoute: String
): Intent {
    val intent = Intent(context, LoginActivity::class.java)
    intent.putExtra(INITIAL_ROUTE, initiateRoute)
    return intent
}

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    @Inject
    lateinit var userRepository: UserRepository
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val initiateRoute = savedInstanceState?.getString(INITIAL_ROUTE) ?: ""

        val activity = this

        setContent {
            QMAppTheme {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .padding(all = 0.dp)
                        .fillMaxSize(),
                ) {
                    Navigation(
                        initiatedRoute = initiateRoute,
                        logInSuccess = {
                            startActivity(Intent(activity, MainActivity::class.java))
                            finish()
                        },
                        onClickUnregister = {
                            val intent = Intent(activity, RegistrationActivity::class.java)
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

    override fun onRestart() {
        super.onRestart()
        lifecycleScope.launch {
            userRepository.getUserState()
        }
    }
}
