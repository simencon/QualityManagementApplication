package com.simenko.qmapp.ui.main.structure.steps

import android.annotation.SuppressLint
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.simenko.qmapp.R
import com.simenko.qmapp.domain.SelectedNumber
import com.simenko.qmapp.domain.entities.DomainManufacturingLine
import com.simenko.qmapp.other.Constants
import com.simenko.qmapp.other.Constants.DEFAULT_SPACE
import com.simenko.qmapp.ui.common.ContentWithTitle
import com.simenko.qmapp.ui.common.HeaderWithTitle
import com.simenko.qmapp.ui.common.StatusChangeBtn
import com.simenko.qmapp.ui.dialogs.scrollToSelectedItem
import com.simenko.qmapp.ui.main.structure.CompanyStructureViewModel
import com.simenko.qmapp.utils.dp
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun Lines(modifier: Modifier = Modifier, viewModel: CompanyStructureViewModel = hiltViewModel()) {

    val channelVisibility by viewModel.channelsVisibility.collectAsStateWithLifecycle()
    val items by viewModel.lines.collectAsStateWithLifecycle(listOf())
    val scrollToRecord by viewModel.scrollToRecord.collectAsStateWithLifecycle(null)

    val onClickDetailsLambda = remember<(Int) -> Unit> { { viewModel.setLinesVisibility(dId = SelectedNumber(it)) } }
    val onClickActionsLambda = remember<(Int) -> Unit> { { viewModel.setLinesVisibility(aId = SelectedNumber(it)) } }
    val onClickDeleteLambda = remember<(Int) -> Unit> { { viewModel.onDeleteLineClick(it) } }
    val onClickAddLambda = remember<(Int) -> Unit> { { viewModel.onAddLineClick(it) } }
    val onClickEditLambda = remember<(Pair<Int, Int>) -> Unit> { { viewModel.onEditLineClick(it) } }
    val onClickProductsLambda = remember<(Int) -> Unit> { { viewModel.onLineProductsClick(it) } }

    val listState = rememberLazyListState()
    LaunchedEffect(scrollToRecord) {
        scrollToRecord?.let { record ->
            record.lineId.getContentIfNotHandled()?.let { lineId ->
                viewModel.channel.trySend(this.launch { listState.scrollToSelectedItem(list = items.map { it.id }.toList(), selectedId = lineId) })
            }
        }
    }

    LazyColumn(modifier = modifier, state = listState, horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.Center) {
        items(items = items, key = { it.id }) { line ->
            LineCard(
                viewModel = viewModel,
                line = line,
                onClickDetails = { onClickDetailsLambda(it) },
                onClickActions = { onClickActionsLambda(it) },
                onClickDelete = { onClickDeleteLambda(it) },
                onClickEdit = { onClickEditLambda(it) },
                onClickProducts = { onClickProductsLambda(it) }
            )
        }
        item {
            Divider(modifier = Modifier.height(0.dp))
            FloatingActionButton(
                modifier = Modifier.padding(top = (DEFAULT_SPACE / 2).dp, end = (DEFAULT_SPACE / 2).dp, bottom = DEFAULT_SPACE.dp),
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                onClick = { onClickAddLambda(channelVisibility.first.num) },
                content = { Icon(imageVector = Icons.Default.Add, contentDescription = "Add sub order") }
            )
            Spacer(modifier = Modifier.height((Constants.FAB_HEIGHT).dp))
        }
    }
}

@SuppressLint("UnusedTransitionTargetStateParameter")
@Composable
fun LineCard(
    viewModel: CompanyStructureViewModel,
    line: DomainManufacturingLine,
    onClickDetails: (Int) -> Unit,
    onClickActions: (Int) -> Unit,
    onClickDelete: (Int) -> Unit,
    onClickEdit: (Pair<Int, Int>) -> Unit,
    onClickProducts: (Int) -> Unit
) {
    val transitionState = remember { MutableTransitionState(line.isExpanded).apply { targetState = !line.isExpanded } }
    val transition = updateTransition(transitionState, "cardTransition")

    val offsetTransition by transition.animateFloat(
        label = "cardOffsetTransition",
        transitionSpec = { tween(durationMillis = Constants.ANIMATION_DURATION) },
        targetValueByState = { (if (line.isExpanded) Constants.CARD_OFFSET * 2 else 0f).dp() },
    )

    val containerColor = when (line.isExpanded) {
        true -> MaterialTheme.colorScheme.secondaryContainer
        false -> MaterialTheme.colorScheme.surfaceVariant
    }

    val borderColor = when (line.detailsVisibility) {
        true -> MaterialTheme.colorScheme.outline
        false -> when (line.isExpanded) {
            true -> MaterialTheme.colorScheme.secondaryContainer
            false -> MaterialTheme.colorScheme.surfaceVariant
        }
    }

    Box(Modifier.fillMaxWidth()) {
        Row(Modifier.padding(all = (DEFAULT_SPACE / 2).dp)) {
            IconButton(
                modifier = Modifier.size(Constants.ACTION_ITEM_SIZE.dp),
                onClick = { onClickDelete(line.id) },
                content = { Icon(imageVector = Icons.Filled.Delete, contentDescription = "delete action") }
            )
            IconButton(
                modifier = Modifier.size(Constants.ACTION_ITEM_SIZE.dp),
                onClick = { onClickEdit(Pair(line.chId, line.id)) },
                content = { Icon(imageVector = Icons.Filled.Edit, contentDescription = "edit action") },
            )
        }
        Card(
            colors = CardDefaults.cardColors(containerColor = containerColor),
            border = BorderStroke(width = 1.dp, borderColor),
            elevation = CardDefaults.cardElevation(4.dp),
            modifier = Modifier
                .padding(horizontal = (DEFAULT_SPACE / 2).dp, vertical = (DEFAULT_SPACE / 2).dp)
                .fillMaxWidth()
                .offset { IntOffset(offsetTransition.roundToInt(), 0) }
                .pointerInput(line.id) { detectTapGestures(onDoubleTap = { onClickActions(line.id) }) }
        ) {
            Line(
                viewModel = viewModel,
                line = line,
                onClickDetails = { onClickDetails(it) },
                onClickProducts = { onClickProducts(it) }
            )
        }
    }
}

@Composable
fun Line(
    viewModel: CompanyStructureViewModel,
    line: DomainManufacturingLine,
    onClickDetails: (Int) -> Unit = {},
    onClickProducts: (Int) -> Unit
) {
    val containerColor = when (line.isExpanded) {
        true -> MaterialTheme.colorScheme.secondaryContainer
        false -> MaterialTheme.colorScheme.surfaceVariant
    }

    Column(modifier = Modifier.animateContentSize(animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow))) {
        Row(modifier = Modifier.padding(all = DEFAULT_SPACE.dp), verticalAlignment = Alignment.Top) {
            Column(modifier = Modifier.weight(0.61f)) {
                HeaderWithTitle(titleFirst = false, titleWight = 0f, text = line.lineOrder.toString())
                Spacer(modifier = Modifier.height(DEFAULT_SPACE.dp))
                HeaderWithTitle(titleWight = 0.18f, title = "Line:", text = line.lineAbbr)
            }
            StatusChangeBtn(modifier = Modifier.weight(weight = 0.29f), containerColor = containerColor, onClick = { onClickProducts(line.id) }) {
                Text(
                    text = "Products",
                    style = MaterialTheme.typography.titleSmall.copy(fontSize = 14.sp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            IconButton(modifier = Modifier.weight(weight = 0.10f), onClick = { onClickDetails(line.id) }) {
                Icon(
                    imageVector = if (line.detailsVisibility) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = if (line.detailsVisibility) stringResource(R.string.show_less) else stringResource(R.string.show_more)
                )
            }
        }
        LineDetails(viewModel = viewModel, line = line)
    }
}

@Composable
fun LineDetails(
    viewModel: CompanyStructureViewModel,
    line: DomainManufacturingLine
) {
    if (line.detailsVisibility) {
        Column(modifier = Modifier.padding(start = DEFAULT_SPACE.dp, top = 0.dp, end = DEFAULT_SPACE.dp, bottom = DEFAULT_SPACE.dp)) {
            Divider(modifier = Modifier.height(1.dp), color = MaterialTheme.colorScheme.secondary)
            Spacer(modifier = Modifier.height(DEFAULT_SPACE.dp))
            ContentWithTitle(title = "Comp. name:", value = line.lineDesignation, titleWight = 0.23f)
        }
        Operations(viewModel = viewModel)
    }
}