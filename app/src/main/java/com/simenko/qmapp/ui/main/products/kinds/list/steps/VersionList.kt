package com.simenko.qmapp.ui.main.products.kinds.list.steps

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.NavigateNext
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.simenko.qmapp.domain.ComponentPref
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.domain.NoString
import com.simenko.qmapp.domain.ProductPref
import com.simenko.qmapp.domain.SelectedString
import com.simenko.qmapp.domain.ZeroValue
import com.simenko.qmapp.domain.entities.products.DomainItemVersionComplete
import com.simenko.qmapp.other.Constants.BOTTOM_ITEM_HEIGHT
import com.simenko.qmapp.other.Constants.DEFAULT_SPACE
import com.simenko.qmapp.storage.ScrollStates
import com.simenko.qmapp.ui.common.HeaderWithTitle
import com.simenko.qmapp.ui.common.ItemCardStringId
import com.simenko.qmapp.ui.common.StatusChangeBtn
import com.simenko.qmapp.ui.main.products.kinds.list.ProductListViewModel
import com.simenko.qmapp.utils.StringUtils.getStringDate
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce

@OptIn(FlowPreview::class)
@Composable
fun Versions(modifier: Modifier = Modifier, viewModel: ProductListViewModel = hiltViewModel()) {

    val items by viewModel.componentVersions.collectAsStateWithLifecycle(listOf())

    val onClickActionsLambda = remember<(String) -> Unit> { { viewModel.setVersionsVisibility(aId = SelectedString(it)) } }
    val onClickDeleteLambda = remember<(String) -> Unit> { { viewModel.onDeleteVersionClick(it) } }
    val onClickEditLambda = remember<(String) -> Unit> { { viewModel.onEditVersionClick(it) } }
    val onClickSpecificationLambda = remember<(String) -> Unit> { { viewModel.onSpecificationClick(it) } }

    LaunchedEffect(Unit) { viewModel.setIsComposed(5, true) }

    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = viewModel.storage.getLong(ScrollStates.VERSIONS.indexKey).let { if (it == NoRecord.num) ZeroValue.num else it }.toInt(),
        initialFirstVisibleItemScrollOffset = viewModel.storage.getLong(ScrollStates.VERSIONS.offsetKey).let { if (it == NoRecord.num) ZeroValue.num else it }.toInt()
    )

    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex }.debounce(500L).collectLatest { index ->
            viewModel.storage.setLong(ScrollStates.VERSIONS.indexKey, index.toLong())
            viewModel.storage.setLong(ScrollStates.VERSIONS.offsetKey, listState.firstVisibleItemScrollOffset.toLong())
        }
    }

    LazyColumn(modifier = modifier, state = listState, horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.Center) {
        items(items = items, key = { it.itemVersion.fId }) { item ->
            VersionCard(
                version = item,
                onClickActions = { onClickActionsLambda(it) },
                onClickDelete = { onClickDeleteLambda(it) },
                onClickEdit = { onClickEditLambda(it) },
                onClickSpecification = { onClickSpecificationLambda(it) }
            )
        }
        if (items.isNotEmpty()) {
            item { Spacer(modifier = Modifier.height(BOTTOM_ITEM_HEIGHT.dp)) }
        }
    }
}

@Composable
fun VersionCard(
    version: DomainItemVersionComplete,
    onClickActions: (String) -> Unit,
    onClickDelete: (String) -> Unit,
    onClickEdit: (String) -> Unit,
    onClickSpecification: (String) -> Unit
) {
    val containerColor = when (version.itemVersion.fId[0]) {
        ProductPref.char -> MaterialTheme.colorScheme.surfaceVariant
        ComponentPref.char -> MaterialTheme.colorScheme.primaryContainer
        else -> MaterialTheme.colorScheme.tertiaryContainer
    }
    ItemCardStringId(
        modifier = Modifier.padding(horizontal = (DEFAULT_SPACE / 2).dp, vertical = (DEFAULT_SPACE / 2).dp),
        item = version,
        onClickActions = onClickActions,
        onClickDelete = onClickDelete,
        onClickEdit = { onClickEdit(it.second) },
        contentColors = Triple(containerColor, MaterialTheme.colorScheme.secondaryContainer, MaterialTheme.colorScheme.outline),
        actionButtonsImages = arrayOf(Icons.Filled.Delete, Icons.Filled.Edit),
    ) {
        ComponentVersion(version = version, color = containerColor, onClickSpecification = { onClickSpecification(it) })
    }
}

@Composable
fun ComponentVersion(
    version: DomainItemVersionComplete,
    color: Color,
    onClickSpecification: (String) -> Unit
) {
    val containerColor = if (version.isExpanded) MaterialTheme.colorScheme.secondaryContainer else color

    Column(modifier = Modifier.animateContentSize(animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow))) {
        Row(modifier = Modifier.padding(all = DEFAULT_SPACE.dp), verticalAlignment = Alignment.Top) {
            Column(modifier = Modifier.weight(0.55f)) {
                HeaderWithTitle(titleWight = 0.436f, title = "Version ID:", text = version.itemVersion.versionDescription ?: NoString.str)
                Spacer(modifier = Modifier.height(DEFAULT_SPACE.dp))
                HeaderWithTitle(titleWight = 0.436f, title = "Status:", text = version.versionStatus.statusDescription)
                Spacer(modifier = Modifier.height(DEFAULT_SPACE.dp))
                HeaderWithTitle(titleWight = 0.436f, title = "Is default:", text = version.itemVersion.isDefault.let { if (it) "yes" else "no" })
                Spacer(modifier = Modifier.height(DEFAULT_SPACE.dp))
                HeaderWithTitle(titleWight = 0.436f, title = "Latest edited:", text = getStringDate(version.itemVersion.versionDate, 6))
            }
            StatusChangeBtn(modifier = Modifier.weight(weight = 0.45f), containerColor = containerColor, onClick = { onClickSpecification(version.itemVersion.fId) }) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Specification",
                        style = MaterialTheme.typography.titleSmall.copy(fontSize = 14.sp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Icon(imageVector = Icons.AutoMirrored.Filled.NavigateNext, contentDescription = "Show specification")
                }
            }
        }
    }
}