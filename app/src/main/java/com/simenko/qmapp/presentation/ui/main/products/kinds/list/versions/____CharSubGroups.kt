package com.simenko.qmapp.presentation.ui.main.products.kinds.list.versions

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import com.simenko.qmapp.domain.entities.products.DomainCharSubGroup
import com.simenko.qmapp.other.Constants.DEFAULT_SPACE
import com.simenko.qmapp.presentation.ui.common.HeaderWithTitle
import com.simenko.qmapp.presentation.ui.common.ItemCard

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CharSubGroups(
    viewModel: VersionTolerancesViewModel = hiltViewModel()
) {
    val items by viewModel.characteristicSubGroups.collectAsStateWithLifecycle(listOf())

    val onClickDetailsLambda = remember<(ID) -> Unit> { { viewModel.setCharSubGroupsVisibility(dId = SelectedNumber(it)) } }

    LaunchedEffect(Unit) { viewModel.setIsComposed(1, true) }

    FlowRow(horizontalArrangement = Arrangement.End, verticalArrangement = Arrangement.Center) {
        items.forEach { item ->
            CharSubGroupCard(
                viewModel = viewModel,
                charSubGroup = item,
                onClickDetails = { onClickDetailsLambda(it) },
            )
        }
    }
}

@Composable
fun CharSubGroupCard(
    viewModel: VersionTolerancesViewModel,
    charSubGroup: DomainCharSubGroup,
    onClickDetails: (ID) -> Unit,
) {
    ItemCard(
        modifier = Modifier.padding(horizontal = (DEFAULT_SPACE).dp, vertical = (DEFAULT_SPACE / 2).dp),
        item = charSubGroup,
        contentColors = Triple(MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.secondaryContainer, MaterialTheme.colorScheme.outline),
        actionButtonsImages = arrayOf(Icons.Filled.Delete, Icons.Filled.Edit),
    ) {
        CharSubGroup(
            viewModel = viewModel,
            charSubGroup = charSubGroup,
            onClickDetails = { onClickDetails(it) }
        )
    }
}

@Composable
fun CharSubGroup(
    viewModel: VersionTolerancesViewModel = hiltViewModel(),
    charSubGroup: DomainCharSubGroup,
    onClickDetails: (ID) -> Unit
) {
    Column(modifier = Modifier.animateContentSize(animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow))) {
        Row(modifier = Modifier.padding(all = DEFAULT_SPACE.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(0.72f)) {
                HeaderWithTitle(titleWight = 0.50f, title = "Characteristics sub group:", text = charSubGroup.ishElement ?: NoString.str)
                Spacer(modifier = Modifier.height(DEFAULT_SPACE.dp))
                HeaderWithTitle(
                    titleWight = 0.50f, title = "Sub group related time:",
                    text = charSubGroup.measurementGroupRelatedTime?.let { "${String.format("%.2f", it)} minutes" } ?: NoString.str
                )
            }
            IconButton(modifier = Modifier.weight(weight = 0.10f), onClick = { onClickDetails(charSubGroup.id) }) {
                Icon(
                    imageVector = if (charSubGroup.detailsVisibility) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = if (charSubGroup.detailsVisibility) stringResource(R.string.show_less) else stringResource(R.string.show_more)
                )
            }
        }
        CharSubGroupDetails(viewModel = viewModel, charGroup = charSubGroup)
    }
}

@Composable
fun CharSubGroupDetails(
    viewModel: VersionTolerancesViewModel,
    charGroup: DomainCharSubGroup
) {
    if (charGroup.detailsVisibility) {
        Characteristics(viewModel = viewModel)
        Spacer(modifier = Modifier.height((DEFAULT_SPACE / 2).dp))
    }
}