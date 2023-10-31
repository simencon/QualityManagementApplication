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
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.simenko.qmapp.other.Constants.ANIMATION_DURATION
import com.simenko.qmapp.other.Constants.TOP_TAB_ROW_HEIGHT
import com.simenko.qmapp.ui.common.animation.HorizonteAnimationImp
import com.simenko.qmapp.ui.dialogs.StatusUpdateDialog
import com.simenko.qmapp.ui.dialogs.DialogInput
import com.simenko.qmapp.ui.main.investigations.InvestigationsViewModel
import com.simenko.qmapp.utils.dp
import kotlinx.coroutines.delay

@Composable
fun InvestigationsMainComposition(
    mainScreenPadding: PaddingValues,
    invModel: InvestigationsViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    val screenHeight = configuration.screenHeightDp.dp - mainScreenPadding.calculateTopPadding() - TOP_TAB_ROW_HEIGHT.dp
    val animator = HorizonteAnimationImp(screenWidth, scope)

    val isSecondRowVisible by invModel.isSecondRowVisible.collectAsStateWithLifecycle(false)

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

    LaunchedEffect(isSecondRowVisible) {
        if (isSecondRowVisible) {
            animator.setRequiredScreenWidth(1) { screenSizes = it }
        } else {
//            animator.setSecondRowVisibility(false) { secondRowVisibility = it }
            animator.run { horizontalScrollState.animateScroll(0) }
            animator.setRequiredScreenWidth(0) { screenSizes = it }
        }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            Modifier
                .verticalScroll(verticalScrollState)
                .horizontalScroll(horizontalScrollState, screenSizes.first != screenWidth.dp)
                .onSizeChanged {
                    println("CompanyStructure resized with new sizes: $it, physical width ${screenWidth.toFloat().dp()}")
                    if (isSecondRowVisible && it.width > screenWidth.toFloat().dp()) {
                        println("CompanyStructure resized, start of animation")
                        animator.run { horizontalScrollState.animateScroll(1) }
//                        animator.setSecondRowVisibility(true) { visibility -> secondRowVisibility = visibility }
                    }
                }
                .width(screenSizes.first)
                .height(screenHeight)
        ) {
            if (invModel.isPcOnly == true)
                SubOrdersStandAlone(modifier = Modifier.width(screenSizes.second), viewModel = invModel)
            else
                Orders(modifier = Modifier.width(screenSizes.second), viewModel = invModel)

            if (isSecondRowVisible)
                SampleComposition(modifier = Modifier.width(screenSizes.third), invModel = invModel)
        }

        if (showStatusChangeDialog.value == true)
            StatusUpdateDialog(
                dialogInput = dialogInput ?: DialogInput(),
                invModel = invModel
            )
    }
}
