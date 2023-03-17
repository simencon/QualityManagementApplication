package com.simenko.qmapp.ui.main.investigations.steps

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.simenko.qmapp.domain.*

@Composable
fun ResultsComposition(
    modifier: Modifier = Modifier
) {
    Text(
        text = "Here will be Measurement results.",
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier
            .padding(top = 7.dp, start = 0.dp, end = 0.dp, bottom = 0.dp)
    )
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
    metrixDescription = "Шорсткість базового торця зовнішнього кільця"
)