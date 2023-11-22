package com.simenko.qmapp.ui.main.products.items.list

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.simenko.qmapp.domain.NoString
import com.simenko.qmapp.other.Constants.DEFAULT_SPACE
import com.simenko.qmapp.ui.common.InfoLine
import com.simenko.qmapp.ui.common.animation.HorizonteAnimationImp
import com.simenko.qmapp.utils.dp
import com.simenko.qmapp.utils.observeAsState

@Composable
fun ProductKindProducts(
    mainScreenPadding: PaddingValues,
    viewModel: ProductListViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    val screenWidthPhysical = screenWidth.toFloat().dp()
    val localDensity = LocalDensity.current
    var titleHeightDp by remember { mutableStateOf(0.dp) }
    val screenHeight = configuration.screenHeightDp.dp - mainScreenPadding.calculateTopPadding() - titleHeightDp

    val animator = HorizonteAnimationImp(screenWidth, scope)

    val productKind by viewModel.productKind.collectAsStateWithLifecycle()
    val isSecondRowVisible by viewModel.isSecondColumnVisible.collectAsStateWithLifecycle(false)
    val listsIsInitialized by viewModel.listsIsInitialized.collectAsStateWithLifecycle(Pair(false, false))

    /**
     * TotalScreenWidth, FirstColumnWidth, SecondColumnWidth
     * */
    var screenSizes: Triple<Dp, Dp, Dp> by remember { mutableStateOf(animator.getRequiredScreenWidth(if (isSecondRowVisible) 1 else 0)) }

    val verticalScrollState = rememberScrollState()
    val horizontalScrollState = rememberScrollState()

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
            animator.run { horizontalScrollState.animateScroll(0) }
            animator.setRequiredScreenWidth(0) { screenSizes = it }
        }
    }

    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start, verticalArrangement = Arrangement.Bottom) {
        Column(modifier = Modifier.onGloballyPositioned {
            titleHeightDp = with(localDensity) { it.size.height.toDp() }
        }) {
            Spacer(modifier = Modifier.height(10.dp))
            InfoLine(modifier = Modifier.padding(start = DEFAULT_SPACE.dp), title = "Product line", body = productKind.productLine.manufacturingProject.projectSubject ?: NoString.str)
            InfoLine(modifier = Modifier.padding(start = DEFAULT_SPACE.dp), title = "Product", body = productKind.productKind.productKindDesignation)
            Divider(modifier = Modifier.height(1.dp), color = MaterialTheme.colorScheme.secondary)
        }
        Row(
            Modifier
                .verticalScroll(verticalScrollState)
                .horizontalScroll(horizontalScrollState, isSecondRowVisible)
                .onSizeChanged { if (isSecondRowVisible && it.width > screenWidthPhysical) animator.run { horizontalScrollState.animateScroll(1) } }
                .width(screenSizes.first)
                .height(screenHeight)
        ) {
            if (listsIsInitialized.first)
                ProductList(modifier = Modifier.width(screenSizes.second), viewModel = viewModel)
            if (isSecondRowVisible && listsIsInitialized.second)
                Versions(modifier = Modifier.width(screenSizes.third), viewModel = viewModel)
        }
    }
}