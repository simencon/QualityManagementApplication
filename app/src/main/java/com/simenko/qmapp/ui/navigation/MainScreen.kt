package com.simenko.qmapp.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import com.simenko.qmapp.ui.main.MainActivityViewModel
import com.simenko.qmapp.ui.main.investigations.allInvestigations
import com.simenko.qmapp.ui.main.investigations.processControl
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
        NavHost(navController = mainViewModel.navHostController, startDestination = RouteCompose.Main.Settings, route = RouteCompose.Main::class) {
            teamNavigation<RouteCompose.Main.Team>()
            companyStructureNavigation<RouteCompose.Main.CompanyStructure>(mainScreenPadding)
            productsNavigation<RouteCompose.Main.ProductLines>(mainScreenPadding)
            allInvestigations<RouteCompose.Main.AllInvestigations>(mainScreenPadding)
            processControl<RouteCompose.Main.ProcessControl>(mainScreenPadding)
            settingsNavigation<RouteCompose.Main.Settings>()
        }
    }
}