package com.simenko.qmapp.presentation.ui.main.products.kinds.list.forms.version.copy_version

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.simenko.qmapp.domain.ComponentPref
import com.simenko.qmapp.domain.ComponentStagePref
import com.simenko.qmapp.domain.EmptyString
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.domain.NoRecordStr
import com.simenko.qmapp.domain.NoString
import com.simenko.qmapp.domain.ProductPref
import com.simenko.qmapp.domain.entities.products.DomainItemVersionComplete
import com.simenko.qmapp.domain.entities.products.DomainKey
import com.simenko.qmapp.other.Constants
import com.simenko.qmapp.presentation.ui.common.RecordFieldItem
import com.simenko.qmapp.presentation.ui.common.RecordFieldItemWithMenu
import com.simenko.qmapp.presentation.ui.common.dialog.SelectionGrid
import com.simenko.qmapp.presentation.ui.main.team.forms.user.subforms.role.Section
import com.simenko.qmapp.navigation.Route

@Composable
fun VersionSingleChoiceDialog(viewModel: ItemVersionsViewModel, route: Route.Main.ProductLines.ProductKinds.Products.CopyItemVersion) {
    val designations by viewModel.availableDesignations.collectAsStateWithLifecycle(initialValue = emptyList())
    val items by viewModel.availableItems.collectAsStateWithLifecycle(initialValue = emptyList())

    val searchValue by viewModel.searchValue.collectAsStateWithLifecycle()
    val versions by viewModel.availableVersions.collectAsStateWithLifecycle(initialValue = emptyList())
    val isReadyToCopy by viewModel.isReadyToCopy.collectAsStateWithLifecycle(initialValue = false)

    LaunchedEffect(key1 = Unit) {
        viewModel.onEntered(route)
    }

    VersionSingleChoiceDialog(
        idPref = route.itemFId.firstOrNull() ?: ProductPref.char,
        versions = versions,

        designations = designations,
        onSelectDesignation = viewModel::onSelectDesignation,

        items = items,
        onVersionItem = viewModel::onVersionItem,

        searchString = searchValue,
        onSearch = viewModel::onChangeSearchValue,

        addIsEnabled = isReadyToCopy,
        onDismiss = viewModel::navBack,

        onItemVersion = viewModel::onSelectVersion,
        onCopyClick = viewModel::onCopy
    )
}

@Composable
fun VersionSingleChoiceDialog(
    idPref: Char,
    versions: List<DomainItemVersionComplete>,

    designations: List<Triple<ID, String, Boolean>>,
    onSelectDesignation: (ID) -> Unit,

    items: List<Triple<String, String, Boolean>>,
    onVersionItem: (String) -> Unit,

    searchString: String,
    onSearch: (String) -> Unit,

    addIsEnabled: Boolean,
    onDismiss: () -> Unit,
    onItemVersion: (String) -> Unit,
    onCopyClick: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        val parent = when (idPref) {
            ProductPref.char -> "Product"
            ComponentPref.char -> "Component"
            ComponentStagePref.char -> "Component stage"
            else -> EmptyString.str
        }
        Box(
            modifier = Modifier
                .wrapContentSize()
                .clickable(
                    indication = null, // disable ripple effect
                    interactionSource = remember { MutableInteractionSource() },
                    onClick = { }
                ),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier.padding(10.dp, 10.dp, 10.dp, 10.dp),
                shape = RoundedCornerShape(10.dp),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                val (parentFR) = FocusRequester.createRefs()
                val (searchFR) = FocusRequester.createRefs()
                val keyboardController = LocalSoftwareKeyboardController.current
                val listState = rememberLazyListState()

                Column(
                    Modifier
                        .wrapContentSize()
                        .background(MaterialTheme.colorScheme.onPrimary),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f, false)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Section(title = "Select designation", isError = false, modifier = Modifier.width(300.dp)) {
                            SelectionGrid(modifier = Modifier.padding(top = 0.dp), designations) { onSelectDesignation(it) }
                        }
                        RecordFieldItemWithMenu(
                            modifier = Modifier.width(300.dp),
                            options = items,
                            isError = false,
                            isMandatoryField = false,
                            containerColor = MaterialTheme.colorScheme.onPrimary,
                            onDropdownMenuItemClick = { onVersionItem(it ?: NoRecordStr.str) },
                            keyboardNavigation = Pair(parentFR) { parentFR.requestFocus() },
                            keyBoardTypeAction = Pair(KeyboardType.Text, ImeAction.Search),
                            contentDescription = Triple(Icons.Outlined.Info, parent, "Select ${parent.lowercase()}"),
                        )
                        Spacer(modifier = Modifier.height((Constants.DEFAULT_SPACE / 2).dp))
                        RecordFieldItem(
                            modifier = Modifier.width(300.dp),
                            valueParam = Triple(searchString, false) { onSearch(it) },
                            keyboardNavigation = Pair(searchFR) { keyboardController?.hide() },
                            keyBoardTypeAction = Pair(KeyboardType.Text, ImeAction.Done),
                            contentDescription = Triple(Icons.Outlined.Search, "Search", "Search"),
                            isMandatoryField = false,
                            containerColor = MaterialTheme.colorScheme.onPrimary
                        )

                        Spacer(modifier = Modifier.height((Constants.DEFAULT_SPACE / 2).dp))

                        LazyColumn(state = listState, modifier = Modifier.height(300.dp)) {
                            items(items = versions, key = { it.getRecordId() }) { item ->
                                VersionToSelect(item.getRecordId(), Triple(item.getIdentityName(), item.getName(), item.getIsSelected())) { selectedName ->
                                    onItemVersion.invoke(selectedName)
                                }
                            }
                        }
                    }


                    Row(
                        Modifier
                            .height(IntrinsicSize.Min)
                            .width(300.dp)
                            .background(MaterialTheme.colorScheme.primary),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        TextButton(
                            modifier = Modifier.weight(1f),
                            onClick = onDismiss,
                            colors = ButtonDefaults.textButtonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ),
                        ) {
                            Text(
                                "Cancel",
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.padding(top = 5.dp, bottom = 5.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                        VerticalDivider(
                            modifier = Modifier
                                .fillMaxHeight()
                                .width(1.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        TextButton(
                            enabled = addIsEnabled,
                            modifier = Modifier.weight(1f),
                            onClick = onCopyClick,
                            colors = ButtonDefaults.textButtonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ),
                        ) {
                            Text(
                                "Copy",
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.padding(top = 5.dp, bottom = 5.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun VersionToSelect(
    itemId: String,
    itemContent: Triple<String, String, Boolean>,
    onClick: (String) -> Unit
) {
    val btnColors = ButtonDefaults.buttonColors(
        contentColor = if (itemContent.third) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
        containerColor = if (itemContent.third) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
    )
    Row(
        modifier = Modifier.padding(horizontal = Constants.DEFAULT_SPACE.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextButton(
            colors = btnColors,
            elevation = ButtonDefaults.buttonElevation(4.dp, 4.dp, 4.dp, 4.dp, 4.dp),
            modifier = Modifier
                .width((300 - Constants.DEFAULT_SPACE * 2).dp)
                .wrapContentHeight(),
            onClick = { onClick(itemId) }
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = itemContent.first.ifEmpty { NoString.str } + itemContent.second,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Preview(
    name = "Light Mode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Composable
fun PreviewVersionSingleChoiceDialog() {
    val list = mutableListOf<DomainKey>()
    for (i in 0..100) {
        if (i % 2 == 0)
            list.add(DomainKey(i.toLong(), 1L, EmptyString.str, "Inner ring"))
        else
            list.add(DomainKey(i.toLong(), 1L, "OR", "Outer ring"))
    }
    VersionSingleChoiceDialog(
        idPref = ComponentPref.char,
        versions = emptyList(),

        designations = listOf(Triple(1L, "Geometry", false), Triple(2L, "Material", true)),
        onSelectDesignation = {},

        items = listOf(Triple("1L", "Geometry", false), Triple("2L", "Material", true)),
        onVersionItem = {},

        searchString = "Mechanical",
        onSearch = {},
        addIsEnabled = true,
        onDismiss = {},
        onItemVersion = {},
        onCopyClick = {}
    )
}