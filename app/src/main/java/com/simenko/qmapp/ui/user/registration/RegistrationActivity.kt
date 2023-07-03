package com.simenko.qmapp.ui.user.registration

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.simenko.qmapp.ui.user.Navigation
import com.simenko.qmapp.ui.main.MainActivity
import com.simenko.qmapp.ui.user.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint

internal const val INITIAL_ROUTE = "INITIATED_ROUTE"
fun createRegistrationActivityIntent(
    context: Context,
    initiateRoute: String
): Intent {
    val intent = Intent(context, RegistrationActivity::class.java)
    intent.putExtra(INITIAL_ROUTE, initiateRoute)
    return intent
}

@AndroidEntryPoint
class RegistrationActivity : AppCompatActivity() {

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
                    initiateRoute
                )
            }
        }
    }


    fun onTermsAndConditionsAccepted() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else {
            super.onBackPressed()
        }
    }
}