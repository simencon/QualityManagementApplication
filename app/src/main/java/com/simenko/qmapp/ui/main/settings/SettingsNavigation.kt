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
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.simenko.qmapp.domain.TrueStr
import com.simenko.qmapp.ui.NavArguments
import com.simenko.qmapp.ui.Route
import com.simenko.qmapp.ui.main.MainActivity
import com.simenko.qmapp.ui.sharedViewModel
import com.simenko.qmapp.ui.theme.QMAppTheme
import com.simenko.qmapp.ui.user.createLoginActivityIntent
import com.simenko.qmapp.ui.user.registration.enterdetails.EnterDetails
import com.simenko.qmapp.ui.user.registration.enterdetails.EnterDetailsViewModel

fun NavGraphBuilder.settingsNavigation(navController: NavHostController) {
    navigation(route = Route.Main.Settings.link, startDestination = Route.Main.Settings.UserDetails.link) {
        composable(route = Route.Main.Settings.UserDetails.link) {
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
                        navController.navigate(Route.Main.Settings.EditUserDetails.withArgs(TrueStr.str)) { launchSingleTop = true }
                    }
                )
            }
        }
        composable(route = Route.Main.Settings.EditUserDetails.link, arguments = Route.Main.Settings.EditUserDetails.arguments) {
            val settingsViewModel: SettingsViewModel = it.sharedViewModel(navController = navController)
            val userDetailsModel: EnterDetailsViewModel = it.sharedViewModel(navController = navController)
            settingsViewModel.validateUserData = { userDetailsModel.validateInput() }
            BackHandler { navController.popBackStack(Route.Main.Settings.UserDetails.link, inclusive = false) }
            val editUserLambda = remember {
                {
                    navController.popBackStack(Route.Main.Settings.UserDetails.link, inclusive = false)
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
                        editMode = it.arguments?.getBoolean(NavArguments.userEditMode) ?: false,
                        userDetailsModel = userDetailsModel,
                        editUserData = editUserLambda
                    )
                }
            }
        }
    }
}