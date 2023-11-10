package com.simenko.qmapp.ui.main.products.characteristics

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.simenko.qmapp.R
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.domain.NoString
import com.simenko.qmapp.domain.SelectedNumber
import com.simenko.qmapp.domain.ZeroValue
import com.simenko.qmapp.domain.entities.products.DomainCharGroup
import com.simenko.qmapp.other.Constants.DEFAULT_SPACE
import com.simenko.qmapp.storage.ScrollStates
import com.simenko.qmapp.ui.common.HeaderWithTitle
import com.simenko.qmapp.ui.common.ItemCard
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce

@OptIn(FlowPreview::class)
@Composable
fun CharGroups(
    modifier: Modifier = Modifier,
    viewModel: CharacteristicsViewModel = hiltViewModel()
) {
    val items by viewModel.charGroups.collectAsStateWithLifecycle(listOf())

    val onClickDetailsLambda = remember<(ID) -> Unit> { { viewModel.setGroupsVisibility(dId = SelectedNumber(it)) } }
    val onClickActionsLambda = remember<(ID) -> Unit> { { viewModel.setGroupsVisibility(aId = SelectedNumber(it)) } }
    val onClickDeleteLambda = remember<(ID) -> Unit> { { viewModel.onDeleteCharGroupClick(it) } }
    val onClickEditLambda = remember<(Pair<ID, ID>) -> Unit> { { viewModel.onEditCharGroupClick(it) } }

    LaunchedEffect(Unit) { viewModel.setIsComposed(0, true) }

    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = viewModel.storage.getLong(ScrollStates.CHAR_GROUPS.indexKey).let { if (it == NoRecord.num) ZeroValue.num else it }.toInt(),
        initialFirstVisibleItemScrollOffset = viewModel.storage.getLong(ScrollStates.CHAR_GROUPS.offsetKey).let { if (it == NoRecord.num) ZeroValue.num else it }.toInt()
    )

    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex }.debounce(500L).collectLatest { index ->
            viewModel.storage.setLong(ScrollStates.CHAR_GROUPS.indexKey, index.toLong())
            viewModel.storage.setLong(ScrollStates.CHAR_GROUPS.offsetKey, listState.firstVisibleItemScrollOffset.toLong())
        }
    }

    LazyColumn(modifier = modifier, state = listState, horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.Center) {
        items(items = items, key = { it.charGroup.id }) { department ->
            CharGroupCard(
                viewModel = viewModel,
                charGroup = department,
                onClickDetails = { onClickDetailsLambda(it) },
                onClickActions = { onClickActionsLambda(it) },
                onClickDelete = { onClickDeleteLambda(it) },
                onClickEdit = { onClickEditLambda(it) }
            )
        }
    }
}

@Composable
fun CharGroupCard(
    viewModel: CharacteristicsViewModel,
    charGroup: DomainCharGroup.DomainCharGroupComplete,
    onClickDetails: (ID) -> Unit,
    onClickActions: (ID) -> Unit,
    onClickDelete: (ID) -> Unit,
    onClickEdit: (Pair<ID, ID>) -> Unit
) {
    ItemCard(
        modifier = Modifier.padding(horizontal = (DEFAULT_SPACE / 2).dp, vertical = (DEFAULT_SPACE / 2).dp),
        item = charGroup,
        onClickActions = onClickActions,
        onClickDelete = onClickDelete,
        onClickEdit = onClickEdit,
        contentColors = Triple(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.secondaryContainer, MaterialTheme.colorScheme.outline),
        actionButtonsImages = arrayOf(Icons.Filled.Delete, Icons.Filled.Edit),
    ) {
        CharGroup(
            viewModel = viewModel,
            charGroup = charGroup,
            onClickDetails = { onClickDetails(it) }
        )
    }
}

@Composable
fun CharGroup(
    viewModel: CharacteristicsViewModel = hiltViewModel(),
    charGroup: DomainCharGroup.DomainCharGroupComplete,
    onClickDetails: (ID) -> Unit
) {
    Column(modifier = Modifier.animateContentSize(animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow))) {
        Row(modifier = Modifier.padding(all = DEFAULT_SPACE.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(0.72f)) {
                HeaderWithTitle(titleWight = 0.37f, title = "Characteristics group:", text = charGroup.charGroup.ishElement ?: NoString.str)
            }
            IconButton(modifier = Modifier.weight(weight = 0.10f), onClick = { onClickDetails(charGroup.charGroup.id) }) {
                Icon(
                    imageVector = if (charGroup.detailsVisibility) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = if (charGroup.detailsVisibility) stringResource(R.string.show_less) else stringResource(R.string.show_more)
                )
            }
        }
        CharGroupDetails(viewModel = viewModel, charGroup = charGroup)
    }
}

@Composable
fun CharGroupDetails(
    viewModel: CharacteristicsViewModel,
    charGroup: DomainCharGroup.DomainCharGroupComplete
) {
    if (charGroup.detailsVisibility) {
//        CharSubGroups(viewModel = viewModel)
    }
}