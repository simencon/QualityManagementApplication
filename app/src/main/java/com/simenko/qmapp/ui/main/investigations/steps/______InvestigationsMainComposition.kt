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
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.other.Constants.ANIMATION_DURATION
import com.simenko.qmapp.other.Constants.TOP_TAB_ROW_HEIGHT
import com.simenko.qmapp.ui.dialogs.StatusUpdateDialog
import com.simenko.qmapp.ui.dialogs.DialogInput
import com.simenko.qmapp.ui.main.investigations.InvestigationsViewModel
import kotlinx.coroutines.delay

@Composable
fun InvestigationsMainComposition(
    mainScreenPadding: PaddingValues,
    invModel: InvestigationsViewModel = hiltViewModel()
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    val screenHeight = configuration.screenHeightDp.dp - mainScreenPadding.calculateTopPadding() - TOP_TAB_ROW_HEIGHT.dp

    val tasksVisibility by invModel.tasksVisibility.collectAsStateWithLifecycle()

    val verticalScrollState = rememberScrollState()
    val horizontalScrollState = rememberScrollState()

    val showStatusChangeDialog = invModel.isStatusUpdateDialogVisible.observeAsState()
    val dialogInput by invModel.dialogInput.observeAsState()

    /**
     * TotalScreenWidth, FirstColumnWidth, SecondColumnWidth
     * */
    var screenSizes: Triple<Dp, Dp, Dp> by remember {
        mutableStateOf(Triple(screenWidth.dp, screenWidth.dp, 0.dp))
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

    LaunchedEffect(Unit) {
        invModel.mainPageHandler?.setupMainPage?.invoke(invModel.selectedTabIndex, true)
    }

    LaunchedEffect(tasksVisibility) {
        when (tasksVisibility.first == NoRecord) {
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

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            Modifier
                .verticalScroll(verticalScrollState)
                .horizontalScroll(horizontalScrollState, screenSizes.first != screenWidth.dp)
                .width(screenSizes.first)
                .height(screenHeight)
        ) {
            if (invModel.isPcOnly == true)
                SubOrdersStandAlone(modifier = Modifier.width(screenSizes.second), invModel = invModel)
            else
                Orders(modifier = Modifier.width(screenSizes.second), invModel = invModel)

            if (tasksVisibility.first != NoRecord)
                SampleComposition(modifier = Modifier.width(screenSizes.third), invModel = invModel)
        }

        if (showStatusChangeDialog.value == true)
            StatusUpdateDialog(
                dialogInput = dialogInput ?: DialogInput(),
                invModel = invModel
            )
    }
}
