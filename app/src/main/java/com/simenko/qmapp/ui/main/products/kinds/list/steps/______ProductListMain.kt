package com.simenko.qmapp.ui.main.products.kinds.list.steps

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.simenko.qmapp.domain.NoString
import com.simenko.qmapp.domain.entities.products.DomainProductKind
import com.simenko.qmapp.other.Constants.DEFAULT_SPACE
import com.simenko.qmapp.ui.common.InfoLine
import com.simenko.qmapp.ui.common.animation.HorizonteAnimationImp
import com.simenko.qmapp.ui.main.products.kinds.list.ProductListViewModel
import com.simenko.qmapp.ui.navigation.Route
import com.simenko.qmapp.utils.dp
import com.simenko.qmapp.utils.observeAsState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductKindProducts(
    mainScreenPadding: PaddingValues,
    viewModel: ProductListViewModel = hiltViewModel(),
    route: Route.Main.ProductLines.ProductKinds.Products.ProductsList
) {
    val scope = rememberCoroutineScope()
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    val screenWidthPhysical = screenWidth.toFloat().dp()
    val localDensity = LocalDensity.current
    var titleHeightDp by remember { mutableStateOf(0.dp) }
    val screenHeight = configuration.screenHeightDp.dp - mainScreenPadding.calculateTopPadding() - titleHeightDp

    val animator = HorizonteAnimationImp(screenWidth, scope)

    val productKind by viewModel.productKind.collectAsStateWithLifecycle(DomainProductKind.DomainProductKindComplete())
    val isBottomSheetExpanded by viewModel.bottomSheetState.collectAsStateWithLifecycle()
    val isSecondRowVisible by viewModel.isSecondColumnVisible.collectAsStateWithLifecycle(false)
    val listsIsInitialized by viewModel.listsIsInitialized.collectAsStateWithLifecycle(Pair(false, false))

    /**
     * TotalScreenWidth, FirstColumnWidth, SecondColumnWidth
     * */
    var screenSizes: Triple<Dp, Dp, Dp> by remember { mutableStateOf(animator.getRequiredScreenWidth(if (isSecondRowVisible) 1 else 0)) }
    val verticalScrollState = rememberScrollState()
    val horizontalScrollState = rememberScrollState()

    LaunchedEffect(Unit) { viewModel.onEntered(route) }

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

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        if (isBottomSheetExpanded.first) {
            ModalBottomSheet(
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                onDismissRequest = { viewModel.onShowHideBottomSheet(false) }
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    val btnColors = ButtonDefaults.buttonColors(contentColor = MaterialTheme.colorScheme.onPrimary, containerColor = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height((DEFAULT_SPACE * 3).dp))
                    TextButton(
                        colors = btnColors,
                        elevation = ButtonDefaults.buttonElevation(4.dp, 4.dp, 4.dp, 4.dp, 4.dp),
                        modifier = Modifier
                            .width(224.dp)
                            .height(56.dp),
                        onClick = { viewModel.onAddNewProduct(isBottomSheetExpanded.second) }
                    ) { Text(text = "Add new item".uppercase()) }
                    Spacer(modifier = Modifier.height((DEFAULT_SPACE * 3).dp))
                    TextButton(
                        colors = btnColors,
                        elevation = ButtonDefaults.buttonElevation(4.dp, 4.dp, 4.dp, 4.dp, 4.dp),
                        modifier = Modifier
                            .width(224.dp)
                            .height(56.dp),
                        onClick = { viewModel.onSelectExistingProduct(isBottomSheetExpanded.second) }
                    ) { Text(text = "Select existing item".uppercase()) }
                    Spacer(modifier = Modifier.height((DEFAULT_SPACE * 15).dp))
                }
            }
        }
        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start, verticalArrangement = Arrangement.Bottom) {
            Column(modifier = Modifier.onGloballyPositioned {
                titleHeightDp = with(localDensity) { it.size.height.toDp() }
            }) {
                Spacer(modifier = Modifier.height(10.dp))
                InfoLine(modifier = Modifier.padding(start = DEFAULT_SPACE.dp), title = "Product line", body = productKind.productLine.manufacturingProject.projectSubject ?: NoString.str)
                InfoLine(modifier = Modifier.padding(start = DEFAULT_SPACE.dp), title = "Product", body = productKind.productKind.productKindDesignation)
                HorizontalDivider(modifier = Modifier.height(1.dp), color = MaterialTheme.colorScheme.secondary)
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
}