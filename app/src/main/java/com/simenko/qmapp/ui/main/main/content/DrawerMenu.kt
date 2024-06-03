package com.simenko.qmapp.ui.main.main.content

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector
import com.simenko.qmapp.domain.EmptyString
import com.simenko.qmapp.utils.StringUtils

enum class DrawerMenuItems(override val tag: String, override val image: ImageVector, override val group: MenuItem.MenuGroup) : MenuItem {
    COMPANY_PROFILE(EmptyString.str, Icons.Filled.Factory, MenuItem.MenuGroup.COMPANY),
    TEAM(Page.TEAM.drawerLink, Icons.Filled.Person, MenuItem.MenuGroup.COMPANY),
    COMPANY_STRUCTURE(Page.COMPANY_STRUCTURE.drawerLink, Icons.Filled.AccountTree, MenuItem.MenuGroup.COMPANY),
    COMPANY_PRODUCTS(Page.PRODUCTS.drawerLink, Icons.Filled.ShoppingBag, MenuItem.MenuGroup.COMPANY),

    ALL_INVESTIGATIONS(Page.INVESTIGATIONS.drawerLink, Icons.Filled.SquareFoot, MenuItem.MenuGroup.QUALITY),
    PROCESS_CONTROL(Page.PROCESS_CONTROL.drawerLink, Icons.Filled.Checklist, MenuItem.MenuGroup.QUALITY),
    SCRAP_LEVEL(EmptyString.str, Icons.Filled.AttachMoney, MenuItem.MenuGroup.QUALITY),

    ACCOUNT_SETTINGS(Page.ACCOUNT_SETTINGS.drawerLink, Icons.Filled.Settings, MenuItem.MenuGroup.GENERAL)
    ;

    override val title: String get() = StringUtils.getWithSpacesTitle(this.name)

    companion object {
        fun toList(): List<MenuItem> = entries
        val startingDrawerMenuItem: MenuItem = TEAM
    }
}