package com.simenko.qmapp.ui.main.investigations.steps

import androidx.compose.animation.core.*
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.other.Constants.ANIMATION_DURATION
import com.simenko.qmapp.ui.Screen
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
    navController: NavHostController,
    modifier: Modifier = Modifier,
    mainScreenPadding: PaddingValues,
    processControlOnly: Boolean = false,
) {
    val invModel: InvestigationsViewModel = hiltViewModel()
    val teamModel: TeamViewModel = hiltViewModel()

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    val tabRowHeight = 36.dp
    val screenHeight = configuration.screenHeightDp.dp - mainScreenPadding.calculateTopPadding() - tabRowHeight

    val currentTask by invModel.currentTaskDetails.collectAsStateWithLifecycle()

    val verticalScrollState = rememberScrollState()
    val horizontalScrollState = rememberScrollState()

    val showStatusChangeDialog = invModel.isStatusUpdateDialogVisible.observeAsState()
    val dialogInput by invModel.dialogInput.observeAsState()
    var selectedTabIndex by rememberSaveable { mutableIntStateOf(InvStatus.ALL.ordinal) }

    /**
     * TotalScreenWidth, FirstColumnWidth, SecondColumnWidth
     * */
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

    val limitToResize = 720

    fun updateSizes(samplesFactor: Int) {
        screenSizes = Triple(
            when {
                screenWidth > limitToResize -> screenWidth.dp
                else -> (screenWidth * (1 + 0.88 * samplesFactor)).dp
            },
            when (samplesFactor) {
                0 -> screenWidth.dp
                else -> {
                    when {
                        screenWidth > limitToResize -> (screenWidth * 0.57).dp
                        else -> screenWidth.dp
                    }
                }
            },
            when {
                screenWidth > limitToResize -> (screenWidth * 0.43 * samplesFactor).dp
                else -> (screenWidth * 0.88 * samplesFactor).dp
            }
        )
    }

    suspend fun animateScroll(samplesFactor: Int) {
        horizontalScrollState.animateScrollTo(
            samplesFactor * horizontalScrollState.maxValue, tween(
                durationMillis = ANIMATION_DURATION,
                easing = LinearOutSlowInEasing
            )
        )
    }

    LaunchedEffect(currentTask) {
        when (currentTask == NoRecord) {
            true -> {
                if (screenWidth <= limitToResize) animateScroll(0)
                updateSizes(0)
            }

            false -> {
                updateSizes(1)
//                ToDo find a way to run animation exactly when screen resized
                delay(50L)
                if (screenWidth <= limitToResize) animateScroll(1)
            }
        }
    }

    QMAppTheme {
        Column(
            modifier = modifier
                .fillMaxWidth()
        ) {
            TabRow(selectedTabIndex = selectedTabIndex) {
                InvStatus.values().forEach {
                    val selected = selectedTabIndex == it.ordinal
                    Tab(
                        modifier = Modifier.height(tabRowHeight),
                        selected = selected,
                        onClick = { onTabSelectedLambda(it, it.ordinal) }
                    ) {
                        Text(
                            text = StringUtils.getWithSpaces(it.name),
                            fontSize = 12.sp,
                            style = if (selected) LocalTextStyle.current.copy(fontWeight = FontWeight.Bold) else LocalTextStyle.current
                        )
                    }
                }
            }
            Row(
                Modifier
                    .verticalScroll(verticalScrollState)
                    .horizontalScroll(horizontalScrollState)
                    .width(screenSizes.first)
                    .height(screenHeight)
            ) {
                if (processControlOnly)
                    SubOrdersStandAlone(modifier = modifier.width(screenSizes.second))
                else
                    Orders(
                        modifier = modifier.width(screenSizes.second),
                        onClickEdit = { navController.navigate(Screen.Main.OrderAddEdit.withArgs(it.toString())) }
                    )

                if (currentTask != NoRecord)
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
