package com.simenko.qmapp.ui.main.structure.steps

import android.annotation.SuppressLint
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
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.NavigateBefore
import androidx.compose.material.icons.filled.NavigateNext
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.simenko.qmapp.R
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.domain.NoString
import com.simenko.qmapp.domain.SelectedNumber
import com.simenko.qmapp.domain.entities.DomainManufacturingChannel
import com.simenko.qmapp.other.Constants.DEFAULT_SPACE
import com.simenko.qmapp.ui.common.ContentWithTitle
import com.simenko.qmapp.ui.common.HeaderWithTitle
import com.simenko.qmapp.ui.common.ItemCard
import com.simenko.qmapp.ui.common.StatusChangeBtn
import com.simenko.qmapp.ui.main.structure.CompanyStructureViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Channels(
    viewModel: CompanyStructureViewModel = hiltViewModel()
) {
    val subDepartmentVisibility by viewModel.subDepartmentsVisibility.collectAsStateWithLifecycle()
    val items by viewModel.channels.collectAsStateWithLifecycle(listOf())

    val onClickDetailsLambda = remember<(ID) -> Unit> { { viewModel.setChannelsVisibility(dId = SelectedNumber(it)) } }
    val onClickActionsLambda = remember<(ID) -> Unit> { { viewModel.setChannelsVisibility(aId = SelectedNumber(it)) } }
    val onClickDeleteLambda = remember<(ID) -> Unit> { { viewModel.onDeleteChannelClick(it) } }
    val onClickAddLambda = remember<(ID) -> Unit> { { viewModel.onAddChannelClick(it) } }
    val onClickEditLambda = remember<(Pair<ID, ID>) -> Unit> { { viewModel.onEditChannelClick(it) } }
    val onClickProductsLambda = remember<(ID) -> Unit> { { viewModel.onChannelProductsClick(it) } }

    LaunchedEffect(Unit) { viewModel.setIsComposed(2, true) }

    Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.Center) {
        FlowRow {
            items.forEach { channel ->
                ChannelCard(
                    channel = channel,
                    onClickActions = { onClickActionsLambda(it) },
                    onClickDelete = { onClickDeleteLambda(it) },
                    onClickEdit = { onClickEditLambda(it) },
                    onClickDetails = { onClickDetailsLambda(it) },
                    onClickProducts = { onClickProductsLambda(it) }
                )
            }
        }
        Divider(modifier = Modifier.height(0.dp))
        FloatingActionButton(
            modifier = Modifier.padding(top = (DEFAULT_SPACE / 2).dp, end = DEFAULT_SPACE.dp, bottom = DEFAULT_SPACE.dp),
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
            onClick = { onClickAddLambda(subDepartmentVisibility.first.num) },
            content = { Icon(imageVector = Icons.Default.Add, contentDescription = "Add channel") }
        )
    }
}

@SuppressLint("UnusedTransitionTargetStateParameter")
@Composable
fun ChannelCard(
    channel: DomainManufacturingChannel,
    onClickActions: (ID) -> Unit,
    onClickDelete: (ID) -> Unit,
    onClickEdit: (Pair<ID, ID>) -> Unit,
    onClickDetails: (ID) -> Unit,
    onClickProducts: (ID) -> Unit
) {
    ItemCard(
        modifier = Modifier.padding(horizontal = (DEFAULT_SPACE).dp, vertical = (DEFAULT_SPACE / 2).dp),
        item = channel,
        onClickActions = onClickActions,
        onClickDelete = onClickDelete,
        onClickEdit = onClickEdit,
        contentColors = Triple(MaterialTheme.colorScheme.tertiaryContainer, MaterialTheme.colorScheme.secondaryContainer, MaterialTheme.colorScheme.outline),
        actionButtonsImages = arrayOf(Icons.Filled.Delete, Icons.Filled.Edit),
    ) {
        Channel(
            channel = channel,
            onClickDetails = onClickDetails,
            onClickProducts = onClickProducts
        )
    }
}

@Composable
fun Channel(
    channel: DomainManufacturingChannel,
    onClickDetails: (ID) -> Unit,
    onClickProducts: (ID) -> Unit
) {
    val containerColor = when (channel.isExpanded) {
        true -> MaterialTheme.colorScheme.secondaryContainer
        false -> MaterialTheme.colorScheme.tertiaryContainer
    }

    Row(
        modifier = Modifier
            .animateContentSize(animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow))
            .padding(all = DEFAULT_SPACE.dp), verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(0.91f)) {
            Row {
                Column(modifier = Modifier.weight(weight = 0.70f), horizontalAlignment = Alignment.Start) {
                    HeaderWithTitle(titleFirst = false, titleWight = 0f, text = channel.channelOrder?.toString() ?: NoString.str)
                    Spacer(modifier = Modifier.height(DEFAULT_SPACE.dp))
                    HeaderWithTitle(titleWight = 0.34f, title = "Channel:", text = channel.channelAbbr ?: NoString.str)
                }
                Spacer(modifier = Modifier.width(DEFAULT_SPACE.dp))
                StatusChangeBtn(modifier = Modifier.weight(weight = 0.30f), containerColor = containerColor, onClick = { onClickProducts(channel.id) }) {
                    Text(
                        text = "Products",
                        style = MaterialTheme.typography.titleSmall.copy(fontSize = 14.sp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }

            }
            Spacer(modifier = Modifier.height((DEFAULT_SPACE / 2).dp))
            ContentWithTitle(title = "Comp. name:", value = channel.channelDesignation ?: NoString.str, titleWight = 0.23f)
        }

        IconButton(onClick = { onClickDetails(channel.id) }, modifier = Modifier.weight(weight = 0.09f)) {
            Icon(
                imageVector = if (channel.detailsVisibility) Icons.Filled.NavigateBefore else Icons.Filled.NavigateNext,
                contentDescription = if (channel.detailsVisibility) stringResource(R.string.show_less) else stringResource(R.string.show_more),
            )
        }
    }
}