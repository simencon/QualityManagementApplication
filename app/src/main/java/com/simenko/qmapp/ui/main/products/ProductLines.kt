package com.simenko.qmapp.ui.main.products

import android.annotation.SuppressLint
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Divider
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
import com.simenko.qmapp.domain.entities.products.DomainProductLine
import com.simenko.qmapp.other.Constants.DEFAULT_SPACE
import com.simenko.qmapp.ui.common.ContentWithTitle
import com.simenko.qmapp.ui.common.HeaderWithTitle
import com.simenko.qmapp.ui.common.ItemCard
import com.simenko.qmapp.ui.common.StatusChangeBtn

@Composable
fun ProductLines(
    modifier: Modifier = Modifier,
    viewModel: ProductsViewModel = hiltViewModel()
) {
    val items by viewModel.productLines.collectAsStateWithLifecycle(listOf())

    val onClickDetailsLambda = remember<(ID) -> Unit> { { viewModel.setProductLinesVisibility(dId = SelectedNumber(it)) } }
    val onClickActionsLambda = remember<(ID) -> Unit> { { viewModel.setProductLinesVisibility(aId = SelectedNumber(it)) } }
    val onClickDeleteLambda = remember<(ID) -> Unit> { { viewModel.onDeleteProductLineClick(it) } }
    val onClickEditLambda = remember<(Pair<ID, ID>) -> Unit> { { viewModel.onEditProductLineClick(it) } }

    val onClickKeysLambda = remember<(ID) -> Unit> { { viewModel.onProductLineKeysClick(it) } }
    val onClickCharacteristicsLambda = remember<(ID) -> Unit> { { viewModel.onProductLineCharacteristicsClick(it) } }
    val onClickItemsLambda = remember<(ID) -> Unit> { { viewModel.onProductLineItemsClick(it) } }

    LaunchedEffect(Unit) { viewModel.mainPageHandler.setupMainPage(0, true) }
    val listState = rememberLazyListState()

    LazyColumn(modifier = modifier, state = listState, horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.Center) {
        items(items = items, key = { it.manufacturingProject.id }) { productLine ->
            ProductLineCard(
                productLine = productLine,
                onClickDetails = { onClickDetailsLambda(it) },
                onClickActions = { onClickActionsLambda(it) },
                onClickDelete = { onClickDeleteLambda(it) },
                onClickEdit = { onClickEditLambda(it) },
                onClickKeys = { onClickKeysLambda(it) },
                onClickCharacteristics = { onClickCharacteristicsLambda(it) },
                onClickItems = { onClickItemsLambda(it) }
            )
        }
    }
}

@SuppressLint("UnusedTransitionTargetStateParameter")
@Composable
fun ProductLineCard(
    modifier: Modifier = Modifier,
    productLine: DomainProductLine.DomainProductLineComplete,
    onClickDetails: (ID) -> Unit,
    onClickActions: (ID) -> Unit,
    onClickDelete: (ID) -> Unit,
    onClickEdit: (Pair<ID, ID>) -> Unit,
    onClickKeys: (ID) -> Unit,
    onClickCharacteristics: (ID) -> Unit,
    onClickItems: (ID) -> Unit
) {
    ItemCard(
        modifier = modifier,
        item = productLine,
        onClickActions = onClickActions,
        onClickDelete = onClickDelete,
        onClickEdit = onClickEdit,
        contentColors = Triple(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.secondaryContainer, MaterialTheme.colorScheme.outline),
        actionButtonsImages = arrayOf(Icons.Filled.Delete, Icons.Filled.Edit),
    ) {
        ProductLine(
            productLine = productLine,
            onClickDetails = { onClickDetails(it) },
            onClickKeys = { onClickKeys(it) },
            onClickCharacteristics = { onClickCharacteristics(it) },
            onClickItems = { onClickItems(it) }
        )
    }
}

@Composable
fun ProductLine(
    productLine: DomainProductLine.DomainProductLineComplete,
    onClickDetails: (ID) -> Unit,
    onClickKeys: (ID) -> Unit,
    onClickCharacteristics: (ID) -> Unit,
    onClickItems: (ID) -> Unit
) {
    val containerColor = when (productLine.isExpanded) {
        true -> MaterialTheme.colorScheme.secondaryContainer
        false -> MaterialTheme.colorScheme.surfaceVariant
    }

    Column(modifier = Modifier.animateContentSize(animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow))) {
        HeaderWithTitle(
            modifier = Modifier.padding(start = DEFAULT_SPACE.dp, top = DEFAULT_SPACE.dp, end = DEFAULT_SPACE.dp),
            titleWight = 0.20f,
            title = "Product line:",
            text = productLine.manufacturingProject.projectSubject ?: NoString.str
        )

        Row(modifier = Modifier.padding(all = DEFAULT_SPACE.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(0.54f)) {
                ContentWithTitle(titleWight = 0.50f, title = "Product line id:", value = productLine.manufacturingProject.pfmeaNum ?: NoString.str)
                Spacer(modifier = Modifier.height(DEFAULT_SPACE.dp))
                ContentWithTitle(titleWight = 0.50f, title = "Start date:", value = productLine.manufacturingProject.startDate ?: NoString.str)
                Spacer(modifier = Modifier.height(DEFAULT_SPACE.dp))
                ContentWithTitle(titleWight = 0.50f, title = "Revision date:", value = productLine.manufacturingProject.revisionDate ?: NoString.str)
            }
            Column(modifier = Modifier.weight(0.46f)) {
                StatusChangeBtn(modifier = Modifier.fillMaxWidth(), containerColor = containerColor, onClick = { onClickCharacteristics(productLine.manufacturingProject.id) }) {
                    Text(
                        text = "Characteristics",
                        style = MaterialTheme.typography.titleSmall.copy(fontSize = 14.sp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Row {
                    StatusChangeBtn(modifier = Modifier.weight(0.50f), containerColor = containerColor, onClick = { onClickKeys(productLine.manufacturingProject.id) }) {
                        Text(
                            text = "Keys",
                            style = MaterialTheme.typography.titleSmall.copy(fontSize = 14.sp),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    Spacer(modifier = Modifier.width(DEFAULT_SPACE.dp))
                    StatusChangeBtn(modifier = Modifier.weight(0.50f), containerColor = containerColor, onClick = { onClickItems(productLine.manufacturingProject.id) }) {
                        Text(
                            text = "Items",
                            style = MaterialTheme.typography.titleSmall.copy(fontSize = 14.sp),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            }

            IconButton(modifier = Modifier.weight(weight = 0.10f), onClick = { onClickDetails(productLine.manufacturingProject.id) }) {
                Icon(
                    imageVector = if (productLine.detailsVisibility) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = if (productLine.detailsVisibility) stringResource(R.string.show_less) else stringResource(R.string.show_more)
                )
            }
        }
        ProductLineDetails(productLine = productLine)
    }
}

@Composable
fun ProductLineDetails(
    productLine: DomainProductLine.DomainProductLineComplete
) {
    if (productLine.detailsVisibility) {
        Column(modifier = Modifier.padding(start = DEFAULT_SPACE.dp, top = 0.dp, end = DEFAULT_SPACE.dp, bottom = DEFAULT_SPACE.dp)) {
            Divider(modifier = Modifier.height(1.dp), color = MaterialTheme.colorScheme.secondary)
            Spacer(modifier = Modifier.height(DEFAULT_SPACE.dp))
            ContentWithTitle(title = "Company:", value = productLine.company.companyName ?: NoString.str, titleWight = 0.30f)
            Spacer(modifier = Modifier.height(DEFAULT_SPACE.dp))
            ContentWithTitle(title = "Design department:", value = productLine.designDepartment.depAbbr ?: NoString.str, titleWight = 0.30f)
            Spacer(modifier = Modifier.height(DEFAULT_SPACE.dp))
            ContentWithTitle(title = "Design manager:", value = productLine.designManager.fullName, titleWight = 0.30f)
            Spacer(modifier = Modifier.height((DEFAULT_SPACE / 2).dp))
        }
    }
}