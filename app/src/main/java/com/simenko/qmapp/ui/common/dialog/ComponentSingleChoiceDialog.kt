package com.simenko.qmapp.ui.common.dialog

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
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
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
import com.simenko.qmapp.domain.DomainBaseModel
import com.simenko.qmapp.domain.EmptyString
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.domain.NoString
import com.simenko.qmapp.domain.entities.products.DomainComponent
import com.simenko.qmapp.domain.entities.products.DomainKey
import com.simenko.qmapp.other.Constants
import com.simenko.qmapp.ui.common.RecordFieldItem
import com.simenko.qmapp.ui.common.RecordFieldItemWithMenu
import com.simenko.qmapp.ui.main.team.forms.user.subforms.role.Section

enum class ItemSelectEnum{
    COMPONENT,
    COMPONENT_STAGE
}

@Composable
fun <DB, DM> ComponentSingleChoiceDialog(
    selectionOf: ItemSelectEnum,
    items: List<DM>,

    designations: List<Triple<ID, String, Boolean>>,
    onSelectDesignation: (ID) -> Unit,

    products: List<Triple<ID, String, Boolean>>,
    onSelectProduct: (ID) -> Unit,

    searchString: String,
    onSearch: (String) -> Unit,

    quantity: Pair<String, Boolean>,
    onEnterQuantity: (String) -> Unit,

    isLoadingState: Boolean,

    addIsEnabled: Boolean,
    onDismiss: () -> Unit,
    onItemSelect: (ID) -> Unit,
    onAddClick: () -> Unit
) where DM : DomainBaseModel<DB> {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        val parent = if (selectionOf == ItemSelectEnum.COMPONENT) "product" else "component"

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
                val (quantityFR) = FocusRequester.createRefs()
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
                            options = products,
                            isError = false,
                            isMandatoryField = false,
                            containerColor = MaterialTheme.colorScheme.onPrimary,
                            onDropdownMenuItemClick = { onSelectProduct(it) },
                            keyboardNavigation = Pair(parentFR) { parentFR.requestFocus() },
                            keyBoardTypeAction = Pair(KeyboardType.Text, ImeAction.Search),
                            contentDescription = Triple(Icons.Outlined.Info, "Belongs to $parent", "Select $parent item"),
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
                            items(items = items, key = { it.getRecordId() }) { item ->
                                ComponentToSelect(item.getRecordId() as ID, Triple(item.getIdentityName(), item.getName(), item.getIsSelected())) { selectedName ->
                                    onItemSelect.invoke(selectedName)
                                }
                            }
                        }
                        if(selectionOf == ItemSelectEnum.COMPONENT) {
                            RecordFieldItem(
                                modifier = Modifier.width(300.dp),
                                valueParam = Triple(quantity.first, quantity.second) { onEnterQuantity(it) },
                                keyboardNavigation = Pair(quantityFR) { keyboardController?.hide() },
                                keyBoardTypeAction = Pair(KeyboardType.Decimal, ImeAction.Done),
                                contentDescription = Triple(Icons.Outlined.ShoppingCart, "Quantity in $parent", "Enter quantity"),
                                isMandatoryField = true,
                                containerColor = MaterialTheme.colorScheme.onPrimary
                            )
                            Spacer(modifier = Modifier.height((Constants.DEFAULT_SPACE).dp))
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
                            onClick = onAddClick,
                            colors = ButtonDefaults.textButtonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ),
                        ) {
                            Text(
                                "Add",
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.padding(top = 5.dp, bottom = 5.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            if (isLoadingState) {
                CircularProgressIndicator(
                    modifier = Modifier.width(64.dp),
                    color = MaterialTheme.colorScheme.secondary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                )
            }
        }
    }
}

@Composable
fun ComponentToSelect(
    itemId: ID,
    itemContent: Triple<String, String, Boolean>,
    onClick: (ID) -> Unit
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
                    text = itemContent.second.ifEmpty { NoString.str },
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
fun PreviewComponentSingleChoiceDialog() {
    val list = mutableListOf<DomainKey>()
    for (i in 0..100) {
        if (i % 2 == 0)
            list.add(DomainKey(i.toLong(), 1L, EmptyString.str, "Inner ring"))
        else
            list.add(DomainKey(i.toLong(), 1L, "OR", "Outer ring"))
    }
    ComponentSingleChoiceDialog(
        selectionOf = ItemSelectEnum.COMPONENT,
        items = list.toList(),
        products = listOf(Triple(1L, "Geometry", false), Triple(2L, "Material", true)),
        onSelectProduct = {},
        designations = listOf(Triple(1L, "Geometry", false), Triple(2L, "Material", true)),
        onSelectDesignation = {},
        searchString = "Mechanical",
        onSearch = {},
        quantity = Pair("10", true),
        onEnterQuantity = {},
        isLoadingState = true,
        addIsEnabled = true,
        onDismiss = {},
        onItemSelect = {},
        onAddClick = {}
    )
}