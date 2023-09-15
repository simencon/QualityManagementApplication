package com.simenko.qmapp.ui.main.team.forms.user.subforms

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.simenko.qmapp.domain.entities.DomainUser

@Composable
fun TrueFalseField(
    modifier: Modifier,
    user: DomainUser,
    onSwitch: (Boolean) -> Unit,
    isError: Boolean
) {
    val tint = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.surfaceTint

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(0.dp, 0.dp, 0.dp, 0.dp, 0.dp, 0.dp),
        modifier = Modifier.width(320.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier.padding(all = 12.dp),
                imageVector = Icons.Default.AdminPanelSettings,
                contentDescription = "Authorities",
                tint = tint
            )
            Text(
                color = tint,
                text = "Enabled",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Normal),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = modifier
                    .weight(1f)
                    .padding(start = 3.dp)
            )

            Switch(
                modifier = Modifier.padding(end = 6.dp),
                checked = user.enabled,
                onCheckedChange = onSwitch,
                thumbContent = if (user.enabled) {
                    {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = null,
                            modifier = Modifier.size(SwitchDefaults.IconSize),
                        )
                    }
                } else {
                    null
                }
            )
        }
    }
}