package com.simenko.qmapp.ui.main.products.keys

import android.annotation.SuppressLint
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateFloat
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.simenko.qmapp.domain.entities.products.DomainKey
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.domain.NoString
import com.simenko.qmapp.domain.SelectedNumber
import com.simenko.qmapp.other.Constants
import com.simenko.qmapp.other.Constants.DEFAULT_SPACE
import com.simenko.qmapp.ui.common.ContentWithTitle
import com.simenko.qmapp.ui.common.HeaderWithTitle
import com.simenko.qmapp.ui.common.InfoLine
import com.simenko.qmapp.utils.dp
import kotlin.math.roundToInt

@Composable
fun ProductLineKeys(
    modifier: Modifier = Modifier,
    viewModel: ProductLineKeysViewModel = hiltViewModel()
) {
    val productLine by viewModel.productLine.collectAsStateWithLifecycle()
    val items by viewModel.productKeys.collectAsStateWithLifecycle(listOf())

    val onClickActionsLambda = remember<(Int) -> Unit> { { viewModel.setProductKeysVisibility(aId = SelectedNumber(it)) } }
    val onClickDeleteLambda = remember<(Int) -> Unit> { { viewModel.onDeleteProductLineClick(it) } }
    val onClickEditLambda = remember<(Pair<Int, Int>) -> Unit> { { viewModel.onEditProductLineClick(it) } }

    LaunchedEffect(Unit) { viewModel.mainPageHandler.setupMainPage(0, true) }

    val listState = rememberLazyListState()

    Column(horizontalAlignment = Alignment.Start, verticalArrangement = Arrangement.Bottom) {
        Spacer(modifier = Modifier.height(10.dp))
        InfoLine(modifier = modifier.padding(start = DEFAULT_SPACE.dp), title = "Product line", body = productLine.projectSubject ?: NoString.str)
        Divider(modifier = Modifier.height(1.dp), color = MaterialTheme.colorScheme.secondary)
        LazyColumn(modifier = modifier, state = listState, horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.Center) {
            items(items = items, key = { it.productLineKey.id }) { key ->
                KeyCard(
                    key = key,
                    onClickActions = { onClickActionsLambda(it) },
                    onClickDelete = { onClickDeleteLambda(it) },
                    onClickEdit = { onClickEditLambda(it) },
                )
            }
        }
    }
}

@SuppressLint("UnusedTransitionTargetStateParameter")
@Composable
fun KeyCard(
    modifier: Modifier = Modifier,
    key: DomainKey.DomainKeyComplete,
    onClickActions: (Int) -> Unit,
    onClickDelete: (Int) -> Unit,
    onClickEdit: (Pair<Int, Int>) -> Unit
) {
    val transitionState = remember { MutableTransitionState(key.isExpanded).apply { targetState = !key.isExpanded } }
    val transition = updateTransition(transitionState, "cardTransition")

    val offsetTransition by transition.animateFloat(
        label = "cardOffsetTransition",
        transitionSpec = { tween(durationMillis = Constants.ANIMATION_DURATION) },
        targetValueByState = { (if (key.isExpanded) Constants.CARD_OFFSET * 2 else 0f).dp() },
    )

    val containerColor = when (key.isExpanded) {
        true -> MaterialTheme.colorScheme.secondaryContainer
        false -> MaterialTheme.colorScheme.surfaceVariant
    }

    val borderColor = when (key.detailsVisibility) {
        true -> MaterialTheme.colorScheme.outline
        false -> when (key.isExpanded) {
            true -> MaterialTheme.colorScheme.secondaryContainer
            false -> MaterialTheme.colorScheme.surfaceVariant
        }
    }

    Box(Modifier.fillMaxWidth()) {
        Row(Modifier.padding(all = (DEFAULT_SPACE / 2).dp)) {
            IconButton(
                modifier = Modifier.size(Constants.ACTION_ITEM_SIZE.dp),
                onClick = { onClickDelete(key.productLineKey.id) },
                content = { Icon(imageVector = Icons.Filled.Delete, contentDescription = "delete action") }
            )

            IconButton(
                modifier = Modifier.size(Constants.ACTION_ITEM_SIZE.dp),
                onClick = { onClickEdit(Pair(key.productLineKey.projectId ?: NoRecord.num, key.productLineKey.id)) },
                content = { Icon(imageVector = Icons.Filled.Edit, contentDescription = "edit action") }
            )
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = containerColor),
            border = BorderStroke(width = 1.dp, borderColor),
            elevation = CardDefaults.cardElevation(4.dp),
            modifier = modifier
                .padding(horizontal = (DEFAULT_SPACE / 2).dp, vertical = (DEFAULT_SPACE / 2).dp)
                .fillMaxWidth()
                .offset { IntOffset(offsetTransition.roundToInt(), 0) }
                .pointerInput(key.productLineKey.id) { detectTapGestures(onDoubleTap = { onClickActions(key.productLineKey.id) }) }
        ) {
            Key(key = key)
        }
    }
}

@Composable
fun Key(
    key: DomainKey.DomainKeyComplete,
) {
    Column(modifier = Modifier.padding(all = DEFAULT_SPACE.dp)) {
        HeaderWithTitle(titleWight = 0.18f, title = "Key:", text = key.productLineKey.componentKey ?: NoString.str)
        Spacer(modifier = Modifier.height(DEFAULT_SPACE.dp))
        ContentWithTitle(titleWight = 0.18f, title = "Description:", value = key.productLineKey.componentKeyDescription ?: NoString.str)
    }
}