package com.simenko.qmapp.presentation.ui.main.structure.products_to_manufacturing.characteristics_operation

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
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.simenko.qmapp.R
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.domain.NoString
import com.simenko.qmapp.domain.SelectedNumber
import com.simenko.qmapp.domain.entities.products.DomainCharGroup
import com.simenko.qmapp.other.Constants.BOTTOM_ITEM_HEIGHT
import com.simenko.qmapp.other.Constants.DEFAULT_SPACE
import com.simenko.qmapp.presentation.ui.common.HeaderWithTitle
import com.simenko.qmapp.presentation.ui.common.ItemCard

@Composable
fun CharGroups(
    modifier: Modifier = Modifier,
    viewModel: OperationCharacteristicsViewModel = hiltViewModel()
) {
    val items by viewModel.charGroups.collectAsStateWithLifecycle(listOf())
    val onClickDetailsLambda = remember<(ID) -> Unit> { { viewModel.setGroupsVisibility(dId = SelectedNumber(it)) } }

    val listState = rememberLazyListState()

    LazyColumn(modifier = modifier, state = listState, horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.Center) {
        items(items = items, key = { it.id }) { item ->
            CharGroupCard(
                viewModel = viewModel,
                charGroup = item,
                onClickDetails = { onClickDetailsLambda(it) }
            )
        }
        if (items.isNotEmpty()) {
            item { Spacer(modifier = Modifier.height(BOTTOM_ITEM_HEIGHT.dp)) }
        }
    }
}

@Composable
fun CharGroupCard(
    viewModel: OperationCharacteristicsViewModel,
    charGroup: DomainCharGroup,
    onClickDetails: (ID) -> Unit
) {
    ItemCard(
        modifier = Modifier.padding(horizontal = (DEFAULT_SPACE / 2).dp, vertical = (DEFAULT_SPACE / 2).dp),
        item = charGroup,
        contentColors = Triple(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.secondaryContainer, MaterialTheme.colorScheme.outline)
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
    viewModel: OperationCharacteristicsViewModel = hiltViewModel(),
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
    viewModel: OperationCharacteristicsViewModel,
    charGroup: DomainCharGroup
) {
    if (charGroup.detailsVisibility) {
        CharSubGroups(viewModel = viewModel)
        Spacer(modifier = Modifier.height((DEFAULT_SPACE / 2).dp))
    }
}