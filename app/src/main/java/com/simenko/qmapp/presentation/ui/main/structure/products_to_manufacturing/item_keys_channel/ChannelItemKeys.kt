package com.simenko.qmapp.presentation.ui.main.structure.products_to_manufacturing.item_keys_channel

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.simenko.qmapp.domain.ComponentPref
import com.simenko.qmapp.domain.ComponentStagePref
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.domain.NoString
import com.simenko.qmapp.domain.ProductPref
import com.simenko.qmapp.domain.SelectedNumber
import com.simenko.qmapp.domain.entities.DomainManufacturingChannel
import com.simenko.qmapp.domain.entities.products.DomainKey
import com.simenko.qmapp.other.Constants.BOTTOM_ITEM_HEIGHT
import com.simenko.qmapp.other.Constants.DEFAULT_SPACE
import com.simenko.qmapp.presentation.ui.common.ContentWithTitle
import com.simenko.qmapp.presentation.ui.common.HeaderWithTitle
import com.simenko.qmapp.presentation.ui.common.InfoLine
import com.simenko.qmapp.presentation.ui.common.ItemCard
import com.simenko.qmapp.navigation.Route
import com.simenko.qmapp.presentation.ui.common.dialog.SingleChoiceDialog
import com.simenko.qmapp.utils.StringUtils.concatTwoStrings

@Composable
fun ChannelItemKeys(
    modifier: Modifier = Modifier,
    viewModel: ChannelItemKeysViewModel,
    route: Route.Main.CompanyStructure.ChannelItemKeys
) {
    val channel by viewModel.channel.collectAsStateWithLifecycle(DomainManufacturingChannel.DomainManufacturingChannelComplete())
    val itemPref by viewModel.itemPref.collectAsStateWithLifecycle()

    val itemKeys by viewModel.itemKeys.collectAsStateWithLifecycle(initialValue = emptyList())

    val availableItemKeys by viewModel.availableItemKeys.collectAsStateWithLifecycle(listOf())

    val isAddItemDialogVisible by viewModel.isAddItemDialogVisible.collectAsStateWithLifecycle()
    val searchString by viewModel.itemToAddSearchStr.collectAsStateWithLifecycle()

    val onClickActionsLambda = remember<(ID) -> Unit> { { viewModel.setItemKeysVisibility(aId = SelectedNumber(it)) } }
    val onClickDeleteLambda = remember<(ID) -> Unit> { { viewModel.onDeleteProductKind(it) } }

    LaunchedEffect(Unit) { viewModel.onEntered(route = route) }

    val listState = rememberLazyListState()

    Column(horizontalAlignment = Alignment.Start, verticalArrangement = Arrangement.Bottom) {
        Spacer(modifier = Modifier.height(10.dp))
        InfoLine(
            modifier = modifier
                .padding(start = DEFAULT_SPACE.dp)
                .fillMaxWidth(), title = "Department", body = concatTwoStrings(channel.subDepartmentWithParents.depAbbr, channel.subDepartmentWithParents.depName)
        )
        InfoLine(
            modifier = modifier
                .padding(start = DEFAULT_SPACE.dp)
                .fillMaxWidth(), title = "Sub department", body = concatTwoStrings(channel.subDepartmentWithParents.subDepAbbr, channel.subDepartmentWithParents.subDepDesignation)
        )
        InfoLine(
            modifier = modifier
                .padding(start = DEFAULT_SPACE.dp)
                .fillMaxWidth(), title = "Channel", body = concatTwoStrings(channel.channel.channelAbbr, channel.channel.channelDesignation)
        )
        HorizontalDivider(modifier = Modifier.height(1.dp), color = MaterialTheme.colorScheme.secondary)

        LazyColumn(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
                .fillMaxSize()
                .padding(all = (DEFAULT_SPACE / 2).dp),
            state = listState
        ) {
            items(items = itemKeys, key = { it.id }) { item ->
                ProductKeyCard(
                    itemPref = itemPref,
                    productKind = item,
                    onClickActions = { onClickActionsLambda(it) },
                    onClickDelete = { onClickDeleteLambda(it) },
                )
            }
            if (itemKeys.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(BOTTOM_ITEM_HEIGHT.dp))
                }
            }
        }
    }

    if (isAddItemDialogVisible) {
        SingleChoiceDialog(
            items = availableItemKeys,
            addIsEnabled = availableItemKeys.any { it.getIsSelected() },
            onDismiss = { viewModel.setAddItemDialogVisibility(false) },
            searchString = searchString,
            onSearch = viewModel::setItemToAddSearchStr,
            onItemSelect = { viewModel.onItemSelect(it) },
            onAddClick = { viewModel.onAddProductKind() }
        )
    }
}

@Composable
fun ProductKeyCard(
    itemPref: Char,
    productKind: DomainKey,
    onClickActions: (ID) -> Unit,
    onClickDelete: (ID) -> Unit,
) {
    val color = when (itemPref) {
        ProductPref.char -> MaterialTheme.colorScheme.surfaceVariant
        ComponentPref.char -> MaterialTheme.colorScheme.primaryContainer
        ComponentStagePref.char -> MaterialTheme.colorScheme.tertiaryContainer
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
    ItemCard(
        modifier = Modifier.padding(horizontal = (DEFAULT_SPACE / 2).dp, vertical = (DEFAULT_SPACE / 2).dp),
        item = productKind,
        onClickActions = onClickActions,
        onClickDelete = onClickDelete,
        contentColors = Triple(color, MaterialTheme.colorScheme.secondaryContainer, MaterialTheme.colorScheme.outline),
        actionButtonsImages = arrayOf(Icons.Filled.Delete),
    ) {
        ProductKey(
            key = productKind,
        )
    }
}

@Composable
fun ProductKey(
    key: DomainKey,
) {
    Column(modifier = Modifier.padding(all = DEFAULT_SPACE.dp)) {
        HeaderWithTitle(titleWight = 0.20f, title = "Designation:", text = key.componentKey)
        Spacer(modifier = Modifier.height(DEFAULT_SPACE.dp))
        ContentWithTitle(titleWight = 0.20f, title = "Description:", value = key.componentKeyDescription ?: NoString.str)
    }
}