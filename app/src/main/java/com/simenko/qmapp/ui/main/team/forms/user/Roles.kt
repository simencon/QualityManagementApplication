package com.simenko.qmapp.ui.main.team.forms.user

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.simenko.qmapp.R
import com.simenko.qmapp.domain.entities.DomainUserRole
import com.simenko.qmapp.other.Constants
import com.simenko.qmapp.ui.common.TopLevelSingleRecordHeader

@Composable
fun RolesCard(
    modifier: Modifier = Modifier.padding(Constants.CARDS_PADDING),
    items: List<DomainUserRole> = listOf(DomainUserRole(), DomainUserRole(), DomainUserRole()),
    onClickDetails: (Int) -> Unit = {}
) {
    var detailsVisibility: Boolean by rememberSaveable { mutableStateOf(false) }

    val borderColor = when (detailsVisibility) {
        true -> MaterialTheme.colorScheme.outline
        false -> MaterialTheme.colorScheme.surfaceVariant
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = BorderStroke(width = 1.dp, borderColor),
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier
            .width(320.dp)
            .clickable { detailsVisibility = !detailsVisibility }
    ) {
        Column {
            Row(
                modifier = modifier,
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "User roles",
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 16.dp)
                )

                IconButton(onClick = { detailsVisibility = !detailsVisibility }) {
                    Icon(
                        imageVector = if (detailsVisibility) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                        contentDescription = if (detailsVisibility) stringResource(R.string.show_less) else stringResource(R.string.show_more)
                    )
                }
            }

            if (detailsVisibility) {
                Divider(modifier = modifier.height(1.dp), color = MaterialTheme.colorScheme.secondary)
                Roles(modifier = modifier, items = items)
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Roles(
    modifier: Modifier,
    items: List<DomainUserRole>
) {
    FlowRow(modifier = modifier) {
        items.forEach { item ->
            Column(
                modifier = modifier.padding(top = 0.dp, start = 0.dp, end = 0.dp, bottom = 0.dp),
            ) {
                TopLevelSingleRecordHeader("Function:", item.function)
                TopLevelSingleRecordHeader("Role level:", item.roleLevel)
                TopLevelSingleRecordHeader("Access level:", item.accessLevel)
            }
        }
    }
}