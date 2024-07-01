package com.simenko.qmapp.ui.common.dialog

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.simenko.qmapp.domain.DomainBaseModel
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.domain.entities.products.DomainKey
import com.simenko.qmapp.other.Constants
import com.simenko.qmapp.ui.main.team.forms.user.subforms.role.Section
import com.simenko.qmapp.ui.main.team.forms.user.subforms.role.SelectionGrid

@Composable
fun <DB, DM> CharacteristicChoiceDialog(
    items: List<DM>,
    charGroups: List<Pair<String, Boolean>>,
    onSelectCharGroup: (String) -> Unit,
    charSubGroups: List<Pair<String, Boolean>>,
    onSelectCharSubGroup: (String) -> Unit,
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
                            CharacteristicToSelect(item.getRecordId() as ID, Triple(item.getIdentityName(), item.getName(), item.getIsSelected())) { selectedName ->
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
fun CharacteristicToSelect(
    itemId: ID,
    itemContent: Triple<String, String, Boolean>,
    onClick: (ID) -> Unit
) {
    val btnColors = ButtonDefaults.buttonColors(
        contentColor = if (itemContent.third) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
        containerColor = if (itemContent.third) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
    )
    Row(
        modifier = Modifier.padding(horizontal = Constants.DEFAULT_SPACE.dp, vertical = (Constants.DEFAULT_SPACE / 2).dp),
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
                    text = itemContent.first,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = itemContent.second,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
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
fun PreviewCharacteristicChoice() {
    val list = mutableListOf<DomainKey>()
    for (i in 0..100) {
        if (i % 2 == 0)
            list.add(DomainKey(i.toLong(), 1L, "IR", "Inner ring"))
        else
            list.add(DomainKey(i.toLong(), 1L, "OR", "Outer ring"))
    }
    CharacteristicChoiceDialog(
        items = list.toList(),
        charGroups = listOf(Pair("Geometry", false), Pair("Material", true)),
        onSelectCharGroup = {},
        charSubGroups = listOf(Pair("Mechanical", false), Pair("Chemical", true)),
        onSelectCharSubGroup = {},
        addIsEnabled = true,
        onDismiss = {},
        onItemSelect = {},
        onAddClick = {}
    )
}