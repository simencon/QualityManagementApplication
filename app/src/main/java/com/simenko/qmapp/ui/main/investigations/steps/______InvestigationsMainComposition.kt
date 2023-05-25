package com.simenko.qmapp.ui.main.investigations.steps

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.simenko.qmapp.domain.OrderTypeProcessOnly
import com.simenko.qmapp.other.Constants.ANIMATION_DURATION
import com.simenko.qmapp.ui.common.CustomDialogUI
import com.simenko.qmapp.ui.common.DialogInput
import com.simenko.qmapp.ui.main.MainActivity
import com.simenko.qmapp.ui.neworder.ActionType
import com.simenko.qmapp.ui.neworder.launchNewItemActivityForResult
import com.simenko.qmapp.ui.theme.Primary900
import com.simenko.qmapp.ui.theme.QMAppTheme

private const val TAG = "InvestigationsMainComposition"

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun InvestigationsMainComposition(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val invModel = (context as MainActivity).investigationsModel
    val teamModel = (context as MainActivity).teamModel

    val parentOrderTypeId by invModel.showSubOrderWithOrderType.observeAsState()
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp

    val currentTask by invModel.currentTaskDetails.observeAsState()

    var isSamplesNumVisible by rememberSaveable { mutableStateOf(1) }
    val rowState = rememberScrollState()

    val showStatusChangeDialog = invModel.isReportDialogVisible.observeAsState()
    val dialogInput by invModel.dialogInput.observeAsState()


    QMAppTheme {

        var fabPositionToSet by remember { mutableStateOf(FabPosition.End) }

        fun changeFlaBtnPosition(position: FabPosition) {
            Log.d(TAG, "changeFlaBtnPosition: $position")
            fabPositionToSet = position
        }

        LaunchedEffect(currentTask) {
            isSamplesNumVisible = when ((currentTask?.num ?: 0) > 0) {
                true -> 1
                false -> 0
            }
        }

        Scaffold(
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        if (parentOrderTypeId != OrderTypeProcessOnly)
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

                val observerLoadingProcess by invModel.isLoadingInProgress.observeAsState(false)
                val observerIsNetworkError by invModel.isNetworkError.observeAsState(false)

                val pullRefreshState = rememberPullRefreshState(
                    refreshing = observerLoadingProcess,
                    onRefresh = { invModel.uploadLatestInvestigationsEntities() }
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

                        if (parentOrderTypeId != OrderTypeProcessOnly)
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
                                appModel = invModel,
                                onListEnd = { changeFlaBtnPosition(it) }
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
                                onListEnd = { changeFlaBtnPosition(it) }
                            )

                        if (isSamplesNumVisible == 1)
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
                                )
                            )
                    }

                    if (showStatusChangeDialog.value == true)
                        CustomDialogUI(
                            dialogInput = dialogInput ?: DialogInput(),
                            teamModel = teamModel,
                            invModel = invModel
                        )

                    PullRefreshIndicator(
                        observerLoadingProcess!!,
                        pullRefreshState,
                        Modifier.align(Alignment.TopCenter),
                        contentColor = ProgressIndicatorDefaults.circularColor
                    )
                }

                if (observerIsNetworkError) {
                    Toast.makeText(context, "Network error!", Toast.LENGTH_SHORT).show()
                    invModel.onNetworkErrorShown()
                }
            }
        )
    }
}
