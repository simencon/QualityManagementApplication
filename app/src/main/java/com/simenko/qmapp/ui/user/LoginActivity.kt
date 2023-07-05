package com.simenko.qmapp.ui.user

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.simenko.qmapp.ui.main.MainActivity
import com.simenko.qmapp.repository.UserRepository
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

        val initiateRoute = intent.extras?.getString(INITIAL_ROUTE) ?: ""
        setContent {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                Navigation(
                    initiateRoute,
                    logInSuccess = {
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        finish()
                    }
                )
            }
        }
    }

    override fun onRestart() {
        super.onRestart()
        lifecycleScope.launch {
            userRepository.getActualUserState()
        }
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else {
            super.onBackPressed()
        }
    }
}