package com.simenko.qmapp.ui.main.investigations.steps

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.simenko.qmapp.R
import com.simenko.qmapp.domain.EmptyString
import com.simenko.qmapp.domain.entities.DomainSampleComplete
import com.simenko.qmapp.domain.SelectedNumber
import com.simenko.qmapp.domain.StatusDoneId
import com.simenko.qmapp.other.Constants.CARDS_PADDING
import com.simenko.qmapp.ui.common.StatusWithPercentage
import com.simenko.qmapp.ui.main.investigations.InvestigationsViewModel
import com.simenko.qmapp.ui.theme.*

private const val TAG = "SampleComposition"

@Composable
fun SampleComposition(
    modifier: Modifier = Modifier,
    invModel: InvestigationsViewModel = hiltViewModel()
) {
    Log.d(TAG, "InvestigationsViewModel: $invModel")

    val observeCurrentSubOrderTask by invModel.currentTaskDetails.collectAsStateWithLifecycle()
    val items by invModel.samplesSF.collectAsStateWithLifecycle(listOf())

    val onClickDetailsLambda = remember<(DomainSampleComplete) -> Unit> { { invModel.setSamplesVisibility(dId = SelectedNumber(it.sample.id)) } }

    LazyColumn(
        modifier = Modifier.animateContentSize(
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
            if (sample.sampleResult.taskId == observeCurrentSubOrderTask.num) {
                SampleCard(
                    modifier = modifier.padding(CARDS_PADDING),
                    appModel = invModel,
                    sample = sample,
                    onClickDetails = { onClickDetailsLambda(it) },
                    onChangeExpandState = { onClickDetailsLambda(it) }
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
    val containerColor = MaterialTheme.colorScheme.surfaceVariant

    val borderColor = when (sample.detailsVisibility) {
        true -> MaterialTheme.colorScheme.outline
        false -> MaterialTheme.colorScheme.surfaceVariant
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = BorderStroke(width = 1.dp, borderColor),
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = modifier
            .fillMaxWidth()
            .clickable { onChangeExpandState(sample) }
    ) {
        Sample(
            modifier = modifier,
            invModel = appModel,
            sample = sample,
            onClickDetails = { onClickDetails(sample) }
        )
    }
}

@Composable
fun Sample(
    modifier: Modifier = Modifier,
    invModel: InvestigationsViewModel = hiltViewModel(),
    sample: DomainSampleComplete = DomainSampleComplete(),
    onClickDetails: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .padding(top = 0.dp, start = 0.dp, end = 0.dp, bottom = 0.dp),
    ) {
        Row(
            modifier = Modifier.padding(top = 0.dp, start = 0.dp, end = 0.dp, bottom = 0.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            StatusWithPercentage(
                status = Pair(StatusDoneId.num, EmptyString.str),
                result = Triple(sample.sampleResult.isOk, sample.sampleResult.total, sample.sampleResult.good),
                onlyInt = true,
                percentageTextSize = 14.sp
            )

            Column(
                modifier = Modifier
                    .padding(top = 0.dp, start = 4.dp, end = 4.dp, bottom = 0.dp)
                    .weight(0.85f),
            ) {

                Row(
                    modifier = Modifier.padding(top = 0.dp, start = 0.dp, end = 0.dp, bottom = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Sample num.: ",
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
                    contentDescription = if (sample.detailsVisibility) stringResource(R.string.show_less) else stringResource(R.string.show_more),
                    modifier = Modifier.padding(top = 0.dp, start = 0.dp, end = 0.dp, bottom = 0.dp)
                )
            }
        }

        if (sample.detailsVisibility) ResultsComposition(modifier = modifier, invModel = invModel)
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



