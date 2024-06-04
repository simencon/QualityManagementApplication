package com.simenko.qmapp.ui.main.investigations.steps

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.simenko.qmapp.BaseApplication
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.other.Constants.TOP_TAB_ROW_HEIGHT
import com.simenko.qmapp.ui.common.animation.HorizonteAnimationImp
import com.simenko.qmapp.ui.dialogs.StatusUpdateDialog
import com.simenko.qmapp.ui.dialogs.DialogInput
import com.simenko.qmapp.ui.main.investigations.InvestigationsViewModel
import com.simenko.qmapp.utils.dp

@Composable
fun InvestigationsMainComposition(
    mainScreenPadding: PaddingValues,
    invModel: InvestigationsViewModel = hiltViewModel(),
    isPcOnly: Boolean, orderId: ID, subOrderId: ID,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    val screenWidthPhysical = screenWidth.toFloat().dp()
    val screenHeight = configuration.screenHeightDp.dp - mainScreenPadding.calculateTopPadding() - TOP_TAB_ROW_HEIGHT.dp
    val animator = HorizonteAnimationImp(screenWidth, scope)

    val isSecondRowVisible by invModel.isSecondRowVisible.collectAsStateWithLifecycle(false)
    val showStatusChangeDialog = invModel.isStatusUpdateDialogVisible.observeAsState()
    val dialogInput by invModel.dialogInput.observeAsState()

    val verticalScrollState = rememberScrollState()
    val horizontalScrollState = rememberScrollState()

    /**
     * TotalScreenWidth, FirstColumnWidth, SecondColumnWidth
     * */

    var screenSizes: Triple<Dp, Dp, Dp> by remember { mutableStateOf(Triple(screenWidth.dp, screenWidth.dp, 0.dp)) }

    LaunchedEffect(Unit) {
        invModel.onEntered(isPcOnly, orderId, subOrderId)

        invModel.syncInvestigationsEvent.collect { event ->
            event.getContentIfNotHandled()?.let {
                if (it) BaseApplication.setupOneTimeSync(context)
            }
        }
    }

    LaunchedEffect(isSecondRowVisible) {
        if (isSecondRowVisible) {
            animator.setRequiredScreenWidth(1) { screenSizes = it }
        } else {
            animator.run { horizontalScrollState.animateScroll(0) }
            animator.setRequiredScreenWidth(0) { screenSizes = it }
        }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            Modifier
                .verticalScroll(verticalScrollState)
                .horizontalScroll(horizontalScrollState, isSecondRowVisible)
                .onSizeChanged { if (isSecondRowVisible && it.width > screenWidthPhysical) animator.run { horizontalScrollState.animateScroll(1) } }
                .width(screenSizes.first)
                .height(screenHeight)
        ) {
            if (isPcOnly)
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