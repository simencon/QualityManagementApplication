package com.simenko.qmapp.ui.main.products.kinds.list.versions

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.simenko.qmapp.domain.EmptyString
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.domain.NoString
import com.simenko.qmapp.domain.ZeroValue
import com.simenko.qmapp.domain.entities.products.DomainItemTolerance
import com.simenko.qmapp.domain.entities.products.DomainMetrix
import com.simenko.qmapp.other.Constants.DEFAULT_SPACE
import com.simenko.qmapp.storage.ScrollStates
import com.simenko.qmapp.ui.common.HeaderWithTitle
import com.simenko.qmapp.ui.common.ItemCard
import com.simenko.qmapp.ui.common.RecordFieldItem
import com.simenko.qmapp.utils.Rounder
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce

@OptIn(FlowPreview::class)
@Composable
fun Tolerances(modifier: Modifier = Modifier, viewModel: VersionTolerancesViewModel = hiltViewModel()) {

    val items by viewModel.tolerances.collectAsStateWithLifecycle(listOf())

    LaunchedEffect(Unit) { viewModel.setIsComposed(3, true) }

    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = viewModel.storage.getLong(ScrollStates.METRICS.indexKey).let { if (it == NoRecord.num) ZeroValue.num else it }.toInt(),
        initialFirstVisibleItemScrollOffset = viewModel.storage.getLong(ScrollStates.METRICS.offsetKey).let { if (it == NoRecord.num) ZeroValue.num else it }.toInt()
    )

    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex }.debounce(500L).collectLatest { index ->
            viewModel.storage.setLong(ScrollStates.METRICS.indexKey, index.toLong())
            viewModel.storage.setLong(ScrollStates.METRICS.offsetKey, listState.firstVisibleItemScrollOffset.toLong())
        }
    }

    LazyColumn(modifier = modifier, state = listState, horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.Center) {
        items(items = items, key = { it.second.id }) { item ->
            MetricCard(
                viewModel = viewModel,
                metric = item.first,
                tolerance = item.second
            )
        }
    }
}

@Composable
fun MetricCard(
    viewModel: VersionTolerancesViewModel,
    metric: DomainMetrix,
    tolerance: DomainItemTolerance,
) {
    val isEditMode by viewModel.versionEditMode.collectAsStateWithLifecycle()
    ItemCard(
        modifier = Modifier.padding(horizontal = (DEFAULT_SPACE / 2).dp, vertical = (DEFAULT_SPACE / 2).dp),
        item = metric,
        contentColors = Triple(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.secondaryContainer, MaterialTheme.colorScheme.outline),
        actionButtonsImages = arrayOf(Icons.Filled.Delete, Icons.Filled.Edit),
    ) {
        Column {
            Metric(metric = metric)
            Tolerance(
                isEditMode = isEditMode,
                tolerance = tolerance,
                viewModel::setLsl,
                viewModel::setNominal,
                viewModel::setUsl
            )
            Spacer(modifier = Modifier.height((DEFAULT_SPACE / 2).dp))
        }
    }
}

@Composable
fun Metric(metric: DomainMetrix) {
    Column(
        modifier = Modifier
            .padding(all = DEFAULT_SPACE.dp)
            .animateContentSize(animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow))
    ) {
        HeaderWithTitle(titleWight = 0.07f, title = metric.metrixOrder.toString(), text = metric.metrixDescription ?: NoString.str)
        Spacer(modifier = Modifier.height(DEFAULT_SPACE.dp))
        HeaderWithTitle(titleWight = 0.50f, title = "Metric designation:", text = metric.metrixDesignation ?: NoString.str)
        Spacer(modifier = Modifier.height(DEFAULT_SPACE.dp))
        HeaderWithTitle(titleWight = 0.50f, title = "Metric unit:", text = metric.units ?: NoString.str)
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun Tolerance(
    isEditMode: Boolean = true,
    tolerance: DomainItemTolerance,
    setLsl: (ID, String) -> Unit = { _, _ -> },
    setNominal: (ID, String) -> Unit = { _, _ -> },
    setUsl: (ID, String) -> Unit = { _, _ -> },
) {
    var lslValue by remember { mutableStateOf(tolerance.lsl?.toDouble()?.let { Rounder.withToleranceStrCustom(it, 2) } ?: EmptyString.str) }
    var nominalValue by remember { mutableStateOf(tolerance.nominal?.toDouble()?.let { Rounder.withToleranceStrCustom(it, 2) } ?: EmptyString.str) }
    var uslValue by remember { mutableStateOf(tolerance.usl?.toDouble()?.let { Rounder.withToleranceStrCustom(it, 2) } ?: EmptyString.str) }

    val keyboardController = LocalSoftwareKeyboardController.current

    val (lslFR) = FocusRequester.createRefs()
    val (nominalFR) = FocusRequester.createRefs()
    val (uslFR) = FocusRequester.createRefs()

    Row {
        Spacer(modifier = Modifier.width(DEFAULT_SPACE.dp))
        RecordFieldItem(
            modifier = Modifier.weight(1f),
            isMandatoryField = false,
            valueParam = Triple(lslValue, tolerance.isLslError) {
                setLsl(tolerance.id, it)
                lslValue = it
            },
            enabled = isEditMode,
            keyboardNavigation = Pair(lslFR) { keyboardController?.hide() },
            keyBoardTypeAction = Pair(KeyboardType.Number, ImeAction.Done),
            contentDescription = Triple(null, "LSL", "LSL"),
            containerColor = Color.White
        )
        Spacer(modifier = Modifier.width(DEFAULT_SPACE.dp))
        RecordFieldItem(
            modifier = Modifier.weight(1f),
            valueParam = Triple(nominalValue, tolerance.isNominalError) {
                setNominal(tolerance.id, it)
                nominalValue = it
            },
            enabled = isEditMode,
            keyboardNavigation = Pair(nominalFR) { keyboardController?.hide() },
            keyBoardTypeAction = Pair(KeyboardType.Number, ImeAction.Done),
            contentDescription = Triple(null, "Nominal", "Nominal"),
            containerColor = Color.White
        )
        Spacer(modifier = Modifier.width(DEFAULT_SPACE.dp))
        RecordFieldItem(
            modifier = Modifier.weight(1f),
            isMandatoryField = false,
            valueParam = Triple(uslValue, tolerance.isUslError) {
                setUsl(tolerance.id, it)
                uslValue = it
            },
            enabled = isEditMode,
            keyboardNavigation = Pair(uslFR) { keyboardController?.hide() },
            keyBoardTypeAction = Pair(KeyboardType.Number, ImeAction.Done),
            contentDescription = Triple(null, "USL", "USL"),
            containerColor = Color.White
        )
        Spacer(modifier = Modifier.width(DEFAULT_SPACE.dp))
    }
}