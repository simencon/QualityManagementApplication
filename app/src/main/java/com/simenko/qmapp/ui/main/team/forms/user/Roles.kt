package com.simenko.qmapp.ui.main.team.forms.user

import android.annotation.SuppressLint
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.simenko.qmapp.domain.entities.DomainUserRole
import com.simenko.qmapp.other.Constants
import com.simenko.qmapp.other.Constants.CARD_OFFSET
import com.simenko.qmapp.ui.common.TopLevelSingleRecordHeader
import com.simenko.qmapp.utils.StringUtils.concatTwoStrings
import com.simenko.qmapp.utils.dp
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RolesHeader(
    modifier: Modifier,
    userRoles: List<DomainUserRole>,
    userRolesError: Boolean,
    onClickActions: (String) -> Unit,
    onClickDelete: (String) -> Unit,
    onClickAdd: () -> Unit
) {
    val tint = if (userRolesError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.surfaceTint
    var detailsVisibility: Boolean by rememberSaveable { mutableStateOf(false) }

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(0.dp, 0.dp, 0.dp, 0.dp, 0.dp, 0.dp),
        modifier = Modifier
            .width(320.dp)
            .clickable { detailsVisibility = !detailsVisibility }
    ) {
        Column(
            modifier = Modifier.animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
        ) {
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier.padding(all = 12.dp),
                    imageVector = Icons.Default.AdminPanelSettings,
                    contentDescription = "Authorities",
                    tint = tint
                )
                Text(
                    color = tint,
                    text = "User roles",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Normal),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = modifier
                        .weight(1f)
                        .padding(start = 3.dp)
                )

                val tClr = if (userRolesError)
                    IconButtonDefaults.iconToggleButtonColors(
                        contentColor = MaterialTheme.colorScheme.error,
                        checkedContentColor = MaterialTheme.colorScheme.error
                    )
                else
                    IconButtonDefaults.iconToggleButtonColors()

                IconToggleButton(checked = detailsVisibility, onCheckedChange = { detailsVisibility = it }, colors = tClr) {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = detailsVisibility)
                }
            }
            if (detailsVisibility) {
                Column(modifier = modifier) {
                    Divider(modifier = modifier.height(1.dp), color = MaterialTheme.colorScheme.secondary)
                    RolesFlow(
                        userRoles = userRoles,
                        onClickActions = onClickActions,
                        onClickDelete = onClickDelete,
                        onClickAdd = onClickAdd
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RolesFlow(
    userRoles: List<DomainUserRole>,
    onClickActions: (String) -> Unit,
    onClickDelete: (String) -> Unit,
    onClickAdd: () -> Unit
) {
    Column(modifier = Modifier.padding(all = 2.dp), horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.Center) {
        FlowRow(modifier = Modifier.padding(vertical = 2.dp)) {
            userRoles.forEach { role ->
                RoleCard(
                    role = role,
                    cardOffset = (CARD_OFFSET / 2).dp(),
                    onClickActions = { onClickActions(it) },
                    onClickDelete = { onClickDelete(it) }
                )
            }
        }
        Divider(thickness = 0.dp, color = Color.Transparent)
        FloatingActionButton(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            onClick = onClickAdd,
            content = { Icon(imageVector = Icons.Default.Add, contentDescription = "Add sub order") }
        )
    }
}

@SuppressLint("UnusedTransitionTargetStateParameter")
@Composable
fun RoleCard(
    role: DomainUserRole,
    cardOffset: Float,
    onClickActions: (String) -> Unit,
    onClickDelete: (String) -> Unit
) {
    val transitionState = remember {
        MutableTransitionState(role.isExpanded).apply {
            targetState = !role.isExpanded
        }
    }

    val transition = updateTransition(transitionState, "cardTransition")

    val offsetTransition by transition.animateFloat(
        label = "cardOffsetTransition",
        transitionSpec = { tween(durationMillis = Constants.ANIMATION_DURATION) },
        targetValueByState = { if (role.isExpanded) cardOffset else 0f },
    )

    val containerColor = when (role.isExpanded) {
        true -> MaterialTheme.colorScheme.secondaryContainer
        false -> MaterialTheme.colorScheme.primaryContainer
    }

    val borderColor = when (role.detailsVisibility) {
        true -> MaterialTheme.colorScheme.outline
        false -> when (role.isExpanded) {
            true -> MaterialTheme.colorScheme.secondaryContainer
            false -> MaterialTheme.colorScheme.primaryContainer
        }
    }

    Box(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
    ) {

        Row {
            IconButton(
                modifier = Modifier.size(Constants.ACTION_ITEM_SIZE.dp),
                onClick = { onClickDelete(role.getRecordId()) },
                content = { Icon(imageVector = Icons.Filled.Delete, contentDescription = "delete action") }
            )
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = containerColor),
            border = BorderStroke(width = 1.dp, borderColor),
            elevation = CardDefaults.cardElevation(4.dp),
            modifier = Modifier
                .fillMaxWidth()
                .offset { IntOffset(offsetTransition.roundToInt(), 0) }
                .pointerInput(role.getRecordId()) {
                    detectTapGestures(
                        onDoubleTap = { onClickActions(role.getRecordId()) }
                    )
                }
        ) {
            Role(item = role)
        }
    }
}

@Composable
fun Role(item: DomainUserRole) {
    Column(
        modifier = Modifier.padding(start = 4.dp),
    ) {
        TopLevelSingleRecordHeader("Function / Role level:", concatTwoStrings(item.function, item.roleLevel), 0.35f)
        TopLevelSingleRecordHeader("Access level:", item.accessLevel, 0.35f)
    }
}