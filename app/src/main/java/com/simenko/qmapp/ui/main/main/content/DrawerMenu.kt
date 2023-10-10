package com.simenko.qmapp.ui.main.main.content

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector
import com.simenko.qmapp.ui.main.main.MenuItem
import com.simenko.qmapp.ui.main.main.Page
import com.simenko.qmapp.ui.navigation.Route
import com.simenko.qmapp.utils.StringUtils

enum class DrawerMenuItems(override val tag: String, override val image: ImageVector, override val group: MenuItem.MenuGroup) : MenuItem {
    COMPANY_PROFILE(Route.Main.CompanyProfile.link, Icons.Filled.Factory, MenuItem.MenuGroup.COMPANY),
    TEAM(Page.TEAM.link, Icons.Filled.Person, MenuItem.MenuGroup.COMPANY),
    COMPANY_STRUCTURE(Route.Main.CompanyStructure.link, Icons.Filled.AccountTree, MenuItem.MenuGroup.COMPANY),
    COMPANY_PRODUCTS(Route.Main.CompanyProducts.link, Icons.Filled.ShoppingBag, MenuItem.MenuGroup.COMPANY),

    ALL_INVESTIGATIONS(Page.INVESTIGATIONS.link, Icons.Filled.SquareFoot, MenuItem.MenuGroup.QUALITY),
    PROCESS_CONTROL(Page.PROCESS_CONTROL.link, Icons.Filled.Checklist, MenuItem.MenuGroup.QUALITY),
    SCRAP_LEVEL(Route.Main.ScrapLevel.link, Icons.Filled.AttachMoney, MenuItem.MenuGroup.QUALITY),

    ACCOUNT_SETTINGS(Page.ACCOUNT_SETTINGS.link, Icons.Filled.Settings, MenuItem.MenuGroup.GENERAL)
    ;

    override val title: String get() = StringUtils.getWithSpacesTitle(this.name)

    companion object {
        fun toList(): List<MenuItem> = DrawerMenuItems.values().toList()
        val startingDrawerMenuItem: MenuItem = TEAM
    }
}