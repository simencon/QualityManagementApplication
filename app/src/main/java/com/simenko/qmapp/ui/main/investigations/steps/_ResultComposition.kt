package com.simenko.qmapp.ui.main.investigations.steps

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simenko.qmapp.domain.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.simenko.qmapp.R
import com.simenko.qmapp.domain.entities.DomainResultComplete
import com.simenko.qmapp.other.Constants.CARDS_PADDING
import com.simenko.qmapp.other.Constants.DEFAULT_SPACE
import com.simenko.qmapp.ui.common.HeaderWithTitle
import com.simenko.qmapp.ui.main.investigations.InvestigationsViewModel
import com.simenko.qmapp.ui.theme.*
import com.simenko.qmapp.utils.InvestigationsUtils.generateResult
import com.simenko.qmapp.utils.StringUtils

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ResultsComposition(
    invModel: InvestigationsViewModel = hiltViewModel()
) {
    val currentSubOrderTask by invModel.currentTaskDetails.collectAsStateWithLifecycle()
    val currentSample by invModel.currentSampleDetails.observeAsState()

    val items by invModel.resultsSF.collectAsState(initial = listOf())

    val onClickDetailsLambda = remember<(Int) -> Unit> { { invModel.setResultsVisibility(dId = SelectedNumber(it)) } }
    val onChangeValueLambda = remember<(DomainResultComplete) -> Unit> { { invModel.editResult(it.result) } }

    Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.Center) {
        FlowRow {
            items.forEach { result ->
                if (result.result.taskId == currentSubOrderTask.num && result.result.sampleId == currentSample?.num) {
                    ResultCard(
                        result = result,
                        onSelect = { onClickDetailsLambda(it) },
                        onChangeValue = { onChangeValueLambda(it) }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height((DEFAULT_SPACE / 2).dp))
    }
}

@SuppressLint("UnusedTransitionTargetStateParameter")
@Composable
fun ResultCard(
    result: DomainResultComplete,
    onSelect: (Int) -> Unit,
    onChangeValue: (DomainResultComplete) -> Unit,
) {
    val containerColor = MaterialTheme.colorScheme.primaryContainer

    val borderColor = when (result.detailsVisibility) {
        true -> MaterialTheme.colorScheme.outline
        false -> MaterialTheme.colorScheme.primaryContainer
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = BorderStroke(width = 1.dp, borderColor),
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier
            .padding(horizontal = DEFAULT_SPACE.dp, vertical = (DEFAULT_SPACE / 2).dp)
            .fillMaxWidth()
    ) {
        Result(
            result = result,
            onChangeValue = { onChangeValue(result) },
            onSelect = onSelect
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun Result(
    result: DomainResultComplete = DomainResultComplete(),
    onChangeValue: (DomainResultComplete) -> Unit = {},
    onSelect: (Int) -> Unit,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    var text: String by rememberSaveable { mutableStateOf(result.result.result.let { it?.toString() ?: EmptyString.str }) }

    Row(
        modifier = Modifier.animateContentSize(animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (result.result.isOk != false) Icons.Filled.Check else Icons.Filled.Close,
            contentDescription = if (result.result.isOk != false) stringResource(R.string.show_less) else stringResource(R.string.show_more),
            tint = if (result.result.isOk != false) Color.Green else Color.Red
        )
        Column(
            modifier = Modifier.weight(0.4f),
        ) {
            Text(
                text = "Measurement",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            TextField(
                value = text,
                placeholder = { Text(NoString.str) },
                maxLines = 1,
                singleLine = true,
                textStyle = MaterialTheme.typography.titleSmall.copy(fontSize = 20.sp),
                onValueChange = { text = it },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                keyboardActions = KeyboardActions(
                    onDone = {
                        if (text != EmptyString.str) generateResult(Triple(text.toFloat(), result.resultTolerance.lsl, result.resultTolerance.usl)).let {
                            result.result.result = it.first
                            result.result.isOk = it.second
                            result.result.resultDecryptionId = it.third
                        }
                        onChangeValue(result)
                        keyboardController?.hide()
                    }
                ),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.primary,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                )
            )
        }
        Column(modifier = Modifier.weight(0.6f)) {
            HeaderWithTitle(
                modifier = Modifier.padding(start = (DEFAULT_SPACE / 2).dp),
                titleFirst = false,
                titleWight = 0.55f,
                title = " - designation",
                textTextSize = 10.sp,
                text = result.metrix.metrixDesignation ?: NoString.str
            )
            HeaderWithTitle(
                modifier = Modifier.padding(start = (DEFAULT_SPACE / 2).dp),
                titleFirst = false,
                titleWight = 0.55f,
                title = " - units",
                textTextSize = 10.sp,
                text = result.metrix.units ?: NoString.str
            )
            HeaderWithTitle(
                modifier = Modifier.padding(start = (DEFAULT_SPACE / 2).dp),
                titleFirst = false,
                titleWight = 0.55f,
                title = " - units",
                textTextSize = 10.sp,
                text = result.metrix.units ?: NoString.str
            )
            HeaderWithTitle(
                modifier = Modifier.padding(start = (DEFAULT_SPACE / 2).dp),
                titleFirst = false,
                titleWight = 0.55f,
                title = " - nominal",
                textTextSize = 10.sp,
                text = result.resultTolerance.nominal?.toString() ?: NoString.str
            )
            HeaderWithTitle(
                modifier = Modifier.padding(start = (DEFAULT_SPACE / 2).dp),
                titleFirst = false,
                titleWight = 0.55f,
                title = " - LSL/USL",
                textTextSize = 10.sp,
                text = StringUtils.concatTwoStrings(result.resultTolerance.lsl?.toString() ?: NoString.str, result.resultTolerance.usl?.toString() ?: NoString.str)
            )
        }
    }
}

@Preview(name = "Light Mode SubOrderTask", showBackground = true, widthDp = 205)
@Composable
fun MyResultPreview() {
    QMAppTheme {
        Result(onSelect = {})
    }
}