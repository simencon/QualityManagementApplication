package com.simenko.qmapp.ui.main

import android.content.Intent
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.simenko.qmapp.ui.Screen
import com.simenko.qmapp.ui.main.investigations.InvestigationsViewModel
import com.simenko.qmapp.ui.main.investigations.steps.InvestigationsMainComposition
import com.simenko.qmapp.ui.main.settings.Settings
import com.simenko.qmapp.ui.main.team.TeamComposition
import com.simenko.qmapp.ui.main.team.TeamViewModel
import com.simenko.qmapp.ui.theme.QMAppTheme
import com.simenko.qmapp.ui.user.createLoginActivityIntent

@Composable
fun Navigation(
    modifier: Modifier = Modifier,
    mainScreenPadding: PaddingValues,
    initiatedRoute: String,
    navController: NavHostController
) {
    NavHost(modifier = modifier, navController = navController, startDestination = initiatedRoute) {
        composable(route = Screen.Main.Employees.route) {
            val invModel: TeamViewModel = hiltViewModel()
            (LocalContext.current as MainActivityCompose).initTeamModel(invModel)
            QMAppTheme {
                TeamComposition()
            }
        }
        composable(
            route = Screen.Main.AllInvestigations.route + "/{processControlOnly}",
            arguments = listOf(
                navArgument("processControlOnly") {
                    type = NavType.BoolType
                }
            )
        ) {
            val invModel: InvestigationsViewModel = hiltViewModel()
            (LocalContext.current as MainActivityCompose).initInvModel(invModel)
            QMAppTheme {
                InvestigationsMainComposition(
                    modifier = Modifier.padding(all = 0.dp),
                    mainScreenPadding = mainScreenPadding,
                    processControlOnly = it.arguments?.getBoolean("processControlOnly")?:false
                )
            }
        }
        composable(route = Screen.Main.Settings.route) {
            QMAppTheme {
                Settings(
                    modifier = Modifier
                        .padding(all = 0.dp)
                        .fillMaxWidth(),
                    onClick = { route ->
                        val intent = createLoginActivityIntent(navController.context, route)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                                Intent.FLAG_ACTIVITY_CLEAR_TASK or
                                Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(navController.context, intent, null)
                    }
                )
            }
        }
    }
}