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
import androidx.hilt.navigation.compose.hiltViewModel
import com.simenko.qmapp.domain.OrderTypeProcessOnly
import com.simenko.qmapp.other.Constants.ANIMATION_DURATION
import com.simenko.qmapp.ui.dialogs.StatusUpdateDialog
import com.simenko.qmapp.ui.dialogs.DialogInput
import com.simenko.qmapp.ui.main.MainActivity
import com.simenko.qmapp.ui.main.investigations.InvestigationsViewModel
import com.simenko.qmapp.ui.main.team.TeamViewModel
import com.simenko.qmapp.ui.neworder.ActionType
import com.simenko.qmapp.ui.neworder.launchNewItemActivityForResult
import com.simenko.qmapp.ui.theme.Primary
import com.simenko.qmapp.ui.theme.QMAppTheme

private const val TAG = "InvestigationsMainComposition"

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun InvestigationsMainComposition(
    modifier: Modifier = Modifier,
    processControlOnly: Boolean = false
) {
    val context = LocalContext.current
    val invModel: InvestigationsViewModel = hiltViewModel()
    Log.d(TAG, "InvestigationsViewModel: $invModel")
    val teamModel: TeamViewModel = hiltViewModel()

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp

    val currentTask by invModel.currentTaskDetails.observeAsState()

    var isSamplesNumVisible by rememberSaveable { mutableIntStateOf(1) }
    val rowState = rememberScrollState()

    val showStatusChangeDialog = invModel.isStatusUpdateDialogVisible.observeAsState()
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
                        if (processControlOnly)
                            launchNewItemActivityForResult(
                                (context as MainActivity),
                                ActionType.ADD_SUB_ORDER_STAND_ALONE.ordinal
                            )
                        else
                            launchNewItemActivityForResult(
                                context as MainActivity,
                                ActionType.ADD_ORDER.ordinal
                            )
                    },
                    content = {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = Primary
                        )
                    }
                )
            },
            floatingActionButtonPosition = fabPositionToSet,
            content = { padding ->
                Log.d(TAG, "InvestigationsMainComposition: $padding")
                val observerLoadingProcess by invModel.isLoadingInProgress.observeAsState(false)
                val observerIsNetworkError by invModel.isErrorMessage.observeAsState(null)

                val pullRefreshState = rememberPullRefreshState(
                    refreshing = observerLoadingProcess,
                    onRefresh = { invModel.uploadNewInvestigations() }
                )

                Box(
                    Modifier.pullRefresh(pullRefreshState)
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

                        if (processControlOnly)
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
                        else
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
                        StatusUpdateDialog(
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

                if (observerIsNetworkError != null) {
                    Toast.makeText(context, observerIsNetworkError, Toast.LENGTH_SHORT).show()
                    invModel.onNetworkErrorShown()
                }
            }
        )
    }
}
