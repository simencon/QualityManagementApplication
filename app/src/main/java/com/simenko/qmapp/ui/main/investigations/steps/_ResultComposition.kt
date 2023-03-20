package com.simenko.qmapp.ui.main.investigations.steps

import android.annotation.SuppressLint
import androidx.compose.animation.animateColor
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simenko.qmapp.domain.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.google.accompanist.flowlayout.FlowRow
import com.simenko.qmapp.R
import com.simenko.qmapp.ui.common.ANIMATION_DURATION
import com.simenko.qmapp.ui.main.QualityManagementViewModel
import com.simenko.qmapp.ui.theme.QMAppTheme
import com.simenko.qmapp.ui.theme._level_1_record_color
import com.simenko.qmapp.ui.theme._level_2_record_color_details

@Composable
fun ResultsComposition(
    modifier: Modifier = Modifier,
    appModel: QualityManagementViewModel
) {
    val observeResults by appModel.completeResultsMediator.observeAsState()
    val currentSubOrderTask by appModel.currentSubOrderTask.observeAsState()
    val currentSample by appModel.currentSample.observeAsState()

    observeResults?.apply {
        if (observeResults!!.first != null) {
            FlowRow(
                modifier = modifier.animateContentSize(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )
            ) {
                observeResults!!.first!!.forEach { result ->
                    if (result.result.taskId == currentSubOrderTask &&
                        result.result.sampleId == currentSample
                    ) {
                        ResultCard(
                            modifier = modifier,
                            result = result,
                            onSelect = { it ->
                                appModel.changeResultsIsSelectedState(it.result)
                            },
                            onChangeValue = {
                            }
                        )
                    }
                }
            }
        }
    }
}

@SuppressLint("UnusedTransitionTargetStateParameter")
@Composable
fun ResultCard(
    modifier: Modifier = Modifier,
    result: DomainResultComplete,
    onSelect: (DomainResultComplete) -> Unit,
    onChangeValue: (DomainResultComplete) -> Unit,
) {
    val transitionState = remember {
        MutableTransitionState(result.isSelected).apply {
            targetState = !result.isSelected
        }
    }

    val transition = updateTransition(transitionState, "cardTransition")

    val cardBgColor by transition.animateColor(
        label = "cardBgColorTransition",
        transitionSpec = { tween(durationMillis = ANIMATION_DURATION) },
        targetValueByState = {
            if (result.isSelected) {
                _level_2_record_color_details
            } else {
                _level_1_record_color
            }
        }
    )

    Card(
        colors = CardDefaults.cardColors(
            containerColor = cardBgColor,
        ),
        modifier = modifier
            .fillMaxWidth()
            .clickable { onSelect(result) }
    ) {
        Result(
            modifier = modifier,
            result = result,
            onChangeValue = { onChangeValue(result) }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Result(
    modifier: Modifier = Modifier,
    result: DomainResultComplete = getResults()[0],
    onChangeValue: (String) -> Unit = {},
) {
    var text: String by rememberSaveable { mutableStateOf("") }

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
                        .padding(top = 0.dp, start = 0.dp, end = 0.dp, bottom = 0.dp),
                    value = text,
                    maxLines = 1,
                    singleLine = true,

                    textStyle = MaterialTheme.typography.titleSmall.copy(fontSize = 20.sp),
                    onValueChange = {
                        text = it
                        onChangeValue(text)
                    },
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
                            .weight(weight = 0.35f)
                            .padding(top = 0.dp, start = 2.dp, end = 0.dp, bottom = 0.dp)
                    )
                    Text(
                        text = " - designation",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .weight(weight = 0.65f)
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
                            .weight(weight = 0.35f)
                            .padding(top = 0.dp, start = 2.dp, end = 0.dp, bottom = 0.dp)
                    )
                    Text(
                        text = " - units",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .weight(weight = 0.65f)
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
                        text = "180 000",
                        style = MaterialTheme.typography.titleSmall.copy(fontSize = 10.sp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .weight(weight = 0.35f)
                            .padding(top = 0.dp, start = 2.dp, end = 0.dp, bottom = 0.dp)
                    )
                    Text(
                        text = " - nominal",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .weight(weight = 0.65f)
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
                        text = "-10/32",
                        style = MaterialTheme.typography.titleSmall.copy(fontSize = 10.sp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .weight(weight = 0.35f)
                            .padding(top = 0.dp, start = 2.dp, end = 0.dp, bottom = 0.dp)
                    )
                    Text(
                        text = " - LSL/USL",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .weight(weight = 0.65f)
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
                .padding(vertical = 0.dp, horizontal = 0.dp)
        )
    }
}

fun getResults() = List(30) { i ->
    DomainResultComplete(
        result = getResult(),
        resultsDecryption = getResultsDecryption(),
        metrix = getMetrix(),
        subOrderTask = getSubOrderTasks()[0]
    )
}

fun getResult() = DomainResult(
    id = 0,
    sampleId = 1,
    metrixId = 2,
    result = 2.4,
    isOk = true,
    resultDecryptionId = 3,
    taskId = 4
)

fun getResultsDecryption() = DomainResultsDecryption(
    id = 1,
    resultDecryption = "In tolerance"
)

fun getMetrix() = DomainMetrix(
    id = 0,
    charId = 1,
    metrixOrder = 1,
    metrixDesignation = "Ra C",
    metrixDescription = "Шорсткість базового торця зовнішнього кільця",
    units = "мкм"
)