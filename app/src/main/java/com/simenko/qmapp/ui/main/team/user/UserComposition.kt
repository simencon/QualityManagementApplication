package com.simenko.qmapp.ui.main.team.user

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
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.simenko.qmapp.domain.EmptyString
import com.simenko.qmapp.domain.NoRecordStr
import com.simenko.qmapp.domain.NoString
import com.simenko.qmapp.domain.SelectedString
import com.simenko.qmapp.domain.entities.DomainUser
import com.simenko.qmapp.other.Constants
import com.simenko.qmapp.other.Constants.CARD_OFFSET
import com.simenko.qmapp.ui.common.RecordActionTextBtn
import com.simenko.qmapp.ui.common.TopLevelSingleRecordDetails
import com.simenko.qmapp.ui.common.TopLevelSingleRecordMainHeader
import com.simenko.qmapp.ui.dialogs.UserExistDialog
import com.simenko.qmapp.ui.dialogs.scrollToSelectedStringItem
import com.simenko.qmapp.ui.main.team.TeamViewModel
import com.simenko.qmapp.utils.StringUtils
import com.simenko.qmapp.utils.dp
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

@Composable
fun UserComposition(
    viewModel: TeamViewModel = hiltViewModel(),
    isUsersPage: Boolean,
    onClickAuthorize: (String) -> Unit,
    onClickEdit: (String) -> Unit
) {
    val items by viewModel.users.collectAsStateWithLifecycle(listOf())

    LaunchedEffect(isUsersPage) {
        viewModel.setUsersFilter(!isUsersPage)
    }

    val isRemoveUserDialogVisible by viewModel.isRemoveUserDialogVisible.collectAsStateWithLifecycle()
    val selectedRecord by viewModel.selectedUserRecord.collectAsStateWithLifecycle()

    val onClickDetailsLambda: (String) -> Unit = { viewModel.setCurrentUserVisibility(dId = SelectedString(it)) }
    val onClickActionsLambda = remember<(String) -> Unit> { { if (isUsersPage) viewModel.setCurrentUserVisibility(aId = SelectedString(it)) } }
    val onClickAuthorizeLambda = remember<(String) -> Unit> { { onClickAuthorize(it) } }
    val onClickRemoveLambda = remember<(String) -> Unit> {
        {
            viewModel.setSelectedUserRecord(it)
            viewModel.setRemoveUserDialogVisibility(true)
        }
    }
    val onClickEditLambda = remember<(String) -> Unit> { { onClickEdit(it) } }

    val listState = rememberLazyListState()

    LaunchedEffect(selectedRecord) {
        selectedRecord.getContentIfNotHandled()?.let { recordId ->
            if (recordId != NoRecordStr.str) {
                listState.scrollToSelectedStringItem(list = items.map { it.email }.toList(), selectedId = recordId)
                delay(25)
                items.find { it.email == recordId }?.let { if (!it.detailsVisibility) onClickDetailsLambda(it.email) }
            }
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = listState
    ) {
        items(items = items, key = { it.email }) { teamMember ->
            UserCard(
                item = teamMember,
                onClickDetails = { onClickDetailsLambda(it) },
                onDoubleClick = { onClickActionsLambda(it) },
                onClickAuthorize = { onClickAuthorizeLambda(it) },
                onClickRemove = { onClickRemoveLambda(it) },
                onClickEdit = { onClickEditLambda(it) }
            )
        }
    }

    if (isRemoveUserDialogVisible)
        UserExistDialog(
            msg = "Remove user ${selectedRecord.peekContent()} from authorized users?",
            btn = Pair("Cancel", "Remove"),
            onCancel = { viewModel.setRemoveUserDialogVisibility(false) },
            onOk = { viewModel.removeUser(selectedRecord.peekContent()) },
            onDismiss = { viewModel.setRemoveUserDialogVisibility(false) }
        )
}

@SuppressLint("UnusedTransitionTargetStateParameter")
@Composable
fun UserCard(
    item: DomainUser,
    onClickDetails: (String) -> Unit,
    onDoubleClick: (String) -> Unit,
    onClickAuthorize: (String) -> Unit,
    onClickRemove: (String) -> Unit,
    onClickEdit: (String) -> Unit
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
        targetValueByState = { if (item.isExpanded) (CARD_OFFSET / 2).dp() else 0f },
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
                onClick = { onClickEdit(item.email) },
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
                onClickAuthorize = onClickAuthorize,
                onClickRemove = onClickRemove,
                modifier = Modifier.padding(Constants.CARDS_PADDING)
            )
        }
    }
}

@Composable
fun User(
    item: DomainUser,
    onClickDetails: (String) -> Unit,
    onClickAuthorize: (String) -> Unit,
    onClickRemove: (String) -> Unit,
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
            val department = item.department + if (item.subDepartment.isNullOrEmpty()) EmptyString.str else "/${item.subDepartment}"
            Divider(modifier = modifier.height(1.dp), color = MaterialTheme.colorScheme.secondary)
            TopLevelSingleRecordDetails("Job role: ", item.jobRole ?: NoString.str, modifier, 0.3f)
            TopLevelSingleRecordDetails("Department: ", department, modifier, 0.3f)
            TopLevelSingleRecordDetails("Email: ", StringUtils.getMail(item.email), modifier, 0.3f)
            TopLevelSingleRecordDetails("Phone num.: ", StringUtils.getMail(item.phoneNumber.toString()), modifier, 0.3f)
            item.roles?.forEach {
                TopLevelSingleRecordDetails("System role: ", it, modifier, 0.3f)
            } ?: TopLevelSingleRecordDetails("System role: ", NoString.str, modifier, 0.3f)
            TopLevelSingleRecordDetails("Email verified: ", item.isEmailVerified.toString(), modifier, 0.3f)
            TopLevelSingleRecordDetails("Account expired: ", item.accountNonExpired.toString(), modifier, 0.3f)
            TopLevelSingleRecordDetails("Account locked: ", item.accountNonLocked.toString(), modifier, 0.3f)
            TopLevelSingleRecordDetails("Credentials expired: ", item.credentialsNonExpired.toString(), modifier, 0.3f)
            TopLevelSingleRecordDetails("Enabled: ", item.enabled.toString(), modifier, 0.3f)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!item.restApiUrl.isNullOrEmpty()) {
                    val containerColor =
                        if (item.isExpanded) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.errorContainer
                    RecordActionTextBtn(
                        text = "Remove user",
                        onClick = { onClickRemove(item.email) },
                        colors = Pair(
                            ButtonDefaults.textButtonColors(containerColor = containerColor, contentColor = contentColorFor(containerColor)),
                            null
                        ),
                        elevation = ButtonDefaults.buttonElevation(4.dp)
                    )
                } else {
                    val containerColor =
                        if (item.isExpanded) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.tertiary
                    RecordActionTextBtn(
                        text = "Authorize user",
                        onClick = { onClickAuthorize(item.email) },
                        colors = Pair(
                            ButtonDefaults.buttonColors(containerColor = containerColor, contentColor = contentColorFor(containerColor)),
                            null
                        ),
                        elevation = ButtonDefaults.buttonElevation(4.dp)
                    )
                }
            }
        }
    }
}