package com.simenko.qmapp.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.simenko.qmapp.ui.main.investigations.allInvestigations
import com.simenko.qmapp.ui.main.investigations.processControl
import com.simenko.qmapp.ui.main.products.productsNavigation
import com.simenko.qmapp.ui.main.settings.settingsNavigation
import com.simenko.qmapp.ui.main.structure.companyStructureNavigation
import com.simenko.qmapp.ui.main.team.teamNavigation
import com.simenko.qmapp.ui.theme.QMAppTheme

@Composable
fun MainScreen(
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