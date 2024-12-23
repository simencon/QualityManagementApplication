package com.simenko.qmapp.presentation.ui.main.products.characteristics

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.domain.NoString
import com.simenko.qmapp.domain.SelectedNumber
import com.simenko.qmapp.domain.entities.products.DomainMetrix
import com.simenko.qmapp.other.Constants.DEFAULT_SPACE
import com.simenko.qmapp.presentation.ui.common.HeaderWithTitle
import com.simenko.qmapp.presentation.ui.common.ItemCard
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce

@OptIn(FlowPreview::class)
@Composable
fun Metrics(modifier: Modifier = Modifier, viewModel: CharacteristicsViewModel = hiltViewModel()) {

    val items by viewModel.metrics.collectAsStateWithLifecycle(listOf())

    val onClickActionsLambda = remember<(ID) -> Unit> { { viewModel.setMetricsVisibility(aId = SelectedNumber(it)) } }
    val onClickDeleteLambda = remember<(ID) -> Unit> { { viewModel.onDeleteMetricClick(it) } }
    val onClickEditLambda = remember<(Pair<ID, ID>) -> Unit> { { viewModel.onEditMetricClick(it) } }

    LaunchedEffect(Unit) { viewModel.setIsComposed(3, true) }

    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = viewModel.scrollStatesPrefs.metricList.first.toInt(),
        initialFirstVisibleItemScrollOffset = viewModel.scrollStatesPrefs.metricList.second.toInt()
    )

    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex }.debounce(500L).collectLatest { index ->
            viewModel.scrollStatesPrefs.metricList = index.toLong() to listState.firstVisibleItemScrollOffset.toLong()
        }
    }

    LazyColumn(modifier = modifier, state = listState, horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.Center) {
        items(items = items, key = { it.id }) { item ->
            MetricCard(
                metric = item,
                onClickActions = { onClickActionsLambda(it) },
                onClickDelete = { onClickDeleteLambda(it) },
                onClickEdit = { onClickEditLambda(it) }
            )
        }
    }
}

@Composable
fun MetricCard(
    metric: DomainMetrix,
    onClickActions: (ID) -> Unit,
    onClickDelete: (ID) -> Unit,
    onClickEdit: (Pair<ID, ID>) -> Unit
) {
    ItemCard(
        modifier = Modifier.padding(horizontal = (DEFAULT_SPACE / 2).dp, vertical = (DEFAULT_SPACE / 2).dp),
        item = metric,
        onClickActions = onClickActions,
        onClickDelete = onClickDelete,
        onClickEdit = onClickEdit,
        contentColors = Triple(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.secondaryContainer, MaterialTheme.colorScheme.outline),
        actionButtonsImages = arrayOf(Icons.Filled.Delete, Icons.Filled.Edit),
    ) {
        Metric(metric = metric)
    }
}

@Composable
fun Metric(metric: DomainMetrix) {
    Column(
        modifier = Modifier
            .padding(all = DEFAULT_SPACE.dp)
            .animateContentSize(animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow))
    ) {
        HeaderWithTitle(titleWight = 0.07f, title = metric.metrixOrder.toString(), text = metric.metrixDescription?: NoString.str)
        Spacer(modifier = Modifier.height(DEFAULT_SPACE.dp))
        HeaderWithTitle(titleWight = 0.50f, title = "Metric designation:", text = metric.metrixDesignation ?: NoString.str)
        Spacer(modifier = Modifier.height(DEFAULT_SPACE.dp))
        HeaderWithTitle(titleWight = 0.50f, title = "Metric unit:", text = metric.units ?: NoString.str)
    }
}