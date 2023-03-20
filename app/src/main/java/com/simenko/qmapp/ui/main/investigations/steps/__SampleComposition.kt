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
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simenko.qmapp.R
import com.simenko.qmapp.domain.DomainSample
import com.simenko.qmapp.ui.common.ANIMATION_DURATION
import com.simenko.qmapp.ui.main.QualityManagementViewModel
import com.simenko.qmapp.ui.theme.*


@Composable
fun SampleComposition(
    modifier: Modifier = Modifier,
    appModel: QualityManagementViewModel
) {
    val observeSamples by appModel.samplesMediator.observeAsState()
    val observeCurrentSubOrder by appModel.currentSubOrder.observeAsState()

    observeSamples?.apply {
        if (observeSamples!!.first != null) {
            LazyColumn(
                modifier = modifier.animateContentSize(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )
            ) {

                items(items = observeSamples!!.first!!) { sample ->
                    if (sample.subOrderId == observeCurrentSubOrder) {
                        SampleCard(
                            modifier = modifier,
                            appModel = appModel,
                            sample = sample,
                            onClickDetails = { it ->
                                appModel.changeSamplesDetailsVisibility(it.id)
                            },
                            onChangeExpandState = {
                                appModel.changeSamplesDetailsVisibility(it.id)
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
fun SampleCard(
    modifier: Modifier = Modifier,
    appModel: QualityManagementViewModel,
    sample: DomainSample,
    onClickDetails: (DomainSample) -> Unit,
    onChangeExpandState: (DomainSample) -> Unit,
) {
    val transitionState = remember {
        MutableTransitionState(sample.detailsVisibility).apply {
            targetState = !sample.detailsVisibility
        }
    }

    val transition = updateTransition(transitionState, "cardTransition")

    val cardBgColor by transition.animateColor(
        label = "cardBgColorTransition",
        transitionSpec = { tween(durationMillis = ANIMATION_DURATION) },
        targetValueByState = {
            if (sample.detailsVisibility) {
                _level_1_record_color_details
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
            .clickable { onChangeExpandState(sample) }
    ) {
        Sample(
            modifier = modifier,
            appModel = appModel,
            sample = sample,
            onClickDetails = { onClickDetails(sample) }
        )
    }
}

@Composable
fun Sample(
    modifier: Modifier = Modifier,
    appModel: QualityManagementViewModel? = null,
    sample: DomainSample = getSamples()[0],
    onClickDetails: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .padding(top = 0.dp, start = 4.dp, end = 4.dp, bottom = 0.dp),
    ) {
        Row(
            modifier = Modifier.padding(top = 0.dp, start = 0.dp, end = 0.dp, bottom = 0.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .padding(top = 0.dp, start = 4.dp, end = 4.dp, bottom = 0.dp)
                    .weight(0.85f),
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
                        text = "Деталь № ",
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.End,
                        modifier = Modifier
                            .weight(weight = 0.5f)
                            .padding(top = 0.dp, start = 3.dp, end = 0.dp, bottom = 0.dp)
                    )
                    Text(
                        text = sample.sampleNumber.toString(),
                        style = MaterialTheme.typography.titleSmall.copy(fontSize = 14.sp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Start,
                        modifier = Modifier
                            .weight(weight = 0.5f)
                            .padding(top = 0.dp, start = 3.dp, end = 0.dp, bottom = 0.dp)
                    )
                }
            }
            IconButton(
                onClick = onClickDetails, modifier = Modifier
                    .weight(weight = 0.15f)
                    .padding(top = 0.dp, start = 0.dp, end = 0.dp, bottom = 0.dp)
                    .fillMaxWidth()
            ) {
                Icon(
                    imageVector = if (sample.detailsVisibility) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = if (sample.detailsVisibility) {
                        stringResource(R.string.show_less)
                    } else {
                        stringResource(R.string.show_more)
                    },
                    modifier = Modifier.padding(top = 0.dp, start = 0.dp, end = 0.dp, bottom = 0.dp)
                )
            }
        }

        if (appModel != null && sample.detailsVisibility)
            ResultsComposition(
                modifier,
                appModel
            )
    }
}

@Preview(name = "Light Mode SubOrderTask", showBackground = true, widthDp = 409)
@Composable
fun MySamplePreview() {
    QMAppTheme {
        Sample(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 0.dp, horizontal = 0.dp)
        )
    }
}

fun getSamples() = List(30) { i ->
    DomainSample(
        id = (1..30).random(),
        subOrderId = (100..300).random(),
        sampleNumber = i + 1
    )
}