package com.simenko.qmapp.ui.main.products.kinds.list.versions

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.NavigateBefore
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.simenko.qmapp.R
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.domain.NoString
import com.simenko.qmapp.domain.SelectedNumber
import com.simenko.qmapp.domain.entities.products.DomainCharacteristic
import com.simenko.qmapp.other.Constants.DEFAULT_SPACE
import com.simenko.qmapp.ui.common.HeaderWithTitle
import com.simenko.qmapp.ui.common.ItemCard

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Characteristics(
    viewModel: VersionTolerancesViewModel = hiltViewModel()
) {
    val items by viewModel.characteristics.collectAsStateWithLifecycle(listOf())

    val onClickDetailsLambda = remember<(ID) -> Unit> { { viewModel.setCharacteristicsVisibility(dId = SelectedNumber(it)) } }

    LaunchedEffect(Unit) { viewModel.setIsComposed(2, true) }

    FlowRow(horizontalArrangement = Arrangement.End, verticalArrangement = Arrangement.Center) {
        items.forEach { item ->
            CharacteristicCard(
                characteristic = item,
                onClickDetails = { onClickDetailsLambda(it) }
            )
        }
    }
}

@Composable
fun CharacteristicCard(
    characteristic: DomainCharacteristic,
    onClickDetails: (ID) -> Unit
) {
    ItemCard(
        modifier = Modifier.padding(horizontal = (DEFAULT_SPACE).dp, vertical = (DEFAULT_SPACE / 2).dp),
        item = characteristic,
        contentColors = Triple(MaterialTheme.colorScheme.tertiaryContainer, MaterialTheme.colorScheme.secondaryContainer, MaterialTheme.colorScheme.outline),
        actionButtonsImages = arrayOf(Icons.Filled.Delete, Icons.Filled.Edit),
    ) {
        Characteristic(
            characteristic = characteristic,
            onClickDetails = onClickDetails
        )
    }
}

@Composable
fun Characteristic(
    characteristic: DomainCharacteristic,
    onClickDetails: (ID) -> Unit
) {
    Row(
        modifier = Modifier
            .animateContentSize(animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow))
            .padding(all = DEFAULT_SPACE.dp), verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(0.91f)) {
            HeaderWithTitle(titleWight = 0.07f, title = characteristic.charOrder.toString(), text = characteristic.charDescription ?: NoString.str)
            Spacer(modifier = Modifier.height(DEFAULT_SPACE.dp))
            HeaderWithTitle(titleWight = 0.50f, title = "Characteristic designation:", text = characteristic.charDesignation ?: NoString.str)
            Spacer(modifier = Modifier.height(DEFAULT_SPACE.dp))
            HeaderWithTitle(
                titleWight = 0.50f, title = "Sample related time:",
                text = characteristic.sampleRelatedTime?.let { "${String.format("%.2f", it)} minutes" } ?: NoString.str
            )
            Spacer(modifier = Modifier.height(DEFAULT_SPACE.dp))
            HeaderWithTitle(
                titleWight = 0.50f, title = "Measurement related time:",
                text = characteristic.measurementRelatedTime?.let { "${String.format("%.2f", it)} minutes" } ?: NoString.str
            )
        }

        IconButton(onClick = { onClickDetails(characteristic.id) }, modifier = Modifier.weight(weight = 0.09f)) {
            Icon(
                imageVector = if (characteristic.detailsVisibility) Icons.Filled.NavigateBefore else Icons.Filled.NavigateNext,
                contentDescription = if (characteristic.detailsVisibility) stringResource(R.string.show_less) else stringResource(R.string.show_more),
            )
        }
    }
}