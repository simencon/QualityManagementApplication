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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Checklist
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
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TextFieldDefaults.colors
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simenko.qmapp.R
import com.simenko.qmapp.ui.theme.QMAppTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(
    screen: MenuItem,

    onNavigationMenuClick: () -> Unit,
    contentColor: Color = MaterialTheme.colorScheme.onPrimary,
    drawerState: DrawerState,

    onActionsMenuClick: () -> Unit,

    isTopMenuVisible: Boolean,
    onDismissTopMenu: () -> Unit,
    onTopMenuItemClick: (MenuItem.MenuGroup) -> Unit,

    isContextMenuVisible: Boolean,
    actionsGroup: MenuItem.MenuGroup,
    onDismissContextMenu: () -> Unit,
    selectedItemId: MutableState<String>,
    onClickBack: () -> Unit,
    onContextMenuItemClick: (String) -> Unit,

    onSearchMenuClick: () -> Unit,
    isSearchModeActivated: Boolean,
    hideSearchBar: () -> Unit
) {
    val orderToSearch = rememberSaveable { mutableStateOf("") }

    TopAppBar(
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isSearchModeActivated) {
                    Box(modifier = Modifier.padding(top = 8.dp, start = 18.dp)) {
                        BasicTextField(
                            value = orderToSearch.value,
                            textStyle = TextStyle(fontSize = 20.sp, color = contentColor, fontWeight = FontWeight.Medium),
                            cursorBrush = SolidColor(contentColor),
                            onValueChange = { orderToSearch.value = it },
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 1,
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Search),
                            keyboardActions = KeyboardActions(onSearch = { /*ToDo - search the proper order*/ }),
                        ) { innerTextField ->
                            TextFieldDefaults.DecorationBox(
                                value = orderToSearch.value,
                                colors = colors(unfocusedContainerColor = MaterialTheme.colorScheme.primary, unfocusedIndicatorColor = contentColor),
                                placeholder = {
                                    Text(text = "Search by order number", color = MaterialTheme.colorScheme.primaryContainer)
                                },
                                innerTextField = innerTextField,
                                enabled = true,
                                singleLine = true,
                                visualTransformation = VisualTransformation.None,
                                interactionSource = remember { MutableInteractionSource() },
                                contentPadding = PaddingValues(2.dp), // this is how you can remove the padding
                            )
                        }
                    }
                } else {
                    Text(text = screen.title, modifier = Modifier.padding(all = 8.dp))

                    if (screen.id == "all_investigations" || screen.id == "process_control")
                        IconButton(onClick = onSearchMenuClick) {
                            Icon(
                                imageVector = Icons.Filled.Search,
                                contentDescription = "Search order by number",
                                tint = contentColor
                            )
                        }
                }
            }
        },
        navigationIcon = {
            if (isSearchModeActivated)
                IconButton(onClick = hideSearchBar) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Hide search bar",
                        tint = contentColor,
                        modifier = Modifier
                            .rotate(drawerState.offset.value / 1080f * 360f)
                    )
                }
            else
                IconButton(onClick = onNavigationMenuClick) {
                    Icon(
                        imageVector = Icons.Filled.Menu,
                        contentDescription = "Toggle drawer",
                        tint = contentColor,
                        modifier = Modifier
                            .rotate(drawerState.offset.value / 1080f * 360f)
                    )
                }
        },
        actions = {
            IconButton(onClick = onActionsMenuClick) {
                Icon(imageVector = Icons.Filled.MoreVert, contentDescription = "More", tint = contentColor)
            }
            ActionsMenu(
                isTopMenuVisible = isTopMenuVisible,
                onDismissTopMenu = onDismissTopMenu,
                onTopMenuItemClick = onTopMenuItemClick,
                isContextMenuVisible = isContextMenuVisible,
                actionsGroup = actionsGroup,
                onDismissContextMenu = onDismissContextMenu,
                selectedItemId = selectedItemId,
                onClickBack = onClickBack,
                onContextMenuItemClick = onContextMenuItemClick
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = contentColor
        ),
        modifier = Modifier
            .height(48.dp)
    )
}

@Composable
fun DrawerHeader() {
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
                painter = painterResource(id = R.drawable.ic_launcher_round),
                contentDescription = null,
                contentScale = ContentScale.FillHeight,
                modifier = Modifier.height(56.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Роман Семенишин",
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
                text = "PrJSC \"SKF Ukraine\", Quality management",
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
    scope: CoroutineScope,
    selectedItemId: MutableState<String>,
    drawerState: DrawerState
) {
    Spacer(modifier = Modifier.height(10.dp))
    ItemsGroup(title = MenuItem.MenuGroup.COMPANY.group, withDivider = false)
    navigationAndActionItems.forEach { item ->
        if (item.category == MenuItem.MenuGroup.COMPANY)
            NavigationDrawerItem(
                icon = { Icon(item.image, contentDescription = item.contentDescription) },
                label = { Text(item.title) },
                selected = item.id == selectedItemId.value,
                onClick = {
                    scope.launch { drawerState.close() }
                    selectedItemId.value = item.id
                },
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
                selected = item.id == selectedItemId.value,
                onClick = {
                    scope.launch { drawerState.close() }
                    selectedItemId.value = item.id
                },
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
                selected = item.id == selectedItemId.value,
                onClick = {
                    scope.launch { drawerState.close() }
                    selectedItemId.value = item.id
                },
                modifier = Modifier
                    .padding(NavigationDrawerItemDefaults.ItemPadding)
                    .height(48.dp)
            )
        else return@forEach
    }
}

@Composable
fun ActionsMenu(
    isTopMenuVisible: Boolean,
    onDismissTopMenu: () -> Unit,
    onTopMenuItemClick: (MenuItem.MenuGroup) -> Unit,
    isContextMenuVisible: Boolean,
    actionsGroup: MenuItem.MenuGroup,
    onDismissContextMenu: () -> Unit,
    selectedItemId: MutableState<String>,
    onClickBack: () -> Unit,
    onContextMenuItemClick: (String) -> Unit
) {
    DropdownMenu(
        expanded = isTopMenuVisible,
        onDismissRequest = onDismissTopMenu
    )
    {
        ActionsMenuTop(onTopMenuItemClick = onTopMenuItemClick)
    }

    DropdownMenu(
        expanded = isContextMenuVisible,
        onDismissRequest = onDismissContextMenu
    )
    {
        ActionsMenuContext(
            actionsGroup = actionsGroup,
            selectedItemId = selectedItemId,
            onClickBack = onClickBack,
            onContextMenuItemClick = onContextMenuItemClick
        )
    }
}

@Composable
fun ActionsMenuTop(
    onTopMenuItemClick: (MenuItem.MenuGroup) -> Unit
) {
    navigationAndActionItems
        .groupBy { it.category }
        .keys
        .toSet()
        .filter { it == MenuItem.MenuGroup.ACTIONS || it == MenuItem.MenuGroup.FILTER }
        .forEach { item ->
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
    onContextMenuItemClick: (String) -> Unit
) {
    DropdownMenuItem(
        text = { Text(actionsGroup.group) },
        onClick = onClickBack,
        trailingIcon = { Icon(Icons.Filled.ArrowForward, contentDescription = actionsGroup.group) },
        modifier = Modifier
            .padding(NavigationDrawerItemDefaults.ItemPadding)
    )

    navigationAndActionItems.filter { it.category == actionsGroup }.forEach { item ->
        val enabled = if (item.category != MenuItem.MenuGroup.ACTIONS && item.id != "custom_filter") {
            selectedItemId.value != item.id
        } else {
            true
        }
        DropdownMenuItem(
            text = { Text(item.title) },
            onClick = { onContextMenuItemClick(item.id) },
            enabled = enabled,
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

data class MenuItem(
    val id: String,
    val title: String,
    val contentDescription: String,
    val image: ImageVector,
    val category: MenuGroup
) {
    companion object {
        fun getStartingDrawerMenuItem() = navigationAndActionItems[0]
        fun getStartingActionsFilterMenuItem() = navigationAndActionItems[10]

        fun getItemById(id: String) = navigationAndActionItems.findLast { it.id == id }
    }

    enum class MenuGroup(val group: String) {
        COMPANY("Company"),
        QUALITY("Quality management"),
        GENERAL("General"),
        ACTIONS("Actions"),
        FILTER("Filter")
    }
}

private val navigationAndActionItems = listOf(
    MenuItem("company_profile", "Company profile", "Company profile", Icons.Filled.Factory, MenuItem.MenuGroup.COMPANY),
    MenuItem("employees", "Employees", "Employees", Icons.Filled.Person, MenuItem.MenuGroup.COMPANY),
    MenuItem("company_structure", "Company structure", "Company structure", Icons.Filled.AccountTree, MenuItem.MenuGroup.COMPANY),
    MenuItem("company_products", "Company products", "Company products", Icons.Filled.ShoppingBag, MenuItem.MenuGroup.COMPANY),

    MenuItem("all_investigations", "All investigations", "All investigations", Icons.Filled.SquareFoot, MenuItem.MenuGroup.QUALITY),
    MenuItem("process_control", "Process control", "Process control", Icons.Filled.Checklist, MenuItem.MenuGroup.QUALITY),
    MenuItem("scrap_level", "Scrap level", "Scrap level", Icons.Filled.AttachMoney, MenuItem.MenuGroup.QUALITY),

    MenuItem("settings", "Settings", "Settings", Icons.Filled.Settings, MenuItem.MenuGroup.GENERAL),

    MenuItem("upload_master_data", "Upload master data", "Upload master data", Icons.Filled.Refresh, MenuItem.MenuGroup.ACTIONS),
    MenuItem("sync_investigations", "Sync investigations", "Sync investigations", Icons.Filled.Refresh, MenuItem.MenuGroup.ACTIONS),

    MenuItem("no_filter", "No filter", "No filter", Icons.Filled.FilterAltOff, MenuItem.MenuGroup.FILTER),
    MenuItem("ppap", "PPAP", "PPAP", Icons.Filled.Filter1, MenuItem.MenuGroup.FILTER),
    MenuItem("incoming_inspection", "Incoming inspection", "Incoming inspection", Icons.Filled.Filter2, MenuItem.MenuGroup.FILTER),
    MenuItem("process_control", "Process control", "Process control", Icons.Filled.Filter3, MenuItem.MenuGroup.FILTER),
    MenuItem("product_audit", "Product audit", "Product audit", Icons.Filled.Filter4, MenuItem.MenuGroup.FILTER),
    MenuItem("custom_filter", "Custom filter", "Custom filter", Icons.Filled.FilterAlt, MenuItem.MenuGroup.FILTER),
)

@Preview(name = "Light DrawerHeader", showBackground = true, widthDp = 409)
@Composable
fun DrawerHeaderPreview() {
    QMAppTheme {
        DrawerHeader()
    }
}