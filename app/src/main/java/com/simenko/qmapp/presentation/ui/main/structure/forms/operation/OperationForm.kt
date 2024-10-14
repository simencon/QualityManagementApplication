package com.simenko.qmapp.presentation.ui.main.structure.forms.operation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FormatListNumbered
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.PrecisionManufacturing
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.simenko.qmapp.domain.EmptyString
import com.simenko.qmapp.domain.FillInErrorState
import com.simenko.qmapp.domain.FillInInitialState
import com.simenko.qmapp.domain.FillInSuccessState
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.domain.SelectedNumber
import com.simenko.qmapp.domain.entities.DomainManufacturingOperation.DomainManufacturingOperationComplete
import com.simenko.qmapp.other.Constants.DEFAULT_SPACE
import com.simenko.qmapp.other.Constants.FAB_HEIGHT
import com.simenko.qmapp.presentation.ui.common.InfoLine
import com.simenko.qmapp.presentation.ui.common.RecordFieldItem
import com.simenko.qmapp.presentation.ui.main.structure.forms.operation.subforms.PreviousOperationHeader
import com.simenko.qmapp.presentation.ui.main.structure.forms.operation.subforms.previous.AddPreviousOperation
import com.simenko.qmapp.navigation.Route
import com.simenko.qmapp.utils.StringUtils.concatTwoStrings
import kotlinx.coroutines.flow.collectLatest

@Composable
fun OperationForm(
    modifier: Modifier = Modifier,
    viewModel: OperationViewModel,
    route: Route.Main.CompanyStructure.OperationAddEdit
) {
    val opComplete by viewModel.operationComplete.collectAsStateWithLifecycle(DomainManufacturingOperationComplete())
    val fillInErrors by viewModel.fillInErrors.collectAsStateWithLifecycle()
    var error by rememberSaveable { mutableStateOf(EmptyString.str) }

    LaunchedEffect(Unit) {
        viewModel.onEntered(route = route)

        viewModel.fillInState.collectLatest { state ->
            when (state) {
                is FillInSuccessState -> viewModel.makeRecord()
                is FillInErrorState -> error = state.errorMsg
                is FillInInitialState -> error = EmptyString.str
            }
        }
    }

    val (orderFR) = FocusRequester.createRefs()
    val (abbreviationFR) = FocusRequester.createRefs()
    val (designationFR) = FocusRequester.createRefs()
    val (equipmentFR) = FocusRequester.createRefs()
    val keyboardController = LocalSoftwareKeyboardController.current

    val isAddPreviousOperationDialogVisible by viewModel.isAddPreviousOperationDialogVisible.collectAsStateWithLifecycle()

    Column(
        horizontalAlignment = Alignment.Start, verticalArrangement = Arrangement.Bottom
    ) {
        if (isAddPreviousOperationDialogVisible) AddPreviousOperation(operationViewModel = viewModel)
        Spacer(modifier = Modifier.height(10.dp))
        InfoLine(
            modifier = modifier
                .padding(start = DEFAULT_SPACE.dp)
                .fillMaxWidth(),
            title = "Department",
            body = concatTwoStrings(opComplete.lineWithParents.depAbbr, opComplete.lineWithParents.depName)
        )
        InfoLine(
            modifier = modifier
                .padding(start = DEFAULT_SPACE.dp)
                .fillMaxWidth(),
            title = "Sub department",
            body = concatTwoStrings(opComplete.lineWithParents.subDepAbbr, opComplete.lineWithParents.subDepDesignation)
        )
        InfoLine(
            modifier = modifier
                .padding(start = DEFAULT_SPACE.dp)
                .fillMaxWidth(),
            title = "Channel",
            body = concatTwoStrings(opComplete.lineWithParents.channelAbbr, opComplete.lineWithParents.channelDesignation)
        )
        InfoLine(
            modifier = modifier
                .padding(start = DEFAULT_SPACE.dp)
                .fillMaxWidth(),
            title = "Line",
            body = concatTwoStrings(opComplete.lineWithParents.lineAbbr, opComplete.lineWithParents.lineDesignation)
        )
        HorizontalDivider(modifier = Modifier.height(1.dp), color = MaterialTheme.colorScheme.secondary)
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
                .fillMaxSize()
                .padding(all = 0.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(10.dp))
            RecordFieldItem(
                modifier = Modifier.width(320.dp),
                valueParam = Triple(opComplete.operation.operationOrder.let { if (it == NoRecord.num.toInt()) EmptyString.str else it }.toString(), fillInErrors.operationOrderError) {
                    viewModel.setOperationOrder(if (it == EmptyString.str) NoRecord.num.toInt() else it.toInt())
                },
                keyboardNavigation = Pair(orderFR) { abbreviationFR.requestFocus() },
                keyBoardTypeAction = Pair(KeyboardType.Number, ImeAction.Next),
                contentDescription = Triple(Icons.Outlined.FormatListNumbered, "Operation order", "Enter order"),
            )
            Spacer(modifier = Modifier.height(10.dp))
            RecordFieldItem(
                modifier = Modifier.width(320.dp),
                valueParam = Triple(opComplete.operation.operationAbbr, fillInErrors.operationAbbrError) { viewModel.setOperationAbbr(it) },
                keyboardNavigation = Pair(abbreviationFR) { designationFR.requestFocus() },
                keyBoardTypeAction = Pair(KeyboardType.Ascii, ImeAction.Next),
                contentDescription = Triple(Icons.Outlined.Info, "Operation id", "Enter id"),
            )
            Spacer(modifier = Modifier.height(10.dp))
            RecordFieldItem(
                modifier = Modifier.width(320.dp),
                valueParam = Triple(opComplete.operation.operationDesignation, fillInErrors.operationDesignationError) { viewModel.setOperationDesignation(it) },
                keyboardNavigation = Pair(designationFR) { equipmentFR.requestFocus() },
                keyBoardTypeAction = Pair(KeyboardType.Ascii, ImeAction.Next),
                contentDescription = Triple(Icons.Outlined.Info, "Operation complete name", "Enter complete name"),
            )
            Spacer(modifier = Modifier.height(10.dp))
            RecordFieldItem(
                modifier = Modifier.width(320.dp),
                valueParam = Triple(opComplete.operation.equipment ?: EmptyString.str, fillInErrors.operationEquipmentError) { viewModel.setOperationEquipment(it) },
                keyboardNavigation = Pair(equipmentFR) { keyboardController?.hide() },
                keyBoardTypeAction = Pair(KeyboardType.Ascii, ImeAction.Done),
                contentDescription = Triple(Icons.Outlined.PrecisionManufacturing, "Operation equipment", "Enter equipment name")
            )
            Spacer(modifier = Modifier.height(10.dp))
            PreviousOperationHeader(
                previousOperations = opComplete.previousOperations,
                userRolesError = fillInErrors.previousOperationsError,
                onClickActions = { viewModel.setPreviousOperationVisibility(aId = SelectedNumber(it.toLong())) },
                onClickDelete = { viewModel.deletePreviousOperation(it) },
                onClickAdd = { viewModel.setPreviousOperationDialogVisibility(true) }
            )
            Spacer(modifier = Modifier.height(10.dp))
            if (error != EmptyString.str)
                Text(
                    modifier = Modifier
                        .padding(all = 5.dp)
                        .width(320.dp),
                    text = error,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 14.sp, color = MaterialTheme.colorScheme.error),
                    textAlign = TextAlign.Center
                )
            Spacer(modifier = Modifier.height((FAB_HEIGHT + DEFAULT_SPACE).dp))
        }
    }
}