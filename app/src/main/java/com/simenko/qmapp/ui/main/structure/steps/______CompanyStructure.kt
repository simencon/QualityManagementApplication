package com.simenko.qmapp.ui.main.structure.steps

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
import com.simenko.qmapp.other.Constants.ANIMATION_DURATION
import com.simenko.qmapp.ui.main.structure.CompanyStructureViewModel
import com.simenko.qmapp.utils.observeAsState
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext

@Composable
fun CompanyStructure(
    mainScreenPadding: PaddingValues,
    viewModel: CompanyStructureViewModel = hiltViewModel()
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    val screenHeight = configuration.screenHeightDp.dp - mainScreenPadding.calculateTopPadding()

    val isSecondRowVisible by viewModel.isSecondColumnVisible.collectAsStateWithLifecycle(false)

    val verticalScrollState = rememberScrollState()
    val horizontalScrollState = rememberScrollState()

    val listsIsInitialized by viewModel.listsIsInitialized.collectAsStateWithLifecycle(Pair(false, false))

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
            value = samplesFactor * horizontalScrollState.maxValue,
            animationSpec = tween(durationMillis = ANIMATION_DURATION, easing = LinearOutSlowInEasing)
        )
    }

    LaunchedEffect(Unit) { viewModel.mainPageHandler.setupMainPage(0, true) }

    val lifecycleState = LocalLifecycleOwner.current.lifecycle.observeAsState()

    LaunchedEffect(lifecycleState.value) {
        when (lifecycleState.value) {
            Lifecycle.Event.ON_RESUME -> viewModel.setViewState(true)
            Lifecycle.Event.ON_STOP -> viewModel.setViewState(false)
            else -> {}
        }
    }

    val scope = rememberCoroutineScope()
    var secondRowVisibility by rememberSaveable { mutableStateOf(false) }
    val channel = kotlinx.coroutines.channels.Channel<Job>(capacity = kotlinx.coroutines.channels.Channel.UNLIMITED).apply {
        scope.launch { consumeEach { it.join() } }
    }

    LaunchedEffect(isSecondRowVisible) {
        if (isSecondRowVisible) {
            channel.trySend(scope.launch(start = CoroutineStart.LAZY) { updateSizes(1) })
        } else {
            channel.trySend(scope.launch(start = CoroutineStart.LAZY) { secondRowVisibility = false })
            channel.trySend(scope.launch(start = CoroutineStart.LAZY) { if (screenWidth <= limitToResize) animateScroll(0) })
            channel.trySend(scope.launch(start = CoroutineStart.LAZY) { updateSizes(0) })
        }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            Modifier
                .verticalScroll(verticalScrollState)
                .horizontalScroll(horizontalScrollState, screenSizes.first != screenWidth.dp)
                .onSizeChanged {
                    if (isSecondRowVisible) {
                        channel.trySend(scope.launch(start = CoroutineStart.LAZY) { if (screenWidth <= limitToResize) animateScroll(1) })
                        channel.trySend(scope.launch(start = CoroutineStart.LAZY) { secondRowVisibility = true })
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
