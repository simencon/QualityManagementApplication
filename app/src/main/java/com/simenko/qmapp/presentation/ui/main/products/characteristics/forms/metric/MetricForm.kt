package com.simenko.qmapp.presentation.ui.main.products.characteristics.forms.metric

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
import com.simenko.qmapp.other.Constants
import com.simenko.qmapp.data.repository.UserError
import com.simenko.qmapp.presentation.ui.common.InfoLine
import com.simenko.qmapp.presentation.ui.common.RecordFieldItem
import com.simenko.qmapp.navigation.Route

@Composable
fun MetricForm(
    modifier: Modifier = Modifier,
    viewModel: MetricViewModel,
    route: Route.Main.ProductLines.Characteristics.AddEditMetric
) {
    val productLine by viewModel.productLine.collectAsStateWithLifecycle()
    val metric by viewModel.metric.collectAsStateWithLifecycle()

    val fillInErrors by viewModel.fillInErrors.collectAsStateWithLifecycle()

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

    val (orderFR) = FocusRequester.createRefs()
    val (designationFR) = FocusRequester.createRefs()
    val (descriptionFR) = FocusRequester.createRefs()
    val (unitsFR) = FocusRequester.createRefs()

    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        viewModel.onEntered(route = route)
        orderFR.requestFocus()
    }

    Column(horizontalAlignment = Alignment.Start, verticalArrangement = Arrangement.Bottom) {
        Spacer(modifier = Modifier.height(10.dp))
        InfoLine(modifier = modifier.padding(start = Constants.DEFAULT_SPACE.dp), title = "Product line", body = productLine.projectSubject ?: EmptyString.str)
        Spacer(modifier = Modifier.height(10.dp))
        InfoLine(modifier = modifier.padding(start = Constants.DEFAULT_SPACE.dp), title = "Char. group", body = metric.groupDescription)
        Spacer(modifier = Modifier.height(10.dp))
        InfoLine(modifier = modifier.padding(start = Constants.DEFAULT_SPACE.dp), title = "Char. sub group", body = metric.subGroupDescription)
        Spacer(modifier = Modifier.height(10.dp))
        InfoLine(
            modifier = modifier.padding(start = Constants.DEFAULT_SPACE.dp), title = "Characteristic",
            body = buildString {
                append(metric.charDescription)
                if (!metric.metricDesignation.isNullOrEmpty()) append(" (${metric.charDesignation})")
            }
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
                valueParam = Triple(
                    first = metric.metricOrder.let { if (it == NoRecord.num.toInt()) EmptyString.str else it }.toString(),
                    second = fillInErrors.metricOrderError
                ) {
                    viewModel.onSetOrder(if (it == EmptyString.str) NoRecord.num.toInt() else it.toInt())
                },
                keyboardNavigation = Pair(orderFR) { designationFR.requestFocus() },
                keyBoardTypeAction = Pair(KeyboardType.Number, ImeAction.Next),
                contentDescription = Triple(Icons.Outlined.FormatListNumbered, "Metric order", "Enter order"),
            )
            Spacer(modifier = Modifier.height(10.dp))
            RecordFieldItem(
                modifier = Modifier.width(320.dp),
                valueParam = Triple(metric.metricDesignation ?: EmptyString.str, fillInErrors.metricDesignationError) { viewModel.onSetDesignation(it) },
                keyboardNavigation = Pair(designationFR) { descriptionFR.requestFocus() },
                keyBoardTypeAction = Pair(KeyboardType.Ascii, ImeAction.Next),
                contentDescription = Triple(Icons.Outlined.Info, "Metric designation", "Enter designation")
            )
            Spacer(modifier = Modifier.height(10.dp))
            RecordFieldItem(
                modifier = Modifier.width(320.dp),
                valueParam = Triple(metric.metricDescription ?: EmptyString.str, fillInErrors.metricDescriptionError) { viewModel.onSetDescription(it) },
                keyboardNavigation = Pair(descriptionFR) { unitsFR.requestFocus() },
                keyBoardTypeAction = Pair(KeyboardType.Ascii, ImeAction.Next),
                contentDescription = Triple(Icons.Outlined.Info, "Metric description", "Enter description")
            )
            Spacer(modifier = Modifier.height(10.dp))
            RecordFieldItem(
                modifier = Modifier.width(320.dp),
                valueParam = Triple(metric.metricUnits, fillInErrors.metricUnitsError) { viewModel.onSetUnits(it) },
                keyboardNavigation = Pair(unitsFR) { keyboardController?.hide() },
                keyBoardTypeAction = Pair(KeyboardType.Ascii, ImeAction.Done),
                contentDescription = Triple(Icons.Outlined.SquareFoot, "Metric units", "Enter units")
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