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
import com.google.accompanist.flowlayout.FlowRow
import com.simenko.qmapp.R
import com.simenko.qmapp.domain.entities.DomainResultComplete
import com.simenko.qmapp.ui.main.investigations.InvestigationsViewModel
import com.simenko.qmapp.ui.theme.*
import com.simenko.qmapp.utils.StringUtils

private const val TAG = "ResultComposition"

@Composable
fun ResultsComposition(
    modifier: Modifier = Modifier,
) {
    val invModel: InvestigationsViewModel = hiltViewModel()
    Log.d(TAG, "InvestigationsViewModel: $invModel")

    val currentSubOrderTask by invModel.currentTaskDetails.collectAsStateWithLifecycle()
    val currentSample by invModel.currentSampleDetails.observeAsState()

    val items by invModel.resultsSF.collectAsState(initial = listOf())

    val onClickDetailsLambda = remember<(DomainResultComplete) -> Unit> {
        {
            invModel.setCurrentResultVisibility(dId = SelectedNumber(it.result.id))
        }
    }
    val onChangeValueLambda = remember<(DomainResultComplete) -> Unit> {
        {
            invModel.editResult(it.result)
        }
    }

    FlowRow(
        modifier = modifier.animateContentSize(
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
    ) {
        items.forEach { result ->
            if (result.result.taskId == currentSubOrderTask.num &&
                result.result.sampleId == currentSample?.num
            ) {
                ResultCard(
                    modifier = modifier,
                    appModel = invModel,
                    result = result,
                    onSelect = {
                        onClickDetailsLambda(it)
                    },
                    onChangeValue = {
                        onChangeValueLambda(it)
                    }
                )
            }
        }
    }
}

@SuppressLint("UnusedTransitionTargetStateParameter")
@Composable
fun ResultCard(
    modifier: Modifier = Modifier,
    appModel: InvestigationsViewModel,
    result: DomainResultComplete,
    onSelect: (DomainResultComplete) -> Unit,
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
        modifier = modifier
            .fillMaxWidth()
    ) {
        Result(
            modifier = modifier,
            result = result,
            onChangeValue = { onChangeValue(result) },
            onSelect = onSelect
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun Result(
    modifier: Modifier = Modifier,
    result: DomainResultComplete = DomainResultComplete(),
    onChangeValue: (DomainResultComplete) -> Unit = {},
    onSelect: (DomainResultComplete) -> Unit,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    var text: String by rememberSaveable {
        mutableStateOf(result.result.result.let {
            when (it) {
                null -> "-"
                else -> it.toString()
            }
        })
    }

    Column(
        modifier = Modifier
            .padding(top = 0.dp, start = 0.dp, end = 0.dp, bottom = 0.dp),
    ) {
        Row(
            modifier = Modifier.padding(top = 0.dp, start = 0.dp, end = 0.dp, bottom = 0.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (result.result.isOk != false) Icons.Filled.Check else Icons.Filled.Close,
                contentDescription = if (result.result.isOk != false) {
                    stringResource(R.string.show_less)
                } else {
                    stringResource(R.string.show_more)
                },
                modifier = Modifier.padding(top = 0.dp, start = 0.dp, end = 0.dp, bottom = 0.dp),
                tint = if (result.result.isOk != false) {
                    Color.Green
                } else {
                    Color.Red
                },
            )
            Column(
                modifier = Modifier
                    .padding(top = 0.dp, start = 0.dp, end = 0.dp, bottom = 0.dp)
                    .weight(0.4f),
            ) {
                Text(
                    text = "Measurement",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .padding(top = 0.dp, start = 0.dp, end = 0.dp, bottom = 0.dp)
                )
                TextField(
                    modifier = Modifier
                        .padding(top = 0.dp, start = 0.dp, end = 0.dp, bottom = 0.dp)
                        .onFocusChanged {
                            if (it.isFocused) {
                                if (text == "-") text = ""
                                onSelect(result)
                            } else
                                if (text == "") text = "-"
                        },
                    value = text,
                    maxLines = 1,
                    singleLine = true,
                    textStyle = MaterialTheme.typography.titleSmall.copy(fontSize = 20.sp),
                    onValueChange = {
                        text = it
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    keyboardActions = KeyboardActions(onDone = {
                        when {
                            (result.resultTolerance.usl != null && result.resultTolerance.lsl != null) -> {
                                when {
                                    (text.toDouble() > result.resultTolerance.usl!!) -> {
                                        result.result.result = text.toFloat()
                                        result.result.isOk = false
                                        result.result.resultDecryptionId = 2
                                    }

                                    (text.toDouble() < result.resultTolerance.lsl!!) -> {
                                        result.result.result = text.toFloat()
                                        result.result.isOk = false
                                        result.result.resultDecryptionId = 3
                                    }

                                    else -> {
                                        result.result.result = text.toFloat()
                                        result.result.isOk = true
                                        result.result.resultDecryptionId = 1
                                    }
                                }
                            }

                            (result.resultTolerance.usl == null && result.resultTolerance.lsl != null) -> {
                                when {
                                    (text.toDouble() < result.resultTolerance.lsl!!) -> {
                                        result.result.result = text.toFloat()
                                        result.result.isOk = false
                                        result.result.resultDecryptionId = 3
                                    }

                                    else -> {
                                        result.result.result = text.toFloat()
                                        result.result.isOk = true
                                        result.result.resultDecryptionId = 1
                                    }
                                }
                            }

                            (result.resultTolerance.usl != null && result.resultTolerance.lsl == null) -> {
                                when {
                                    (text.toDouble() > result.resultTolerance.usl!!) -> {
                                        result.result.result = text.toFloat()
                                        result.result.isOk = false
                                        result.result.resultDecryptionId = 2
                                    }

                                    else -> {
                                        result.result.result = text.toFloat()
                                        result.result.isOk = true
                                        result.result.resultDecryptionId = 1
                                    }
                                }
                            }
                        }
                        onChangeValue(result)
                        keyboardController?.hide()
                    }),
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedIndicatorColor = MaterialTheme.colorScheme.primary,
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                    )
                )
            }
            Column(
                modifier = Modifier
                    .padding(top = 0.dp, start = 0.dp, end = 0.dp, bottom = 0.dp)
                    .weight(0.6f),
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
                        text = result.metrix.metrixDesignation ?: "",
                        style = MaterialTheme.typography.titleSmall.copy(fontSize = 10.sp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .weight(weight = 0.45f)
                            .padding(top = 0.dp, start = 2.dp, end = 0.dp, bottom = 0.dp)
                    )
                    Text(
                        text = " - designation",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .weight(weight = 0.55f)
                            .padding(top = 0.dp, start = 0.dp, end = 0.dp, bottom = 0.dp)
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
                        text = result.metrix.units ?: "",
                        style = MaterialTheme.typography.titleSmall.copy(fontSize = 10.sp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .weight(weight = 0.45f)
                            .padding(top = 0.dp, start = 2.dp, end = 0.dp, bottom = 0.dp)
                    )
                    Text(
                        text = " - units",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .weight(weight = 0.55f)
                            .padding(top = 0.dp, start = 0.dp, end = 0.dp, bottom = 0.dp)
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
                        text = (result.resultTolerance.nominal ?: "-").toString(),
                        style = MaterialTheme.typography.titleSmall.copy(fontSize = 10.sp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .weight(weight = 0.45f)
                            .padding(top = 0.dp, start = 2.dp, end = 0.dp, bottom = 0.dp)
                    )
                    Text(
                        text = " - nominal",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .weight(weight = 0.55f)
                            .padding(top = 0.dp, start = 0.dp, end = 0.dp, bottom = 0.dp)
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
                        text = StringUtils.concatTwoStrings(
                            (result.resultTolerance.lsl ?: "-").toString(),
                            (result.resultTolerance.usl ?: "-").toString()
                        ),
                        style = MaterialTheme.typography.titleSmall.copy(fontSize = 10.sp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .weight(weight = 0.45f)
                            .padding(top = 0.dp, start = 2.dp, end = 0.dp, bottom = 0.dp)
                    )
                    Text(
                        text = " - LSL/USL",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .weight(weight = 0.55f)
                            .padding(top = 0.dp, start = 0.dp, end = 0.dp, bottom = 0.dp)
                    )
                }
            }
        }
    }
}

@Preview(name = "Light Mode SubOrderTask", showBackground = true, widthDp = 205)
@Composable
fun MyResultPreview() {
    QMAppTheme {
        Result(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 0.dp, horizontal = 0.dp),
            onSelect = {}
        )
    }
}