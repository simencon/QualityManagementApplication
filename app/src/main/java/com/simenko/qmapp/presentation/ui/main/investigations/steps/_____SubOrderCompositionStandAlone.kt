package com.simenko.qmapp.presentation.ui.main.investigations.steps

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.simenko.qmapp.domain.*
import com.simenko.qmapp.domain.entities.DomainSubOrderComplete
import com.simenko.qmapp.presentation.ui.dialogs.scrollToSelectedItem
import com.simenko.qmapp.presentation.ui.main.investigations.InvestigationsViewModel
import com.simenko.qmapp.utils.BaseFilter
import com.simenko.qmapp.utils.observeAsState
import kotlinx.coroutines.*

@Composable
fun SubOrdersStandAlone(
    modifier: Modifier = Modifier,
    viewModel: InvestigationsViewModel = hiltViewModel()
) {
    val scrollToRecord by viewModel.scrollToRecord.collectAsStateWithLifecycle(null)
    val items by viewModel.subOrdersSF.collectAsStateWithLifecycle(listOf())

    LaunchedEffect(Unit) {
        viewModel.setSubOrdersFilter(BaseFilter(typeId = ProcessControlOrderTypeId.num))
    }

    val onClickDetailsLambda = remember<(ID) -> Unit> { { viewModel.setSubOrdersVisibility(dId = SelectedNumber(it)) } }
    val onClickActionsLambda = remember<(ID) -> Unit> { { viewModel.setSubOrdersVisibility(aId = SelectedNumber(it)) } }
    val onClickDeleteLambda = remember<(ID) -> Unit> { { viewModel.onDeleteSubOrderClick(it) } }
    val onClickEditLambda = remember<(Pair<ID, ID>) -> Unit> { { viewModel.onEditProcessControlClick(it) } }
    val onClickStatusLambda = remember<(DomainSubOrderComplete, ID?) -> Unit> { { so, completedById -> viewModel.showStatusUpdateDialog(currentSubOrder = so, performerId = completedById) } }

    val lifecycleState = LocalLifecycleOwner.current.lifecycle.observeAsState()

    LaunchedEffect(lifecycleState.value) {
        when (lifecycleState.value) {
            Lifecycle.Event.ON_RESUME -> viewModel.setIsComposed(true)
            Lifecycle.Event.ON_STOP -> viewModel.setIsComposed(false)
            else -> {}
        }
    }

    val listState = rememberLazyListState()
    LaunchedEffect(scrollToRecord) {
        scrollToRecord?.let { record ->
            record.second.getContentIfNotHandled()?.let { subOrderId ->
                viewModel.channel.trySend(
                    this.launch {
                        listState.scrollToSelectedItem(list = items.map { it.subOrder.id }.toList(), selectedId = subOrderId)
                    }
                )
            }
        }
    }

    val lastItemIsVisible by remember { derivedStateOf { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index == listState.layoutInfo.totalItemsCount - 1 } }
    LaunchedEffect(lastItemIsVisible) {
        if (lastItemIsVisible) viewModel.mainPageHandler?.onListEnd?.invoke(true) else viewModel.mainPageHandler?.onListEnd?.invoke(false)
    }

    val lastVisibleItemKey by remember { derivedStateOf { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.key?.toString()?.toLongOrNull() } }
    LaunchedEffect(listState.isScrollInProgress) {
        if (!listState.isScrollInProgress) lastVisibleItemKey?.let { viewModel.setLastVisibleItemKey(it) }
    }

    LazyColumn(
        modifier = modifier,
        state = listState
    ) {
        items(items = items, key = { it.subOrder.id }) { subOrder ->
            SubOrderCard(
                invModel = viewModel,
                processControlOnly = true,
                subOrder = subOrder,
                onClickDetails = { onClickDetailsLambda(it) },
                onClickActions = { onClickActionsLambda(it) },
                onClickDelete = { onClickDeleteLambda(it) },
                onClickEdit = { onClickEditLambda(it) },
                onClickStatus = { subOrderComplete, completedById -> onClickStatusLambda(subOrderComplete, completedById) }
            )
        }
    }
}