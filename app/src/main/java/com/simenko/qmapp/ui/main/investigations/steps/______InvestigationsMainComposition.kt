package com.simenko.qmapp.ui.main.investigations.steps

import android.content.Context
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.*
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
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
import com.simenko.qmapp.ui.neworder.launchNewItemActivity
import com.simenko.qmapp.ui.theme.Primary900
import com.simenko.qmapp.ui.theme.QMAppTheme

private const val TAG = "InvestigationsMai"

fun statusDialog(recordId: Int, dialogFor: DialogFor) {
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvestigationsMainComposition(
    modifier: Modifier = Modifier,
    appModel: QualityManagementViewModel,
    context: Context,
    createdRecord: CreatedRecord?
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp

    val currentTask by appModel.currentSubOrderTask.observeAsState()

    var isSamplesNumVisible by rememberSaveable { mutableStateOf(1) }
    var isResultsVisible by rememberSaveable { mutableStateOf(1) }
    val rowState = rememberScrollState()

    val showStatusChangeDialog = appModel.isStatusDialogVisible.observeAsState()
    val dialogInput by appModel.dialogInput.observeAsState()

    fun statusDialog(recordId: Int, dialogFor: DialogFor) {
        appModel.dialogInput.value = DialogInput(recordId, dialogFor)
        appModel.isStatusDialogVisible.value = true
    }

    QMAppTheme {
        var fabPositionToRemember by remember { mutableStateOf(FabPosition.End) }
        var fabPositionToSet by remember { mutableStateOf(FabPosition.End) }

        fun changeFlaBtnPosition(position: FabPosition) {
            fabPositionToRemember = position
        }

        LaunchedEffect(fabPositionToRemember) {
            fabPositionToSet = fabPositionToRemember
        }

        LaunchedEffect(currentTask) {
            isSamplesNumVisible = when ((currentTask ?: 0) > 0) {
                true -> 1
                false -> 0
            }
            isResultsVisible = when ((currentTask ?: 0) > 0) {
                true -> 1
                false -> 0
            }
        }

        Scaffold(

            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        launchNewItemActivity(context, ActionType.ADD_ORDER)
//                        ToDo while adding new item Main activity still added in the run stack
                        (context as MainActivity).finish()
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
                Row(
                    Modifier
                        .horizontalScroll(rowState)
                        .width(
                            (screenWidth * (1 + 0.38 * isSamplesNumVisible + 0.5 * isResultsVisible)).dp
                        )
                        .padding(padding)
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

                    Orders(
                        modifier = modifier.width(screenWidth.dp),
                        appModel = appModel,
                        onListEnd = { changeFlaBtnPosition(it) },
                        createdRecord = createdRecord,
                        showStatusDialog = { a, b -> statusDialog(a, b) }
                    )
                    SampleComposition(
                        modifier.width((screenWidth * 0.38 * isSamplesNumVisible).dp),
                        appModel
                    )
                    ResultsComposition(modifier.width((screenWidth * 0.5 * isResultsVisible).dp))
                }

                if (showStatusChangeDialog.value == true)
                    CustomDialogUI(
                        dialogInput = dialogInput?: DialogInput(0,DialogFor.ORDER),
                        openDialogCustom = appModel.isStatusDialogVisible,
                        appModel = appModel
                    )

            }
        )
    }
}
