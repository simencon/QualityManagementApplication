package com.simenko.qmapp.presentation.ui.common.dialog

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.simenko.qmapp.domain.DomainBaseModel
import com.simenko.qmapp.domain.EmptyString
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.domain.entities.products.DomainKey
import com.simenko.qmapp.presentation.ui.dialogs.scrollToSelectedItem
import com.simenko.qmapp.presentation.ui.main.team.forms.user.subforms.role.Section

@Composable
fun <DB, DM> CharacteristicChoiceDialog(
    items: List<DM>,
    charGroups: List<Triple<ID, String, Boolean>>,
    onSelectCharGroup: (ID) -> Unit,
    charSubGroups: List<Triple<ID, String, Boolean>>,
    onSelectCharSubGroup: (ID) -> Unit,
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
                    Section(title = "Select char. group", isError = false, modifier = Modifier.width(300.dp)) {
                        SelectionGrid(modifier = Modifier.padding(top = 0.dp), charGroups) { onSelectCharGroup(it) }
                    }

                    Section(title = "Select char. sub group", isError = false, modifier = Modifier.width(300.dp)) {
                        SelectionGrid(modifier = Modifier.padding(top = 0.dp), charSubGroups) { onSelectCharSubGroup(it) }
                    }

                    Text(
                        text = "Select characteristic".uppercase(),
                        style = MaterialTheme.typography.displayMedium.copy(fontSize = 18.sp),
                        color = Color.Unspecified,
                        modifier = Modifier
                            .paddingFromBaseline(top = 40.dp, bottom = 8.dp)
                            .padding(horizontal = 16.dp)
                    )

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

@Composable
fun SelectionGrid(
    modifier: Modifier = Modifier,
    items: List<Triple<ID, String, Boolean>>,
    onSelect: (ID) -> Unit
) {
    val gritState = rememberLazyGridState()
    var currentItem by rememberSaveable { mutableLongStateOf(NoRecord.num) }

    LaunchedEffect(items) {
        items.find { it.third }?.let {
            currentItem = it.first
        }
    }

    LaunchedEffect(currentItem) {
        if (currentItem != NoRecord.num)
            gritState.scrollToSelectedItem(
                list = items.map { it.first }.toList(),
                selectedId = currentItem,
            )
    }

    LazyHorizontalGrid(
        rows = GridCells.Fixed(1),
        state = gritState,
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier.height(60.dp)
    ) {
        items(items = items, key = { it.first }) { item ->
            ItemToSelect(item, onClick = { onSelect(it) })
        }
    }
}

@Composable
fun ItemToSelect(
    item: Triple<ID, String, Boolean>,
    onClick: (ID) -> Unit
) {
    val btnColors = ButtonDefaults.buttonColors(
        contentColor = if (item.third) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
        containerColor = if (item.third) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
    )
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextButton(
            colors = btnColors,
            elevation = ButtonDefaults.buttonElevation(4.dp, 4.dp, 4.dp, 4.dp, 4.dp),
            modifier = Modifier
                .width(224.dp)
                .height(56.dp),
            onClick = { onClick(item.first) }
        ) { Text(text = item.second) }
    }
}



@Preview(
    name = "Light Mode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Composable
fun PreviewCharacteristicChoice() {
    val list = mutableListOf<DomainKey>()
    for (i in 0..100) {
        if (i % 2 == 0)
            list.add(DomainKey(i.toLong(), 1L, EmptyString.str, "Inner ring"))
        else
            list.add(DomainKey(i.toLong(), 1L, "OR", "Outer ring"))
    }
    CharacteristicChoiceDialog(
        items = list.toList(),
        charGroups = listOf(Triple(1L, "Geometry", false), Triple(2L, "Material", true)),
        onSelectCharGroup = {},
        charSubGroups = listOf(Triple(1L, "Mechanical", false), Triple(2L, "Chemical", true)),
        onSelectCharSubGroup = {},
        addIsEnabled = true,
        onDismiss = {},
        onItemSelect = {},
        onAddClick = {}
    )
}