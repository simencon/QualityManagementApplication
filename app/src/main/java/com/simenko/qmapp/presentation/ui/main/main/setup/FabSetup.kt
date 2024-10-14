package com.simenko.qmapp.presentation.ui.main.main.setup

import androidx.compose.material3.FabPosition
import androidx.compose.ui.graphics.vector.ImageVector

data class FabSetup(
    val fabIcon: ImageVector? = null,
    val fabAction: (() -> Unit)? = null,
    var isFabVisible: Boolean = true,
    var fabPosition: FabPosition = FabPosition.End
) {
    fun onEndOfList(position: Boolean) {
        fabPosition = if (position) FabPosition.Center else FabPosition.End
    }
}