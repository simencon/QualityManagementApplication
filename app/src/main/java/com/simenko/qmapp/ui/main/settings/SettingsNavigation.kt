package com.simenko.qmapp.ui.main.settings

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.simenko.qmapp.domain.TrueStr
import com.simenko.qmapp.domain.UserEditMode
import com.simenko.qmapp.ui.Screen
import com.simenko.qmapp.ui.main.MainActivity
import com.simenko.qmapp.ui.sharedViewModel
import com.simenko.qmapp.ui.theme.QMAppTheme
import com.simenko.qmapp.ui.user.createLoginActivityIntent
import com.simenko.qmapp.ui.user.registration.enterdetails.EnterDetails
import com.simenko.qmapp.ui.user.registration.enterdetails.EnterDetailsViewModel

fun NavGraphBuilder.settingsNavigation(navController: NavHostController) {
    navigation(
        route = Screen.Main.Settings.route,
        startDestination = Screen.Main.Settings.UserDetails.route
    ) {
        composable(route = Screen.Main.Settings.UserDetails.route) {
            println("Main Navigation - Settings has been build")
            val userDetailsModel: EnterDetailsViewModel = hiltViewModel()
            val settingsModel: SettingsViewModel = hiltViewModel()
            val activity = (LocalContext.current as MainActivity)
            activity.initSettingsModel(settingsModel)
            settingsModel.initUserDetailsModel(userDetailsModel)
            QMAppTheme {
                Settings(
                    modifier = Modifier
                        .padding(all = 0.dp)
                        .fillMaxWidth(),
                    onLogOut = {
                        ContextCompat.startActivity(navController.context, createLoginActivityIntent(navController.context), null)
                    },
                    onEditUserData = {
                        userDetailsModel.resetToInitialState()
                        navController.navigate(Screen.Main.Settings.EditUserDetails.withArgs(TrueStr.str)) { launchSingleTop = true }
                    }
                )
            }
        }
        composable(
            route = Screen.Main.Settings.EditUserDetails.routeWithArgKeys(),
            arguments = listOf(
                navArgument(UserEditMode.str) {
                    type = NavType.BoolType
                    defaultValue = false
                }
            )
        ) {
            val settingsViewModel: SettingsViewModel = it.sharedViewModel(navController = navController)
            val userDetailsModel: EnterDetailsViewModel = it.sharedViewModel(navController = navController)
            settingsViewModel.validateUserData = { userDetailsModel.validateInput() }
            BackHandler { navController.popBackStack(Screen.Main.Settings.UserDetails.route, inclusive = false) }
            val editUserLambda = remember {
                {
                    navController.popBackStack(Screen.Main.Settings.UserDetails.route, inclusive = false)
                    settingsViewModel.editUserData()
                }
            }
            QMAppTheme {
                Column(
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Spacer(modifier = Modifier.height(10.dp))
                    EnterDetails(
                        navController = navController,
                        editMode = it.arguments?.getBoolean(UserEditMode.str) ?: false,
                        userDetailsModel = userDetailsModel,
                        editUserData = editUserLambda
                    )
                }
            }
        }
    }
}