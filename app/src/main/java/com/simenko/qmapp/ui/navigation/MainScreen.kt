package com.simenko.qmapp.ui.navigation

import androidx.compose.animation.EnterExitState
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.simenko.qmapp.ui.main.MainActivityViewModel
import com.simenko.qmapp.ui.main.investigations.InvestigationsViewModel
import com.simenko.qmapp.ui.main.investigations.steps.InvestigationsMainComposition
import com.simenko.qmapp.ui.main.investigations.forms.NewItemViewModel
import com.simenko.qmapp.ui.main.investigations.forms.OrderForm
import com.simenko.qmapp.ui.main.investigations.forms.SubOrderForm
import com.simenko.qmapp.ui.main.settings.settingsNavigation
import com.simenko.qmapp.ui.main.team.teamNavigation
import com.simenko.qmapp.ui.theme.QMAppTheme

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MainScreen(
    mainViewModel: MainActivityViewModel = hiltViewModel(),
    navController: NavHostController = rememberNavController(),
    mainScreenPadding: PaddingValues
) {
    NavigationEffects(
        navigationChannel = mainViewModel.navigationChannel,
        navHostController = navController
    )

    QMAppTheme {
        NavHost(navController = navController, startDestination = Route.Main.Team) {
            teamNavigation(navController)

            composable(destination = Route.Main.Inv) {
                val invModel: InvestigationsViewModel = hiltViewModel()
                if (!transition.isRunning && transition.currentState == EnterExitState.Visible && it.lifecycle.currentState == Lifecycle.State.RESUMED)
                    invModel.enableScrollToCreatedRecord()
                InvestigationsMainComposition(modifier = Modifier.padding(all = 0.dp), mainScreenPadding = mainScreenPadding, invModel = invModel)
            }

            composable(destination = Route.Main.ProcessControl) {
                val invModel: InvestigationsViewModel = hiltViewModel()
                if (!transition.isRunning && transition.currentState == EnterExitState.Visible && it.lifecycle.currentState == Lifecycle.State.RESUMED)
                    invModel.enableScrollToCreatedRecord()
                InvestigationsMainComposition(modifier = Modifier.padding(all = 0.dp), mainScreenPadding = mainScreenPadding, invModel = invModel)
            }

            composable(destination = Route.Main.OrderAddEdit) {
                val newOrderModel: NewItemViewModel = hiltViewModel()
                OrderForm(viewModel = newOrderModel)
            }

            composable(destination = Route.Main.SubOrderAddEdit) {
                val newOrderModel: NewItemViewModel = hiltViewModel()
                SubOrderForm(viewModel = newOrderModel)
            }

            settingsNavigation()
        }
    }
}