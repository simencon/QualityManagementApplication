package com.simenko.qmapp.ui.main.main.content

import androidx.compose.ui.graphics.Color
import com.simenko.qmapp.domain.*
import com.simenko.qmapp.utils.StringUtils.getWithSpaces

enum class TeamTabs(override val tag: SelectedNumber) : TabItem {
    EMPLOYEES(FirstTabId),
    USERS(SecondTabId),
    REQUESTS(ThirdTabId);

    override val index: Int get() = this.ordinal
    override val title: String get() = getWithSpaces(this.name)

    override var badgeCount: Int = 0
    override var badgeBackgroundColor: Color = Color.Red
    override var badgeContentColor: Color = Color.White

    override fun getBadge(): Triple<Int, Color, Color> = Triple(this.badgeCount, this.badgeBackgroundColor, this.badgeContentColor)
    override fun updateBadge(state: Triple<Int, Color, Color>): TabItem {
        this.badgeCount = state.first
        this.badgeBackgroundColor = state.second
        this.badgeContentColor = state.third
        return this
    }

    companion object : TabItem.Static {
        fun toList(): List<TabItem> = TeamTabs.values().toList()
        override val startingTabItem: TabItem = EMPLOYEES
    }
}

enum class ProgressTabs(override val tag: SelectedNumber) : TabItem {
    ALL(FirstTabId),
    TO_DO(SecondTabId),
    IN_PROGRESS(ThirdTabId),
    DONE(FourthTabId);

    override val index: Int get() = this.ordinal
    override val title: String get() = getWithSpaces(this.name)

    override var badgeCount: Int = 0
    override var badgeBackgroundColor: Color = Color.Red
    override var badgeContentColor: Color = Color.White

    override fun getBadge(): Triple<Int, Color, Color> = Triple(this.badgeCount, this.badgeBackgroundColor, this.badgeContentColor)
    override fun updateBadge(state: Triple<Int, Color, Color>): TabItem {
        this.badgeCount = state.first
        this.badgeBackgroundColor = state.second
        this.badgeContentColor = state.third
        return this
    }

    companion object : TabItem.Static {
        fun toList(): List<TabItem> = ProgressTabs.values().toList()
        override val startingTabItem: TabItem = ALL
    }
}