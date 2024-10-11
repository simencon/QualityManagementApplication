package com.simenko.qmapp.ui.main.structure.products.item_kinds

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
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
import com.simenko.qmapp.domain.entities.DomainSubDepartment
import com.simenko.qmapp.domain.entities.products.DomainComponentKind
import com.simenko.qmapp.domain.entities.products.DomainProductKind
import com.simenko.qmapp.other.Constants.BOTTOM_ITEM_HEIGHT
import com.simenko.qmapp.other.Constants.DEFAULT_SPACE
import com.simenko.qmapp.ui.common.ContentWithTitle
import com.simenko.qmapp.ui.common.HeaderWithTitle
import com.simenko.qmapp.ui.common.InfoLine
import com.simenko.qmapp.ui.common.ItemCard
import com.simenko.qmapp.ui.common.dialog.SingleChoiceDialog
import com.simenko.qmapp.ui.navigation.Route
import com.simenko.qmapp.utils.StringUtils.concatTwoStrings

@Composable
fun SubDepartmentItemKinds(
    modifier: Modifier = Modifier,
    viewModel: SubDepartmentItemKindsViewModel,
    route: Route.Main.CompanyStructure.SubDepartmentItemKinds
) {
    val subDepartment by viewModel.subDepartment.collectAsStateWithLifecycle(DomainSubDepartment.DomainSubDepartmentComplete())
    val itemKindPref by viewModel.itemKindPref.collectAsStateWithLifecycle()

    val productKinds by viewModel.productKinds.collectAsStateWithLifecycle(initialValue = emptyList())
    val componentKinds by viewModel.componentKinds.collectAsStateWithLifecycle(initialValue = emptyList())

    val availableItemKinds by viewModel.availableItemKinds.collectAsStateWithLifecycle(listOf())

    val isAddItemDialogVisible by viewModel.isAddItemDialogVisible.collectAsStateWithLifecycle()
    val searchString by viewModel.itemToAddSearchStr.collectAsStateWithLifecycle()

    val onClickActionsLambda = remember<(ID) -> Unit> { { viewModel.setProductLinesVisibility(aId = SelectedNumber(it)) } }
    val onClickDeleteLambda = remember<(ID) -> Unit> { { viewModel.onDeleteProductKind(it) } }

    LaunchedEffect(Unit) { viewModel.onEntered(route = route) }

    val listState = rememberLazyListState()

    Column(horizontalAlignment = Alignment.Start, verticalArrangement = Arrangement.Bottom) {
        Spacer(modifier = Modifier.height(10.dp))
        InfoLine(
            modifier = modifier
                .padding(start = DEFAULT_SPACE.dp)
                .fillMaxWidth(), title = "Department", body = concatTwoStrings(subDepartment.department.depAbbr, subDepartment.department.depName)
        )
        InfoLine(
            modifier = modifier
                .padding(start = DEFAULT_SPACE.dp)
                .fillMaxWidth(), title = "Sub department", body = concatTwoStrings(subDepartment.subDepartment.subDepAbbr, subDepartment.subDepartment.subDepDesignation)
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
            when(itemKindPref) {
                ProductPref.char -> {
                    items(items = productKinds, key = { it.productKind.id }) { item ->
                        ProductKindCard(
                            productKind = item,
                            onClickActions = { onClickActionsLambda(it) },
                            onClickDelete = { onClickDeleteLambda(it) },
                        )
                    }
                }
                ComponentPref.char -> {
                    items(items = componentKinds, key = { it.componentKind.id }) { item ->
                        ComponentKindCard(
                            productKind = item,
                            onClickActions = { onClickActionsLambda(it) },
                            onClickDelete = { onClickDeleteLambda(it) },
                        )
                    }
                }
                ComponentStagePref.char -> {

                }
            }
            item {
                Spacer(modifier = Modifier.height(BOTTOM_ITEM_HEIGHT.dp))
            }
        }
    }

    if (isAddItemDialogVisible) {
        SingleChoiceDialog(
            items = availableItemKinds,
            addIsEnabled = availableItemKinds.any { it.getIsSelected() },
            onDismiss = { viewModel.setAddItemDialogVisibility(false) },
            searchString = searchString,
            onSearch = viewModel::setItemToAddSearchStr,
            onItemSelect = { viewModel.onItemSelect(it) },
            onAddClick = { viewModel.onAddProductKind() }
        )
    }
}

@Composable
fun ProductKindCard(
    productKind: DomainProductKind.DomainProductKindComplete,
    onClickActions: (ID) -> Unit,
    onClickDelete: (ID) -> Unit,
) {
    ItemCard(
        modifier = Modifier.padding(horizontal = (DEFAULT_SPACE / 2).dp, vertical = (DEFAULT_SPACE / 2).dp),
        item = productKind,
        onClickActions = onClickActions,
        onClickDelete = onClickDelete,
        contentColors = Triple(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.secondaryContainer, MaterialTheme.colorScheme.outline),
        actionButtonsImages = arrayOf(Icons.Filled.Delete),
    ) {
        ProductKind(
            productKind = productKind,
        )
    }
}

@Composable
fun ProductKind(
    productKind: DomainProductKind.DomainProductKindComplete,
) {
    Column(modifier = Modifier.animateContentSize(animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow))) {
        Row(modifier = Modifier.padding(all = DEFAULT_SPACE.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(0.54f)) {
                HeaderWithTitle(titleFirst = false, titleWight = 0f, text = productKind.productKind.productKindDesignation)
                Spacer(modifier = Modifier.height(DEFAULT_SPACE.dp))
                ContentWithTitle(titleWight = 0.50f, title = "Industry:", value = productKind.productKind.comments ?: NoString.str)
            }
        }
    }
}

@Composable
fun ComponentKindCard(
    productKind: DomainComponentKind.DomainComponentKindComplete,
    onClickActions: (ID) -> Unit,
    onClickDelete: (ID) -> Unit,
) {
    ItemCard(
        modifier = Modifier.padding(horizontal = (DEFAULT_SPACE / 2).dp, vertical = (DEFAULT_SPACE / 2).dp),
        item = productKind,
        onClickActions = onClickActions,
        onClickDelete = onClickDelete,
        contentColors = Triple(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.secondaryContainer, MaterialTheme.colorScheme.outline),
        actionButtonsImages = arrayOf(Icons.Filled.Delete),
    ) {
        ComponentKind(
            productKind = productKind,
        )
    }
}

@Composable
fun ComponentKind(
    productKind: DomainComponentKind.DomainComponentKindComplete,
) {
    Column(modifier = Modifier.animateContentSize(animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow))) {
        Row(modifier = Modifier.padding(all = DEFAULT_SPACE.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(0.54f)) {
                HeaderWithTitle(titleFirst = false, titleWight = 0f, text = productKind.componentKind.componentKindDescription)
                Spacer(modifier = Modifier.height(DEFAULT_SPACE.dp))
                ContentWithTitle(titleWight = 0.50f, title = "Quantity units:", value = productKind.componentKind.quantityUnits)
            }
        }
    }
}