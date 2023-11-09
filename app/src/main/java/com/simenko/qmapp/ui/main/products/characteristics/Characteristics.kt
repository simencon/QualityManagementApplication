package com.simenko.qmapp.ui.main.products.characteristics

import android.annotation.SuppressLint
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.simenko.qmapp.R
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.domain.NoString
import com.simenko.qmapp.domain.SelectedNumber
import com.simenko.qmapp.domain.ZeroValue
import com.simenko.qmapp.domain.entities.DomainDepartment
import com.simenko.qmapp.domain.entities.products.DomainCharacteristic
import com.simenko.qmapp.other.Constants
import com.simenko.qmapp.storage.ScrollStates
import com.simenko.qmapp.ui.common.ContentWithTitle
import com.simenko.qmapp.ui.common.HeaderWithTitle
import com.simenko.qmapp.ui.common.StatusChangeBtn
import com.simenko.qmapp.ui.main.structure.CompanyStructureViewModel
import com.simenko.qmapp.utils.dp
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlin.math.roundToInt

@OptIn(FlowPreview::class)
@Composable
fun Characteristics(
    modifier: Modifier = Modifier,
    viewModel: CompanyStructureViewModel = hiltViewModel()
) {
    val items by viewModel.departments.collectAsStateWithLifecycle(listOf())

    val onClickDetailsLambda = remember<(ID) -> Unit> { { viewModel.setDepartmentsVisibility(dId = SelectedNumber(it)) } }
    val onClickActionsLambda = remember<(ID) -> Unit> { { viewModel.setDepartmentsVisibility(aId = SelectedNumber(it)) } }
    val onClickDeleteLambda = remember<(ID) -> Unit> { { viewModel.onDeleteDepartmentClick(it) } }
    val onClickEditLambda = remember<(Pair<ID, ID>) -> Unit> { { viewModel.onEditDepartmentClick(it) } }
    val onClickProductsLambda = remember<(ID) -> Unit> { { viewModel.onDepartmentProductsClick(it) } }

    LaunchedEffect(Unit) { viewModel.setIsComposed(0, true) }

    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = viewModel.storage.getLong(ScrollStates.DEPARTMENTS.indexKey).let { if (it == NoRecord.num) ZeroValue.num else it }.toInt(),
        initialFirstVisibleItemScrollOffset = viewModel.storage.getLong(ScrollStates.DEPARTMENTS.offsetKey).let { if (it == NoRecord.num) ZeroValue.num else it }.toInt()
    )

    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex }.debounce(500L).collectLatest { index ->
            viewModel.storage.setLong(ScrollStates.DEPARTMENTS.indexKey, index.toLong())
            viewModel.storage.setLong(ScrollStates.DEPARTMENTS.offsetKey, listState.firstVisibleItemScrollOffset.toLong())
        }
    }

    LazyColumn(modifier = modifier, state = listState, horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.Center) {
        items(items = items, key = { it.department.id }) { department ->
            DepartmentCard(
                viewModel = viewModel,
                department = department,
                onClickDetails = { onClickDetailsLambda(it) },
                onClickActions = { onClickActionsLambda(it) },
                onClickDelete = { onClickDeleteLambda(it) },
                onClickEdit = { onClickEditLambda(it) },
                onClickProducts = { onClickProductsLambda(it) }
            )
        }
    }
}

@SuppressLint("UnusedTransitionTargetStateParameter")
@Composable
fun DepartmentCard(
    modifier: Modifier = Modifier,
    viewModel: CompanyStructureViewModel,
    department: DomainDepartment.DomainDepartmentComplete,
    onClickDetails: (ID) -> Unit,
    onClickActions: (ID) -> Unit,
    onClickDelete: (ID) -> Unit,
    onClickEdit: (Pair<ID, ID>) -> Unit,
    onClickProducts: (ID) -> Unit
) {
    val transitionState = remember { MutableTransitionState(department.isExpanded).apply { targetState = !department.isExpanded } }
    val transition = updateTransition(transitionState, "cardTransition")

    val offsetTransition by transition.animateFloat(
        label = "cardOffsetTransition",
        transitionSpec = { tween(durationMillis = Constants.ANIMATION_DURATION) },
        targetValueByState = { (if (department.isExpanded) Constants.CARD_OFFSET * 2 else 0f).dp() },
    )

    val containerColor = when (department.isExpanded) {
        true -> MaterialTheme.colorScheme.secondaryContainer
        false -> MaterialTheme.colorScheme.surfaceVariant
    }

    val borderColor = when (department.detailsVisibility) {
        true -> MaterialTheme.colorScheme.outline
        false -> when (department.isExpanded) {
            true -> MaterialTheme.colorScheme.secondaryContainer
            false -> MaterialTheme.colorScheme.surfaceVariant
        }
    }

    Box(Modifier.fillMaxWidth()) {
        Row(Modifier.padding(all = (Constants.DEFAULT_SPACE / 2).dp)) {
            IconButton(
                modifier = Modifier.size(Constants.ACTION_ITEM_SIZE.dp),
                onClick = { onClickDelete(department.department.id) },
                content = { Icon(imageVector = Icons.Filled.Delete, contentDescription = "delete action") }
            )

            IconButton(
                modifier = Modifier.size(Constants.ACTION_ITEM_SIZE.dp),
                onClick = { onClickEdit(Pair(department.department.companyId ?: NoRecord.num, department.department.id)) },
                content = { Icon(imageVector = Icons.Filled.Edit, contentDescription = "edit action") }
            )
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = containerColor),
            border = BorderStroke(width = 1.dp, borderColor),
            elevation = CardDefaults.cardElevation(4.dp),
            modifier = modifier
                .padding(horizontal = (Constants.DEFAULT_SPACE / 2).dp, vertical = (Constants.DEFAULT_SPACE / 2).dp)
                .fillMaxWidth()
                .offset { IntOffset(offsetTransition.roundToInt(), 0) }
                .pointerInput(department.department.id) { detectTapGestures(onDoubleTap = { onClickActions(department.department.id) }) }
        ) {
            Department(
                viewModel = viewModel,
                department = department,
                onClickDetails = { onClickDetails(it) },
                onClickProducts = { onClickProducts(it) }
            )
        }
    }
}

@Composable
fun Department(
    viewModel: CompanyStructureViewModel = hiltViewModel(),
    department: DomainDepartment.DomainDepartmentComplete,
    onClickDetails: (ID) -> Unit,
    onClickProducts: (ID) -> Unit
) {
    val containerColor = when (department.isExpanded) {
        true -> MaterialTheme.colorScheme.secondaryContainer
        false -> MaterialTheme.colorScheme.surfaceVariant
    }

    Column(modifier = Modifier.animateContentSize(animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow))) {
        Row(modifier = Modifier.padding(all = Constants.DEFAULT_SPACE.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(0.72f)) {
                HeaderWithTitle(titleFirst = false, titleWight = 0f, text = department.department.depOrder?.toString() ?: NoString.str)
                Spacer(modifier = Modifier.height(Constants.DEFAULT_SPACE.dp))
                HeaderWithTitle(titleWight = 0.37f, title = "Department:", text = department.department.depAbbr ?: NoString.str)
            }
            StatusChangeBtn(modifier = Modifier.weight(weight = 0.28f), containerColor = containerColor, onClick = { onClickProducts(department.department.id) }) {
                Text(
                    text = "Products",
                    style = MaterialTheme.typography.titleSmall.copy(fontSize = 14.sp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            IconButton(modifier = Modifier.weight(weight = 0.10f), onClick = { onClickDetails(department.department.id) }) {
                Icon(
                    imageVector = if (department.detailsVisibility) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = if (department.detailsVisibility) stringResource(R.string.show_less) else stringResource(R.string.show_more)
                )
            }
        }
//        DepartmentDetails(characteristic = department)
    }
}

@Composable
fun DepartmentDetails(
    characteristic: DomainCharacteristic.DomainCharacteristicComplete
) {
    if (characteristic.detailsVisibility) {
        Column(modifier = Modifier.padding(start = Constants.DEFAULT_SPACE.dp, top = 0.dp, end = Constants.DEFAULT_SPACE.dp, bottom = Constants.DEFAULT_SPACE.dp)) {
            Divider(modifier = Modifier.height(1.dp), color = MaterialTheme.colorScheme.secondary)
            Spacer(modifier = Modifier.height(Constants.DEFAULT_SPACE.dp))
            ContentWithTitle(title = "Group:", value = characteristic.characteristicSubGroup.charGroup.charGroup.ishElement ?: NoString.str, titleWight = 0.24f)
            Spacer(modifier = Modifier.height(Constants.DEFAULT_SPACE.dp))
            ContentWithTitle(title = "Sub group:", value = characteristic.characteristicSubGroup.charSubGroup.ishElement ?: NoString.str, titleWight = 0.24f)
            Spacer(modifier = Modifier.height(Constants.DEFAULT_SPACE.dp))
            ContentWithTitle(title = "Designation:", value = characteristic.characteristic.charDesignation ?: NoString.str, titleWight = 0.24f)
            Spacer(modifier = Modifier.height(Constants.DEFAULT_SPACE.dp))
            ContentWithTitle(title = "Complete name:", value = characteristic.characteristic.charDescription ?: NoString.str, titleWight = 0.24f)
            Spacer(modifier = Modifier.height(Constants.DEFAULT_SPACE.dp))
            ContentWithTitle(title = "Sample related time (minutes):", value = characteristic.characteristic.sampleRelatedTime?.toString() ?: NoString.str, titleWight = 0.76f)
            Spacer(modifier = Modifier.height(Constants.DEFAULT_SPACE.dp))
            ContentWithTitle(title = "Measurement related time (minutes):", value = characteristic.characteristic.sampleRelatedTime?.toString() ?: NoString.str, titleWight = 0.76f)
            Spacer(modifier = Modifier.height(Constants.DEFAULT_SPACE.dp))
            ContentWithTitle(title = "Char. sub group related time (minutes):", value = characteristic.characteristicSubGroup.charSubGroup.measurementGroupRelatedTime?.toString() ?: NoString.str, titleWight = 0.76f)
            Spacer(modifier = Modifier.height(Constants.DEFAULT_SPACE.dp))
//            ToDo - find analysis and correct all fields, specially this one
            ContentWithTitle(title = "Movements related time (minutes):", value = "0.93", titleWight = 0.76f)
            Spacer(modifier = Modifier.height((Constants.DEFAULT_SPACE / 2).dp))
        }
    }
}