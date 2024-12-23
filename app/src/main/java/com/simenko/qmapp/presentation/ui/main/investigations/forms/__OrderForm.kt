package com.simenko.qmapp.presentation.ui.main.investigations.forms

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.simenko.qmapp.R
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.ui.main.investigations.forms.NewItemViewModel
import com.simenko.qmapp.presentation.ui.main.investigations.forms.steps.CustomersSelection
import com.simenko.qmapp.presentation.ui.main.investigations.forms.steps.InitiatorsSelection
import com.simenko.qmapp.presentation.ui.main.investigations.forms.steps.ReasonsSelection
import com.simenko.qmapp.presentation.ui.main.investigations.forms.steps.TypesSelection
import java.util.Locale

@Composable
fun OrderForm(
    modifier: Modifier = Modifier,
    viewModel: NewItemViewModel = hiltViewModel(),
    isPcOnly: Boolean, orderId: ID, subOrderId: ID
) {
    LaunchedEffect(Unit) { viewModel.onEntered(isPcOnly, orderId, subOrderId) }

    Box {
        Column(
            modifier.verticalScroll(rememberScrollState())
        ) {
            ButtonsSection(title = R.string.select_type) { TypesSelection(modifier = Modifier.padding(top = 0.dp)) }
            ButtonsSection(title = R.string.select_reason) { ReasonsSelection(modifier = Modifier.padding(top = 0.dp), isPcOnly = isPcOnly) }
            ButtonsSection(title = R.string.select_customer) { CustomersSelection(modifier = Modifier.padding(top = 0.dp)) }

            ButtonsSection(title = R.string.select_placer) {
                SearchBarProducts(Modifier.padding(horizontal = 16.dp))
                Spacer(Modifier.height(16.dp))
                InitiatorsSelection(modifier = Modifier.padding(top = 0.dp))
            }

            Spacer(Modifier.height((16 + 56).dp))
        }
    }
}

@Composable
fun ButtonsSection(
    @StringRes title: Int,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Column(modifier) {
        Text(
            text = stringResource(title).uppercase(Locale.getDefault()),
            style = MaterialTheme.typography.displayMedium.copy(fontSize = 18.sp),
            modifier = Modifier
                .paddingFromBaseline(top = 40.dp, bottom = 8.dp)
                .padding(horizontal = 16.dp)
        )
        content()
        Spacer(Modifier.height(16.dp))
        HorizontalDivider(modifier = modifier.height(2.dp), color = MaterialTheme.colorScheme.secondary)
    }
}

@Composable
fun SearchBarProducts(
    modifier: Modifier = Modifier
) {
    TextField(
        value = "",
        onValueChange = {},
        leadingIcon = { Icon(imageVector = Icons.Filled.Search, contentDescription = null) },
        colors = TextFieldDefaults.colors(),
        placeholder = { Text("Search") },
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
    )
}

@Composable
fun ItemToSelect(
    item: Triple<ID, String, Boolean>,
    onClick: (ID) -> Unit,
    modifier: Modifier = Modifier
        .width(224.dp)
        .height(56.dp)
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
            modifier = modifier,
            onClick = { onClick(item.first) }
        ) { Text(text = item.second) }
    }
}