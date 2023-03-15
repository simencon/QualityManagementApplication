package com.simenko.qmapp.ui.main.investigations.steps

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.animation.animateColor
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simenko.qmapp.R
import com.simenko.qmapp.domain.*
import com.simenko.qmapp.utils.StringUtils
import com.google.accompanist.flowlayout.FlowRow
import com.simenko.qmapp.ui.main.*
import com.simenko.qmapp.ui.common.ACTION_ITEM_SIZE
import com.simenko.qmapp.ui.common.ANIMATION_DURATION
import com.simenko.qmapp.ui.common.ActionsRow
import com.simenko.qmapp.ui.common.CARD_OFFSET
import com.simenko.qmapp.ui.neworder.ActionType
import com.simenko.qmapp.ui.neworder.launchNewItemActivity
import com.simenko.qmapp.ui.theme.*
import com.simenko.qmapp.utils.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun SubOrdersFlowColumn(
    modifier: Modifier = Modifier,
    parentId: Int = 0,
    appModel: QualityManagementViewModel,
    context: Context,
    createdRecord: CreatedRecord? = null
) {
    val observeSubOrders by appModel.completeSubOrdersMediator.observeAsState()

    observeSubOrders?.first?.forEach { it ->
        when(it.subOrder.itemPreffix) {
            "p" -> {
                it.itemVersionComplete = appModel.itemVersionsCompleteP.value?.find { item -> item.itemVersion.id == it.subOrder.itemVersionId }
            }
            "c" -> {
                it.itemVersionComplete = appModel.itemVersionsCompleteC.value?.find { item -> item.itemVersion.id == it.subOrder.itemVersionId }
            }
            "s" -> {
                it.itemVersionComplete = appModel.itemVersionsCompleteS.value?.find { item -> item.itemVersion.id == it.subOrder.itemVersionId }
            }
        }
    }

    val coroutineScope = rememberCoroutineScope()
    var lookForRecord by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(lookForRecord) {
        if (observeSubOrders?.first != null && createdRecord != null)
            coroutineScope.launch {
                delay(200)
                val subOrder = observeSubOrders?.first!!.find {
                    it.subOrder.id == createdRecord.subOrderId
                }
                if (subOrder != null)
                    appModel.changeCompleteSubOrdersDetailsVisibility(subOrder.subOrder.id)

            } else if (createdRecord != null && createdRecord.subOrderId != 0) {
            delay(50)
            lookForRecord = !lookForRecord
        }
    }

    var clickCounter = 0

    observeSubOrders?.apply {
        if (observeSubOrders!!.first != null) {
            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.Center) {
                FlowRow(modifier = modifier) {
                    observeSubOrders!!.first!!.forEach { subOrder ->
                        if (subOrder.subOrder.orderId == parentId) {

                            Box(Modifier.fillMaxWidth()) {
                                ActionsRow(
                                    subOrder = subOrder,
                                    actionIconSize = ACTION_ITEM_SIZE.dp,
                                    onDeleteSubOrder = {
                                        appModel.deleteSubOrder(it)
                                    },
                                    onEdit = {
                                        launchNewItemActivity(
                                            context,
                                            ActionType.EDIT_SUB_ORDER,
                                            subOrder.subOrder.orderId,
                                            subOrder.subOrder.id
                                        )
                                    },
                                    onFavorite = {}
                                )

                                SubOrderCard(
                                    modifier = modifier,
                                    viewModel = appModel,
                                    subOrder = subOrder,
                                    onClickDetails = { it ->
                                        appModel.changeCompleteSubOrdersDetailsVisibility(it.subOrder.id)
                                    },
                                    cardOffset = CARD_OFFSET.dp(),
                                    onChangeExpandState = {
                                        clickCounter++
                                        if (clickCounter == 1) {
                                            CoroutineScope(Dispatchers.Main).launch {
                                                delay(200)
                                                clickCounter--
                                            }
                                        }
                                        if (clickCounter == 2) {
                                            clickCounter = 0
                                            appModel.changeCompleteSubOrdersExpandState(it)
                                        }
                                    }
                                )
                            }
                            Divider(thickness = 4.dp, color = Color.Transparent)
                        }
                    }
                }
                Divider(modifier = modifier.height(0.dp))
                FloatingActionButton(
                    containerColor = _level_2_record_color,
                    modifier = Modifier.padding(vertical = 4.dp),
                    onClick = {
                        launchNewItemActivity(context, ActionType.ADD_SUB_ORDER, parentId)
                    },
                    content = {
                        androidx.compose.material.Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = Primary900
                        )
                    }
                )
            }

        }
    }
}

@SuppressLint("UnusedTransitionTargetStateParameter")
@Composable
fun SubOrderCard(
    modifier: Modifier = Modifier,
    viewModel: QualityManagementViewModel? = null,
    subOrder: DomainSubOrderComplete,
    onClickDetails: (DomainSubOrderComplete) -> Unit,
    cardOffset: Float,
    onChangeExpandState: (DomainSubOrderComplete) -> Unit,
) {
    val transitionState = remember {
        MutableTransitionState(subOrder.isExpanded).apply {
            targetState = !subOrder.isExpanded
        }
    }

    val transition = updateTransition(transitionState, "cardTransition")

    val offsetTransition by transition.animateFloat(
        label = "cardOffsetTransition",
        transitionSpec = { tween(durationMillis = ANIMATION_DURATION) },
        targetValueByState = { if (subOrder.isExpanded) cardOffset else 0f },
    )

    val cardBgColor by transition.animateColor(
        label = "cardBgColorTransition",
        transitionSpec = { tween(durationMillis = ANIMATION_DURATION) },
        targetValueByState = {
            if (subOrder.isExpanded) Accent200 else
                if (subOrder.detailsVisibility) {
                    _level_2_record_color_details
                } else {
                    _level_2_record_color
                }
        }
    )

    val cardElevation by transition.animateDp(
        label = "cardElevation",
        transitionSpec = { tween(durationMillis = ANIMATION_DURATION) },
        targetValueByState = { if (subOrder.isExpanded) 40.dp else 2.dp }
    )

    Card(
        colors = CardDefaults.cardColors(
            containerColor = cardBgColor,
        ),
        modifier = modifier
            .fillMaxWidth()
            .offset { IntOffset(offsetTransition.roundToInt(), 0) }
            .clickable { onChangeExpandState(subOrder) },
        elevation = CardDefaults.cardElevation(cardElevation),
    ) {
        SubOrder(
            modifier = modifier,
            viewModel = viewModel,
            subOrder = subOrder,
            onClickDetails = { onClickDetails(subOrder) }
        )
    }
}

@Composable
fun SubOrder(
    modifier: Modifier = Modifier,
    viewModel: QualityManagementViewModel? = null,
    onClickDetails: () -> Unit = {},
    subOrder: DomainSubOrderComplete = getSubOrders()[0]
) {
    Column(
        modifier = Modifier
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
            .padding(top = 0.dp, start = 4.dp, end = 4.dp, bottom = 0.dp),
    ) {
        Row(
            modifier = Modifier.padding(top = 0.dp, start = 0.dp, end = 0.dp, bottom = 0.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .padding(top = 0.dp, start = 4.dp, end = 4.dp, bottom = 0.dp)
                    .weight(0.90f),
            ) {
                Row(
                    modifier = Modifier.padding(
                        top = 0.dp,
                        start = 0.dp,
                        end = 0.dp,
                        bottom = 4.dp
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Num.:",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .weight(weight = 0.15f)
                            .padding(top = 7.dp, start = 0.dp, end = 0.dp, bottom = 0.dp)
                    )
                    Text(
                        text = subOrder.subOrder.subOrderNumber.toString(),
                        style = MaterialTheme.typography.titleSmall.copy(fontSize = 14.sp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .weight(weight = 0.15f)
                            .padding(top = 0.dp, start = 3.dp, end = 0.dp, bottom = 0.dp)
                    )
                    Text(
                        text = "Status:",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .weight(weight = 0.15f)
                            .padding(top = 5.dp, start = 3.dp, end = 0.dp, bottom = 0.dp)
                    )
                    Text(
                        text = subOrder.status.statusDescription ?: "-",
                        style = MaterialTheme.typography.titleSmall.copy(fontSize = 14.sp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .weight(weight = 0.25f)
                            .padding(top = 0.dp, start = 3.dp, end = 0.dp, bottom = 0.dp)
                    )
                    Text(
                        text = "Quantity:",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .weight(weight = 0.18f)
                            .padding(top = 5.dp, start = 3.dp, end = 0.dp, bottom = 0.dp)
                    )
                    Text(
                        text = subOrder.subOrder.samplesCount.toString(),
                        style = MaterialTheme.typography.titleSmall.copy(fontSize = 14.sp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .weight(weight = 0.12f)
                            .padding(top = 0.dp, start = 3.dp, end = 0.dp, bottom = 0.dp)
                    )
                }
                Row(
                    modifier = Modifier.padding(
                        top = 0.dp,
                        start = 0.dp,
                        end = 0.dp,
                        bottom = 4.dp
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Process:",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .weight(weight = 0.20f)
                            .padding(top = 7.dp, start = 0.dp, end = 0.dp, bottom = 0.dp)
                    )
                    Text(
                        text = StringUtils.concatFourStrings(
                            subOrder.department.depAbbr,
                            subOrder.subDepartment.subDepAbbr,
                            subOrder.channel.channelAbbr,
                            subOrder.line.lineAbbr
                        ),
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .weight(weight = 0.80f)
                            .padding(top = 0.dp, start = 3.dp, end = 0.dp, bottom = 0.dp)
                    )
                }
                Row(
                    modifier = Modifier.padding(
                        top = 0.dp,
                        start = 0.dp,
                        end = 0.dp,
                        bottom = 4.dp
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Product:",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .weight(weight = 0.20f)
                            .padding(top = 5.dp, start = 0.dp, end = 0.dp, bottom = 0.dp)
                    )
                    Text(
                        text = StringUtils.concatTwoStrings1(
                            StringUtils.concatTwoStrings3(
                                subOrder.itemVersionComplete?.itemComplete?.key?.componentKey,
                                subOrder.itemVersionComplete?.itemComplete?.item?.itemDesignation
                            ), subOrder.itemVersionComplete?.itemVersion?.versionDescription
                        ),
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .weight(weight = 0.80f)
                            .padding(top = 0.dp, start = 3.dp, end = 0.dp, bottom = 0.dp)
                    )
                }
                Row(
                    modifier = Modifier.padding(
                        top = 0.dp,
                        start = 0.dp,
                        end = 0.dp,
                        bottom = 4.dp
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Operation:",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .weight(weight = 0.20f)
                            .padding(top = 5.dp, start = 0.dp, end = 0.dp, bottom = 0.dp)
                    )
                    Text(
                        text = StringUtils.concatTwoStrings2(
                            subOrder.operation.operationAbbr,
                            subOrder.operation.operationDesignation
                        ),
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .weight(weight = 0.80f)
                            .padding(top = 0.dp, start = 3.dp, end = 0.dp, bottom = 0.dp)
                    )
                }
            }
            IconButton(
                onClick = onClickDetails, modifier = Modifier
                    .weight(weight = 0.10f)
                    .padding(top = 0.dp, start = 0.dp, end = 0.dp, bottom = 0.dp)
                    .fillMaxWidth()
            ) {
                Icon(
                    imageVector = if (subOrder.detailsVisibility) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = if (subOrder.detailsVisibility) {
                        stringResource(R.string.show_less)
                    } else {
                        stringResource(R.string.show_more)
                    },
                    modifier = Modifier.padding(top = 0.dp, start = 0.dp, end = 0.dp, bottom = 0.dp)
                )
            }
        }

        SubOrderDetails(modifier = modifier, viewModel = viewModel, subOrder = subOrder)
    }
}

@Composable
fun SubOrderDetails(
    modifier: Modifier = Modifier,
    viewModel: QualityManagementViewModel? = null,
    subOrder: DomainSubOrderComplete = getSubOrders()[0]
) {
    if (subOrder.detailsVisibility) {
        Divider(modifier = modifier.height(1.dp), color = MaterialTheme.colorScheme.secondary)
        Row(
            modifier = Modifier.padding(top = 0.dp, start = 8.dp, end = 0.dp, bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Ordered by:",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(weight = 0.22f)
                    .padding(top = 5.dp, start = 0.dp, end = 0.dp, bottom = 0.dp)
            )
            Text(
                text = subOrder.orderedBy.fullName,
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(weight = 0.78f)
                    .padding(top = 0.dp, start = 3.dp, end = 0.dp, bottom = 0.dp)
            )
        }
        Row(
            modifier = Modifier.padding(top = 0.dp, start = 8.dp, end = 0.dp, bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Created:",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(weight = 0.22f)
                    .padding(top = 5.dp, start = 0.dp, end = 0.dp, bottom = 0.dp)
            )
            Text(
                text = StringUtils.getDateTime(subOrder.subOrder.createdDate),
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(weight = 0.78f)
                    .padding(top = 0.dp, start = 3.dp, end = 0.dp, bottom = 0.dp)
            )
        }
        Row(
            modifier = Modifier.padding(top = 0.dp, start = 8.dp, end = 0.dp, bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Completed by:",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(weight = 0.22f)
                    .padding(top = 5.dp, start = 0.dp, end = 0.dp, bottom = 0.dp)
            )
            Text(
                text = subOrder.completedBy?.fullName ?: "-",
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(weight = 0.78f)
                    .padding(top = 0.dp, start = 3.dp, end = 0.dp, bottom = 0.dp)
            )
        }
        Row(
            modifier = Modifier.padding(top = 0.dp, start = 8.dp, end = 0.dp, bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Completed:",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(weight = 0.22f)
                    .padding(top = 5.dp, start = 0.dp, end = 0.dp, bottom = 0.dp)
            )
            Text(
                text = StringUtils.getDateTime(subOrder.subOrder.completedDate),
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(weight = 0.78f)
                    .padding(top = 0.dp, start = 3.dp, end = 0.dp, bottom = 0.dp)
            )
        }
        if (viewModel != null)
            SubOrderTasksFlowColumn(
                modifier = Modifier,
                parentId = subOrder.subOrder.id,
                appModel = viewModel
            )
    }
}

@Preview(name = "Light Mode SubOrder", showBackground = true, widthDp = 409)
@Composable
fun MySubOrderPreview() {
    QMAppTheme {
        SubOrder(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 0.dp, horizontal = 0.dp)
        )
    }
}

fun getSubOrders() = List(30) { i ->

    DomainSubOrderComplete(
        subOrder = DomainSubOrder(
            id = 1,
            orderId = 1,
            subOrderNumber = (100..300).random(),
            orderedById = 1,
            completedById = 1,
            statusId = 1,
            createdDate = "2022-12-15T22:24:43",
            completedDate = "2022-12-15T22:24:43",
            departmentId = 1,
            subDepartmentId = 1,
            channelId = 1,
            lineId = 1,
            operationId = 1,
            itemPreffix = "c",
            itemTypeId = 1,
            itemVersionId = 1,
            samplesCount = (1..10).random()
        ),
        orderedBy = DomainTeamMember(
            id = 1,
            departmentId = 1,
            department = "ГШСК№1",
            email = "roman.semenyshyn@skf.com",
            fullName = "Роман Семенишин",
            jobRole = "Quality Manager",
            roleLevelId = 1,
            passWord = "13050513",
            companyId = 1,
            detailsVisibility = false
        ),
        completedBy = DomainTeamMember(
            id = 1,
            departmentId = 1,
            department = "ГШСК№1",
            email = "roman.semenyshyn@skf.com",
            fullName = "Дмитро Ліщук",
            jobRole = "Quality Manager",
            roleLevelId = 1,
            passWord = "13050513",
            companyId = 1,
            detailsVisibility = false
        ),
        status = DomainOrdersStatus(1, "In Progress"),
        department = DomainDepartment(
            id = 1,
            depAbbr = "ГШСК№1",
            depName = "Група шліфувально-складальних ліній",
            depManager = 1,
            depOrganization = "Manufacturing",
            depOrder = 1,
            companyId = 1
        ),
        subDepartment = DomainSubDepartment(
            id = 1,
            subDepAbbr = "ДБШ",
            depId = 1,
            subDepDesignation = "Дільниця безцетрової обробки",
            subDepOrder = 1,
            channelsVisibility = false
        ),
        channel = DomainManufacturingChannel(
            id = 1,
            channelAbbr = "ДБШ 1",
            subDepId = 1,
            channelDesignation = "Канал 1 нової дільниці безцентрового шліфування",
            channelOrder = 1,
            linesVisibility = false
        ),
        line = DomainManufacturingLine(
            id = 1,
            lineAbbr = "IR",
            chId = 1,
            lineDesignation = "Лінія обробки торців IR",
            lineOrder = 1,
            operationVisibility = false
        ),
        operation = DomainManufacturingOperation(
            id = 1,
            operationAbbr = "T",
            lineId = 1,
            operationDesignation = "Шліфування торців",
            operationOrder = 1,
            detailsVisibility = false,
            equipment = "MTD-250"
        ),
        detailsVisibility = true,
        tasksVisibility = true
    )
}