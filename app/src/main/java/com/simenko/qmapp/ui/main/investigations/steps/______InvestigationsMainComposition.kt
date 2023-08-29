package com.simenko.qmapp.ui.main.investigations.steps

import androidx.compose.animation.core.*
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.other.Constants.ANIMATION_DURATION
import com.simenko.qmapp.ui.dialogs.StatusUpdateDialog
import com.simenko.qmapp.ui.dialogs.DialogInput
import com.simenko.qmapp.ui.main.InvStatus
import com.simenko.qmapp.ui.main.investigations.InvestigationsViewModel
import com.simenko.qmapp.ui.main.team.TeamViewModel
import com.simenko.qmapp.ui.theme.QMAppTheme
import com.simenko.qmapp.utils.StringUtils
import kotlinx.coroutines.delay

@Composable
fun InvestigationsMainComposition(
    modifier: Modifier = Modifier,
    processControlOnly: Boolean = false
) {
    val invModel: InvestigationsViewModel = hiltViewModel()
    val teamModel: TeamViewModel = hiltViewModel()

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp

    val currentTask by invModel.currentTaskDetails.collectAsStateWithLifecycle()

    var isSamplesNumVisible by rememberSaveable { mutableIntStateOf(0) }
    val rowState = rememberScrollState()

    val showStatusChangeDialog = invModel.isStatusUpdateDialogVisible.observeAsState()
    val dialogInput by invModel.dialogInput.observeAsState()
    var selectedTabIndex by rememberSaveable { mutableIntStateOf(InvStatus.ALL.ordinal) }

    var screenSizes: Triple<Dp, Dp, Dp> by remember {
        mutableStateOf(Triple(screenWidth.dp, screenWidth.dp, 0.dp))
    }

    val onTabSelectedLambda = remember<(InvStatus, Int) -> Unit> {
        { status, tabIndex ->
            if (processControlOnly)
                invModel.setCurrentSubOrdersFilter(status = status.statusId)
            else
                invModel.setCurrentOrdersFilter(status = status.statusId)
            selectedTabIndex = tabIndex
        }
    }

    LaunchedEffect(Unit) {
        if (processControlOnly)
            invModel.setCurrentSubOrdersFilter(status = InvStatus.values()[selectedTabIndex].statusId)
        else
            invModel.setCurrentOrdersFilter(status = InvStatus.values()[selectedTabIndex].statusId)
    }

    fun updateSizes() {
        screenSizes = Triple(
            when {
                screenWidth > 720 -> screenWidth.dp
                else -> (screenWidth * (1 + 0.88 * isSamplesNumVisible)).dp
            },
            when (isSamplesNumVisible) {
                0 -> screenWidth.dp
                else -> {
                    when {
                        screenWidth > 720 -> (screenWidth * 0.57).dp
                        else -> screenWidth.dp
                    }
                }
            },
            when {
                screenWidth > 720 -> (screenWidth * 0.43 * isSamplesNumVisible).dp
                else -> (screenWidth * 0.88 * isSamplesNumVisible).dp
            }
        )
    }

    suspend fun animateScroll() {
        if (isSamplesNumVisible == 1)
            rowState.animateScrollTo(
                rowState.maxValue, tween(
                    durationMillis = ANIMATION_DURATION,
                    easing = LinearOutSlowInEasing
                )
            )
        else if (isSamplesNumVisible == 0) {
            rowState.animateScrollTo(
                0, tween(
                    durationMillis = ANIMATION_DURATION,
                    easing = LinearOutSlowInEasing
                )
            )
        }
    }

    LaunchedEffect(currentTask) {
        println("current task is: $currentTask")
        when (currentTask != NoRecord) {
            true -> {
                isSamplesNumVisible = 1
                updateSizes()
                if (screenWidth <= 720) animateScroll()
            }

            false -> {
                isSamplesNumVisible = 0
                if (screenWidth <= 720) animateScroll()
                updateSizes()
            }
        }
    }

    QMAppTheme {
        Column(
            modifier = modifier.fillMaxWidth()
        ) {
            TabRow(selectedTabIndex = selectedTabIndex) {
                InvStatus.values().forEach {
                    Tab(
                        modifier = Modifier.height(36.dp),
                        selected = selectedTabIndex == it.ordinal,
                        onClick = { onTabSelectedLambda(it, it.ordinal) }
                    ) {
                        Text(text = StringUtils.getWithSpaces(it.name), fontSize = 12.sp)
                    }
                }
            }
            Row(
                Modifier
                    .horizontalScroll(rowState)
                    .width(screenSizes.first)
            ) {
                if (processControlOnly)
                    SubOrdersStandAlone(modifier = modifier.width(screenSizes.second))
                else
                    Orders(modifier = modifier.width(screenSizes.second))

                if (isSamplesNumVisible == 1)
                    SampleComposition(modifier = modifier.width(screenSizes.third))
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
