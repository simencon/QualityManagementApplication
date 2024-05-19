package com.simenko.qmapp.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.simenko.qmapp.ui.main.MainActivityViewModel
import com.simenko.qmapp.ui.main.investigations.InvestigationsViewModel
import com.simenko.qmapp.ui.main.investigations.steps.InvestigationsMainComposition
import com.simenko.qmapp.ui.main.investigations.forms.NewItemViewModel
import com.simenko.qmapp.ui.main.investigations.forms.OrderForm
import com.simenko.qmapp.ui.main.investigations.forms.SubOrderForm
import com.simenko.qmapp.ui.main.products.productsNavigation
import com.simenko.qmapp.ui.main.settings.settingsNavigation
import com.simenko.qmapp.ui.main.structure.companyStructureNavigation
import com.simenko.qmapp.ui.main.team.teamNavigation
import com.simenko.qmapp.ui.theme.QMAppTheme

@Composable
fun MainScreen(
    mainViewModel: MainActivityViewModel = hiltViewModel(),
    mainScreenPadding: PaddingValues
) {
    QMAppTheme {
        NavHost(navController = mainViewModel.navHostController, startDestination = RouteCompose.Main.Settings) {
            teamNavigation<RouteCompose.Main.Team>()
            companyStructureNavigation<RouteCompose.Main.CompanyStructure>(mainScreenPadding)
            productsNavigation<RouteCompose.Main.ProductLines>(mainScreenPadding)

            composable<RouteCompose.Main.AllInvestigations.AllInvestigationsList> {
                val viewModel: InvestigationsViewModel = hiltViewModel()
                InvestigationsMainComposition(mainScreenPadding = mainScreenPadding, invModel = viewModel)
            }

            composable<RouteCompose.Main.ProcessControl.ProcessControlList> {
                val viewModel: InvestigationsViewModel = hiltViewModel()
                InvestigationsMainComposition(mainScreenPadding = mainScreenPadding, invModel = viewModel)
            }

            composable<RouteCompose.Main.AllInvestigations.OrderAddEdit> {
                val viewModel: NewItemViewModel = hiltViewModel()
                OrderForm(viewModel = viewModel)
            }

            composable<RouteCompose.Main.AllInvestigations.SubOrderAddEdit> {
                val viewModel: NewItemViewModel = hiltViewModel()
                SubOrderForm(viewModel = viewModel)
            }

            settingsNavigation<RouteCompose.Main.Settings>()
        }
    }
}