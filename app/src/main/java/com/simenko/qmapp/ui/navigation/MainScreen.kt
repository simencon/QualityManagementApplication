package com.simenko.qmapp.ui.navigation

import androidx.compose.animation.EnterExitState
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import com.simenko.qmapp.ui.main.MainActivityViewModel
import com.simenko.qmapp.ui.main.investigations.InvestigationsViewModel
import com.simenko.qmapp.ui.main.investigations.steps.InvestigationsMainComposition
import com.simenko.qmapp.ui.main.investigations.forms.NewItemViewModel
import com.simenko.qmapp.ui.main.investigations.forms.OrderForm
import com.simenko.qmapp.ui.main.investigations.forms.SubOrderForm
import com.simenko.qmapp.ui.main.settings.settingsNavigation
import com.simenko.qmapp.ui.main.structure.companyStructureNavigation
import com.simenko.qmapp.ui.main.team.teamNavigation
import com.simenko.qmapp.ui.theme.QMAppTheme

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MainScreen(
    mainViewModel: MainActivityViewModel = hiltViewModel(),
    mainScreenPadding: PaddingValues
) {
    QMAppTheme {
        NavHost(navController = mainViewModel.navHostController, startDestination = Route.Main.Team) {
            teamNavigation()
            companyStructureNavigation(mainScreenPadding)

            composable(destination = Route.Main.Inv) {
                val viewModel: InvestigationsViewModel = hiltViewModel()
                InvestigationsMainComposition(mainScreenPadding = mainScreenPadding, invModel = viewModel)
            }

            composable(destination = Route.Main.ProcessControl) {
                val viewModel: InvestigationsViewModel = hiltViewModel()
                InvestigationsMainComposition(mainScreenPadding = mainScreenPadding, invModel = viewModel)
            }

            composable(destination = Route.Main.OrderAddEdit) {
                val viewModel: NewItemViewModel = hiltViewModel()
                OrderForm(viewModel = viewModel)
            }

            composable(destination = Route.Main.SubOrderAddEdit) {
                val viewModel: NewItemViewModel = hiltViewModel()
                SubOrderForm(viewModel = viewModel)
            }

            settingsNavigation()
        }
    }
}