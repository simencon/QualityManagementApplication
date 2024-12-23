package com.simenko.qmapp.presentation.ui.main.structure.steps

import android.annotation.SuppressLint
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.NavigateNext
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.simenko.qmapp.R
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.domain.SelectedNumber
import com.simenko.qmapp.domain.entities.DomainManufacturingLine
import com.simenko.qmapp.other.Constants.BOTTOM_ITEM_HEIGHT
import com.simenko.qmapp.other.Constants.DEFAULT_SPACE
import com.simenko.qmapp.presentation.ui.common.ContentWithTitle
import com.simenko.qmapp.presentation.ui.common.HeaderWithTitle
import com.simenko.qmapp.presentation.ui.common.ItemCard
import com.simenko.qmapp.presentation.ui.common.StatusChangeBtn
import com.simenko.qmapp.presentation.ui.main.structure.CompanyStructureViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce

@OptIn(FlowPreview::class)
@Composable
fun Lines(modifier: Modifier = Modifier, viewModel: CompanyStructureViewModel = hiltViewModel()) {

    val items by viewModel.lines.collectAsStateWithLifecycle(listOf())

    val onClickDetailsLambda = remember<(ID) -> Unit> { { viewModel.setLinesVisibility(dId = SelectedNumber(it)) } }
    val onClickActionsLambda = remember<(ID) -> Unit> { { viewModel.setLinesVisibility(aId = SelectedNumber(it)) } }
    val onClickDeleteLambda = remember<(ID) -> Unit> { { viewModel.onDeleteLineClick(it) } }
    val onClickEditLambda = remember<(Pair<ID, ID>) -> Unit> { { viewModel.onEditLineClick(it) } }
    val onClickProductsLambda = remember<(Pair<ID, ID>) -> Unit> { { viewModel.onLineProductsClick(it) } }

    LaunchedEffect(Unit) { viewModel.setIsComposed(3, true) }

    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = viewModel.scrollStatesPrefs.linesList.first.toInt(),
        initialFirstVisibleItemScrollOffset = viewModel.scrollStatesPrefs.linesList.second.toInt()
    )

    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex }.debounce(500L).collectLatest { index ->
            viewModel.scrollStatesPrefs.linesList = index.toLong() to listState.firstVisibleItemScrollOffset.toLong()
        }
    }

    LazyColumn(modifier = modifier, state = listState, horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.Center) {
        items(items = items, key = { it.id }) { line ->
            LineCard(
                viewModel = viewModel,
                line = line,
                onClickActions = { onClickActionsLambda(it) },
                onClickDelete = { onClickDeleteLambda(it) },
                onClickEdit = { onClickEditLambda(it) },
                onClickDetails = { onClickDetailsLambda(it) },
                onClickProducts = { onClickProductsLambda(it) }
            )
        }
        if (items.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(BOTTOM_ITEM_HEIGHT.dp))
            }
        }
    }
}

@SuppressLint("UnusedTransitionTargetStateParameter")
@Composable
fun LineCard(
    viewModel: CompanyStructureViewModel,
    line: DomainManufacturingLine,
    onClickActions: (ID) -> Unit,
    onClickDelete: (ID) -> Unit,
    onClickEdit: (Pair<ID, ID>) -> Unit,
    onClickDetails: (ID) -> Unit,
    onClickProducts: (Pair<ID, ID>) -> Unit
) {
    ItemCard(
        modifier = Modifier.padding(horizontal = (DEFAULT_SPACE / 2).dp, vertical = (DEFAULT_SPACE / 2).dp),
        item = line,
        onClickActions = onClickActions,
        onClickDelete = onClickDelete,
        onClickEdit = onClickEdit,
        contentColors = Triple(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.secondaryContainer, MaterialTheme.colorScheme.outline),
        actionButtonsImages = arrayOf(Icons.Filled.Delete, Icons.Filled.Edit),
    ) {
        Line(
            viewModel = viewModel,
            line = line,
            onClickDetails = { onClickDetails(it) },
            onClickProducts = { onClickProducts(it) }
        )
    }
}

@Composable
fun Line(
    viewModel: CompanyStructureViewModel,
    line: DomainManufacturingLine,
    onClickDetails: (ID) -> Unit = {},
    onClickProducts: (Pair<ID, ID>) -> Unit
) {
    Column(modifier = Modifier.animateContentSize(animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow))) {
        Row(modifier = Modifier.padding(all = DEFAULT_SPACE.dp), verticalAlignment = Alignment.Top) {
            Column(modifier = Modifier.weight(0.90f)) {
                HeaderWithTitle(titleFirst = false, titleWight = 0f, text = line.lineOrder.toString())
                Spacer(modifier = Modifier.height(DEFAULT_SPACE.dp))
                HeaderWithTitle(titleWight = 0.18f, title = "Line:", text = line.lineAbbr)
            }
            IconButton(modifier = Modifier.weight(weight = 0.10f), onClick = { onClickDetails(line.id) }) {
                Icon(
                    imageVector = if (line.detailsVisibility) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = if (line.detailsVisibility) stringResource(R.string.show_less) else stringResource(R.string.show_more)
                )
            }
        }
        LineDetails(viewModel = viewModel, line = line, onClickProducts = onClickProducts)
    }
}

@Composable
fun LineDetails(
    viewModel: CompanyStructureViewModel,
    line: DomainManufacturingLine,
    onClickProducts: (Pair<ID, ID>) -> Unit
) {
    val containerColor = when (line.isExpanded) {
        true -> MaterialTheme.colorScheme.secondaryContainer
        false -> MaterialTheme.colorScheme.surfaceVariant
    }

    if (line.detailsVisibility) {
        Column(modifier = Modifier.padding(start = DEFAULT_SPACE.dp, top = 0.dp, end = DEFAULT_SPACE.dp, bottom = DEFAULT_SPACE.dp)) {
            HorizontalDivider(modifier = Modifier.height(1.dp), color = MaterialTheme.colorScheme.secondary)
            Spacer(modifier = Modifier.height(DEFAULT_SPACE.dp))
            ContentWithTitle(title = "Complete name:", value = line.lineDesignation, titleWight = 0.23f)

            Spacer(modifier = Modifier.height(DEFAULT_SPACE.dp))
            Row(modifier = Modifier.fillMaxSize()) {
                Spacer(modifier = Modifier.weight(0.30f))
                Column(modifier = Modifier.weight(0.70f)) {
                    StatusChangeBtn(
                        modifier = Modifier.fillMaxWidth(), containerColor = containerColor, onClick = { onClickProducts(line.chId to line.id) }) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "Product items", style = MaterialTheme.typography.titleSmall.copy(fontSize = 14.sp), maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Icon(imageVector = Icons.AutoMirrored.Filled.NavigateNext, contentDescription = "Product items")
                        }
                    }
                }
            }
        }
        Operations(viewModel = viewModel)
    }
}