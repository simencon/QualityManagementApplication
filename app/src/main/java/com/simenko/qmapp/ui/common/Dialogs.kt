package com.simenko.qmapp.ui.common

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.Task
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.view.WindowCompat
import androidx.lifecycle.MutableLiveData
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
    var target: DialogFor,
    var performerId: Int?
)

fun findCurrentObject(id: Int, items: List<DomainOrderComplete>): DomainOrderComplete {
    return items.find { it.order.id == id }!!
}

fun findCurrentObject(id: Int, items: List<DomainSubOrderComplete>): DomainSubOrderComplete {
    return items.find { it.subOrder.id == id }!!
}

fun findCurrentObject(
    id: Int,
    items: List<DomainSubOrderTaskComplete>
): DomainSubOrderTaskComplete {
    return items.find { it.subOrderTask.id == id }!!
}

//Layout
@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CustomDialogUI(
    modifier: Modifier = Modifier,
    dialogInput: DialogInput,
    openDialogCustom: MutableLiveData<Boolean>,
    appModel: QualityManagementViewModel
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

    var enableToEdit by rememberSaveable { mutableStateOf(false) }
    var placeHolder by rememberSaveable { mutableStateOf("") }

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
        enableToEdit = when (dialogInput.performerId) {
            null -> {
                placeHolder = "Виберіть виконавця"
                false
            }
            else -> {
                placeHolder = appModel.teamMembers.value!!.find { it.id == dialogInput.performerId }!!.fullName
                true
            }
        }
    }

    fun changeStatus(id: Int) {
        when (dialogInput.target) {
            DialogFor.ORDER -> {
                currentOrder.order.statusId = id
            }
            DialogFor.SUB_ORDER -> {
                currentSubOrder.subOrder.statusId = id
            }
            DialogFor.CHARACTERISTIC -> {
                currentSubOrderTask.subOrderTask.statusId = id
            }
        }
        selectSingleRecordI(
            appModel.investigationStatuses,
            appModel.pairedTrigger,
            id
        )
    }

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
                    imageVector = Icons.Filled.TaskAlt,
                    contentDescription = null, // decorative
                    contentScale = ContentScale.Fit,
                    colorFilter = ColorFilter.tint(
                        color = Primary900
                    ),
                    modifier = Modifier
                        .padding(vertical = 20.dp)
                        .height(50.dp)
                        .fillMaxWidth(),
                )

                Column(
                    modifier = Modifier.padding(horizontal = 8.dp)
                ) {
                    SearchableExpandedDropDownMenu(
                        listOfItems = appModel.teamMembers.value!!,
                        placeholder = placeHolder,
                        modifier = Modifier.fillMaxWidth(),
                        onDropDownItemSelected = { item ->
                            dialogInput.performerId = item.id
                            enableToEdit = true
                        },
                        dropdownItem = { test ->
                            DropDownItem(test = test)
                        }
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                }

                if (enableToEdit)
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
                            onSelectStatus = { changeStatus(it) }
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
                    TextButton(
                        enabled = enableToEdit,
                        onClick = {
                            when (dialogInput.target) {
                                DialogFor.ORDER -> {
                                    openDialogCustom.value = false
                                }
                                DialogFor.SUB_ORDER -> {
                                    currentSubOrder.subOrder.completedById = dialogInput.performerId
                                    appModel.editSubOrder(currentSubOrder.subOrder)
                                }
                                DialogFor.CHARACTERISTIC -> {
                                    currentSubOrderTask.subOrderTask.completedById = dialogInput.performerId
                                    appModel.editSubOrderTask(currentSubOrderTask.subOrderTask)
                                }
                            }
                        }) {
                        Text(
                            "Save",
                            fontWeight = FontWeight.ExtraBold,
                            color = when(enableToEdit) {true -> Color.White false -> Color.Gray},
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
    onSelectStatus: (Int) -> Unit
) {
    val observeInputForOrder by appModel.investigationStatusesMediator.observeAsState()
    val gritState = rememberLazyGridState()

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
                            onSelectStatus(first!![item].id)
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

@Composable
fun DropDownItem(test: DomainTeamMember) {
    Row(
        modifier = Modifier
            .padding(8.dp)
            .wrapContentSize()
    ) {
        androidx.compose.material.Text(text = test.fullName)
        Spacer(modifier = Modifier.width(12.dp))
        androidx.compose.material.Text("")
    }
}