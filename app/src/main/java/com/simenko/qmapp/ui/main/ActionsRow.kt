package com.simenko.qmapp.ui.main

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.simenko.qmapp.R
import com.simenko.qmapp.ui.theme.PrimaryVariant900

const val ACTION_ITEM_SIZE = 45
const val CARD_HEIGHT = 45
const val CARD_OFFSET = 135f

const val ANIMATION_DURATION = 500
const val MIN_DRAG_AMOUNT = 6

@Composable
fun ActionsRow(
    actionIconSize: Dp,
    onDelete: () -> Unit,
    onEdit: () -> Unit,
    onFavorite: () -> Unit,
) {
    Row(Modifier.padding(horizontal = 3.dp, vertical = 3.dp)) {
        IconButton(
            modifier = Modifier.size(actionIconSize),
            onClick = onDelete,
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
        IconButton(
            modifier = Modifier.size(actionIconSize),
            onClick = onFavorite,
            content = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_favorite),
                    tint = Color.Red,
                    contentDescription = "Expandable Arrow",
                )
            }
        )
    }
}