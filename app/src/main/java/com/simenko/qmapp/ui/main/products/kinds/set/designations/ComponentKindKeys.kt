package com.simenko.qmapp.ui.main.products.kinds.set.designations

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
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
import com.simenko.qmapp.domain.NoString
import com.simenko.qmapp.domain.SelectedNumber
import com.simenko.qmapp.other.Constants.DEFAULT_SPACE
import com.simenko.qmapp.ui.common.InfoLine
import com.simenko.qmapp.ui.main.products.designations.KeyCard

@Composable
fun ComponentKindKeys(
    modifier: Modifier = Modifier,
    viewModel: ComponentKindKeysViewModel = hiltViewModel()
) {
    val componentKind by viewModel.componentKind.collectAsStateWithLifecycle()
    val items by viewModel.componentKindKeys.collectAsStateWithLifecycle(listOf())

    val onClickActionsLambda = remember<(ID) -> Unit> { { viewModel.setComponentKindKeysVisibility(aId = SelectedNumber(it)) } }
    val onClickDeleteLambda = remember<(ID) -> Unit> { { viewModel.onDeleteComponentKindKeyClick(it) } }
    val onClickEditLambda = remember<(Pair<ID, ID>) -> Unit> { { viewModel.onEditComponentKindKeyClick(it) } }

    LaunchedEffect(Unit) { viewModel.mainPageHandler.setupMainPage(0, true) }

    val listState = rememberLazyListState()

    Column(horizontalAlignment = Alignment.Start, verticalArrangement = Arrangement.Bottom) {
        Spacer(modifier = Modifier.height(10.dp))
        InfoLine(modifier = modifier.padding(start = DEFAULT_SPACE.dp), title = "Product line", body = componentKind.productKind.productLine.manufacturingProject.projectSubject ?: NoString.str)
        InfoLine(modifier = modifier.padding(start = DEFAULT_SPACE.dp), title = "Product", body = componentKind.productKind.productKind.productKindDesignation)
        InfoLine(modifier = modifier.padding(start = DEFAULT_SPACE.dp), title = "Component", body = componentKind.componentKind.componentKindDescription)
        HorizontalDivider(modifier = Modifier.height(1.dp), color = MaterialTheme.colorScheme.secondary)
        LazyColumn(modifier = modifier, state = listState, horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.Center) {
            items(items = items, key = { it.componentKindKey.id }) { key ->
                KeyCard(
                    key = key.key,
                    onClickActions = { onClickActionsLambda(it) },
                    onClickDelete = { onClickDeleteLambda(it) },
                    onClickEdit = { onClickEditLambda(it) },
                )
            }
        }
    }
}