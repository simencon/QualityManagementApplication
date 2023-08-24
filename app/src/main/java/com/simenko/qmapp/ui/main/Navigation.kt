package com.simenko.qmapp.ui.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Factory
import androidx.compose.material.icons.filled.Filter1
import androidx.compose.material.icons.filled.Filter2
import androidx.compose.material.icons.filled.Filter3
import androidx.compose.material.icons.filled.Filter4
import androidx.compose.material.icons.filled.FilterAltOff
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
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
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simenko.qmapp.R
import com.simenko.qmapp.ui.theme.QMAppTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(
    onNavigationMenuClick: () -> Unit,
    contentColor: Color = MaterialTheme.colorScheme.onPrimary,
    drawerState: DrawerState,
    isActionsMenuVisible: Boolean,
    onActionsMenuClick: () -> Unit,
    onDismissRequest: () -> Unit,
) {
    TopAppBar(
        title = { Text(text = "Here will be search field", modifier = Modifier.padding(all = 8.dp)) },
        navigationIcon = {
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
                isActionsMenuVisible = isActionsMenuVisible,
                onDismissRequest = { onDismissRequest() },
                onItemMenuClick = {})
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = contentColor
        ),
        modifier = Modifier.height(48.dp)
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
    isActionsMenuVisible: Boolean = false,
    onDismissRequest: () -> Unit,
    onItemMenuClick: (MenuItem.MenuGroup) -> Unit
) {
    DropdownMenu(
        expanded = isActionsMenuVisible,
        onDismissRequest = onDismissRequest,
        offset = DpOffset(x = (-102).dp, y = (-48).dp),
    )
    {
        ActionsTopMenu(onItemMenuClick = onItemMenuClick)
    }
}

@Composable
fun ActionsTopMenu(
    onItemMenuClick: (MenuItem.MenuGroup) -> Unit
) {
    navigationAndActionItems
        .groupBy { it.category }
        .keys
        .toSet()
        .filter { it == MenuItem.MenuGroup.ACTIONS || it == MenuItem.MenuGroup.FILTER }
        .forEach { item ->
            DropdownMenuItem(
                text = { Text(item.group) },
                onClick = { onItemMenuClick(item) },
                leadingIcon = { Icon(Icons.Filled.NavigateNext, contentDescription = item.group) },
                modifier = Modifier
                    .padding(NavigationDrawerItemDefaults.ItemPadding)
//                    .height(48.dp)
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

    MenuItem("clear_all_filters", "Clear all filters", "Clear all filters", Icons.Filled.FilterAltOff, MenuItem.MenuGroup.FILTER),
    MenuItem("ppap", "PPAP", "PPAP", Icons.Filled.Filter1, MenuItem.MenuGroup.FILTER),
    MenuItem("incoming_inspection", "Incoming inspection", "Incoming inspection", Icons.Filled.Filter2, MenuItem.MenuGroup.FILTER),
    MenuItem("process_control", "Process control", "Process control", Icons.Filled.Filter3, MenuItem.MenuGroup.FILTER),
    MenuItem("product_audit", "Product audit", "Product audit", Icons.Filled.Filter4, MenuItem.MenuGroup.FILTER),
)

@Preview(name = "Light DrawerHeader", showBackground = true, widthDp = 409)
@Composable
fun DrawerHeaderPreview() {
    QMAppTheme {
        DrawerHeader()
    }
}