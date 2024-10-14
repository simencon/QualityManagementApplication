package com.simenko.qmapp.presentation.ui.main.structure.products_to_manufacturing.product_lines_department

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
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.domain.NoString
import com.simenko.qmapp.domain.SelectedNumber
import com.simenko.qmapp.domain.entities.DomainDepartment
import com.simenko.qmapp.domain.entities.products.DomainProductLine
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
fun DepartmentProductLines(
    modifier: Modifier = Modifier,
    viewModel: DepartmentProductLinesViewModel,
    route: Route.Main.CompanyStructure.DepartmentProductLines
) {
    val department by viewModel.department.collectAsStateWithLifecycle(DomainDepartment.DomainDepartmentComplete())
    val items by viewModel.productLines.collectAsStateWithLifecycle(initialValue = emptyList())

    val availableItems by viewModel.availableProductLines.collectAsStateWithLifecycle(listOf())
    val isAddItemDialogVisible by viewModel.isAddItemDialogVisible.collectAsStateWithLifecycle()
    val searchString by viewModel.itemToAddSearchStr.collectAsStateWithLifecycle()

    val onClickActionsLambda = remember<(ID) -> Unit> { { viewModel.setProductLinesVisibility(aId = SelectedNumber(it)) } }
    val onClickDeleteLambda = remember<(ID) -> Unit> { { viewModel.onDeleteProductLineClick(it) } }

    LaunchedEffect(Unit) { viewModel.onEntered(route = route) }

    val listState = rememberLazyListState()

    Column(horizontalAlignment = Alignment.Start, verticalArrangement = Arrangement.Bottom) {
        Spacer(modifier = Modifier.height(10.dp))
        InfoLine(
            modifier = modifier
                .padding(start = DEFAULT_SPACE.dp)
                .fillMaxWidth(), title = "Department", body = concatTwoStrings(department.department.depAbbr, department.department.depName)
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
            items(items = items, key = { it.manufacturingProject.id }) { productLine ->
                ProductLineCard(
                    productLine = productLine,
                    onClickActions = { onClickActionsLambda(it) },
                    onClickDelete = { onClickDeleteLambda(it) },
                )
            }
            if (items.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(BOTTOM_ITEM_HEIGHT.dp))
                }
            }
        }
    }

    if (isAddItemDialogVisible) {
        SingleChoiceDialog(
            items = availableItems,
            addIsEnabled = availableItems.any { it.isSelected },
            onDismiss = { viewModel.setAddItemDialogVisibility(false) },
            searchString = searchString,
            onSearch = viewModel::setItemToAddSearchStr,
            onItemSelect = { viewModel.onItemSelect(it) },
            onAddClick = { viewModel.onAddProductLine() }
        )
    }
}

@Composable
fun ProductLineCard(
    productLine: DomainProductLine.DomainProductLineComplete,
    onClickActions: (ID) -> Unit,
    onClickDelete: (ID) -> Unit,
) {
    ItemCard(
        modifier = Modifier.padding(horizontal = (DEFAULT_SPACE / 2).dp, vertical = (DEFAULT_SPACE / 2).dp),
        item = productLine,
        onClickActions = onClickActions,
        onClickDelete = onClickDelete,
        contentColors = Triple(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.secondaryContainer, MaterialTheme.colorScheme.outline),
        actionButtonsImages = arrayOf(Icons.Filled.Delete),
    ) {
        ProductLine(
            productLine = productLine,
        )
    }
}

@Composable
fun ProductLine(
    productLine: DomainProductLine.DomainProductLineComplete,
) {
    Column(modifier = Modifier.animateContentSize(animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow))) {
        Row(modifier = Modifier.padding(all = DEFAULT_SPACE.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(0.54f)) {
                HeaderWithTitle(titleFirst = false, titleWight = 0f, text = productLine.manufacturingProject.projectSubject ?: NoString.str)
                Spacer(modifier = Modifier.height(DEFAULT_SPACE.dp))
                ContentWithTitle(titleWight = 0.50f, title = "Product line id:", value = productLine.manufacturingProject.pfmeaNum ?: NoString.str)
                Spacer(modifier = Modifier.height(DEFAULT_SPACE.dp))
                ContentWithTitle(titleWight = 0.50f, title = "Start date:", value = productLine.manufacturingProject.startDate ?: NoString.str)
                Spacer(modifier = Modifier.height(DEFAULT_SPACE.dp))
                ContentWithTitle(titleWight = 0.50f, title = "Revision date:", value = productLine.manufacturingProject.revisionDate ?: NoString.str)
            }
        }
    }
}