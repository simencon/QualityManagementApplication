package com.simenko.qmapp.ui.neworder.steps

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.material.MaterialTheme
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.simenko.qmapp.R
import com.simenko.qmapp.domain.DomainInputForOrder
import com.simenko.qmapp.ui.neworder.NewItemViewModel
import com.simenko.qmapp.ui.theme.Primary900
import com.simenko.qmapp.ui.theme.StatusBar400
import com.simenko.qmapp.utils.StringUtils

@Composable
fun SearchBarProducts(
    modifier: Modifier = Modifier
) {
    androidx.compose.material.TextField(
        value = "",
        onValueChange = {},
        leadingIcon = {
            androidx.compose.material.Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null
            )
        },
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = MaterialTheme.colors.surface
        ),
        placeholder = {
            androidx.compose.material.Text(stringResource(R.string.placeholder_search))
        },
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
    )
}

@Composable
fun ProductsSelection(
    modifier: Modifier = Modifier,
    appModel: NewItemViewModel,
    parentId: Int
) {
    val observeInputForOrder by appModel.inputForOrderMediator.observeAsState()
    val litOfInput = arrayListOf<DomainInputForOrder>()

    observeInputForOrder?.apply {
        if (observeInputForOrder!!.first != null) {
            litOfInput.clear()

            observeInputForOrder!!.first!!.filter { it.depId > parentId }.forEach { input ->
                if (litOfInput.find { it.id == input.id } == null) {
                    litOfInput.add(input)
                }
            }
        }
    }

    LazyHorizontalGrid(
//        ToDo for products list 2 rows and 120dp height
        rows = GridCells.Fixed(2),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier.height(120.dp)
    ) {
        items(litOfInput.size) { item ->
            ProductCard(litOfInput[item], modifier)
        }
    }
}

@Composable
fun ProductCard(
    inputForOrder: DomainInputForOrder,
    modifier: Modifier = Modifier
) {

    var checked by rememberSaveable { mutableStateOf(false) }

    val btnBackgroundColor = if (checked) Primary900 else StatusBar400
    val btnContentColor = if (checked) Color.White else Color.Black

    val btnColors = ButtonDefaults.buttonColors(
        contentColor = btnContentColor,
        containerColor = btnBackgroundColor
    )

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextButton(
            colors = btnColors,
            modifier = Modifier
                .width(224.dp)
                .height(56.dp),
            onClick = {
                checked = !checked
            }
        ) {
            Text(
                text = StringUtils.concatTwoStrings3(
                    inputForOrder.itemKey,
                    inputForOrder.itemDesignation
                )
            )
        }
    }
}