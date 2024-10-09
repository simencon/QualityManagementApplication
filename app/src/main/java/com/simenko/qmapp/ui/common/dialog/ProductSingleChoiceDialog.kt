package com.simenko.qmapp.ui.common.dialog

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.simenko.qmapp.domain.DomainBaseModel
import com.simenko.qmapp.domain.EmptyString
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.domain.entities.products.DomainKey
import com.simenko.qmapp.other.Constants
import com.simenko.qmapp.ui.common.RecordFieldItem
import com.simenko.qmapp.ui.main.team.forms.user.subforms.role.Section

@Composable
fun <DB, DM> ProductSingleChoiceDialog(
    items: List<DM>,

    productLines: List<Triple<ID, String, Boolean>>,
    onSelectProductLine: (ID) -> Unit,

    designations: List<Triple<ID, String, Boolean>>,
    onSelectDesignation: (ID) -> Unit,

    searchString: String,
    onSearch: (String) -> Unit,

    addIsEnabled: Boolean,
    onDismiss: () -> Unit,
    onItemSelect: (ID) -> Unit,
    onAddClick: () -> Unit
) where DM : DomainBaseModel<DB> {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Card(
            modifier = Modifier.padding(10.dp, 10.dp, 10.dp, 10.dp),
            shape = RoundedCornerShape(10.dp),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            val (searchFR) = FocusRequester.createRefs()
            val keyboardController = LocalSoftwareKeyboardController.current
            val listState = rememberLazyListState()

            Column(
                Modifier
                    .wrapContentWidth()
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.onPrimary),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f, false)) {
                    Section(title = "Select product line", isError = false, modifier = Modifier.width(300.dp)) {
                        SelectionGrid(modifier = Modifier.padding(top = 0.dp), productLines) { onSelectProductLine(it) }
                    }
                    Section(title = "Select designation", isError = false, modifier = Modifier.width(300.dp)) {
                        SelectionGrid(modifier = Modifier.padding(top = 0.dp), designations) { onSelectDesignation(it) }
                    }
                    Text(
                        text = "Select item".uppercase(),
                        style = MaterialTheme.typography.displayMedium.copy(fontSize = 18.sp),
                        color = Color.Unspecified,
                        modifier = Modifier
                            .paddingFromBaseline(top = 40.dp, bottom = 8.dp)
                            .padding(horizontal = 16.dp)
                    )

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

                    LazyColumn(state = listState) {
                        items(items = items, key = { it.getRecordId() }) { item ->
                            ItemToSelect(item.getRecordId() as ID, Triple(item.getIdentityName(), item.getName(), item.getIsSelected())) { selectedName ->
                                onItemSelect.invoke(selectedName)
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
    }
}

@Preview(
    name = "Light Mode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Composable
fun PreviewSingleChoiceDialogWithExtraFiltration() {
    val list = mutableListOf<DomainKey>()
    for (i in 0..100) {
        if (i % 2 == 0)
            list.add(DomainKey(i.toLong(), 1L, EmptyString.str, "Inner ring"))
        else
            list.add(DomainKey(i.toLong(), 1L, "OR", "Outer ring"))
    }
    ProductSingleChoiceDialog(
        items = list.toList(),
        productLines = listOf(Triple(1L, "Geometry", false), Triple(2L, "Material", true)),
        onSelectProductLine = {},
        designations = listOf(Triple(1L, "Geometry", false), Triple(2L, "Material", true)),
        onSelectDesignation = {},
        searchString = "Mechanical",
        onSearch = {},
        addIsEnabled = true,
        onDismiss = {},
        onItemSelect = {},
        onAddClick = {}
    )
}