package com.simenko.qmapp.ui.main.products.keys

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.domain.entities.products.DomainKey
import com.simenko.qmapp.domain.NoString
import com.simenko.qmapp.domain.SelectedNumber
import com.simenko.qmapp.other.Constants.DEFAULT_SPACE
import com.simenko.qmapp.ui.common.ContentWithTitle
import com.simenko.qmapp.ui.common.HeaderWithTitle
import com.simenko.qmapp.ui.common.InfoLine
import com.simenko.qmapp.ui.common.ItemCard

@Composable
fun ProductLineKeys(
    modifier: Modifier = Modifier,
    viewModel: ProductLineKeysViewModel = hiltViewModel()
) {
    val productLine by viewModel.productLine.collectAsStateWithLifecycle()
    val items by viewModel.productKeys.collectAsStateWithLifecycle(listOf())

    val onClickActionsLambda = remember<(ID) -> Unit> { { viewModel.setProductLineKeysVisibility(aId = SelectedNumber(it)) } }
    val onClickDeleteLambda = remember<(ID) -> Unit> { { viewModel.onDeleteProductLineKeyClick(it) } }
    val onClickEditLambda = remember<(Pair<ID, ID>) -> Unit> { { viewModel.onEditProductLineKeyClick(it) } }

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
    key: DomainKey.DomainKeyComplete,
    onClickActions: (ID) -> Unit,
    onClickDelete: (ID) -> Unit,
    onClickEdit: (Pair<ID, ID>) -> Unit
) {
    ItemCard(
        modifier = Modifier.padding(horizontal = (DEFAULT_SPACE / 2).dp, vertical = (DEFAULT_SPACE / 2).dp),
        item = key,
        onClickActions = onClickActions,
        onClickDelete = onClickDelete,
        onClickEdit = onClickEdit,
        contentColors = Triple(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.secondaryContainer, MaterialTheme.colorScheme.outline),
        actionButtonsImages = arrayOf(Icons.Filled.Delete, Icons.Filled.Edit),
    ) {
        Key(key = key)
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