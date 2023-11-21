package com.simenko.qmapp.ui.main.main.content

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
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
    val link: String,
    val navIcon: ImageVector?,
    val title: String?,
    val titlePlaceholderText: String?,
    val keyboardType: KeyboardType?,
    val searchBtnIcon: ImageVector?,
    val topTabsContent: List<TabItem>?,
    val fabIcon: ImageVector?,
    val actionBtnIcon: ImageVector?,
    val actionMenuItems: List<MenuItem>? = null
) {
    EMPTY_PAGE(EmptyString.str, null, null, null, null, null, null, null, null, null),

    TEAM(
        link = Route.Main.Team.link,
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
        link = Route.Main.CompanyStructure.link,
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
        link = Route.Main.Products.link,
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
        link = Route.Main.Products.ProductLines.Characteristics.link,
        navIcon = Icons.Filled.ArrowBack,
        title = "Characteristics",
        titlePlaceholderText = "Search by description",
        keyboardType = KeyboardType.Text,
        searchBtnIcon = Icons.Filled.Search,
        topTabsContent = null,
        fabIcon = Icons.Filled.Add,
        actionBtnIcon = null,
        actionMenuItems = null
    ),
    PRODUCT_LINE_KEYS(
        link = Route.Main.Products.ProductLines.ProductLineKeys.link,
        navIcon = Icons.Filled.ArrowBack,
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
        link = Route.Main.Products.ProductLines.ProductKinds.link,
        navIcon = Icons.Filled.ArrowBack,
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
        link = Route.Main.Products.ProductLines.ProductKinds.ProductKindKeys.link,
        navIcon = Icons.Filled.ArrowBack,
        title = "Product designations",
        titlePlaceholderText = "Search by designations",
        keyboardType = KeyboardType.Text,
        searchBtnIcon = Icons.Filled.Search,
        topTabsContent = null,
        fabIcon = Icons.Filled.Add,
        actionBtnIcon = null,
        actionMenuItems = null
    ),
    PRODUCT_KIND_SPECIFICATION(
        link = Route.Main.Products.ProductLines.ProductKinds.ProductSpecification.link,
        navIcon = Icons.Filled.ArrowBack,
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
        link = Route.Main.Products.ProductLines.ProductKinds.ProductList.link,
        navIcon = Icons.Filled.ArrowBack,
        title = "Product list",
        titlePlaceholderText = "Search by name",
        keyboardType = KeyboardType.Text,
        searchBtnIcon = Icons.Filled.Search,
        topTabsContent = null,
        fabIcon = Icons.Filled.Add,
        actionBtnIcon = null,
        actionMenuItems = null
    ),
    COMPONENT_KIND_KEYS(
        link = Route.Main.Products.ProductLines.ProductKinds.ProductSpecification.ComponentKindKeys.link,
        navIcon = Icons.Filled.ArrowBack,
        title = "Component designations",
        titlePlaceholderText = "Search by designations",
        keyboardType = KeyboardType.Text,
        searchBtnIcon = Icons.Filled.Search,
        topTabsContent = null,
        fabIcon = Icons.Filled.Add,
        actionBtnIcon = null,
        actionMenuItems = null
    ),
    COMPONENT_STAGE_KIND_KEYS(
        link = Route.Main.Products.ProductLines.ProductKinds.ProductSpecification.ComponentStageKindKeys.link,
        navIcon = Icons.Filled.ArrowBack,
        title = "Component stage designations",
        titlePlaceholderText = "Search by designations",
        keyboardType = KeyboardType.Text,
        searchBtnIcon = Icons.Filled.Search,
        topTabsContent = null,
        fabIcon = Icons.Filled.Add,
        actionBtnIcon = null,
        actionMenuItems = null
    ),
    INVESTIGATIONS(
        link = Route.Main.Inv.link,
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
        link = Route.Main.ProcessControl.link,
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
        link = Route.Main.Settings.link,
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

    ADD_EMPLOYEE(Route.Main.Team.EmployeeAddEdit.link, Icons.Filled.ArrowBack, "Add new employee", null, null, null, null, Icons.Filled.Save, null),
    EDIT_EMPLOYEE(Route.Main.Team.EmployeeAddEdit.link, Icons.Filled.ArrowBack, "Edit employee", null, null, null, null, Icons.Filled.Save, null),

    AUTHORIZE_USER(Route.Main.Team.AuthorizeUser.link, Icons.Filled.ArrowBack, "Authorize user", null, null, null, null, Icons.Filled.Save, null),
    EDIT_USER(Route.Main.Team.EditUser.link, Icons.Filled.ArrowBack, "Edit user", null, null, null, null, Icons.Filled.Save, null),

    ADD_DEPARTMENT(Route.Main.CompanyStructure.DepartmentAddEdit.link, Icons.Filled.ArrowBack, "Add new department", null, null, null, null, Icons.Filled.Save, null),
    EDIT_DEPARTMENT(Route.Main.CompanyStructure.DepartmentAddEdit.link, Icons.Filled.ArrowBack, "Edit department", null, null, null, null, Icons.Filled.Save, null),

    ADD_SUB_DEPARTMENT(Route.Main.CompanyStructure.SubDepartmentAddEdit.link, Icons.Filled.ArrowBack, "Add new sub department", null, null, null, null, Icons.Filled.Save, null),
    EDIT_SUB_DEPARTMENT(Route.Main.CompanyStructure.SubDepartmentAddEdit.link, Icons.Filled.ArrowBack, "Edit sub department", null, null, null, null, Icons.Filled.Save, null),

    ADD_CHANNEL(Route.Main.CompanyStructure.ChannelAddEdit.link, Icons.Filled.ArrowBack, "Add new channel", null, null, null, null, Icons.Filled.Save, null),
    EDIT_CHANNEL(Route.Main.CompanyStructure.ChannelAddEdit.link, Icons.Filled.ArrowBack, "Edit channel", null, null, null, null, Icons.Filled.Save, null),

    ADD_LINE(Route.Main.CompanyStructure.LineAddEdit.link, Icons.Filled.ArrowBack, "Add new line", null, null, null, null, Icons.Filled.Save, null),
    EDIT_LINE(Route.Main.CompanyStructure.LineAddEdit.link, Icons.Filled.ArrowBack, "Edit line", null, null, null, null, Icons.Filled.Save, null),

    ADD_OPERATION(Route.Main.CompanyStructure.OperationAddEdit.link, Icons.Filled.ArrowBack, "Add new operation", null, null, null, null, Icons.Filled.Save, null),
    EDIT_OPERATION(Route.Main.CompanyStructure.OperationAddEdit.link, Icons.Filled.ArrowBack, "Edit operation", null, null, null, null, Icons.Filled.Save, null),

    ADD_PRODUCT_PROJECT(Route.Main.CompanyStructure.OperationAddEdit.link, Icons.Filled.ArrowBack, "Add new manufacturing project", null, null, null, null, Icons.Filled.Save, null),
    EDIT_PRODUCT_PROJECT(Route.Main.CompanyStructure.OperationAddEdit.link, Icons.Filled.ArrowBack, "Edit manufacturing project", null, null, null, null, Icons.Filled.Save, null),

    ADD_PRODUCT_KIND(Route.Main.CompanyStructure.OperationAddEdit.link, Icons.Filled.ArrowBack, "Add new product kind", null, null, null, null, Icons.Filled.Save, null),
    EDIT_PRODUCT_KIND(Route.Main.CompanyStructure.OperationAddEdit.link, Icons.Filled.ArrowBack, "Edit product kind", null, null, null, null, Icons.Filled.Save, null),

    ADD_COMPONENT_KIND(Route.Main.CompanyStructure.OperationAddEdit.link, Icons.Filled.ArrowBack, "Add new component kind", null, null, null, null, Icons.Filled.Save, null),
    EDIT_COMPONENT_KIND(Route.Main.CompanyStructure.OperationAddEdit.link, Icons.Filled.ArrowBack, "Edit component kind", null, null, null, null, Icons.Filled.Save, null),

    ADD_COMPONENT_STAGE_KIND(Route.Main.CompanyStructure.OperationAddEdit.link, Icons.Filled.ArrowBack, "Add new component stage kind", null, null, null, null, Icons.Filled.Save, null),
    EDIT_COMPONENT_STAGE_KIND(Route.Main.CompanyStructure.OperationAddEdit.link, Icons.Filled.ArrowBack, "Edit component stage kind", null, null, null, null, Icons.Filled.Save, null),

    ADD_ORDER(Route.Main.OrderAddEdit.link, Icons.Filled.ArrowBack, "New investigation order", null, null, null, null, Icons.Filled.Save, null),
    EDIT_ORDER(Route.Main.OrderAddEdit.link, Icons.Filled.ArrowBack, "Edit investigation order", null, null, null, null, Icons.Filled.Save, null),
    ADD_SUB_ORDER(Route.Main.SubOrderAddEdit.link, Icons.Filled.ArrowBack, "Add new sub order", null, null, null, null, Icons.Filled.Save, null),
    EDIT_SUB_ORDER(Route.Main.SubOrderAddEdit.link, Icons.Filled.ArrowBack, "Edit sub order", null, null, null, null, Icons.Filled.Save, null),
    ADD_SUB_ORDER_SA(Route.Main.SubOrderAddEdit.link, Icons.Filled.ArrowBack, "New process control order", null, null, null, null, Icons.Filled.Save, null),
    EDIT_SUB_ORDER_SA(Route.Main.SubOrderAddEdit.link, Icons.Filled.ArrowBack, "Edit process control order", null, null, null, null, Icons.Filled.Save, null),

    ACCOUNT_EDIT(Route.Main.Settings.EditUserDetails.link, Icons.Filled.ArrowBack, "Edit account data", null, null, null, null, Icons.Filled.Save, null),
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