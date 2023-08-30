package com.simenko.qmapp.ui.neworder

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.simenko.qmapp.R
import com.simenko.qmapp.ui.main.AddEditMode
import com.simenko.qmapp.ui.neworder.steps.*

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SubOrderForm(
    modifier: Modifier = Modifier,
    addEditMode: AddEditMode,
    viewModel: NewItemViewModel,
    parentId: Int
) {
    val observerLoadingProcess by viewModel.isLoadingInProgress.observeAsState()
    val observerIsNetworkError by viewModel.isNetworkError.observeAsState()

    val pullRefreshState = rememberPullRefreshState(
        refreshing = observerLoadingProcess!!,
        onRefresh = {
            viewModel.refreshDataFromRepository()
        }
    )

    Box(Modifier.pullRefresh(pullRefreshState)) {
        Column(
            modifier
                .verticalScroll(rememberScrollState())
        ) {
            if (addEditMode == AddEditMode.ADD_SUB_ORDER_STAND_ALONE ||
                addEditMode == AddEditMode.EDIT_SUB_ORDER_STAND_ALONE) {
                ButtonsSection(title = R.string.select_reason) {
                    ReasonsSelection(
                        modifier = Modifier.padding(top = 0.dp)
                    )
                }
            }
            ButtonsSection(title = R.string.select_department) {
                DepartmentsSelection(
                    modifier = Modifier.padding(top = 0.dp),
                    appModel = viewModel
                )
            }
            ButtonsSection(title = R.string.select_sub_department) {
                SubDepartmentsSelection(
                    modifier = Modifier.padding(top = 0.dp),
                    appModel = viewModel
                )
            }
            ButtonsSection(title = R.string.select_ordered_by) {
                SubOrderPlacersSelection(
                    modifier = Modifier.padding(top = 0.dp),
                    appModel = viewModel
                )
            }
            ButtonsSection(title = R.string.select_channel) {
                ChannelsSelection(
                    modifier = Modifier.padding(top = 0.dp),
                    appModel = viewModel
                )
            }
            ButtonsSection(title = R.string.select_line) {
                LinesSelection(
                    modifier = Modifier.padding(top = 0.dp),
                    appModel = viewModel
                )
            }
            ButtonsSection(title = R.string.select_item_type) {
                VersionsSelection(
                    modifier = Modifier.padding(top = 0.dp),
                    appModel = viewModel
                )
            }
            ButtonsSection(title = R.string.select_operation) {
                OperationsSelection(
                    modifier = Modifier.padding(top = 0.dp),
                    appModel = viewModel
                )
            }
            ButtonsSectionQuantity(title = R.string.select_quantity) {
                QuantitySelection(
                    modifier = Modifier.padding(top = 0.dp)
                )
            }
            ButtonsSection(title = R.string.select_characteristics) {
                CharacteristicsSelection(
                    modifier = Modifier.padding(top = 0.dp),
                    appModel = viewModel
                )
            }
            Spacer(Modifier.height((16 + 56).dp))
        }
        PullRefreshIndicator(
            observerLoadingProcess!!,
            pullRefreshState,
            modifier.align(Alignment.TopCenter),
            contentColor = ProgressIndicatorDefaults.circularColor
        )
    }
    if (observerIsNetworkError == true) {
        Toast.makeText(LocalContext.current, "Network error!", Toast.LENGTH_SHORT).show()
        viewModel.onNetworkErrorShown()
    }
}