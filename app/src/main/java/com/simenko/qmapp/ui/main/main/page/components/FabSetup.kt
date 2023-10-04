package com.simenko.qmapp.ui.main.main.page.components

import androidx.compose.material3.FabPosition
import androidx.compose.ui.graphics.vector.ImageVector
import com.simenko.qmapp.ui.main.main.page.Page
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class FabSetup(
    private val screen: Page = Page.values()[0],
    val fabAction: (() -> Unit)? = null
) {
    val fabIcon: ImageVector? = screen.fabIcon
    private val _fabPosition: MutableStateFlow<FabPosition> = MutableStateFlow(FabPosition.End)
    val fabPosition: StateFlow<FabPosition> get() = _fabPosition

    fun onEndOfList(position: Boolean) {
        _fabPosition.value = if (position) FabPosition.Center else FabPosition.End
    }
}