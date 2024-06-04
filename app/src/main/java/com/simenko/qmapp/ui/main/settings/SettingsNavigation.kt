package com.simenko.qmapp.ui.main.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.toRoute
import com.simenko.qmapp.ui.navigation.Route
import com.simenko.qmapp.ui.user.createLoginActivityIntent
import com.simenko.qmapp.ui.user.registration.enterdetails.EnterDetails
import com.simenko.qmapp.ui.user.registration.enterdetails.EnterDetailsViewModel

inline fun <reified T : Any> NavGraphBuilder.settingsNavigation() {
    navigation<T>(startDestination = Route.Main.Settings.UserDetails) {
        composable<Route.Main.Settings.UserDetails> {
            val settingsModel: SettingsViewModel = hiltViewModel()
            val context = LocalContext.current

            Settings(
                viewModel = settingsModel,
                modifier = Modifier
                    .padding(all = 0.dp)
                    .fillMaxWidth(),
                onLogOut = { ContextCompat.startActivity(context, createLoginActivityIntent(context), null) },
                onEditUserData = { settingsModel.onUserDataEditClick() }
            )
        }
        composable<Route.Main.Settings.EditUserDetails> { backStackEntry ->
            val isUserEditMode = backStackEntry.toRoute<Route.Main.Settings.EditUserDetails>().userEditMode
            val userDetailsModel: EnterDetailsViewModel = hiltViewModel()
            Column(
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                Spacer(modifier = Modifier.height(10.dp))
                EnterDetails(
                    viewModel = userDetailsModel,
                    editMode = isUserEditMode
                )
            }
        }
    }
}