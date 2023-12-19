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
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.NavigateBefore
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.simenko.qmapp.R
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.domain.ProductPref
import com.simenko.qmapp.domain.SelectedNumber
import com.simenko.qmapp.domain.ZeroValue
import com.simenko.qmapp.domain.entities.products.DomainProductKindProduct
import com.simenko.qmapp.other.Constants.DEFAULT_SPACE
import com.simenko.qmapp.storage.ScrollStates
import com.simenko.qmapp.ui.common.HeaderWithTitle
import com.simenko.qmapp.ui.common.ItemCard
import com.simenko.qmapp.ui.common.StatusChangeBtn
import com.simenko.qmapp.ui.main.products.kinds.list.ProductListViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce

@OptIn(FlowPreview::class)
@Composable
fun ProductList(
    modifier: Modifier = Modifier,
    viewModel: ProductListViewModel = hiltViewModel()
) {
    val versionsForItem by viewModel.versionsForItem.collectAsStateWithLifecycle()
    val items by viewModel.products.collectAsStateWithLifecycle(listOf())

    val onClickDetailsLambda = remember<(ID) -> Unit> { { viewModel.setProductsVisibility(dId = SelectedNumber(it)) } }
    val onClickActionsLambda = remember<(ID) -> Unit> { { viewModel.setProductsVisibility(aId = SelectedNumber(it)) } }
    val onClickDeleteLambda = remember<(ID) -> Unit> { { viewModel.onDeleteProductClick(it) } }
    val onClickEditLambda = remember<(Pair<ID, ID>) -> Unit> { { viewModel.onEditProductClick(it) } }
    val onClickVersionsLambda = remember<(ID) -> Unit> { { viewModel.onVersionsClick(ProductPref.char.toString() + it) } }

    LaunchedEffect(Unit) { viewModel.setIsComposed(0, true) }

    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = viewModel.storage.getLong(ScrollStates.PRODUCTS.indexKey).let { if (it == NoRecord.num) ZeroValue.num else it }.toInt(),
        initialFirstVisibleItemScrollOffset = viewModel.storage.getLong(ScrollStates.PRODUCTS.offsetKey).let { if (it == NoRecord.num) ZeroValue.num else it }.toInt()
    )

    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex }.debounce(500L).collectLatest { index ->
            viewModel.storage.setLong(ScrollStates.PRODUCTS.indexKey, index.toLong())
            viewModel.storage.setLong(ScrollStates.PRODUCTS.offsetKey, listState.firstVisibleItemScrollOffset.toLong())
        }
    }

    Column(horizontalAlignment = Alignment.Start, verticalArrangement = Arrangement.Bottom) {
        LazyColumn(modifier = modifier, state = listState, horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.Center) {
            items(items = items, key = { it.productKindProduct.id }) { componentKind ->
                ProductCard(
                    viewModel = viewModel,
                    versionsForItem = versionsForItem.str,
                    product = componentKind,
                    onClickActions = { onClickActionsLambda(it) },
                    onClickDelete = { onClickDeleteLambda(it) },
                    onClickEdit = { onClickEditLambda(it) },
                    onClickDetails = { onClickDetailsLambda(it) },
                    onClickVersions = { onClickVersionsLambda(it) }
                )
            }
        }
    }
}


@Composable
fun ProductCard(
    viewModel: ProductListViewModel,
    versionsForItem: String,
    product: DomainProductKindProduct.DomainProductKindProductComplete,
    onClickActions: (ID) -> Unit,
    onClickDelete: (ID) -> Unit,
    onClickEdit: (Pair<ID, ID>) -> Unit,
    onClickDetails: (ID) -> Unit,
    onClickVersions: (ID) -> Unit,
) {
    ItemCard(
        modifier = Modifier.padding(horizontal = (DEFAULT_SPACE / 2).dp, vertical = (DEFAULT_SPACE / 2).dp),
        item = product,
        onClickActions = onClickActions,
        onClickDelete = onClickDelete,
        onClickEdit = onClickEdit,
        contentColors = Triple(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.secondaryContainer, MaterialTheme.colorScheme.outline),
        actionButtonsImages = arrayOf(Icons.Filled.Delete, Icons.Filled.Edit),
    ) {
        Product(
            viewModel = viewModel,
            versionsForItem = versionsForItem,
            product = product,
            onClickDetails = { onClickDetails(it) },
            onClickVersions = { onClickVersions(it) }
        )
    }
}

@Composable
fun Product(
    viewModel: ProductListViewModel,
    versionsForItem: String,
    product: DomainProductKindProduct.DomainProductKindProductComplete,
    onClickDetails: (ID) -> Unit = {},
    onClickVersions: (ID) -> Unit
) {
    val borderColor = if (versionsForItem == ProductPref.char.toString() + product.productKindProduct.productId) MaterialTheme.colorScheme.outline else null
    val containerColor = if (product.isExpanded) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceVariant

    Column(modifier = Modifier.animateContentSize(animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow))) {
        Row(modifier = Modifier.padding(all = DEFAULT_SPACE.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(0.90f)) {
                Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.SpaceBetween) {
                    HeaderWithTitle(modifier = Modifier.weight(0.55f), titleWight = 0.35f, title = "Designation:", text = product.product.key.componentKey)
                    StatusChangeBtn(
                        modifier = Modifier.weight(0.45f),
                        borderColor = borderColor,
                        containerColor = containerColor,
                        onClick = { onClickVersions(product.productKindProduct.productId) }) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                modifier = Modifier.height(15.dp),
                                imageVector = Icons.Filled.Circle,
                                contentDescription = "Is filled",
                                tint = if (product.versions.isNotEmpty()) Color.Green else Color.Red,
                            )
                            Text(text = "Versions", style = MaterialTheme.typography.titleSmall.copy(fontSize = 14.sp), maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Icon(imageVector = if (borderColor == null) Icons.Filled.NavigateNext else Icons.Filled.NavigateBefore, contentDescription = "Show versions")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(DEFAULT_SPACE.dp))
                HeaderWithTitle(titleWight = 0.2275f, title = "Base:", text = product.product.productBase.componentBaseDesignation)
                Spacer(modifier = Modifier.height(DEFAULT_SPACE.dp))
                HeaderWithTitle(titleWight = 0.2275f, title = "Product:", text = product.product.product.productDesignation)
            }
            IconButton(modifier = Modifier.weight(weight = 0.10f), onClick = { onClickDetails(product.productKindProduct.productId) }) {
                Icon(
                    imageVector = if (product.detailsVisibility) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = if (product.detailsVisibility) stringResource(R.string.show_less) else stringResource(R.string.show_more)
                )
            }
        }
        ProductDetails(viewModel = viewModel, product = product)
    }
}

@Composable
fun ProductDetails(
    viewModel: ProductListViewModel,
    product: DomainProductKindProduct.DomainProductKindProductComplete,
) {
    if (product.detailsVisibility) {
        Column(modifier = Modifier.padding(start = DEFAULT_SPACE.dp, top = 0.dp, end = DEFAULT_SPACE.dp, bottom = 0.dp)) {
            Divider(modifier = Modifier.height(1.dp), color = MaterialTheme.colorScheme.secondary)
            Spacer(modifier = Modifier.height((DEFAULT_SPACE / 2).dp))
        }
        ComponentKindList(viewModel = viewModel)
    }
}