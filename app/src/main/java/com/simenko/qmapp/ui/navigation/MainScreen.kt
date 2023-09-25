package com.simenko.qmapp.ui.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.ui.main.MainActivity
import com.simenko.qmapp.ui.main.MainActivityViewModel
import com.simenko.qmapp.ui.main.investigations.InvestigationsViewModel
import com.simenko.qmapp.ui.main.investigations.steps.InvestigationsMainComposition
import com.simenko.qmapp.ui.main.investigations.forms.NewItemViewModel
import com.simenko.qmapp.ui.main.investigations.forms.OrderForm
import com.simenko.qmapp.ui.main.investigations.forms.SubOrderForm
import com.simenko.qmapp.ui.main.settings.settingsNavigation
import com.simenko.qmapp.ui.main.team.teamNavigation
import com.simenko.qmapp.ui.theme.QMAppTheme

@Composable
fun MainScreen(
    mainViewModel: MainActivityViewModel = hiltViewModel(),
    navController: NavHostController = rememberNavController()
) {
    NavigationEffects(
        navigationChannel = mainViewModel.navigationChannel,
        navHostController = navController
    )

    QMAppTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            NavHost(navController = navController, startDestination = Route.Main.Team) {
                teamNavigation(navController)

                composable(destination = Route.Main.Inv) {
                    val invModel: InvestigationsViewModel = hiltViewModel()
                    (LocalContext.current as MainActivity).initInvModel(invModel)
                    invModel.setCreatedRecord(
                        it.arguments?.getInt(NavArguments.orderId) ?: NoRecord.num,
                        it.arguments?.getInt(NavArguments.subOrderId) ?: NoRecord.num
                    )

                    InvestigationsMainComposition(
                        modifier = Modifier.padding(all = 0.dp),
                        processControlOnly = false
                    )
                }

                composable(destination = Route.Main.ProcessControl) {
                    val invModel: InvestigationsViewModel = hiltViewModel()
                    (LocalContext.current as MainActivity).initInvModel(invModel)
                    invModel.setCreatedRecord(
                        it.arguments?.getInt(NavArguments.orderId) ?: NoRecord.num,
                        it.arguments?.getInt(NavArguments.subOrderId) ?: NoRecord.num
                    )
                    QMAppTheme {
                        InvestigationsMainComposition(
                            modifier = Modifier.padding(all = 0.dp),
                            processControlOnly = true
                        )
                    }
                }

                composable(destination = Route.Main.OrderAddEdit) {
                    val newOrderModel: NewItemViewModel = hiltViewModel()
                    (LocalContext.current as MainActivity).initNewOrderModel(newOrderModel)
                    BackHandler { navController.popBackStack() }
                    QMAppTheme {
                        OrderForm(
                            orderId = it.arguments?.getInt(NavArguments.orderId) ?: NoRecord.num
                        )
                    }
                }

                composable(destination = Route.Main.SubOrderAddEdit) {
                    val newOrderModel: NewItemViewModel = hiltViewModel()
                    (LocalContext.current as MainActivity).initNewOrderModel(newOrderModel)
                    newOrderModel.setSubOrderStandAlone(it.arguments?.getBoolean(NavArguments.subOrderAddEditMode) ?: false)
                    BackHandler { navController.popBackStack() }
                    QMAppTheme {
                        SubOrderForm(
                            record = Pair(
                                it.arguments?.getInt(NavArguments.orderId) ?: NoRecord.num,
                                it.arguments?.getInt(NavArguments.subOrderId) ?: NoRecord.num
                            )
                        )
                    }
                }

                settingsNavigation(navController)
            }
        }
    }
}