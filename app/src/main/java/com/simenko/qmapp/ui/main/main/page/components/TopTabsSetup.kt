package com.simenko.qmapp.ui.main.main.page.components

import android.util.Log
import androidx.compose.ui.graphics.Color
import com.simenko.qmapp.domain.SelectedNumber
import com.simenko.qmapp.domain.ZeroValue
import com.simenko.qmapp.ui.main.main.page.MainPage
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow

@OptIn(ExperimentalCoroutinesApi::class)
data class TopTabsSetup(private val screen: MainPage = MainPage.values()[0], var onTabSelectAction: ((SelectedNumber) -> Unit)? = null) {

    private val _badgesContent = MutableStateFlow( Array(screen.topTabsContent.size) { Triple(0, Color.Red, Color.White) })
    private val _selectedTab = MutableStateFlow(ZeroValue.num)
    val selectedTab = _selectedTab.asStateFlow()

    val onTabSelect: (Int, SelectedNumber) -> Unit = { index, tag ->
        if (_selectedTab.value != index) {
            _selectedTab.value = index
            onTabSelectAction?.let { it(tag) }
        }
    }

    fun setBadgeContent(index: Int, value: Triple<Int, Color, Color>) {
        if (index < _badgesContent.value.size && _badgesContent.value[index] != value) {
            var i = 0
            _badgesContent.value = _badgesContent.value.map { if (index == i++) value else it }.toTypedArray()
        }
    }

    val topTabsContent: Flow<List<TopTabContent>> = _badgesContent.flatMapLatest { badges ->
        val tmp: MutableList<TopTabContent> = mutableListOf()
        for (i in screen.topTabsContent.indices) {
            tmp.add(
                TopTabContent(
                    index = i,
                    tag = screen.topTabsContent[i].third,
                    name = screen.topTabsContent[i].first,

                    badgeCount = badges[i].first,
                    badgeBg = badges[i].second,
                    badgeFr = badges[i].third,
                )
            )
        }
        flow { emit(tmp.toList()) }
    }
}

data class TopTabContent(
    val index: Int,
    val tag: SelectedNumber,
    val name: String,

    val badgeCount: Int,
    val badgeBg: Color,
    val badgeFr: Color
)