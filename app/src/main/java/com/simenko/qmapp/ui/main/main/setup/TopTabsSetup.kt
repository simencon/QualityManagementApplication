package com.simenko.qmapp.ui.main.main.setup

import androidx.compose.ui.graphics.Color
import com.simenko.qmapp.domain.SelectedNumber
import com.simenko.qmapp.domain.ZeroValue
import com.simenko.qmapp.ui.main.main.content.Page
import com.simenko.qmapp.ui.main.main.content.TabItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

data class TopTabsSetup(private val screen: Page = Page.values()[0], var onTabSelectAction: ((SelectedNumber) -> Unit)? = null) {

    private val _selectedTab = MutableStateFlow(ZeroValue.num)
    val selectedTab = _selectedTab.asStateFlow()

    val setSelectedTab: (Int) -> Unit = {
        if (_selectedTab.value != it) _selectedTab.value = it
    }

    val onTabSelect: (Int, SelectedNumber) -> Unit = { index, tag ->
        if (_selectedTab.value != index) {
            _selectedTab.value = index
            onTabSelectAction?.let { it(tag) }
        }
    }

    private val _topTabsContent = MutableStateFlow(TabItems(screen.topTabsContent ?: emptyList(), false))
    val topTabsContent get() = _topTabsContent.asStateFlow()
    fun updateBadgeContent(value: List<Triple<Int, Color, Color>>) {
        if (value.size == _topTabsContent.value.items.size) {
            var isEqual = true

            value.mapIndexed { index, triple ->
                if (triple != _topTabsContent.value.items[index].getBadge()) isEqual = false
                _topTabsContent.value.items[index].updateBadge(triple)
            }

            if (!isEqual) {
                val trigger = _topTabsContent.value.trigger
                _topTabsContent.value = _topTabsContent.value.copy(trigger = !trigger)
            }
        }
    }

    data class TabItems(val items: List<TabItem>, val trigger: Boolean)
}