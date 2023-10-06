package com.simenko.qmapp.ui.main.main.content

import androidx.compose.ui.graphics.Color
import com.simenko.qmapp.domain.*
import com.simenko.qmapp.ui.main.main.TabItem
import com.simenko.qmapp.utils.StringUtils.getWithSpaces

enum class TeamTabs(val tabId: SelectedNumber): TabItem {
    EMPLOYEES(FirstTabId),
    USERS(SecondTabId),
    REQUESTS(ThirdTabId);

    override val index: Int get() = this.ordinal
    override val tag: SelectedNumber get() = this.tabId
    override val title: String get() = getWithSpaces(this.name)

    override var badgeCount: Int = 0
    override var badgeBg: Color = Color.Red
    override var badgeFr: Color = Color.White

    override fun updateBadge(state: Triple<Int, Color, Color>){
        this.badgeCount = state.first
        this.badgeBg = state.second
        this.badgeFr = state.third
    }

    companion object {
        fun toList(): List<TabItem> = TeamTabs.values().toList()
        val startingDrawerMenuItem: TabItem = EMPLOYEES
        fun toListOfTriples() = TeamTabs.values().map { Triple(it.name, it.ordinal, it.tabId) }
    }
}

enum class ProgressTabs(val tabId: SelectedNumber): TabItem {
    ALL(FirstTabId),
    TO_DO(SecondTabId),
    IN_PROGRESS(ThirdTabId),
    DONE(FourthTabId);

    override val index: Int get() = this.ordinal
    override val tag: SelectedNumber get() = this.tabId
    override val title: String get() = getWithSpaces(this.name)

    override var badgeCount: Int = 0
    override var badgeBg: Color = Color.Red
    override var badgeFr: Color = Color.White

    override fun updateBadge(state: Triple<Int, Color, Color>){
        this.badgeCount = state.first
        this.badgeBg = state.second
        this.badgeFr = state.third
    }

    companion object {
        fun toList(): List<TabItem> = ProgressTabs.values().toList()
        val startingDrawerMenuItem: TabItem = ALL
        fun toListOfTriples() = ProgressTabs.values().map { Triple(it.name, it.ordinal, it.tabId) }
    }
}