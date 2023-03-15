package com.simenko.qmapp.ui.main.investigations.orders

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

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