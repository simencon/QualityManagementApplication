package com.simenko.qmapp.ui.main.team

import android.annotation.SuppressLint
import android.widget.Toast

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.simenko.qmapp.domain.SelectedNumber
import com.simenko.qmapp.domain.entities.DomainUser
import com.simenko.qmapp.other.Constants
import com.simenko.qmapp.other.Constants.CARD_OFFSET
import com.simenko.qmapp.ui.common.TopLevelSingleRecordDetails
import com.simenko.qmapp.ui.common.TopLevelSingleRecordMainHeader
import com.simenko.qmapp.utils.StringUtils
import com.simenko.qmapp.utils.dp
import kotlin.math.roundToInt

@Composable
fun UserComposition(
    appModel: TeamViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val items by appModel.users.collectAsStateWithLifecycle(listOf())

    val onClickDetailsLambda: (String) -> Unit = { /*appModel.setCurrentOrderVisibility(dId = SelectedNumber(it))*/ }
    val onClickActionsLambda = remember<(String) -> Unit> { { /*appModel.setCurrentOrderVisibility(aId = SelectedNumber(it))*/ } }
    val onClickDeleteLambda = remember<(String) -> Unit> { { /*appModel.deleteRecord(it)*/ } }
    val onClickEditLambda =
        remember<(String, String) -> Unit> { { p1, p2 -> Toast.makeText(context, "id = $p1, name = $p2", Toast.LENGTH_LONG).show() } }
    val listState = rememberLazyListState()

    val lastItemIsVisible by remember {
        derivedStateOf {
            listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index == listState.layoutInfo.totalItemsCount - 1
        }
    }

    if (lastItemIsVisible) appModel.onListEnd(FabPosition.Center) else appModel.onListEnd(FabPosition.End)

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = listState
    ) {
        items(items = items, key = { it.email }) { teamMember ->
            UserCard(
                item = teamMember,
                onClickDetails = { onClickDetailsLambda(it) },
                onDoubleClick = { onClickActionsLambda(it) },
                onClickDelete = { onClickDeleteLambda(it) },
                onClickEdit = { p1, p2 -> onClickEditLambda(p1, p2) }
            )
        }
    }
}

@SuppressLint("UnusedTransitionTargetStateParameter")
@Composable
fun UserCard(
    item: DomainUser,
    onClickDetails: (String) -> Unit,
    onDoubleClick: (String) -> Unit,
    onClickDelete: (String) -> Unit,
    onClickEdit: (String, String) -> Unit
) {
    val transitionState = remember {
        MutableTransitionState(item.isExpanded).apply {
            targetState = !item.isExpanded
        }
    }

    val transition = updateTransition(transitionState, "cardTransition")

    val offsetTransition by transition.animateFloat(
        label = "cardOffsetTransition",
        transitionSpec = { tween(durationMillis = Constants.ANIMATION_DURATION) },
        targetValueByState = { if (item.isExpanded) CARD_OFFSET.dp() else 0f },
    )
    val containerColor = when (item.isExpanded) {
        true -> MaterialTheme.colorScheme.secondaryContainer
        false -> MaterialTheme.colorScheme.surfaceVariant
    }

    val borderColor = when (item.detailsVisibility) {
        true -> MaterialTheme.colorScheme.outline
        false -> when (item.isExpanded) {
            true -> MaterialTheme.colorScheme.secondaryContainer
            false -> MaterialTheme.colorScheme.surfaceVariant
        }
    }

    Box(Modifier.fillMaxWidth()) {
        Row(Modifier.padding(horizontal = 3.dp, vertical = 3.dp)) {
            IconButton(
                modifier = Modifier.size(Constants.ACTION_ITEM_SIZE.dp),
                onClick = { onClickDelete(item.email) },
                content = { Icon(imageVector = Icons.Filled.Delete, contentDescription = "delete action") }
            )

            IconButton(
                modifier = Modifier.size(Constants.ACTION_ITEM_SIZE.dp),
                onClick = { onClickEdit(item.email, item.email) },
                content = { Icon(imageVector = Icons.Filled.Edit, contentDescription = "edit action") }
            )
        }
        Card(
            colors = CardDefaults.cardColors(containerColor = containerColor),
            border = BorderStroke(width = 1.dp, borderColor),
            elevation = CardDefaults.cardElevation(4.dp),
            modifier = Modifier
                .padding(vertical = 4.dp, horizontal = 8.dp)
                .offset { IntOffset(offsetTransition.roundToInt(), 0) }
                .pointerInput(item.email) {
                    detectTapGestures(onDoubleTap = { onDoubleClick(item.email) })
                },
        ) {
            User(
                item = item,
                onClickDetails = onClickDetails,
                modifier = Modifier.padding(Constants.CARDS_PADDING)
            )
        }
    }
}

@Composable
fun User(
    item: DomainUser,
    onClickDetails: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = Modifier
            .animateContentSize(animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start
    ) {
        TopLevelSingleRecordMainHeader(modifier, item, item.detailsVisibility, onClickDetails)

        if (item.detailsVisibility) {
            TopLevelSingleRecordDetails("Email:", StringUtils.getMail(item.email), modifier, 0.2f)
            TopLevelSingleRecordDetails("Department:", item.department ?: "-", modifier, 0.2f)
            TopLevelSingleRecordDetails("Job role:", item.jobRole ?: "-", modifier, 0.2f)
        }
    }
}