package com.simenko.qmapp.ui.main.settings

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
import com.simenko.qmapp.ui.navigation.Route
import com.simenko.qmapp.ui.main.MainActivity
import com.simenko.qmapp.ui.navigation.composable
import com.simenko.qmapp.ui.navigation.navigation
import com.simenko.qmapp.ui.navigation.sharedViewModel
import com.simenko.qmapp.ui.user.createLoginActivityIntent
import com.simenko.qmapp.ui.user.registration.enterdetails.EnterDetails
import com.simenko.qmapp.ui.user.registration.enterdetails.EnterDetailsViewModel

fun NavGraphBuilder.settingsNavigation(navController: NavHostController) {
    navigation(startDestination = Route.Main.Settings.UserDetails) {
        composable(destination = Route.Main.Settings.UserDetails) {
            val settingsModel: SettingsViewModel = hiltViewModel()
            val context = LocalContext.current
            (context as MainActivity).initSettingsModel(settingsModel)

            Settings(
                viewModel = settingsModel,
                modifier = Modifier
                    .padding(all = 0.dp)
                    .fillMaxWidth(),
                onLogOut = { ContextCompat.startActivity(context, createLoginActivityIntent(context), null) },
                onEditUserData = { settingsModel.onUserDataEditClick() }
            )
        }
        composable(destination = Route.Main.Settings.EditUserDetails) {
            val userDetailsModel: EnterDetailsViewModel = hiltViewModel()
            val settingsViewModel: SettingsViewModel = it.sharedViewModel(navController = navController)

            settingsViewModel.validateUserData = { userDetailsModel.validateInput() }
            val editUserLambda = remember { { userDetailsModel.onSaveUserDataClick() } }
            Column(
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                Spacer(modifier = Modifier.height(10.dp))
                EnterDetails(
                    viewModel = userDetailsModel,
                    editMode = true,
                    editUserData = editUserLambda
                )
            }
        }
    }
}