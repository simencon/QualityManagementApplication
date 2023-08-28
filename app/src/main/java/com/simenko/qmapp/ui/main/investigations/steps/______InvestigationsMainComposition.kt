package com.simenko.qmapp.ui.main.investigations.steps

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.*
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.simenko.qmapp.other.Constants.ANIMATION_DURATION
import com.simenko.qmapp.ui.dialogs.StatusUpdateDialog
import com.simenko.qmapp.ui.dialogs.DialogInput
import com.simenko.qmapp.ui.main.investigations.InvestigationsViewModel
import com.simenko.qmapp.ui.main.team.TeamViewModel
import com.simenko.qmapp.ui.theme.QMAppTheme

@Composable
fun InvestigationsMainComposition(
    modifier: Modifier = Modifier,
    processControlOnly: Boolean = false
) {
    val invModel: InvestigationsViewModel = hiltViewModel()
    val teamModel: TeamViewModel = hiltViewModel()

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp

    val currentTask by invModel.currentTaskDetails.observeAsState()

    var isSamplesNumVisible by rememberSaveable { mutableIntStateOf(1) }
    val rowState = rememberScrollState()

    val showStatusChangeDialog = invModel.isStatusUpdateDialogVisible.observeAsState()
    val dialogInput by invModel.dialogInput.observeAsState()


    QMAppTheme {
        LaunchedEffect(currentTask) {
            isSamplesNumVisible = when ((currentTask?.num ?: 0) > 0) {
                true -> 1
                false -> 0
            }
        }

        Box {
            Row(
                Modifier
                    .horizontalScroll(rowState)
                    .width(
                        when {
                            screenWidth > 720 -> screenWidth.dp
                            else -> (screenWidth * (1 + 0.88 * isSamplesNumVisible)).dp
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
                                0 -> screenWidth.dp
                                else -> {
                                    when {
                                        screenWidth > 720 -> (screenWidth * 0.57).dp
                                        else -> screenWidth.dp
                                    }
                                }
                            }
                        )
                    )
                else
                    Orders(
                        modifier = modifier.width(
                            when (isSamplesNumVisible) {
                                0 -> screenWidth.dp
                                else -> {
                                    when {
                                        screenWidth > 720 -> (screenWidth * 0.57).dp
                                        else -> screenWidth.dp
                                    }
                                }
                            }
                        )
                    )

                if (isSamplesNumVisible == 1)
                    SampleComposition(
                        modifier.width(
                            when {
                                screenWidth > 720 -> (screenWidth * 0.43 * isSamplesNumVisible).dp
                                else -> (screenWidth * 0.88 * isSamplesNumVisible).dp
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
        }
    }
}
