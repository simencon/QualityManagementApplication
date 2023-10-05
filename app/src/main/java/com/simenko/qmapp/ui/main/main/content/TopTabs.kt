package com.simenko.qmapp.ui.main.main.content

import com.simenko.qmapp.domain.*

enum class TeamTabs(val tabId: SelectedNumber) {
    EMPLOYEES(FirstTabId),
    USERS(SecondTabId),
    REQUESTS(ThirdTabId);

    companion object {
        fun toListOfTriples() = TeamTabs.values().map { Triple(it.name, it.ordinal, it.tabId) }
    }
}

enum class ProgressTabs(val tabId: SelectedNumber) {
    ALL(FirstTabId),
    TO_DO(SecondTabId),
    IN_PROGRESS(ThirdTabId),
    DONE(FourthTabId);

    companion object {
        fun toListOfTriples() = ProgressTabs.values().map { Triple(it.name, it.ordinal, it.tabId) }
    }
}