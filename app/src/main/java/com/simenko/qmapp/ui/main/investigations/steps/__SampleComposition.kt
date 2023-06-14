package com.simenko.qmapp.ui.main.investigations.steps

import android.annotation.SuppressLint
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.simenko.qmapp.R
import com.simenko.qmapp.domain.entities.DomainSample
import com.simenko.qmapp.domain.entities.DomainSampleComplete
import com.simenko.qmapp.domain.entities.DomainSampleResult
import com.simenko.qmapp.domain.SelectedNumber
import com.simenko.qmapp.ui.main.MainActivity
import com.simenko.qmapp.ui.main.investigations.InvestigationsViewModel
import com.simenko.qmapp.ui.theme.*
import kotlin.math.roundToInt

@Composable
fun SampleComposition(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val appModel = (context as MainActivity).investigationsModel

    val observeCurrentSubOrderTask by appModel.currentTaskDetails.observeAsState()

    val items by appModel.samplesSF.collectAsStateWithLifecycle(listOf())

    val onClickDetailsLambda = remember<(DomainSampleComplete) -> Unit> {
        {
            appModel.setCurrentSampleVisibility(dId = SelectedNumber(it.sample.id))
        }
    }

    LazyColumn(
        modifier = modifier.animateContentSize(
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
    ) {
        items(
            items = items,
            key = {
                it.sampleResult.id.toString() + "_" + (it.sampleResult.taskId ?: 0).toString()
            }) { sample ->
            if (sample.sampleResult.taskId == observeCurrentSubOrderTask?.num) {
                SampleCard(
                    modifier = modifier,
                    appModel = appModel,
                    sample = sample,
                    onClickDetails = { it ->
                        onClickDetailsLambda(it)
                    },
                    onChangeExpandState = {
                        onClickDetailsLambda(it)
                    }
                )
            }
        }
    }
}


@SuppressLint("UnusedTransitionTargetStateParameter")
@Composable
fun SampleCard(
    modifier: Modifier = Modifier,
    appModel: InvestigationsViewModel,
    sample: DomainSampleComplete,
    onClickDetails: (DomainSampleComplete) -> Unit,
    onChangeExpandState: (DomainSampleComplete) -> Unit,
) {
    val cardBgColor =
        when (sample.detailsVisibility) {
            true -> _level_1_record_color_details
            else -> _level_1_record_color
        }

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
    appModel: InvestigationsViewModel? = null,
    sample: DomainSampleComplete = DomainSampleComplete(),
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
            Icon(
                imageVector = if (sample.sampleResult.isOk != false) Icons.Filled.Check else Icons.Filled.Close,
                contentDescription = if (sample.sampleResult.isOk != false) {
                    stringResource(R.string.show_less)
                } else {
                    stringResource(R.string.show_more)
                },
                modifier = Modifier.padding(top = 0.dp, start = 0.dp, end = 0.dp, bottom = 0.dp),
                tint = if (sample.sampleResult.isOk != false) {
                    Color.Green
                } else {
                    Color.Red
                },
            )

            val conformity = (sample.sampleResult.total?.toFloat()?.let {
                sample.sampleResult.good?.toFloat()
                    ?.div(it)
            }?.times(100)) ?: 0.0f

            Text(
                text = conformity.roundToInt().toString() + "%",
                style = MaterialTheme.typography.titleSmall.copy(fontSize = 14.sp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Start,
                modifier = Modifier
                    .weight(weight = 0.5f)
                    .padding(top = 0.dp, start = 3.dp, end = 0.dp, bottom = 0.dp)
            )
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
                        text = sample.sample.sampleNumber.toString(),
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
                modifier
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



