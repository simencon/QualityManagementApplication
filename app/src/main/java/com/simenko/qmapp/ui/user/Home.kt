package com.simenko.qmapp.ui.user

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.simenko.qmapp.ui.theme.QMAppTheme
import com.simenko.qmapp.ui.user.login.views.WaitingForVerification

@Composable
fun Home(
    modifier: Modifier,
    navController: NavController = rememberNavController(),
    route: String = ""
) {
    Box {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(all = 0.dp)
        ) {
            LaunchedEffect(key1 = Unit, block = {
                if (route != "") {
                    navController.navigate(route)
                }
            })
            Text(
                text = "Welcome",
                style = MaterialTheme.typography.labelLarge.copy(fontSize = 18.sp, color = MaterialTheme.colorScheme.primary),
                modifier = Modifier
                    .padding(all = 5.dp)
            )
        }
    }
}

@Preview(name = "Lite Mode Home", showBackground = true, widthDp = 360)
@Composable
fun HomePreview() {
    QMAppTheme {
        WaitingForVerification(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 0.dp),
            logInSuccess = {}
        )
    }
}
