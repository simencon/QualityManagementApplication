package com.simenko.qmapp.ui.main.investigations.steps

import android.annotation.SuppressLint
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.simenko.qmapp.R
import com.simenko.qmapp.domain.entities.DomainSampleComplete
import com.simenko.qmapp.domain.SelectedNumber
import com.simenko.qmapp.domain.StatusDoneId
import com.simenko.qmapp.other.Constants.DEFAULT_SPACE
import com.simenko.qmapp.ui.common.HeaderWithTitle
import com.simenko.qmapp.ui.common.StatusChangeBtn
import com.simenko.qmapp.ui.common.StatusWithPercentage
import com.simenko.qmapp.ui.main.investigations.InvestigationsViewModel
import com.simenko.qmapp.ui.theme.*

@Composable
fun SampleComposition(
    modifier: Modifier = Modifier,
    invModel: InvestigationsViewModel = hiltViewModel()
) {
    val observeCurrentSubOrderTask by invModel.currentTaskDetails.collectAsStateWithLifecycle()
    val items by invModel.samplesSF.collectAsStateWithLifecycle(listOf())

    val onClickDetailsLambda = remember<(DomainSampleComplete) -> Unit> { { invModel.setSamplesVisibility(dId = SelectedNumber(it.sample.id)) } }

    LazyColumn(modifier = modifier) {
        items(items = items, key = { it.sampleResult.id.toString() + "_" + (it.sampleResult.taskId ?: 0).toString() }) { sample ->
            if (sample.sampleResult.taskId == observeCurrentSubOrderTask.num) {
                SampleCard(
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
        modifier = Modifier
            .padding(horizontal = (DEFAULT_SPACE / 2).dp, vertical = (DEFAULT_SPACE / 2).dp)
            .fillMaxWidth()
            .clickable { onChangeExpandState(sample) }
    ) {
        Sample(
            invModel = appModel,
            sample = sample,
            onClickDetails = { onClickDetails(sample) }
        )
    }
}

@Composable
fun Sample(
    invModel: InvestigationsViewModel = hiltViewModel(),
    sample: DomainSampleComplete = DomainSampleComplete(),
    onClickDetails: () -> Unit = {},
) {
    val containerColor = MaterialTheme.colorScheme.surfaceVariant

    Column(modifier = Modifier.animateContentSize(animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow))) {
        Row(modifier = Modifier.padding(all = DEFAULT_SPACE.dp), verticalAlignment = Alignment.CenterVertically) {
            HeaderWithTitle(
                modifier = Modifier.weight(0.39f),
                titleWight = 0.65f,
                title = "Sample num.: ",
                text = sample.sample.sampleNumber.toString()
            )
            StatusChangeBtn(Modifier.weight(weight = 0.46f), containerColor, {}) {
                StatusWithPercentage(
                    status = Pair(StatusDoneId.num, "Done"),
                    result = Triple(sample.sampleResult.isOk, sample.sampleResult.total, sample.sampleResult.good),
                    onlyInt = true
                )
            }
            IconButton(
                onClick = onClickDetails,
                modifier = Modifier.weight(weight = 0.15f)
            ) {
                Icon(
                    imageVector = if (sample.detailsVisibility) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = if (sample.detailsVisibility) stringResource(R.string.show_less) else stringResource(R.string.show_more),
                )
            }
        }
        if (sample.detailsVisibility) ResultsComposition(invModel = invModel)
    }
}

@Preview(name = "Light Mode SubOrderTask", showBackground = true, widthDp = 409)
@Composable
fun MySamplePreview() {
    QMAppTheme {
        Sample()
    }
}