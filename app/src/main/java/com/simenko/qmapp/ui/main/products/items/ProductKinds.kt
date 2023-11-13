package com.simenko.qmapp.ui.main.products.items

import android.annotation.SuppressLint
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.NavigateNext
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
import com.simenko.qmapp.domain.entities.products.DomainProductKind
import com.simenko.qmapp.other.Constants.DEFAULT_SPACE
import com.simenko.qmapp.ui.common.ContentWithTitle
import com.simenko.qmapp.ui.common.HeaderWithTitle
import com.simenko.qmapp.ui.common.ItemCard
import com.simenko.qmapp.ui.common.StatusChangeBtn

@Composable
fun ProductKinds(
    modifier: Modifier = Modifier,
    viewModel: ProductKindsViewModel = hiltViewModel()
) {
    val items by viewModel.productKinds.collectAsStateWithLifecycle(listOf())

    val onClickDetailsLambda = remember<(ID) -> Unit> { { viewModel.setProductKindsVisibility(dId = SelectedNumber(it)) } }
    val onClickActionsLambda = remember<(ID) -> Unit> { { viewModel.setProductKindsVisibility(aId = SelectedNumber(it)) } }
    val onClickDeleteLambda = remember<(ID) -> Unit> { { viewModel.onDeleteProductKindClick(it) } }
    val onClickEditLambda = remember<(Pair<ID, ID>) -> Unit> { { viewModel.onEditProductKindClick(it) } }

    val onClickSpecificationLambda = remember<(ID) -> Unit> { { viewModel.onProductKindSpecificationClick(it) } }
    val onClickItemsLambda = remember<(ID) -> Unit> { { viewModel.onProductKindItemsClick(it) } }

    LaunchedEffect(Unit) { viewModel.mainPageHandler.setupMainPage(0, true) }
    val listState = rememberLazyListState()

    LazyColumn(modifier = modifier, state = listState, horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.Center) {
        items(items = items, key = { it.productKind.id }) { productLine ->
            ProductKindCard(
                viewModel = viewModel,
                productLine = productLine,
                onClickActions = { onClickActionsLambda(it) },
                onClickDelete = { onClickDeleteLambda(it) },
                onClickEdit = { onClickEditLambda(it) },
                onClickDetails = { onClickDetailsLambda(it) },
                onClickSpecification = { onClickSpecificationLambda(it) },
                onClickItems = { onClickItemsLambda(it) }
            )
        }
    }
}

@SuppressLint("UnusedTransitionTargetStateParameter")
@Composable
fun ProductKindCard(
    viewModel: ProductKindsViewModel,
    productLine: DomainProductKind.DomainProductKindComplete,
    onClickActions: (ID) -> Unit,
    onClickDelete: (ID) -> Unit,
    onClickEdit: (Pair<ID, ID>) -> Unit,
    onClickDetails: (ID) -> Unit,
    onClickSpecification: (ID) -> Unit,
    onClickItems: (ID) -> Unit
) {
    ItemCard(
        modifier = Modifier.padding(horizontal = (DEFAULT_SPACE / 2).dp, vertical = (DEFAULT_SPACE / 2).dp),
        item = productLine,
        onClickActions = onClickActions,
        onClickDelete = onClickDelete,
        onClickEdit = onClickEdit,
        contentColors = Triple(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.secondaryContainer, MaterialTheme.colorScheme.outline),
        actionButtonsImages = arrayOf(Icons.Filled.Delete, Icons.Filled.Edit),
    ) {
        ProductLine(
            viewModel = viewModel,
            productKind = productLine,
            onClickDetails = { onClickDetails(it) },
            onClickSpecification = { onClickSpecification(it) },
            onClickItems = { onClickItems(it) }
        )
    }
}

@Composable
fun ProductLine(
    viewModel: ProductKindsViewModel,
    productKind: DomainProductKind.DomainProductKindComplete,
    onClickDetails: (ID) -> Unit,
    onClickSpecification: (ID) -> Unit,
    onClickItems: (ID) -> Unit
) {
    val containerColor = when (productKind.isExpanded) {
        true -> MaterialTheme.colorScheme.secondaryContainer
        false -> MaterialTheme.colorScheme.primaryContainer
    }

    Column(modifier = Modifier.animateContentSize(animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow))) {
        Row(modifier = Modifier.padding(all = DEFAULT_SPACE.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(0.90f)) {

                HeaderWithTitle(titleFirst = false, titleWight = 0f, text = productKind.productKind.productKindDesignation)
                Spacer(modifier = Modifier.height(DEFAULT_SPACE.dp))
                ContentWithTitle(titleWight = 0.50f, title = "Industry:", value = productKind.productKind.comments ?: NoString.str)
                Spacer(modifier = Modifier.height(DEFAULT_SPACE.dp))

                Row(modifier = Modifier.fillMaxSize()) {
                    Spacer(modifier = Modifier.weight(0.50f))
                    Column(modifier = Modifier.weight(0.50f)) {
                        StatusChangeBtn(modifier = Modifier.fillMaxWidth(), containerColor = containerColor, onClick = { onClickSpecification(productKind.productKind.id) }) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "Product specification", style = MaterialTheme.typography.titleSmall.copy(fontSize = 14.sp), maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Image(imageVector = Icons.Filled.NavigateNext, contentDescription = "Show characteristics")
                            }
                        }

                        StatusChangeBtn(modifier = Modifier.fillMaxWidth(), containerColor = containerColor, onClick = { onClickItems(productKind.productKind.id) }) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "Product items", style = MaterialTheme.typography.titleSmall.copy(fontSize = 14.sp), maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Image(imageVector = Icons.Filled.NavigateNext, contentDescription = "Show items")
                            }
                        }
                    }
                }
            }

            IconButton(modifier = Modifier.weight(weight = 0.10f), onClick = { onClickDetails(productKind.productKind.id) }) {
                Icon(
                    imageVector = if (productKind.detailsVisibility) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = if (productKind.detailsVisibility) stringResource(R.string.show_less) else stringResource(R.string.show_more)
                )
            }
        }
        ProductKindDetails(viewModel = viewModel, productKind = productKind)
    }
}

@Composable
fun ProductKindDetails(
    viewModel: ProductKindsViewModel,
    productKind: DomainProductKind.DomainProductKindComplete
) {
    if (productKind.detailsVisibility) {
//        ProductKindKeys(viewModel)
    }
}