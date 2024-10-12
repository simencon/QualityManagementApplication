package com.simenko.qmapp.ui.main.main.content

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType
import com.simenko.qmapp.domain.EmptyString
import com.simenko.qmapp.domain.SelectedNumber
import com.simenko.qmapp.ui.navigation.Route

enum class Page(
    val drawerLink: String,
    val navIcon: ImageVector?,
    val title: String?,
    val titlePlaceholderText: String?,
    val keyboardType: KeyboardType?,
    val searchBtnIcon: ImageVector?,
    val topTabsContent: List<TabItem>?,
    var fabIcon: ImageVector?,
    val actionBtnIcon: ImageVector?,
    val actionMenuItems: List<MenuItem>? = null
) {
    EMPTY_PAGE(EmptyString.str, null, null, null, null, null, null, null, null, null),

    TEAM(
        drawerLink = Route.Main.Team::class.qualifiedName ?: EmptyString.str,
        navIcon = Icons.Filled.Menu,
        title = "Company team",
        titlePlaceholderText = "Search by full name",
        keyboardType = KeyboardType.Text,
        searchBtnIcon = Icons.Filled.Search,
        topTabsContent = TeamTabs.toList(),
        fabIcon = Icons.Filled.Add,
        actionBtnIcon = Icons.Filled.MoreVert,
        actionMenuItems = TeamActions.toList()
    ),
    COMPANY_STRUCTURE(
        drawerLink = Route.Main.CompanyStructure::class.qualifiedName ?: EmptyString.str,
        navIcon = Icons.Filled.Menu,
        title = "Company structure",
        titlePlaceholderText = null,
        keyboardType = null,
        searchBtnIcon = null,
        topTabsContent = null,
        fabIcon = Icons.Filled.Add,
        actionBtnIcon = Icons.Filled.MoreVert,
        actionMenuItems = CompanyStructureActions.toList()
    ),
    PRODUCTS(
        drawerLink = Route.Main.ProductLines::class.qualifiedName ?: EmptyString.str,
        navIcon = Icons.Filled.Menu,
        title = "Product lines",
        titlePlaceholderText = null,
        keyboardType = null,
        searchBtnIcon = null,
        topTabsContent = null,
        fabIcon = Icons.Filled.Add,
        actionBtnIcon = Icons.Filled.MoreVert,
        actionMenuItems = CompanyProductsActions.toList()
    ),
    PRODUCT_LINE_CHARACTERISTICS(
        drawerLink = EmptyString.str,
        navIcon = Icons.AutoMirrored.Filled.ArrowBack,
        title = "All characteristics",
        titlePlaceholderText = "Search by description",
        keyboardType = KeyboardType.Text,
        searchBtnIcon = Icons.Filled.Search,
        topTabsContent = null,
        fabIcon = Icons.Filled.Add,
        actionBtnIcon = null,
        actionMenuItems = null
    ),
    PRODUCT_LINE_KEYS(
        drawerLink = EmptyString.str,
        navIcon = Icons.AutoMirrored.Filled.ArrowBack,
        title = "Product line designations",
        titlePlaceholderText = "Search by designations",
        keyboardType = KeyboardType.Text,
        searchBtnIcon = Icons.Filled.Search,
        topTabsContent = null,
        fabIcon = Icons.Filled.Add,
        actionBtnIcon = null,
        actionMenuItems = null
    ),
    PRODUCT_KINDS(
        drawerLink = EmptyString.str,
        navIcon = Icons.AutoMirrored.Filled.ArrowBack,
        title = "Products",
        titlePlaceholderText = "Search by description",
        keyboardType = KeyboardType.Text,
        searchBtnIcon = Icons.Filled.Search,
        topTabsContent = null,
        fabIcon = Icons.Filled.Add,
        actionBtnIcon = null,
        actionMenuItems = null
    ),
    PRODUCT_KIND_KEYS(
        drawerLink = EmptyString.str,
        navIcon = Icons.AutoMirrored.Filled.ArrowBack,
        title = "Product designations",
        titlePlaceholderText = "Search by designations",
        keyboardType = KeyboardType.Text,
        searchBtnIcon = Icons.Filled.Search,
        topTabsContent = null,
        fabIcon = Icons.Filled.Add,
        actionBtnIcon = null,
        actionMenuItems = null
    ),
    PRODUCT_KIND_CHARACTERISTICS(
        drawerLink = EmptyString.str,
        navIcon = Icons.AutoMirrored.Filled.ArrowBack,
        title = "Product characteristics",
        titlePlaceholderText = "Search by char. name",
        keyboardType = KeyboardType.Text,
        searchBtnIcon = Icons.Filled.Search,
        topTabsContent = null,
        fabIcon = Icons.Filled.Add,
        actionBtnIcon = null,
        actionMenuItems = null
    ),
    PRODUCT_KIND_SPECIFICATION(
        drawerLink = EmptyString.str,
        navIcon = Icons.AutoMirrored.Filled.ArrowBack,
        title = "Product specification",
        titlePlaceholderText = "Search description",
        keyboardType = KeyboardType.Text,
        searchBtnIcon = Icons.Filled.Search,
        topTabsContent = null,
        fabIcon = Icons.Filled.Add,
        actionBtnIcon = null,
        actionMenuItems = null
    ),
    PRODUCT_KIND_LIST(
        drawerLink = EmptyString.str,
        navIcon = Icons.AutoMirrored.Filled.ArrowBack,
        title = "Product list",
        titlePlaceholderText = "Search by name",
        keyboardType = KeyboardType.Text,
        searchBtnIcon = Icons.Filled.Search,
        topTabsContent = null,
        fabIcon = Icons.Filled.Add,
        actionBtnIcon = null,
        actionMenuItems = null
    ),
    VERSION_TOLERANCES(
        drawerLink = EmptyString.str,
        navIcon = Icons.AutoMirrored.Filled.ArrowBack,
        title = "Version specification",
        titlePlaceholderText = "Search by name",
        keyboardType = KeyboardType.Text,
        searchBtnIcon = Icons.Filled.Search,
        topTabsContent = null,
        fabIcon = Icons.Filled.Edit,
        actionBtnIcon = null,
        actionMenuItems = null
    ),
    COMPONENT_KIND_KEYS(
        drawerLink = EmptyString.str,
        navIcon = Icons.AutoMirrored.Filled.ArrowBack,
        title = "Component designations",
        titlePlaceholderText = "Search by designations",
        keyboardType = KeyboardType.Text,
        searchBtnIcon = Icons.Filled.Search,
        topTabsContent = null,
        fabIcon = Icons.Filled.Add,
        actionBtnIcon = null,
        actionMenuItems = null
    ),
    COMPONENT_KIND_CHARACTERISTICS(
        drawerLink = EmptyString.str,
        navIcon = Icons.AutoMirrored.Filled.ArrowBack,
        title = "Comp. characteristics",
        titlePlaceholderText = "Search by char. name",
        keyboardType = KeyboardType.Text,
        searchBtnIcon = Icons.Filled.Search,
        topTabsContent = null,
        fabIcon = Icons.Filled.Add,
        actionBtnIcon = null,
        actionMenuItems = null
    ),
    COMPONENT_STAGE_KIND_KEYS(
        drawerLink = EmptyString.str,
        navIcon = Icons.AutoMirrored.Filled.ArrowBack,
        title = "Comp. stage designations",
        titlePlaceholderText = "Search by designations",
        keyboardType = KeyboardType.Text,
        searchBtnIcon = Icons.Filled.Search,
        topTabsContent = null,
        fabIcon = Icons.Filled.Add,
        actionBtnIcon = null,
        actionMenuItems = null
    ),
    COMPONENT_STAGE_KIND_CHARACTERISTICS(
        drawerLink = EmptyString.str,
        navIcon = Icons.AutoMirrored.Filled.ArrowBack,
        title = "Comp. stage char.",
        titlePlaceholderText = "Search by char. name",
        keyboardType = KeyboardType.Text,
        searchBtnIcon = Icons.Filled.Search,
        topTabsContent = null,
        fabIcon = Icons.Filled.Add,
        actionBtnIcon = null,
        actionMenuItems = null
    ),
    INVESTIGATIONS(
        drawerLink = Route.Main.AllInvestigations::class.qualifiedName ?: EmptyString.str,
        navIcon = Icons.Filled.Menu,
        title = "All investigations",
        titlePlaceholderText = "Search order by number",
        keyboardType = KeyboardType.Decimal,
        searchBtnIcon = Icons.Filled.Search,
        topTabsContent = ProgressTabs.toList(),
        fabIcon = Icons.Filled.Add,
        actionBtnIcon = Icons.Filled.MoreVert,
        actionMenuItems = InvestigationsActions.toList()
    ),
    PROCESS_CONTROL(
        drawerLink = Route.Main.ProcessControl::class.qualifiedName ?: EmptyString.str,
        navIcon = Icons.Filled.Menu,
        title = "Process control",
        titlePlaceholderText = "Search order by number",
        keyboardType = KeyboardType.Decimal,
        searchBtnIcon = Icons.Filled.Search,
        topTabsContent = ProgressTabs.toList(),
        fabIcon = Icons.Filled.Add,
        actionBtnIcon = null,
        actionMenuItems = ProcessControlActions.toList()
    ),
    ACCOUNT_SETTINGS(
        drawerLink = Route.Main.Settings::class.qualifiedName ?: EmptyString.str,
        navIcon = Icons.Filled.Menu,
        title = "Account settings",
        titlePlaceholderText = null,
        keyboardType = null,
        searchBtnIcon = null,
        topTabsContent = null,
        fabIcon = null,
        actionBtnIcon = Icons.Filled.MoreVert,
        actionMenuItems = AccountSettingsActions.toList()
    ),

    ADD_EMPLOYEE(EmptyString.str, Icons.AutoMirrored.Filled.ArrowBack, "Add new employee", null, null, null, null, Icons.Filled.Save, null),
    EDIT_EMPLOYEE(EmptyString.str, Icons.AutoMirrored.Filled.ArrowBack, "Edit employee", null, null, null, null, Icons.Filled.Save, null),

    AUTHORIZE_USER(EmptyString.str, Icons.AutoMirrored.Filled.ArrowBack, "Authorize user", null, null, null, null, Icons.Filled.Save, null),
    EDIT_USER(EmptyString.str, Icons.AutoMirrored.Filled.ArrowBack, "Edit user", null, null, null, null, Icons.Filled.Save, null),

    ADD_DEPARTMENT(EmptyString.str, Icons.AutoMirrored.Filled.ArrowBack, "Add new department", null, null, null, null, Icons.Filled.Save, null),
    EDIT_DEPARTMENT(EmptyString.str, Icons.AutoMirrored.Filled.ArrowBack, "Edit department", null, null, null, null, Icons.Filled.Save, null),
    DEPARTMENT_PRODUCT_LINES(EmptyString.str, Icons.AutoMirrored.Filled.ArrowBack, "Dep. product lines", null, null, null, null, Icons.Filled.Add, null),

    ADD_SUB_DEPARTMENT(EmptyString.str, Icons.AutoMirrored.Filled.ArrowBack, "Add new sub department", null, null, null, null, Icons.Filled.Save, null),
    EDIT_SUB_DEPARTMENT(EmptyString.str, Icons.AutoMirrored.Filled.ArrowBack, "Edit sub department", null, null, null, null, Icons.Filled.Save, null),
    SUB_DEPARTMENT_ITEM_KINDS(
        drawerLink = EmptyString.str,
        navIcon = Icons.AutoMirrored.Filled.ArrowBack,
        title = "Sub dep. item kinds",
        titlePlaceholderText = null,
        keyboardType = null,
        searchBtnIcon = null,
        topTabsContent = SubDepItemKindsTabs.toList(),
        fabIcon = Icons.Filled.Add,
        actionBtnIcon = null
    ),

    ADD_CHANNEL(EmptyString.str, Icons.AutoMirrored.Filled.ArrowBack, "Add new channel", null, null, null, null, Icons.Filled.Save, null),
    EDIT_CHANNEL(EmptyString.str, Icons.AutoMirrored.Filled.ArrowBack, "Edit channel", null, null, null, null, Icons.Filled.Save, null),
    CHANNEL_ITEM_KEYS(
        drawerLink = EmptyString.str,
        navIcon = Icons.AutoMirrored.Filled.ArrowBack,
        title = "Channel item keys",
        titlePlaceholderText = null,
        keyboardType = null,
        searchBtnIcon = null,
        topTabsContent = ChannelItemKeysTabs.toList(),
        fabIcon = Icons.Filled.Add,
        actionBtnIcon = null
    ),

    ADD_LINE(EmptyString.str, Icons.AutoMirrored.Filled.ArrowBack, "Add new line", null, null, null, null, Icons.Filled.Save, null),
    EDIT_LINE(EmptyString.str, Icons.AutoMirrored.Filled.ArrowBack, "Edit line", null, null, null, null, Icons.Filled.Save, null),
    LINE_ITEMS(
        drawerLink = EmptyString.str,
        navIcon = Icons.AutoMirrored.Filled.ArrowBack,
        title = "Manufacturing line items",
        titlePlaceholderText = null,
        keyboardType = null,
        searchBtnIcon = null,
        topTabsContent = LineItemsTabs.toList(),
        fabIcon = Icons.Filled.Add,
        actionBtnIcon = null
    ),

    ADD_OPERATION(EmptyString.str, Icons.AutoMirrored.Filled.ArrowBack, "Add new operation", null, null, null, null, Icons.Filled.Save, null),
    EDIT_OPERATION(EmptyString.str, Icons.AutoMirrored.Filled.ArrowBack, "Edit operation", null, null, null, null, Icons.Filled.Save, null),

    ADD_PRODUCT_LINE(EmptyString.str, Icons.AutoMirrored.Filled.ArrowBack, "Add product line", null, null, null, null, Icons.Filled.Save, null),
    EDIT_PRODUCT_LINE(EmptyString.str, Icons.AutoMirrored.Filled.ArrowBack, "Edit product line", null, null, null, null, Icons.Filled.Save, null),

    ADD_PRODUCT_LINE_KEY(EmptyString.str, Icons.AutoMirrored.Filled.ArrowBack, "Add product line design.", null, null, null, null, Icons.Filled.Save, null),
    EDIT_PRODUCT_LINE_KEY(EmptyString.str, Icons.AutoMirrored.Filled.ArrowBack, "Edit product line design.", null, null, null, null, Icons.Filled.Save, null),

    ADD_PRODUCT_KIND(EmptyString.str, Icons.AutoMirrored.Filled.ArrowBack, "Add product kind", null, null, null, null, Icons.Filled.Save, null),
    EDIT_PRODUCT_KIND(EmptyString.str, Icons.AutoMirrored.Filled.ArrowBack, "Edit product kind", null, null, null, null, Icons.Filled.Save, null),

    ADD_COMPONENT_KIND(EmptyString.str, Icons.AutoMirrored.Filled.ArrowBack, "Add component kind", null, null, null, null, Icons.Filled.Save, null),
    EDIT_COMPONENT_KIND(EmptyString.str, Icons.AutoMirrored.Filled.ArrowBack, "Edit component kind", null, null, null, null, Icons.Filled.Save, null),

    ADD_COMPONENT_STAGE_KIND(EmptyString.str, Icons.AutoMirrored.Filled.ArrowBack, "Add component stage kind", null, null, null, null, Icons.Filled.Save, null),
    EDIT_COMPONENT_STAGE_KIND(EmptyString.str, Icons.AutoMirrored.Filled.ArrowBack, "Edit component stage kind", null, null, null, null, Icons.Filled.Save, null),

    ADD_PRODUCT_KIND_PRODUCT(EmptyString.str, Icons.AutoMirrored.Filled.ArrowBack, "Add product", null, null, null, null, Icons.Filled.Save, null),
    EDIT_PRODUCT_KIND_PRODUCT(EmptyString.str, Icons.AutoMirrored.Filled.ArrowBack, "Edit product", null, null, null, null, Icons.Filled.Save, null),

    ADD_PRODUCT_COMPONENT(EmptyString.str, Icons.AutoMirrored.Filled.ArrowBack, "Add component", null, null, null, null, Icons.Filled.Save, null),
    EDIT_PRODUCT_COMPONENT(EmptyString.str, Icons.AutoMirrored.Filled.ArrowBack, "Edit component", null, null, null, null, Icons.Filled.Save, null),

    ADD_PRODUCT_LINE_CHAR_GROUP(EmptyString.str, Icons.AutoMirrored.Filled.ArrowBack, "Add char. group", null, null, null, null, Icons.Filled.Save, null),
    EDIT_PRODUCT_LINE_CHAR_GROUP(EmptyString.str, Icons.AutoMirrored.Filled.ArrowBack, "Edit char. group", null, null, null, null, Icons.Filled.Save, null),

    ADD_PRODUCT_LINE_CHAR_SUB_GROUP(EmptyString.str, Icons.AutoMirrored.Filled.ArrowBack, "Add new char. sub group", null, null, null, null, Icons.Filled.Save, null),
    EDIT_PRODUCT_LINE_CHAR_SUB_GROUP(EmptyString.str, Icons.AutoMirrored.Filled.ArrowBack, "Edit char. sub group", null, null, null, null, Icons.Filled.Save, null),

    ADD_PRODUCT_LINE_CHAR(EmptyString.str, Icons.AutoMirrored.Filled.ArrowBack, "Add characteristic", null, null, null, null, Icons.Filled.Save, null),
    EDIT_PRODUCT_LINE_CHAR(EmptyString.str, Icons.AutoMirrored.Filled.ArrowBack, "Edit characteristic", null, null, null, null, Icons.Filled.Save, null),

    ADD_PRODUCT_LINE_METRIC(EmptyString.str, Icons.AutoMirrored.Filled.ArrowBack, "Add metric", null, null, null, null, Icons.Filled.Save, null),
    EDIT_PRODUCT_LINE_METRIC(EmptyString.str, Icons.AutoMirrored.Filled.ArrowBack, "Edit metric", null, null, null, null, Icons.Filled.Save, null),

    ADD_ORDER(EmptyString.str, Icons.AutoMirrored.Filled.ArrowBack, "New investigation order", null, null, null, null, Icons.Filled.Save, null),
    EDIT_ORDER(EmptyString.str, Icons.AutoMirrored.Filled.ArrowBack, "Edit investigation order", null, null, null, null, Icons.Filled.Save, null),
    ADD_SUB_ORDER(EmptyString.str, Icons.AutoMirrored.Filled.ArrowBack, "Add new sub order", null, null, null, null, Icons.Filled.Save, null),
    EDIT_SUB_ORDER(EmptyString.str, Icons.AutoMirrored.Filled.ArrowBack, "Edit sub order", null, null, null, null, Icons.Filled.Save, null),
    ADD_SUB_ORDER_SA(EmptyString.str, Icons.AutoMirrored.Filled.ArrowBack, "New process control order", null, null, null, null, Icons.Filled.Save, null),
    EDIT_SUB_ORDER_SA(EmptyString.str, Icons.AutoMirrored.Filled.ArrowBack, "Edit process control order", null, null, null, null, Icons.Filled.Save, null),

    ACCOUNT_EDIT(EmptyString.str, Icons.AutoMirrored.Filled.ArrowBack, "Edit account data", null, null, null, null, Icons.Filled.Save, null);

    fun withCustomFabIcon(icon: ImageVector): Page {
        fabIcon = icon
        return this
    }
}

interface MenuItem {
    val tag: String
    val title: String
    val image: ImageVector
    val group: MenuGroup

    enum class MenuGroup(val group: String) {
        COMPANY("Company"),
        QUALITY("Quality management"),
        GENERAL("General"),
        ACTIONS("Actions"),
        FILTER("Filter")
    }
}

interface TabItem {
    val index: Int
    val tag: SelectedNumber
    val title: String

    var badgeCount: Int
    var badgeBackgroundColor: Color
    var badgeContentColor: Color

    fun getBadge(): Triple<Int, Color, Color>
    fun updateBadge(state: Triple<Int, Color, Color>): TabItem

    interface Static {
        val startingTabItem: TabItem
    }
}