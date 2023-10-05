package com.simenko.qmapp.ui.main.main.content

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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Divider
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.simenko.qmapp.domain.EmptyString
import com.simenko.qmapp.storage.Principle
import com.simenko.qmapp.ui.main.main.MenuItem
import com.simenko.qmapp.ui.main.main.setup.TopBarSetup
import com.simenko.qmapp.ui.main.main.setup.TopTabsSetup
import com.simenko.qmapp.utils.BaseFilter
import com.simenko.qmapp.utils.StringUtils
import com.simenko.qmapp.utils.StringUtils.getWithSpaces
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun AppBar(
    topBarSetup: TopBarSetup
) {
    val drawerMenuState by topBarSetup.drawerMenuState.collectAsStateWithLifecycle()
    val searchBarState by topBarSetup.searchBarState.collectAsStateWithLifecycle()
    val actionsMenuState by topBarSetup.actionsMenuState.collectAsStateWithLifecycle()

    val scope = rememberCoroutineScope()
    val contentColor: Color = MaterialTheme.colorScheme.onPrimary

    val stringToSearch = rememberSaveable { mutableStateOf("") }
    val (focusRequesterSearchBar) = FocusRequester.createRefs()

    LaunchedEffect(searchBarState) {
        if (searchBarState) focusRequesterSearchBar.requestFocus()
    }

    TopAppBar(
        modifier = Modifier.height(48.dp),
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (searchBarState) {
                    BasicTextField(
                        modifier = Modifier
                            .width(202.dp)
                            .padding(start = 10.dp)
                            .focusRequester(focusRequesterSearchBar),
                        value = stringToSearch.value,
                        textStyle = TextStyle(fontSize = 20.sp, color = contentColor, fontWeight = FontWeight.Medium),
                        cursorBrush = SolidColor(contentColor),
                        onValueChange = { stringToSearch.value = it },
                        maxLines = 1,
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = topBarSetup.keyboardType ?: KeyboardType.Ascii,
                            imeAction = ImeAction.Search
                        ),
                        keyboardActions = KeyboardActions(onSearch = { topBarSetup.onSearchAction?.let { it(BaseFilter(stringToSearch = stringToSearch.value)) } }),
                    ) { innerTextField ->
                        TextFieldDefaults.DecorationBox(
                            value = stringToSearch.value,
                            colors = colors(unfocusedContainerColor = MaterialTheme.colorScheme.primary, unfocusedIndicatorColor = contentColor),
                            placeholder = {
                                Text(text = topBarSetup.placeholderText ?: EmptyString.str, color = MaterialTheme.colorScheme.primaryContainer)
                            },
                            innerTextField = innerTextField,
                            enabled = true,
                            singleLine = true,
                            visualTransformation = VisualTransformation.None,
                            interactionSource = remember { MutableInteractionSource() },
                            contentPadding = PaddingValues(2.dp)
                        )
                    }

                    IconButton(
                        onClick = {
                            stringToSearch.value = EmptyString.str
                            topBarSetup.onSearchAction?.let { it(BaseFilter(stringToSearch = stringToSearch.value)) }
                        },
                        enabled = stringToSearch.value != EmptyString.str,
                        colors = IconButtonDefaults.iconButtonColors(
                            contentColor = contentColor,
                            disabledContentColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Icon(imageVector = Icons.Filled.Close, contentDescription = topBarSetup.placeholderText)
                    }
                } else {
                    Text(text = topBarSetup.title, modifier = Modifier.padding(all = 8.dp))
                    if (topBarSetup.titleBtnIcon != null)
                        IconButton(onClick = { topBarSetup.setSearchBarState(true) }) {
                            Icon(imageVector = topBarSetup.titleBtnIcon, contentDescription = topBarSetup.placeholderText, tint = contentColor)
                        }
                }
            }
        },
        navigationIcon = {
            if (searchBarState)
                IconButton(
                    onClick = { topBarSetup.setSearchBarState(false) },
                    colors = IconButtonDefaults.iconButtonColors(contentColor = contentColor)
                ) {
                    Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Hide search bar")
                }
            else
                IconButton(
                    onClick = { scope.launch { topBarSetup.setDrawerMenuState(true) } },
                    colors = IconButtonDefaults.iconButtonColors(contentColor = contentColor)
                ) {
                    Icon(
                        imageVector = topBarSetup.navIcon,
                        contentDescription = "Navigation button",
                        modifier = Modifier
                            .rotate(drawerMenuState.offset.value / 1080f * 360f)
                    )
                }
        },
        actions = {
            if (topBarSetup.actionBtnIcon != null) {
                IconButton(
                    onClick = { topBarSetup.setActionMenuState(true) },
                    colors = IconButtonDefaults.iconButtonColors(contentColor = contentColor)
                ) {
                    Icon(imageVector = topBarSetup.actionBtnIcon, contentDescription = "More")
                }
                ActionsMenu(
                    actionsMenuState = actionsMenuState,
                    setActionMenuState = { topBarSetup.setActionMenuState(it) },
                    topBarSetup = topBarSetup
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
    DrawerMenuItems.toList().forEach { item ->
        if (item.group == MenuItem.MenuGroup.COMPANY)
            NavigationDrawerItem(
                icon = { Icon(item.image, contentDescription = item.title) },
                label = { Text(item.title) },
                selected = item.tag == selectedItemId,
                onClick = { onDrawerItemClick(item.tag) },
                modifier = Modifier
                    .padding(NavigationDrawerItemDefaults.ItemPadding)
                    .height(48.dp)
            )
        else return@forEach
    }
    Spacer(modifier = Modifier.height(10.dp))
    ItemsGroup(title = MenuItem.MenuGroup.QUALITY.group)
    DrawerMenuItems.toList().forEach { item ->
        if (item.group == MenuItem.MenuGroup.QUALITY)
            NavigationDrawerItem(
                icon = { Icon(item.image, contentDescription = item.title) },
                label = { Text(item.title) },
                selected = item.tag == selectedItemId,
                onClick = { onDrawerItemClick(item.tag) },
                modifier = Modifier
                    .padding(NavigationDrawerItemDefaults.ItemPadding)
                    .height(48.dp)
            )
        else return@forEach
    }
    Spacer(modifier = Modifier.height(10.dp))
    ItemsGroup(title = MenuItem.MenuGroup.GENERAL.group)
    DrawerMenuItems.toList().forEach { item ->
        if (item.group == MenuItem.MenuGroup.GENERAL)
            NavigationDrawerItem(
                icon = { Icon(item.image, contentDescription = item.title) },
                label = { Text(item.title) },
                selected = item.tag == selectedItemId,
                onClick = { onDrawerItemClick(item.tag) },
                modifier = Modifier
                    .padding(NavigationDrawerItemDefaults.ItemPadding)
                    .height(48.dp)
            )
        else return@forEach
    }
}

@Composable
fun ActionsMenu(
    actionsMenuState: Boolean,
    setActionMenuState: (Boolean) -> Unit,
    topBarSetup: TopBarSetup
) {
    val actionsGroup = rememberSaveable { mutableStateOf(EmptyString.str) }
    val isContextMenuVisible = rememberSaveable { mutableStateOf(false) }

    DropdownMenu(
        expanded = actionsMenuState,
        onDismissRequest = { setActionMenuState(false) }
    )
    {
        ActionsMenuTop(
            onTopMenuItemClick = {
                actionsGroup.value = it
                setActionMenuState(false)
                isContextMenuVisible.value = true
            },
            categories = topBarSetup.actionMenuItems.map { it.group.name }.toSet().toList()
        )
    }

    DropdownMenu(
        expanded = isContextMenuVisible.value,
        onDismissRequest = { isContextMenuVisible.value = false }
    )
    {
        ActionsMenuContext(
            actionsGroup = actionsGroup.value,
            onClickBack = {
                setActionMenuState(true)
                isContextMenuVisible.value = false
            },
            onContextMenuItemClick = {
                topBarSetup.onActionMenuItemClick(it)
                isContextMenuVisible.value = false
            },
            topBarSetup = topBarSetup
        )
    }
}

@Composable
fun ActionsMenuTop(
    onTopMenuItemClick: (String) -> Unit,
    categories: List<String>
) {
    categories.forEach {
        DropdownMenuItem(
            text = { Text(getWithSpaces(it)) },
            onClick = { onTopMenuItemClick(it) },
            leadingIcon = { Icon(Icons.Filled.ArrowBack, contentDescription = getWithSpaces(it)) },
            modifier = Modifier
                .padding(NavigationDrawerItemDefaults.ItemPadding)
        )
    }
}

@Composable
fun ActionsMenuContext(
    actionsGroup: String,
    onClickBack: () -> Unit,
    onContextMenuItemClick: (MenuItem) -> Unit,
    topBarSetup: TopBarSetup
) {
    val selectedItemId by topBarSetup.selectedActionMenuItem.collectAsStateWithLifecycle()

    DropdownMenuItem(
        text = { Text(getWithSpaces(actionsGroup)) },
        onClick = onClickBack,
        trailingIcon = { Icon(Icons.Filled.ArrowForward, contentDescription = getWithSpaces(actionsGroup)) },
        modifier = Modifier
            .padding(NavigationDrawerItemDefaults.ItemPadding)
    )

    topBarSetup.actionMenuItems.filter { it.group.name == actionsGroup }.forEach { item ->
        DropdownMenuItem(
            text = { Text(item.title) },
            onClick = { onContextMenuItemClick(item) },
            enabled = selectedItemId != item,
            leadingIcon = { Icon(item.image, contentDescription = item.title) },
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
fun TopTabs(topTabsSetup: TopTabsSetup) {
    val tabs by topTabsSetup.topTabsContent.collectAsStateWithLifecycle(initialValue = emptyList())
    val selectedTabIndex by topTabsSetup.selectedTab.collectAsStateWithLifecycle()

    TabRow(selectedTabIndex = selectedTabIndex) {
        tabs.forEach {
            val selected = selectedTabIndex == it.index
            Tab(
                modifier = Modifier.height(40.dp),
                selected = selected,
                onClick = { topTabsSetup.onTabSelect(it.index, it.tag) }
            ) {
                if (it.badgeCount > 0)
                    BadgedBox(badge = {
                        Box(modifier = Modifier.background(color = it.badgeBg, shape = RoundedCornerShape(size = 3.dp))) {
                            Text(
                                text = it.badgeCount.toString(),
                                color = it.badgeFr,
                                fontSize = 8.sp,
                                fontWeight = if (selected) FontWeight.Black else FontWeight.SemiBold
                            )
                        }
                    }
                    ) {
                        Text(
                            text = getWithSpaces(it.name),
                            fontSize = 12.sp,
                            style = if (selected) LocalTextStyle.current.copy(fontWeight = FontWeight.Bold) else LocalTextStyle.current
                        )
                    }
                else
                    Text(
                        text = getWithSpaces(it.name),
                        fontSize = 12.sp,
                        style = if (selected) LocalTextStyle.current.copy(fontWeight = FontWeight.Bold) else LocalTextStyle.current
                    )
            }
        }
    }
}

enum class AddEditMode(val mode: String) {
    ADD_EMPLOYEE("Add new employee"),
    EDIT_EMPLOYEE("Edit employee"),
    AUTHORIZE_USER("Authorize user"),
    EDIT_USER("Edit user"),
    NO_MODE("No mode"),
    ADD_ORDER("New investigation order"),
    EDIT_ORDER("Edit investigation order"),
    ADD_SUB_ORDER("Add new sub order"),
    EDIT_SUB_ORDER("Edit sub order"),
    ADD_SUB_ORDER_STAND_ALONE("New process control order"),
    EDIT_SUB_ORDER_STAND_ALONE("Edit process control order"),
    ACCOUNT_EDIT("Edit account data")
}