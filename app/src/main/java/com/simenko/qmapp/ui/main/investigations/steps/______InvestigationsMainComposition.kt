package com.simenko.qmapp.ui.main.investigations.steps

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.*
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.simenko.qmapp.ui.common.ANIMATION_DURATION
import com.simenko.qmapp.ui.common.CustomDialogUI
import com.simenko.qmapp.ui.common.DialogFor
import com.simenko.qmapp.ui.common.DialogInput
import com.simenko.qmapp.ui.main.CreatedRecord
import com.simenko.qmapp.ui.main.MainActivity
import com.simenko.qmapp.ui.main.QualityManagementViewModel
import com.simenko.qmapp.ui.neworder.ActionType
import com.simenko.qmapp.ui.neworder.launchNewItemActivityForResult
import com.simenko.qmapp.ui.theme.Primary900
import com.simenko.qmapp.ui.theme.QMAppTheme

fun statusDialog(recordId: Int, dialogFor: DialogFor, performerId: Int?) {
}

private const val TAG = "InvestigationsMainComposition"

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun InvestigationsMainComposition(
    modifier: Modifier = Modifier,
    appModel: QualityManagementViewModel,
    context: Context,
    createdRecord: CreatedRecord?
) {
    appModel.investigationStatuses.observeAsState()
    appModel.productTolerances.observeAsState()
    appModel.componentTolerances.observeAsState()
    appModel.componentInStageTolerances.observeAsState()
    appModel.metrixes.observeAsState()
    appModel.team.observeAsState()
    appModel.completeTasks.observeAsState()

    val showAllInvestigations by appModel.showAllInvestigations.observeAsState()
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp

    val currentTask by appModel.currentSubOrderTask.observeAsState()

    var isSamplesNumVisible by rememberSaveable { mutableStateOf(1) }
    val rowState = rememberScrollState()

    val showStatusChangeDialog = appModel.isStatusDialogVisible.observeAsState()
    val dialogInput by appModel.dialogInput.observeAsState()



    QMAppTheme {

        var fabPositionToSet by remember { mutableStateOf(FabPosition.End) }

        fun changeFlaBtnPosition(position: FabPosition) {
            Log.d(TAG, "changeFlaBtnPosition: $position")
            fabPositionToSet = position
        }

        LaunchedEffect(currentTask) {
            isSamplesNumVisible = when ((currentTask ?: 0) > 0) {
                true -> 1
                false -> 0
            }
        }

        Scaffold(
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        if (showAllInvestigations == true)
                            launchNewItemActivityForResult(
                                context as MainActivity,
                                ActionType.ADD_ORDER.ordinal
                            )
                        else
                            launchNewItemActivityForResult(
                                (context as MainActivity),
                                ActionType.ADD_SUB_ORDER_STAND_ALONE.ordinal
                            )
                    },
                    content = {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = Primary900
                        )
                    }
                )
            },
            floatingActionButtonPosition = fabPositionToSet,
            content = { padding ->

                val observerLoadingProcess by appModel.isLoadingInProgress.observeAsState()
                val observerIsNetworkError by appModel.isNetworkError.observeAsState()

                val pullRefreshState = rememberPullRefreshState(
                    refreshing = observerLoadingProcess!!,
                    onRefresh = { appModel.syncOrders() }
                )

                Box(
                    Modifier
                        .pullRefresh(pullRefreshState)
                ) {
                    Row(
                        Modifier
                            .horizontalScroll(rowState)
                            .width(
                                when {
                                    screenWidth > 720 -> {
                                        screenWidth.dp
                                    }
                                    else -> {
                                        (screenWidth * (1 + 0.88 * isSamplesNumVisible)).dp
                                    }
                                }
                            )
                            .animateContentSize(
                                tween(
                                    durationMillis = ANIMATION_DURATION,
                                    easing = LinearOutSlowInEasing
                                )
                            )
                    ) {
                        LaunchedEffect(isSamplesNumVisible) {
                            if (isSamplesNumVisible == 1)
                                rowState.animateScrollTo(
                                    rowState.maxValue, tween(
                                        durationMillis = ANIMATION_DURATION,
                                        easing = LinearOutSlowInEasing
                                    )
                                )
                        }

                        if (showAllInvestigations == true)
                            Orders(
                                modifier = modifier.width(
                                    when (isSamplesNumVisible) {
                                        0 -> {
                                            screenWidth.dp
                                        }
                                        else -> {
                                            when {
                                                screenWidth > 720 -> {
                                                    (screenWidth * 0.57).dp
                                                }
                                                else -> {
                                                    screenWidth.dp
                                                }
                                            }
                                        }
                                    }
                                ),
                                appModel = appModel,
                                onListEnd = { changeFlaBtnPosition(it) },
                                createdRecord = createdRecord
                            )
                        else
                            SubOrdersStandAlone(
                                modifier = modifier.width(
                                    when (isSamplesNumVisible) {
                                        0 -> {
                                            screenWidth.dp
                                        }
                                        else -> {
                                            when {
                                                screenWidth > 720 -> {
                                                    (screenWidth * 0.57).dp
                                                }
                                                else -> {
                                                    screenWidth.dp
                                                }
                                            }
                                        }
                                    }
                                ),
                                appModel = appModel,
                                onListEnd = { changeFlaBtnPosition(it) },
                                createdRecord = createdRecord,
                                showStatusDialog = { a, b, c -> statusDialog(a, b, c) }
                            )

                        SampleComposition(
                            modifier.width(
                                when {
                                    screenWidth > 720 -> {
                                        (screenWidth * 0.43 * isSamplesNumVisible).dp
                                    }
                                    else -> {
                                        (screenWidth * 0.88 * isSamplesNumVisible).dp
                                    }
                                }

                            ),
                            appModel
                        )
                    }

                    if (showStatusChangeDialog.value == true)
                        CustomDialogUI(
                            dialogInput = dialogInput ?: DialogInput(0, DialogFor.ORDER, null),
                            openDialogCustom = appModel.isStatusDialogVisible,
                            appModel = appModel
                        )

                    PullRefreshIndicator(
                        observerLoadingProcess!!,
                        pullRefreshState,
                        Modifier.align(Alignment.TopCenter),
                        contentColor = ProgressIndicatorDefaults.circularColor
                    )
                }

                if (observerIsNetworkError == true) {
                    Toast.makeText(context, "Network error!", Toast.LENGTH_SHORT).show()
                    appModel.onNetworkErrorShown()
                }
            }
        )
    }
}
