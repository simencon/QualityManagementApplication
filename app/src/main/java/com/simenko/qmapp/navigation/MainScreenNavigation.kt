package com.simenko.qmapp.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.simenko.qmapp.presentation.ui.main.investigations.allInvestigations
import com.simenko.qmapp.presentation.ui.main.investigations.processControl
import com.simenko.qmapp.presentation.ui.main.products.productsNavigation
import com.simenko.qmapp.presentation.ui.main.settings.settingsNavigation
import com.simenko.qmapp.presentation.ui.main.structure.companyStructureNavigation
import com.simenko.qmapp.presentation.ui.main.team.teamNavigation
import com.simenko.qmapp.presentation.theme.QMAppTheme

@Composable
fun MainScreenNavigation(
    navController: NavHostController,
    mainScreenPadding: PaddingValues
) {
    QMAppTheme {
        NavHost(navController = navController, startDestination = Route.Main.Team, route = Route.Main::class) {
            teamNavigation<Route.Main.Team>()
            companyStructureNavigation<Route.Main.CompanyStructure>(mainScreenPadding)
            productsNavigation<Route.Main.ProductLines>(mainScreenPadding)
            allInvestigations<Route.Main.AllInvestigations>(mainScreenPadding)
            processControl<Route.Main.ProcessControl>(mainScreenPadding)
            settingsNavigation<Route.Main.Settings>()
        }
    }
}