package com.simenko.qmapp.presentation.ui.main.main.content

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector
import com.simenko.qmapp.utils.StringUtils

enum class Common(override val tag: String, override val image: ImageVector, override val group: MenuItem.MenuGroup) : MenuItem {
    UPLOAD_MASTER_DATA("upload_master_data", Icons.Filled.Refresh, MenuItem.MenuGroup.ACTIONS),
    NO_FILTER("no_filter", Icons.Filled.FilterAltOff, MenuItem.MenuGroup.FILTER),
    CUSTOM_FILTER("custom_filter", Icons.Filled.FilterAlt, MenuItem.MenuGroup.FILTER)
    ;

    override val title: String get() = StringUtils.getWithSpacesTitle(this.name)

    companion object {
        fun toList(): List<MenuItem> = Common.values().toList()
    }
}

enum class CompanyStructureActions(override val tag: String, override val image: ImageVector, override val group: MenuItem.MenuGroup) : MenuItem {
    SYNC_STRUCTURE_DATA("sync_structure_data", Icons.Filled.Refresh, MenuItem.MenuGroup.ACTIONS),
    ;

    override val title: String get() = StringUtils.getWithSpacesTitle(this.name)

    companion object {
        fun toList(): List<MenuItem> = Common.toList().union(TeamActions.values().toList()).toList()
    }
}

enum class CompanyProductsActions(override val tag: String, override val image: ImageVector, override val group: MenuItem.MenuGroup) : MenuItem {
    SYNC_STRUCTURE_DATA("sync_structure_data", Icons.Filled.Refresh, MenuItem.MenuGroup.ACTIONS),
    ;

    override val title: String get() = StringUtils.getWithSpacesTitle(this.name)

    companion object {
        fun toList(): List<MenuItem> = Common.toList().union(TeamActions.values().toList()).toList()
    }
}

enum class TeamActions(override val tag: String, override val image: ImageVector, override val group: MenuItem.MenuGroup) : MenuItem {
    SYNC_TEAM_DATA("sync_team_data", Icons.Filled.Refresh, MenuItem.MenuGroup.ACTIONS),
    ;

    override val title: String get() = StringUtils.getWithSpacesTitle(this.name)

    companion object {
        fun toList(): List<MenuItem> = Common.toList().union(TeamActions.values().toList()).toList()
    }
}

enum class InvestigationsActions(override val tag: String, override val image: ImageVector, override val group: MenuItem.MenuGroup) : MenuItem {
    SYNC_INVESTIGATIONS("sync_investigations", Icons.Filled.Refresh, MenuItem.MenuGroup.ACTIONS),
    PPAP("ppap", Icons.Filled.Filter1, MenuItem.MenuGroup.FILTER),
    INCOMING_INSPECTION("incoming_inspection", Icons.Filled.Filter2, MenuItem.MenuGroup.FILTER),
    PROCESS_CONTROL("process_control", Icons.Filled.Filter3, MenuItem.MenuGroup.FILTER),
    PRODUCT_AUDIT("product_audit", Icons.Filled.Filter4, MenuItem.MenuGroup.FILTER)
    ;

    override val title: String get() = if (this == PPAP) StringUtils.getWithSpaces(this.name) else StringUtils.getWithSpacesTitle(this.name)

    companion object {
        fun toList(): List<MenuItem> = Common.toList().union(InvestigationsActions.values().toList()).toList()
    }
}

enum class ProcessControlActions(override val tag: String, override val image: ImageVector, override val group: MenuItem.MenuGroup) : MenuItem {
    SYNC_INVESTIGATIONS("sync_investigations", Icons.Filled.Refresh, MenuItem.MenuGroup.ACTIONS),
    ;

    override val title: String get() = StringUtils.getWithSpacesTitle(this.name)

    companion object {
        fun toList(): List<MenuItem> = Common.toList().union(ProcessControlActions.values().toList()).toList()
    }
}

enum class AccountSettingsActions(override val tag: String, override val image: ImageVector, override val group: MenuItem.MenuGroup) : MenuItem {
    SYNC_ACCOUNT_DATA("sync_account_data", Icons.Filled.Refresh, MenuItem.MenuGroup.ACTIONS),
    ;

    override val title: String get() = StringUtils.getWithSpacesTitle(this.name)

    companion object {
        fun toList(): List<MenuItem> = Common.toList().union(AccountSettingsActions.values().toList()).toList()
    }
}