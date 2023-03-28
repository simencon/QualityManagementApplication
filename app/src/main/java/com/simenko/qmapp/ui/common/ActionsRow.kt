package com.simenko.qmapp.ui.common

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.simenko.qmapp.R
import com.simenko.qmapp.domain.DomainOrderComplete
import com.simenko.qmapp.domain.DomainSubOrderComplete
import com.simenko.qmapp.ui.theme.PrimaryVariant900

const val ACTION_ITEM_SIZE = 45
const val CARD_HEIGHT = 45
const val CARD_OFFSET = 90f//135f

const val ANIMATION_DURATION = 500
const val MIN_DRAG_AMOUNT = 6

@Composable
fun ActionsRow(
    order: DomainOrderComplete? = null,
    subOrder: DomainSubOrderComplete? = null,
    actionIconSize: Dp,
    onDeleteOrder: (DomainOrderComplete) -> Unit = {},
    onDeleteSubOrder: (DomainSubOrderComplete) -> Unit = {},
    onEdit: () -> Unit
) {
    Row(Modifier.padding(horizontal = 3.dp, vertical = 3.dp)) {
        IconButton(
            modifier = Modifier.size(actionIconSize),
            onClick = {
                when {
                    (order != null) -> {
                        onDeleteOrder(order)
                    }
                    (subOrder != null) -> {
                        onDeleteSubOrder(subOrder)
                    }
                }
            },
            content = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_bin),
                    tint = PrimaryVariant900,
                    contentDescription = "delete action",
                )
            }
        )
        IconButton(
            modifier = Modifier.size(actionIconSize),
            onClick = onEdit,
            content = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_edit),
                    tint = PrimaryVariant900,
                    contentDescription = "edit action",
                )
            },
        )
    }
}