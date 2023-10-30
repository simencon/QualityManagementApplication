package com.simenko.qmapp.ui.main.structure.steps

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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.simenko.qmapp.ui.common.animation.HorizonteAnimationImp
import com.simenko.qmapp.ui.main.structure.CompanyStructureViewModel
import com.simenko.qmapp.utils.observeAsState

@Composable
fun CompanyStructure(
    mainScreenPadding: PaddingValues,
    viewModel: CompanyStructureViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    val screenHeight = configuration.screenHeightDp.dp - mainScreenPadding.calculateTopPadding()

    val animator = HorizonteAnimationImp(screenWidth, scope)

    val isSecondRowVisible by viewModel.isSecondColumnVisible.collectAsStateWithLifecycle(false)
    val listsIsInitialized by viewModel.listsIsInitialized.collectAsStateWithLifecycle(Pair(false, false))

    /**
     * TotalScreenWidth, FirstColumnWidth, SecondColumnWidth
     * */
    var screenSizes: Triple<Dp, Dp, Dp> by remember { mutableStateOf(Triple(screenWidth.dp, screenWidth.dp, 0.dp)) }

    val verticalScrollState = rememberScrollState()
    val horizontalScrollState = rememberScrollState()
    var secondRowVisibility by rememberSaveable { mutableStateOf(false) }


    LaunchedEffect(Unit) { viewModel.mainPageHandler.setupMainPage(0, true) }

    val lifecycleState = LocalLifecycleOwner.current.lifecycle.observeAsState()

    LaunchedEffect(lifecycleState.value) {
        when (lifecycleState.value) {
            Lifecycle.Event.ON_RESUME -> viewModel.setViewState(true)
            Lifecycle.Event.ON_STOP -> viewModel.setViewState(false)
            else -> {}
        }
    }

    LaunchedEffect(isSecondRowVisible) {
        if (isSecondRowVisible) {
            animator.setRequiredScreenWidth(1) { screenSizes = it }
        } else {
            animator.setSecondRowVisibility(false) { secondRowVisibility = it }
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
                    if (isSecondRowVisible) {
                        animator.run { horizontalScrollState.animateScroll(1) }
                        animator.setSecondRowVisibility(true) { secondRowVisibility = it }
                    }
                }
                .width(screenSizes.first)
                .height(screenHeight)
        ) {
            if (listsIsInitialized.first)
                Departments(modifier = Modifier.width(screenSizes.second), viewModel = viewModel)
            if (secondRowVisibility && listsIsInitialized.second)
                Lines(modifier = Modifier.width(screenSizes.third), viewModel = viewModel)
        }
    }
}
