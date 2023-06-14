package com.simenko.qmapp.ui.common

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.simenko.qmapp.domain.*
import com.simenko.qmapp.domain.entities.*
import com.simenko.qmapp.ui.main.investigations.InvestigationsViewModel
import com.simenko.qmapp.ui.main.team.TeamViewModel
import com.simenko.qmapp.ui.theme.*

private const val TAG = "Dialogs"

data class DialogInput(
    var currentOrder: DomainOrderComplete? = null,
    var currentSubOrder: DomainSubOrderComplete? = null,
    var currentSubOrderTask: DomainSubOrderTaskComplete? = null,
    var performerId: Int? = null
)

@Composable
fun CustomDialogUI(
    modifier: Modifier = Modifier,
    dialogInput: DialogInput,
    teamModel: TeamViewModel,
    invModel: InvestigationsViewModel
) {
    val currentOrder = dialogInput.currentOrder
    val currentSubOrder = dialogInput.currentSubOrder
    val currentSubOrderTask = dialogInput.currentSubOrderTask

    val statuses by invModel.invStatusListSF.collectAsStateWithLifecycle(listOf())
    val team by teamModel.teamSF.collectAsStateWithLifecycle(listOf())

    var enableToEdit by rememberSaveable { mutableStateOf(false) }
    var placeHolder by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(team) {
        if (team.isNotEmpty()) {
            invModel.selectStatus(
                when {
                    currentOrder != null ->
                        SelectedNumber(currentOrder.order.statusId)

                    currentSubOrder != null ->
                        SelectedNumber(currentSubOrder.subOrder.statusId)

                    currentSubOrderTask != null ->
                        SelectedNumber(currentSubOrderTask.subOrderTask.statusId)

                    else -> NoRecord
                }
            )
            enableToEdit = when (dialogInput.performerId) {
                null -> {
                    placeHolder = "Виберіть виконавця"
                    false
                }
                else -> {
                    placeHolder = team
                        .find { it.teamMember.id == dialogInput.performerId }
                        .let {
                            it?.teamMember?.fullName ?: "Performer not found"
                        }
                    true
                }
            }
        }
    }

    fun changeStatus(id: Int) {
        when {
            currentOrder != null ->
                currentOrder.order.statusId = id

            currentSubOrder != null ->
                currentSubOrder.subOrder.statusId = id

            currentSubOrderTask != null ->
                currentSubOrderTask.subOrderTask.statusId = id
        }
        invModel.selectStatus(SelectedNumber(id))
    }

    Dialog(
        onDismissRequest = { invModel.hideReportDialog() },
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
                        listOfItems = team.map { it.teamMember },
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
                        StatusesSelection(
                            modifier = Modifier
                                .padding(top = 10.dp)
                                .height(50.dp),
                            statuses = statuses,
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
                        invModel.hideReportDialog()
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
                            when {
                                currentOrder != null ->
                                    invModel.hideReportDialog()

                                currentSubOrder != null -> {
                                    currentSubOrder.subOrder.completedById = dialogInput.performerId
                                    invModel.editSubOrder(currentSubOrder.subOrder)
                                }

                                currentSubOrderTask != null -> {
                                    currentSubOrderTask.subOrderTask.completedById =
                                        dialogInput.performerId
                                    Log.d(TAG, "CustomDialogUI: ${currentSubOrderTask.subOrderTask}")
                                    invModel.editSubOrderTask(currentSubOrderTask.subOrderTask)
                                }
                            }
                        }) {
                        Text(
                            "Save",
                            fontWeight = FontWeight.ExtraBold,
                            color = when (enableToEdit) {
                                true -> Color.White
                                false -> Color.Gray
                            },
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
    statuses: List<DomainOrdersStatus>,
    onSelectStatus: (Int) -> Unit
) {
    val gritState = rememberLazyGridState()

    LazyHorizontalGrid(
        rows = GridCells.Fixed(1),
        state = gritState,
        contentPadding = PaddingValues(horizontal = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp),
        modifier = modifier.height(60.dp)
    ) {
        items(statuses.size) { item ->
            if (statuses[item].id != 2) //To remove In Progress
                InvestigationStatusCard(
                    input = statuses[item],
                    modifier = modifier.padding(top = 0.dp),
                    onClick = {
                        onSelectStatus(statuses[item].id)
                    }
                )
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