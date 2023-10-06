package com.simenko.qmapp.ui.main.main.setup

import androidx.compose.ui.graphics.Color
import com.simenko.qmapp.domain.SelectedNumber
import com.simenko.qmapp.domain.ZeroValue
import com.simenko.qmapp.domain.entities.DomainEmployeeComplete
import com.simenko.qmapp.ui.main.main.Page
import com.simenko.qmapp.ui.main.main.TabItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

data class TopTabsSetup(private val screen: Page = Page.values()[0], var onTabSelectAction: ((SelectedNumber) -> Unit)? = null) {

    private val _selectedTab = MutableStateFlow(ZeroValue.num)
    val selectedTab = _selectedTab.asStateFlow()

    val onTabSelect: (Int, SelectedNumber) -> Unit = { index, tag ->
        if (_selectedTab.value != index) {
            _selectedTab.value = index
            onTabSelectAction?.let { it(tag) }
        }
    }

    private val _topTabsContent = MutableStateFlow(screen.topTabsContent)
    val topTabsContent get() = _topTabsContent.asStateFlow()
    fun updateBadgeContent(value: List<Triple<Int, Color, Color>>) {
        if (value.size == _topTabsContent.value?.size) {
            var isEqual = true
            val cpy = mutableListOf<TabItem>()
            for (i in value.indices) {
                if (value[i] != _topTabsContent.value?.get(i)?.getBadge()) isEqual = false
                _topTabsContent.value?.get(i)?.updateBadge(value[i])?.let { cpy.add(it) }
            }

            if (!isEqual) _topTabsContent.value = cpy
        }
    }
}