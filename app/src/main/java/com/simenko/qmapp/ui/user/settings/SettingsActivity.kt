package com.simenko.qmapp.ui.user.settings

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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simenko.qmapp.ui.user.login.LoginActivity
import com.simenko.qmapp.ui.user.user.UserManager
import com.simenko.qmapp.ui.theme.QMAppTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingsActivity : AppCompatActivity() {

    private val settingsViewModel: SettingsViewModel by viewModels()

    @Inject
    lateinit var userManager: UserManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            QMAppTheme {
                Settings(
                    modifier = Modifier
                        .padding(all = 0.dp)
                        .fillMaxWidth(),
                    onCLickRefreshNotifications = {
                        settingsViewModel.refreshNotifications()
                    },
                    onClickLogOut = {
                        settingsViewModel.logout()
                        val intent = Intent(this, LoginActivity::class.java)
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
fun Settings(
    modifier: Modifier,
    onCLickRefreshNotifications: () -> Unit,
    onClickLogOut: () -> Unit,
) {
    Column(
        modifier = Modifier
            .padding(all = 0.dp)
    ) {
        TextButton(onClick = { onCLickRefreshNotifications() }, content = {
            Text(
                text = "Refresh notifications",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 14.sp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .padding(top = 0.dp, start = 0.dp, end = 0.dp, bottom = 0.dp)
            )
        })
        TextButton(onClick = { onClickLogOut() }, content = {
            Text(
                text = "Logout",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 14.sp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .padding(top = 0.dp, start = 0.dp, end = 0.dp, bottom = 0.dp)
            )
        })
    }
}

@Preview(name = "Lite Mode Settings", showBackground = true, widthDp = 360)
@Composable
fun TermsAndConditionsPreview() {
    QMAppTheme {
        Settings(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 0.dp),
            onCLickRefreshNotifications = {},
            onClickLogOut = {}
        )
    }
}
