package com.simenko.qmapp.ui.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.simenko.qmapp.R
import com.simenko.qmapp.domain.*
import com.simenko.qmapp.ui.main.QualityManagementViewModel
import com.simenko.qmapp.ui.neworder.selectSingleRecordI
import com.simenko.qmapp.ui.theme.*


enum class DialogFor {
    ORDER,
    SUB_ORDER,
    CHARACTERISTIC
}

data class DialogInput(
    var recordId: Int,
    var target: DialogFor
)

fun findCurrentObject(id: Int, orders: List<DomainOrderComplete>): DomainOrderComplete {
    return orders.find { it.order.id == id }!!
}

fun findCurrentObject(id: Int, orders: List<DomainSubOrderComplete>): DomainSubOrderComplete {
    return orders.find { it.subOrder.id == id }!!
}

fun findCurrentObject(id: Int, orders: List<DomainSubOrderTaskComplete>): DomainSubOrderTaskComplete {
    return orders.find { it.subOrderTask.id == id }!!
}

//Layout
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun CustomDialogUI(
    modifier: Modifier = Modifier,
    dialogInput: DialogInput,
    openDialogCustom: MutableState<Boolean>,
    appModel: QualityManagementViewModel
) {
    Dialog(
        onDismissRequest = { openDialogCustom.value = false },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            //shape = MaterialTheme.shapes.medium,
            shape = RoundedCornerShape(10.dp),
            // modifier = modifier.size(280.dp, 240.dp)
            modifier = Modifier.padding(10.dp, 10.dp, 10.dp, 10.dp),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier
                    .background(Color.White),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                //.......................................................................
                Image(
                    painter = painterResource(id = R.drawable.ic_status),
                    contentDescription = null, // decorative
                    contentScale = ContentScale.Fit,
                    colorFilter = ColorFilter.tint(
                        color = Primary900
                    ),
                    modifier = Modifier
                        .padding(top = 35.dp)
                        .height(70.dp)
                        .fillMaxWidth(),
                    )

                Column(
                    modifier = Modifier.padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Target id = ${dialogInput.recordId} \nAction will be with: ${dialogInput.target.name}",
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .padding(top = 10.dp)
                            .fillMaxWidth(),
                        style = MaterialTheme.typography.labelLarge,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    StatusesSelection(
                        modifier = Modifier
                            .padding(top = 10.dp)
                            .height(50.dp),
                        appModel = appModel,
                        dialogInput
                    )
                }
                //.......................................................................
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                        .background(Primary900),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {

                    TextButton(onClick = {
                        openDialogCustom.value = false
                    }) {

                        Text(
                            "Cansel",
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            modifier = Modifier.padding(top = 5.dp, bottom = 5.dp)
                        )
                    }
                    TextButton(onClick = {
                        openDialogCustom.value = false
                    }) {
                        Text(
                            "Save",
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            modifier = Modifier.padding(top = 5.dp, bottom = 5.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatusesSelection(
    modifier: Modifier = Modifier,
    appModel: QualityManagementViewModel,
    dialogInput: DialogInput
) {
    val currentOrder by lazy {
        findCurrentObject(dialogInput.recordId, appModel.completeOrdersMediator.value?.first!!)
    }
    val currentSubOrder by lazy {
        findCurrentObject(dialogInput.recordId, appModel.completeSubOrdersMediator.value?.first!!)
    }
    val currentSubOrderTask by lazy {
        findCurrentObject(
            dialogInput.recordId,
            appModel.completeSubOrderTasksMediator.value?.first!!
        )
    }

    val observeInputForOrder by appModel.investigationStatusesMediator.observeAsState()
    val gritState = rememberLazyGridState()

    LaunchedEffect(Unit) {
        selectSingleRecordI(
            appModel.investigationStatuses,
            appModel.pairedTrigger,
            when (dialogInput.target) {
                DialogFor.ORDER -> {
                    currentOrder.order.statusId
                }
                DialogFor.SUB_ORDER -> {
                    currentSubOrder.subOrder.statusId
                }
                DialogFor.CHARACTERISTIC -> {
                    currentSubOrderTask.subOrderTask.statusId
                }
            }
        )
    }

    observeInputForOrder?.apply {
        LazyHorizontalGrid(
            rows = GridCells.Fixed(1),
            state = gritState,
            contentPadding = PaddingValues(horizontal = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
            modifier = modifier.height(60.dp)
        ) {
            items(first!!.size) { item ->
                if (first!![item].id != 2) //To remove In Progress
                    InvestigationStatusCard(
                        input = first!![item],
                        modifier = modifier.padding(top = 0.dp),
                        onClick = {
                            when(dialogInput.target) {
                                DialogFor.ORDER -> {
                                    currentOrder.order.statusId = first!![item].id
                                }
                                DialogFor.SUB_ORDER -> {
                                    currentSubOrder.subOrder.statusId = first!![item].id
                                }
                                DialogFor.CHARACTERISTIC -> {
                                    currentSubOrderTask.subOrderTask.statusId = first!![item].id
                                }
                            }
                            selectSingleRecordI(
                                appModel.investigationStatuses,
                                appModel.pairedTrigger,
                                first!![item].id
                            )
                        }
                    )
            }
        }
    }
}


@Composable
fun InvestigationStatusCard(
    input: DomainOrdersStatus,
    modifier: Modifier = Modifier,
    onClick: (DomainOrdersStatus) -> Unit
) {

    val btnBackgroundColor = if (input.isSelected) Primary900 else StatusBar400
    val btnContentColor = if (input.isSelected) Color.White else Color.Black
    val btnColors = ButtonDefaults.buttonColors(
        contentColor = btnContentColor,
        containerColor = btnBackgroundColor
    )

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextButton(
            modifier = Modifier
                .width(100.dp)
                .height(40.dp)
                .padding(top = 0.dp, start = 3.dp, end = 0.dp, bottom = 0.dp),
            onClick = { onClick(input) },
            content = {
                Text(
                    text = input.statusDescription ?: "-",
                    style = MaterialTheme.typography.titleSmall.copy(fontSize = 14.sp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .padding(top = 0.dp, start = 0.dp, end = 0.dp, bottom = 0.dp)
                )
            },
            enabled = true,
            shape = MaterialTheme.shapes.medium,
            elevation = ButtonDefaults.buttonElevation(4.dp),
            border = null,
            colors = btnColors
        )

    }
}