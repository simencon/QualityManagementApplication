package com.simenko.qmapp.presentation.ui.main.team.user

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
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.simenko.qmapp.domain.EmptyString
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.domain.NoString
import com.simenko.qmapp.domain.SelectedString
import com.simenko.qmapp.domain.entities.DomainUser
import com.simenko.qmapp.other.Constants
import com.simenko.qmapp.other.Constants.CARD_OFFSET
import com.simenko.qmapp.other.Constants.DEFAULT_SPACE
import com.simenko.qmapp.presentation.ui.common.ContentWithTitle
import com.simenko.qmapp.presentation.ui.common.RecordActionTextBtn
import com.simenko.qmapp.presentation.ui.common.SimpleRecordHeader
import com.simenko.qmapp.presentation.ui.dialogs.UserExistDialog
import com.simenko.qmapp.presentation.ui.dialogs.scrollToSelectedItem
import com.simenko.qmapp.presentation.ui.main.team.TeamViewModel
import com.simenko.qmapp.utils.BaseFilter
import com.simenko.qmapp.utils.StringUtils
import com.simenko.qmapp.utils.dp
import com.simenko.qmapp.utils.observeAsState
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun Users(
    viewModel: TeamViewModel = hiltViewModel(),
    userId: String,
    isUsersPage: Boolean
) {
    val items by viewModel.users.collectAsStateWithLifecycle(listOf())
    val currentUserVisibility by viewModel.currentUserVisibility.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.onEntered(employeeId = NoRecord.num, userId = userId, selectedTabIndex = if (isUsersPage) 1 else 2, isFabVisible = false)
        viewModel.setUsersFilter(BaseFilter(newUsers = !isUsersPage))
    }

    val isRemoveUserDialogVisible by viewModel.isRemoveUserDialogVisible.collectAsStateWithLifecycle()
    val scrollToRecord by viewModel.scrollToRecord.collectAsStateWithLifecycle(null)

    val onClickDetailsLambda: (String) -> Unit = { viewModel.setUsersVisibility(dId = SelectedString(it)) }
    val onClickActionsLambda = remember<(String) -> Unit> { { if (isUsersPage) viewModel.setUsersVisibility(aId = SelectedString(it)) } }
    val onClickAuthorizeLambda = remember<(String) -> Unit> { { viewModel.onUserAuthorizeClick(it) } }
    val onClickRemoveLambda = remember<(String) -> Unit> { { viewModel.setRemoveUserDialogVisibility(true, it) } }
    val onClickEditLambda = remember<(String) -> Unit> { { viewModel.onUserEditClick(it) } }

    val lifecycleState = LocalLifecycleOwner.current.lifecycle.observeAsState()

    LaunchedEffect(lifecycleState.value) {
        when (lifecycleState.value) {
            Lifecycle.Event.ON_RESUME -> viewModel.setIsComposed(true)
            Lifecycle.Event.ON_STOP -> viewModel.setIsComposed(false)
            else -> {}
        }
    }

    val listState = rememberLazyListState()
    LaunchedEffect(scrollToRecord) {
        scrollToRecord?.let { record ->
            record.second.getContentIfNotHandled()?.let { userId ->
                viewModel.channel.trySend(this.launch { listState.scrollToSelectedItem(list = items.map { it.email }.toList(), selectedId = userId) })
            }
        }
    }

    LazyColumn(modifier = Modifier.fillMaxSize(), state = listState) {
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
            msg = "Remove user ${currentUserVisibility.first.str} from authorized users?",
            btn = Pair("Cancel", "Remove"),
            onCancel = { viewModel.setRemoveUserDialogVisibility(false) },
            onOk = { viewModel.removeUser(currentUserVisibility.first.str) },
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
    val transitionState = remember { MutableTransitionState(item.isExpanded).apply { targetState = !item.isExpanded } }
    val transition = updateTransition(transitionState, "cardTransition")

    val offsetTransition by transition.animateFloat(
        label = "cardOffsetTransition",
        transitionSpec = { tween(durationMillis = Constants.ANIMATION_DURATION) },
        targetValueByState = { (if (item.isExpanded) CARD_OFFSET else 0f).dp() },
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
        Row(Modifier.padding(all = (DEFAULT_SPACE / 2).dp)) {
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
                .padding(horizontal = (DEFAULT_SPACE / 2).dp, vertical = (DEFAULT_SPACE / 2).dp)
                .offset { IntOffset(offsetTransition.roundToInt(), 0) }
                .pointerInput(item.email) { detectTapGestures(onDoubleTap = { onDoubleClick(item.email) }) },
        ) {
            User(
                item = item,
                onClickDetails = onClickDetails,
                onClickAuthorize = onClickAuthorize,
                onClickRemove = onClickRemove
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
) {
    Column(modifier = Modifier.animateContentSize(animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow))) {
        SimpleRecordHeader(item, item.detailsVisibility, onClickDetails)
        if (item.detailsVisibility) {
            Column(
                modifier = Modifier.padding(start = DEFAULT_SPACE.dp, top = DEFAULT_SPACE.dp, end = DEFAULT_SPACE.dp, bottom = (DEFAULT_SPACE / 2).dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.End
            ) {
                val modifier: Modifier = Modifier.padding(bottom = DEFAULT_SPACE.dp)
                val department = item.department + if (item.subDepartment.isNullOrEmpty()) EmptyString.str else "/${item.subDepartment}"
                HorizontalDivider(modifier = Modifier.height(1.dp), color = MaterialTheme.colorScheme.secondary)
                Spacer(modifier = Modifier.height(DEFAULT_SPACE.dp))
                ContentWithTitle(modifier = modifier, title = "Job role: ", value = item.jobRole ?: NoString.str, titleWight = 0.3f)
                ContentWithTitle(modifier = modifier, title = "Department: ", value = department, titleWight = 0.3f)
                ContentWithTitle(modifier = modifier, title = "Email: ", value = StringUtils.getMail(item.email), titleWight = 0.3f)
                ContentWithTitle(modifier = modifier, title = "Phone num.: ", value = StringUtils.getMail(item.phoneNumber.toString()), titleWight = 0.3f)
                item.roles?.forEach { ContentWithTitle(modifier = modifier, title = "System role: ", value = it, titleWight = 0.3f) }
                    ?: ContentWithTitle(modifier = modifier, title = "System role: ", value = NoString.str, titleWight = 0.3f)
                ContentWithTitle(modifier = modifier, title = "Email verified: ", value = item.isEmailVerified.toString(), titleWight = 0.3f)
                ContentWithTitle(modifier = modifier, title = "Account expired: ", value = item.accountNonExpired.toString(), titleWight = 0.3f)
                ContentWithTitle(modifier = modifier, title = "Account locked: ", value = item.accountNonLocked.toString(), titleWight = 0.3f)
                ContentWithTitle(modifier = modifier, title = "Credentials expired: ", value = item.credentialsNonExpired.toString(), titleWight = 0.3f)
                ContentWithTitle(modifier = modifier, title = "Enabled: ", value = item.enabled.toString(), titleWight = 0.3f)
                if (!item.restApiUrl.isNullOrEmpty()) {
                    val containerColor = if (item.isExpanded) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.errorContainer
                    RecordActionTextBtn(
                        text = "Remove user",
                        onClick = { onClickRemove(item.email) },
                        colors = Pair(ButtonDefaults.textButtonColors(containerColor = containerColor, contentColor = contentColorFor(containerColor)), null),
                        elevation = ButtonDefaults.buttonElevation(4.dp)
                    )
                } else {
                    val containerColor = if (item.isExpanded) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.tertiary
                    RecordActionTextBtn(
                        text = "Authorize user",
                        onClick = { onClickAuthorize(item.email) },
                        colors = Pair(ButtonDefaults.buttonColors(containerColor = containerColor, contentColor = contentColorFor(containerColor)), null),
                        elevation = ButtonDefaults.buttonElevation(4.dp)
                    )
                }
            }
        }
    }
}