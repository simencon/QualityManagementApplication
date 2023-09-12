package com.simenko.qmapp.ui.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Factory
import androidx.compose.material.icons.filled.Filter1
import androidx.compose.material.icons.filled.Filter2
import androidx.compose.material.icons.filled.Filter3
import androidx.compose.material.icons.filled.Filter4
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.FilterAltOff
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.SquareFoot
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TextFieldDefaults.colors
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination
import com.simenko.qmapp.domain.EmptyString
import com.simenko.qmapp.domain.FalseStr
import com.simenko.qmapp.domain.FirstTabId
import com.simenko.qmapp.domain.FourthTabId
import com.simenko.qmapp.domain.NoRecordStr
import com.simenko.qmapp.domain.SecondTabId
import com.simenko.qmapp.domain.TrueStr
import com.simenko.qmapp.domain.SelectedNumber
import com.simenko.qmapp.domain.ThirdTabId
import com.simenko.qmapp.storage.Principle
import com.simenko.qmapp.ui.Screen
import com.simenko.qmapp.utils.StringUtils

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun AppBar(
    screen: MenuItem,
    destination: NavDestination?,

    onDrawerMenuClick: () -> Unit,
    drawerState: DrawerState,

    selectedActionsMenuItemId: MutableState<String>,
    onActionsMenuItemClick: (String, String) -> Unit,

    searchBarState: MutableState<Boolean>,
    onSearchBarSearch: (String) -> Unit,

    addEditMode: Int,
    onBackFromAddEditModeClick: () -> Unit
) {
    val contentColor: Color = MaterialTheme.colorScheme.onPrimary

    val actionsMenuState = rememberSaveable { mutableStateOf(false) }

    val orderToSearch = rememberSaveable { mutableStateOf("") }
    val (focusRequesterSearchBar) = FocusRequester.createRefs()

    LaunchedEffect(searchBarState.value) {
        if (searchBarState.value) focusRequesterSearchBar.requestFocus()
    }

    TopAppBar(
        modifier = Modifier.height(48.dp),
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (addEditMode == AddEditMode.NO_MODE.ordinal)
                    if (searchBarState.value) {
                        BasicTextField(
                            modifier = Modifier
                                .width(202.dp)
                                .padding(start = 10.dp)
                                .focusRequester(focusRequesterSearchBar),
                            value = orderToSearch.value,
                            textStyle = TextStyle(fontSize = 20.sp, color = contentColor, fontWeight = FontWeight.Medium),
                            cursorBrush = SolidColor(contentColor),
                            onValueChange = { orderToSearch.value = it },
                            maxLines = 1,
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Search),
                            keyboardActions = KeyboardActions(onSearch = { onSearchBarSearch(orderToSearch.value) }),
                        ) { innerTextField ->
                            TextFieldDefaults.DecorationBox(
                                value = orderToSearch.value,
                                colors = colors(unfocusedContainerColor = MaterialTheme.colorScheme.primary, unfocusedIndicatorColor = contentColor),
                                placeholder = { Text(text = "Search by order number", color = MaterialTheme.colorScheme.primaryContainer) },
                                innerTextField = innerTextField,
                                enabled = true,
                                singleLine = true,
                                visualTransformation = VisualTransformation.None,
                                interactionSource = remember { MutableInteractionSource() },
                                contentPadding = PaddingValues(2.dp)
                            )
                        }

                        IconButton(
                            onClick = { orderToSearch.value = "" },
                            enabled = orderToSearch.value != "",
                            colors = IconButtonDefaults.iconButtonColors(
                                contentColor = contentColor,
                                disabledContentColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        ) {
                            Icon(imageVector = Icons.Filled.Close, contentDescription = "Search order by number")
                        }
                    } else {
                        Text(text = screen.title, modifier = Modifier.padding(all = 8.dp))
                        if (destination?.route == Screen.Main.Inv.routeWithArgKeys() || destination?.route == Screen.Main.ProcessControl.routeWithArgKeys())
                            IconButton(onClick = { searchBarState.value = true }) {
                                Icon(imageVector = Icons.Filled.Search, contentDescription = "Search order by number", tint = contentColor)
                            }
                    }
                else
                    Text(text = AddEditMode.values()[addEditMode].mode, modifier = Modifier.padding(all = 8.dp))
            }
        },
        navigationIcon = {
            if (addEditMode == AddEditMode.NO_MODE.ordinal) {
                if (searchBarState.value)
                    IconButton(
                        onClick = { searchBarState.value = false },
                        colors = IconButtonDefaults.iconButtonColors(contentColor = contentColor)
                    ) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Hide search bar")
                    }
                else
                    IconButton(
                        onClick = onDrawerMenuClick,
                        colors = IconButtonDefaults.iconButtonColors(contentColor = contentColor)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Menu,
                            contentDescription = "Toggle drawer",
                            modifier = Modifier
                                .rotate(drawerState.offset.value / 1080f * 360f)
                        )
                    }
            } else {
                IconButton(
                    onClick = onBackFromAddEditModeClick,
                    colors = IconButtonDefaults.iconButtonColors(contentColor = contentColor)
                ) {
                    Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Hide add edit bar")
                }
            }
        },
        actions = {
            if (addEditMode == AddEditMode.NO_MODE.ordinal) {
                IconButton(
                    onClick = { actionsMenuState.value = true },
                    colors = IconButtonDefaults.iconButtonColors(contentColor = contentColor)
                ) {
                    Icon(imageVector = Icons.Filled.MoreVert, contentDescription = "More")
                }
                ActionsMenu(
                    actionsMenuState = actionsMenuState,
                    selectedActionsMenuItemId = selectedActionsMenuItemId,
                    onActionsMenuItemClick = onActionsMenuItemClick
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = contentColor
        )
    )
}

@Composable
fun DrawerHeader(userInfo: Principle) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(all = 0.dp)
            .background(MaterialTheme.colorScheme.onPrimaryContainer),
        contentAlignment = Alignment.CenterStart,
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.padding(all = 10.dp)
        ) {
            Image(
                painter = painterResource(id = userInfo.logo),
                contentDescription = null,
                contentScale = ContentScale.FillHeight,
                modifier = Modifier.height(79.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = userInfo.fullName,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    fontWeight = FontWeight.SemiBold
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .padding(all = 0.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = StringUtils.concatThreeStrings1(userInfo.company, userInfo.department, userInfo.subDepartment),
                style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.primaryContainer),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .padding(all = 0.dp)
            )
        }
    }
}

@Composable
fun DrawerBody(
    selectedItemId: String,
    onDrawerItemClick: (String) -> Unit
) {
    Spacer(modifier = Modifier.height(10.dp))
    ItemsGroup(title = MenuItem.MenuGroup.COMPANY.group, withDivider = false)
    navigationAndActionItems.forEach { item ->
        if (item.category == MenuItem.MenuGroup.COMPANY)
            NavigationDrawerItem(
                icon = { Icon(item.image, contentDescription = item.contentDescription) },
                label = { Text(item.title) },
                selected = item.id == selectedItemId,
                onClick = { onDrawerItemClick(item.id) },
                modifier = Modifier
                    .padding(NavigationDrawerItemDefaults.ItemPadding)
                    .height(48.dp)
            )
        else return@forEach
    }
    Spacer(modifier = Modifier.height(10.dp))
    ItemsGroup(title = MenuItem.MenuGroup.QUALITY.group)
    navigationAndActionItems.forEach { item ->
        if (item.category == MenuItem.MenuGroup.QUALITY)
            NavigationDrawerItem(
                icon = { Icon(item.image, contentDescription = item.contentDescription) },
                label = { Text(item.title) },
                selected = item.id == selectedItemId,
                onClick = { onDrawerItemClick(item.id) },
                modifier = Modifier
                    .padding(NavigationDrawerItemDefaults.ItemPadding)
                    .height(48.dp)
            )
        else return@forEach
    }
    Spacer(modifier = Modifier.height(10.dp))
    ItemsGroup(title = MenuItem.MenuGroup.GENERAL.group)
    navigationAndActionItems.forEach { item ->
        if (item.category == MenuItem.MenuGroup.GENERAL)
            NavigationDrawerItem(
                icon = { Icon(item.image, contentDescription = item.contentDescription) },
                label = { Text(item.title) },
                selected = item.id == selectedItemId,
                onClick = { onDrawerItemClick(item.id) },
                modifier = Modifier
                    .padding(NavigationDrawerItemDefaults.ItemPadding)
                    .height(48.dp)
            )
        else return@forEach
    }
}

@Composable
fun ActionsMenu(
    actionsMenuState: MutableState<Boolean>,
    selectedActionsMenuItemId: MutableState<String>,
    onActionsMenuItemClick: (String, String) -> Unit
) {
    val actionsGroup = rememberSaveable { mutableStateOf(MenuItem.MenuGroup.ACTIONS) }
    val isContextMenuVisible = rememberSaveable { mutableStateOf(false) }

    DropdownMenu(
        expanded = actionsMenuState.value,
        onDismissRequest = { actionsMenuState.value = false }
    )
    {
        ActionsMenuTop(onTopMenuItemClick = {
            actionsGroup.value = it
            actionsMenuState.value = false
            isContextMenuVisible.value = true
        }
        )
    }

    DropdownMenu(
        expanded = isContextMenuVisible.value,
        onDismissRequest = { isContextMenuVisible.value = false }
    )
    {
        ActionsMenuContext(
            actionsGroup = actionsGroup.value,
            selectedItemId = selectedActionsMenuItemId,
            onClickBack = {
                actionsMenuState.value = true
                isContextMenuVisible.value = false
            },
            onContextMenuItemClick = { p1, p2 ->
                onActionsMenuItemClick(p1, p2)
                isContextMenuVisible.value = false
            }
        )
    }
}

@Composable
fun ActionsMenuTop(
    onTopMenuItemClick: (MenuItem.MenuGroup) -> Unit
) {
    listOf(MenuItem.MenuGroup.ACTIONS, MenuItem.MenuGroup.FILTER).forEach { item ->
        DropdownMenuItem(
            text = { Text(item.group) },
            onClick = { onTopMenuItemClick(item) },
            leadingIcon = { Icon(Icons.Filled.ArrowBack, contentDescription = item.group) },
            modifier = Modifier
                .padding(NavigationDrawerItemDefaults.ItemPadding)
        )
    }
}

@Composable
fun ActionsMenuContext(
    actionsGroup: MenuItem.MenuGroup,
    selectedItemId: MutableState<String>,
    onClickBack: () -> Unit,
    onContextMenuItemClick: (String, String) -> Unit
) {
    DropdownMenuItem(
        text = { Text(actionsGroup.group) },
        onClick = onClickBack,
        trailingIcon = { Icon(Icons.Filled.ArrowForward, contentDescription = actionsGroup.group) },
        modifier = Modifier
            .padding(NavigationDrawerItemDefaults.ItemPadding)
    )

    navigationAndActionItems.filter { it.category == actionsGroup }.forEach { item ->
        DropdownMenuItem(
            text = { Text(item.title) },
            onClick = {
                onContextMenuItemClick(
                    if (item.category != MenuItem.MenuGroup.ACTIONS && item.id != "custom_filter") item.id else selectedItemId.value,
                    item.id
                )
            },
            enabled = selectedItemId.value != item.id,
            leadingIcon = { Icon(item.image, contentDescription = item.contentDescription) },
            modifier = Modifier
                .padding(NavigationDrawerItemDefaults.ItemPadding)
        )
    }
}

@Composable
fun ItemsGroup(
    title: String,
    withDivider: Boolean = true
) {
    if (withDivider) {
        Divider(
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier
                .height(1.dp)
                .fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(10.dp))
    }

    Text(
        text = title,
        style = MaterialTheme.typography.labelSmall.copy(fontSize = 14.sp),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 10.dp, bottom = 0.dp)
    )
    Spacer(modifier = Modifier.height(5.dp))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopTabs(
    tabs: List<Triple<String, Int, SelectedNumber>>,
    selectedTabIndex: Int,
    badgeCounts: List<Triple<Int, Color, Color>> = listOf(),
    onTabSelectedLambda: (SelectedNumber, Int) -> Unit
) {
    TabRow(selectedTabIndex = selectedTabIndex) {
        tabs.forEach {
            val selected = selectedTabIndex == it.second
            Tab(
                modifier = Modifier.height(40.dp),
                selected = selected,
                onClick = { onTabSelectedLambda(it.third, it.second) }
            ) {
                if (badgeCounts.size >= it.second && badgeCounts[it.second].first > 0)
                    BadgedBox(badge = {
                        Box(modifier = Modifier.background(color = badgeCounts[it.second].second, shape = RoundedCornerShape(size = 3.dp))) {
                            Text(
                                text = badgeCounts[it.second].first.toString(),
                                color = badgeCounts[it.second].third,
                                fontSize = 8.sp,
                                fontWeight = if (selected) FontWeight.Black else FontWeight.SemiBold
                            )
                        }
                    }
                    ) {
                        Text(
                            text = StringUtils.getWithSpaces(it.first),
                            fontSize = 12.sp,
                            style = if (selected) LocalTextStyle.current.copy(fontWeight = FontWeight.Bold) else LocalTextStyle.current
                        )
                    }
                else
                    Text(
                        text = StringUtils.getWithSpaces(it.first),
                        fontSize = 12.sp,
                        style = if (selected) LocalTextStyle.current.copy(fontWeight = FontWeight.Bold) else LocalTextStyle.current
                    )
            }
        }
    }
}

data class MenuItem(
    val id: String,
    val title: String,
    val contentDescription: String,
    val image: ImageVector,
    val category: MenuGroup
) {
    companion object {
        fun getStartingDrawerMenuItem() =
            navigationAndActionItems.find { it.id == Screen.Main.Team.route } ?: navigationAndActionItems[4]

        fun getStartingActionsFilterMenuItem() = navigationAndActionItems[10]

        fun getItemById(id: String) = navigationAndActionItems.findLast { it.id == id } ?: getStartingDrawerMenuItem()
    }

    enum class MenuGroup(val group: String) {
        COMPANY("Company"),
        QUALITY("Quality management"),
        GENERAL("General"),
        ACTIONS("Actions"),
        FILTER("Filter")
    }

    enum class Actions(val action: String) {
        UPLOAD_MD("upload_master_data"),
        SYNC_INV("sync_investigations"),
        CUSTOM_FILTER("custom_filter")
    }
}

private val navigationAndActionItems = listOf(
    MenuItem(Screen.Main.CompanyProfile.route, "Company profile", "Company profile", Icons.Filled.Factory, MenuItem.MenuGroup.COMPANY),
    MenuItem(Screen.Main.Team.route, "Team", "Team", Icons.Filled.Person, MenuItem.MenuGroup.COMPANY),
    MenuItem(Screen.Main.CompanyStructure.route, "Company structure", "Company structure", Icons.Filled.AccountTree, MenuItem.MenuGroup.COMPANY),
    MenuItem(Screen.Main.CompanyProducts.route, "Company products", "Company products", Icons.Filled.ShoppingBag, MenuItem.MenuGroup.COMPANY),

    MenuItem(Screen.Main.Inv.withArgs(NoRecordStr.str, NoRecordStr.str), "All investigations", "All investigations", Icons.Filled.SquareFoot, MenuItem.MenuGroup.QUALITY),
    MenuItem(Screen.Main.ProcessControl.withArgs(NoRecordStr.str, NoRecordStr.str), "Process control", "Process control", Icons.Filled.Checklist, MenuItem.MenuGroup.QUALITY),
    MenuItem(Screen.Main.ScrapLevel.route, "Scrap level", "Scrap level", Icons.Filled.AttachMoney, MenuItem.MenuGroup.QUALITY),

    MenuItem(Screen.Main.Settings.route, "Account settings", "Account settings", Icons.Filled.Settings, MenuItem.MenuGroup.GENERAL),

    MenuItem(MenuItem.Actions.UPLOAD_MD.action, "Upload master data", "Upload master data", Icons.Filled.Refresh, MenuItem.MenuGroup.ACTIONS),
    MenuItem(MenuItem.Actions.SYNC_INV.action, "Sync investigations", "Sync investigations", Icons.Filled.Refresh, MenuItem.MenuGroup.ACTIONS),

    MenuItem("no_filter", "No filter", "No filter", Icons.Filled.FilterAltOff, MenuItem.MenuGroup.FILTER),
    MenuItem("ppap", "PPAP", "PPAP", Icons.Filled.Filter1, MenuItem.MenuGroup.FILTER),
    MenuItem("incoming_inspection", "Incoming inspection", "Incoming inspection", Icons.Filled.Filter2, MenuItem.MenuGroup.FILTER),
    MenuItem("process_control", "Process control", "Process control", Icons.Filled.Filter3, MenuItem.MenuGroup.FILTER),
    MenuItem("product_audit", "Product audit", "Product audit", Icons.Filled.Filter4, MenuItem.MenuGroup.FILTER),
    MenuItem(MenuItem.Actions.CUSTOM_FILTER.action, "Custom filter", "Custom filter", Icons.Filled.FilterAlt, MenuItem.MenuGroup.FILTER),
)

enum class ProgressTabs(val tabId: SelectedNumber) {
    ALL(FirstTabId),
    TO_DO(SecondTabId),
    IN_PROGRESS(ThirdTabId),
    DONE(FourthTabId);

    companion object {
        fun toListOfTriples() = ProgressTabs.values().map { Triple(it.name, it.ordinal, it.tabId) }
    }
}

enum class UsersTabs(val tabId: SelectedNumber) {
    EMPLOYEES(FirstTabId),
    USERS(SecondTabId),
    REQUESTS(ThirdTabId);

    companion object {
        fun toListOfTriples() = UsersTabs.values().map { Triple(it.name, it.ordinal, it.tabId) }
    }
}

enum class AddEditMode(val mode: String) {
    ADD_EMPLOYEE("Add new employee"),
    EDIT_EMPLOYEE("Edit employee"),
    NO_MODE("No mode"),
    ADD_ORDER("New investigation order"),
    EDIT_ORDER("Edit investigation order"),
    ADD_SUB_ORDER("Add new sub order"),
    EDIT_SUB_ORDER("Edit sub order"),
    ADD_SUB_ORDER_STAND_ALONE("New process control order"),
    EDIT_SUB_ORDER_STAND_ALONE("Edit process control order"),
    ACCOUNT_EDIT("Edit account data")
}