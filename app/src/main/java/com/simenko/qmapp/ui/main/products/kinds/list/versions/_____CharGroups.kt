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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.FloatingActionButton
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
    viewModel: VersionTolerancesViewModel = hiltViewModel()
) {
    val itemVersion by viewModel.itemVersion.collectAsStateWithLifecycle()
    val items by viewModel.characteristicGroups.collectAsStateWithLifecycle(listOf())

    val onClickDetailsLambda = remember<(ID) -> Unit> { { viewModel.setCharGroupsVisibility(dId = SelectedNumber(it)) } }
    val onClickAddLambda = remember<(String) -> Unit> { { viewModel.addCharacteristic(it) } }

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
        items(items = items, key = { it.id }) { item ->
            CharGroupCard(
                viewModel = viewModel,
                charGroup = item,
                onClickDetails = { onClickDetailsLambda(it) },
            )
        }
        item {
            Spacer(modifier = Modifier.height(0.dp))
            FloatingActionButton(
                modifier = Modifier.padding(top = (DEFAULT_SPACE / 2).dp, end = DEFAULT_SPACE.dp, bottom = DEFAULT_SPACE.dp),
                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                onClick = { onClickAddLambda(itemVersion.itemVersion.fId) },
                content = { Icon(imageVector = Icons.Default.Add, contentDescription = "Add sub order") }
            )
        }
    }
}

@Composable
fun CharGroupCard(
    viewModel: VersionTolerancesViewModel,
    charGroup: DomainCharGroup,
    onClickDetails: (ID) -> Unit
) {
    ItemCard(
        modifier = Modifier.padding(horizontal = (DEFAULT_SPACE / 2).dp, vertical = (DEFAULT_SPACE / 2).dp),
        item = charGroup,
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
    viewModel: VersionTolerancesViewModel = hiltViewModel(),
    charGroup: DomainCharGroup,
    onClickDetails: (ID) -> Unit
) {
    Column(modifier = Modifier.animateContentSize(animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow))) {
        Row(modifier = Modifier.padding(all = DEFAULT_SPACE.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(0.72f)) {
                HeaderWithTitle(titleWight = 0.37f, title = "Characteristics group:", text = charGroup.ishElement ?: NoString.str)
            }
            IconButton(modifier = Modifier.weight(weight = 0.10f), onClick = { onClickDetails(charGroup.id) }) {
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
    viewModel: VersionTolerancesViewModel,
    charGroup: DomainCharGroup
) {
    if (charGroup.detailsVisibility) {
        CharSubGroups(viewModel = viewModel)
        Spacer(modifier = Modifier.height((DEFAULT_SPACE / 2).dp))
    }
}