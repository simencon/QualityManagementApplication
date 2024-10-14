package com.simenko.qmapp.presentation.ui.main.products.kinds.set.stages.characteristics

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.domain.NoString
import com.simenko.qmapp.domain.SelectedNumber
import com.simenko.qmapp.domain.entities.products.DomainCharacteristic
import com.simenko.qmapp.other.Constants.DEFAULT_SPACE
import com.simenko.qmapp.presentation.ui.common.HeaderWithTitle
import com.simenko.qmapp.presentation.ui.common.ItemCard

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Characteristics(
    viewModel: ComponentStageKindCharacteristicsViewModel = hiltViewModel()
) {
    val items by viewModel.characteristics.collectAsStateWithLifecycle(listOf())
    val onClickActionsLambda = remember<(ID) -> Unit> { { viewModel.setCharacteristicsVisibility(aId = SelectedNumber(it)) } }
    val onClickDeleteLambda = remember<(ID) -> Unit> { { viewModel.onDeleteCharacteristicClick(it) } }

    Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.Center) {
        FlowRow {
            items.forEach { item ->
                CharacteristicCard(
                    characteristic = item,
                    onClickActions = { onClickActionsLambda(it) },
                    onClickDelete = { onClickDeleteLambda(it) },
                )
            }
        }
    }
}

@Composable
fun CharacteristicCard(
    characteristic: DomainCharacteristic,
    onClickActions: (ID) -> Unit,
    onClickDelete: (ID) -> Unit,
) {
    ItemCard(
        modifier = Modifier.padding(horizontal = (DEFAULT_SPACE).dp, vertical = (DEFAULT_SPACE / 2).dp),
        item = characteristic,
        onClickActions = onClickActions,
        onClickDelete = onClickDelete,
        contentColors = Triple(MaterialTheme.colorScheme.tertiaryContainer, MaterialTheme.colorScheme.secondaryContainer, MaterialTheme.colorScheme.outline),
        actionButtonsImages = arrayOf(Icons.Filled.Delete),
    ) {
        Characteristic(
            characteristic = characteristic,
        )
    }
}

@Composable
fun Characteristic(
    characteristic: DomainCharacteristic,
) {
    Row(
        modifier = Modifier
            .animateContentSize(animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow))
            .padding(all = DEFAULT_SPACE.dp), verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
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
    }
}