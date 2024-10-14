package com.simenko.qmapp.presentation.ui.main.products.characteristics.forms.characteristic

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FormatListNumbered
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.SquareFoot
import androidx.compose.material.icons.outlined.Timer
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
import androidx.compose.ui.focus.onFocusChanged
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
import com.simenko.qmapp.domain.NoString
import com.simenko.qmapp.other.Constants
import com.simenko.qmapp.data.repository.UserError
import com.simenko.qmapp.presentation.ui.common.InfoLine
import com.simenko.qmapp.presentation.ui.common.RecordFieldItemWithMenu
import com.simenko.qmapp.presentation.ui.common.RecordFieldItem
import com.simenko.qmapp.navigation.Route

@Composable
fun CharacteristicForm(
    modifier: Modifier = Modifier,
    viewModel: CharacteristicViewModel,
    route: Route.Main.ProductLines.Characteristics.AddEditChar
) {

    val characteristic by viewModel.characteristic.collectAsStateWithLifecycle()
    val sampleRelatedTime by viewModel.sampleRelatedTime.collectAsStateWithLifecycle()
    val measurementRelatedTime by viewModel.measurementRelated.collectAsStateWithLifecycle()

    val fillInErrors by viewModel.fillInErrors.collectAsStateWithLifecycle()

    val charGroups by viewModel.charGroups.collectAsStateWithLifecycle()
    val charSubGroups by viewModel.charSubGroups.collectAsStateWithLifecycle()

    val fillInState by viewModel.fillInState.collectAsStateWithLifecycle()
    var error by rememberSaveable { mutableStateOf(UserError.NO_ERROR.error) }

    LaunchedEffect(fillInState) {
        fillInState.let { state ->
            when (state) {
                is FillInSuccessState -> viewModel.makeRecord()
                is FillInErrorState -> error = state.errorMsg
                is FillInInitialState -> error = UserError.NO_ERROR.error
            }
        }
    }

    val (groupFR) = FocusRequester.createRefs()
    val (subGroupFR) = FocusRequester.createRefs()
    val (orderFR) = FocusRequester.createRefs()
    val (designationFR) = FocusRequester.createRefs()
    val (descriptionFR) = FocusRequester.createRefs()
    val (sampleRelatedTimeFR) = FocusRequester.createRefs()
    val (measurementRelatedTimeFR) = FocusRequester.createRefs()

    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        viewModel.onEntered(route = route)
        orderFR.requestFocus()
    }

    Column(horizontalAlignment = Alignment.Start, verticalArrangement = Arrangement.Bottom) {
        Spacer(modifier = Modifier.height(10.dp))
        InfoLine(
            modifier = modifier.padding(start = Constants.DEFAULT_SPACE.dp),
            title = "Product line",
            body = characteristic.characteristicSubGroup.charGroup.productLine.manufacturingProject.projectSubject ?: NoString.str
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
            RecordFieldItemWithMenu(
                modifier = Modifier.width(320.dp),
                options = charGroups,
                isError = fillInErrors.charGroupError,
                onDropdownMenuItemClick = { viewModel.onSetCharGroup(it ?: NoRecord.num) },
                keyboardNavigation = Pair(groupFR) { subGroupFR.requestFocus() },
                keyBoardTypeAction = Pair(KeyboardType.Ascii, ImeAction.Next),
                contentDescription = Triple(Icons.Outlined.SquareFoot, "Characteristic group", "Select group"),
            )
            Spacer(modifier = Modifier.height(10.dp))
            RecordFieldItemWithMenu(
                modifier = Modifier.width(320.dp),
                options = charSubGroups,
                isError = fillInErrors.charSubGroupError,
                onDropdownMenuItemClick = { viewModel.onSetCharSubGroup(it ?: NoRecord.num) },
                keyboardNavigation = Pair(subGroupFR) { orderFR.requestFocus() },
                keyBoardTypeAction = Pair(KeyboardType.Ascii, ImeAction.Next),
                contentDescription = Triple(Icons.Outlined.SquareFoot, "Char. sub group", "Select sub group"),
            )
            Spacer(modifier = Modifier.height(10.dp))
            RecordFieldItem(
                modifier = Modifier.width(320.dp),
                valueParam = Triple(
                    first = characteristic.characteristic.charOrder?.let { if (it == NoRecord.num.toInt()) EmptyString.str else it }?.toString() ?: EmptyString.str,
                    second = fillInErrors.charOrderError
                ) {
                    viewModel.onSetOrder(if (it == EmptyString.str) NoRecord.num.toInt() else it.toIntOrNull()?: NoRecord.num.toInt())
                },
                keyboardNavigation = Pair(orderFR) { designationFR.requestFocus() },
                keyBoardTypeAction = Pair(KeyboardType.Number, ImeAction.Next),
                contentDescription = Triple(Icons.Outlined.FormatListNumbered, "Characteristic order", "Enter order"),
            )
            Spacer(modifier = Modifier.height(10.dp))
            RecordFieldItem(
                modifier = Modifier.width(320.dp),
                valueParam = Triple(characteristic.characteristic.charDesignation ?: EmptyString.str, fillInErrors.charDesignationError) { viewModel.onSetCharDesignation(it) },
                keyboardNavigation = Pair(designationFR) { descriptionFR.requestFocus() },
                keyBoardTypeAction = Pair(KeyboardType.Ascii, ImeAction.Next),
                contentDescription = Triple(Icons.Outlined.Info, "Char. designation", "Enter designation")
            )
            Spacer(modifier = Modifier.height(10.dp))
            RecordFieldItem(
                modifier = Modifier.width(320.dp),
                valueParam = Triple(characteristic.characteristic.charDescription ?: EmptyString.str, fillInErrors.charDescriptionError) { viewModel.onSetCharDescription(it) },
                keyboardNavigation = Pair(descriptionFR) { sampleRelatedTimeFR.requestFocus() },
                keyBoardTypeAction = Pair(KeyboardType.Text, ImeAction.Next),
                contentDescription = Triple(Icons.Outlined.Info, "Char. description", "Enter description")
            )
            Spacer(modifier = Modifier.height(10.dp))
            RecordFieldItem(
                modifier = Modifier
                    .width(320.dp)
                    .onFocusChanged {
                        if (it.isFocused) {
                            if (sampleRelatedTime == NoString.str) viewModel.onSetSampleRelatedTime(EmptyString.str)
                        } else {
                            if (sampleRelatedTime == EmptyString.str) viewModel.onSetSampleRelatedTime(NoString.str)
                        }
                    },
                valueParam = Triple(sampleRelatedTime, fillInErrors.sampleRelatedTimeError) { viewModel.onSetSampleRelatedTime(it) },
                keyboardNavigation = Pair(sampleRelatedTimeFR) { measurementRelatedTimeFR.requestFocus() },
                keyBoardTypeAction = Pair(KeyboardType.Number, ImeAction.Next),
                contentDescription = Triple(Icons.Outlined.Timer, "Sample related time (minutes)", "Enter time in minutes"),
                isMandatoryField = false
            )
            Spacer(modifier = Modifier.height(10.dp))
            RecordFieldItem(
                modifier = Modifier
                    .width(320.dp)
                    .onFocusChanged {
                        if (it.isFocused) {
                            if (measurementRelatedTime == NoString.str) viewModel.onSetMeasurementRelatedTime(EmptyString.str)
                        } else {
                            if (measurementRelatedTime == EmptyString.str) viewModel.onSetMeasurementRelatedTime(NoString.str)
                        }
                    },
                valueParam = Triple(measurementRelatedTime, fillInErrors.measurementRelatedTimeError) { viewModel.onSetMeasurementRelatedTime(it) },
                keyboardNavigation = Pair(measurementRelatedTimeFR) { keyboardController?.hide() },
                keyBoardTypeAction = Pair(KeyboardType.Number, ImeAction.Done),
                contentDescription = Triple(Icons.Outlined.Timer, "Measurement related time (minutes)", "Enter time in minutes"),
                isMandatoryField = false
            )
            Spacer(modifier = Modifier.height(10.dp))
            if (error != UserError.NO_ERROR.error)
                Text(
                    modifier = Modifier
                        .padding(all = 5.dp)
                        .width(320.dp),
                    text = error,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 14.sp, color = MaterialTheme.colorScheme.error),
                    textAlign = TextAlign.Center
                )
        }
    }
}