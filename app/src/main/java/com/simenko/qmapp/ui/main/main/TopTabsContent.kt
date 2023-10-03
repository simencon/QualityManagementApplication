package com.simenko.qmapp.ui.main.main

import androidx.compose.ui.graphics.Color
import com.simenko.qmapp.domain.SelectedNumber

interface TopTabsContent {
    fun setBadgeContent(index: Int, value: Triple<Int, Color, Color>)
    fun toList(): List<TopTabContent>
}

class TopTabsContentImpl(private val tabsContent: List<Triple<String, Int, SelectedNumber>>) : TopTabsContent {
    private var badgesContent = Array(tabsContent.size) { Triple(0, Color.Red, Color.White) }
    override fun setBadgeContent(index: Int, value: Triple<Int, Color, Color>) {
        badgesContent[index] = value
    }
    override fun toList(): List<TopTabContent> {
        val tmp: MutableList<TopTabContent> = mutableListOf()
        for (i in tabsContent.indices) {
            tmp.add(
                TopTabContent(
                    index = i,
                    tag = tabsContent[i].third,
                    name = tabsContent[i].first,

                    badgeCount = badgesContent[i].first,
                    badgeBg = badgesContent[i].second,
                    badgeFr = badgesContent[i].third
                )
            )
        }
        return tmp
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