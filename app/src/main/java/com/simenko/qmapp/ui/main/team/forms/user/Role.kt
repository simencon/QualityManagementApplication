package com.simenko.qmapp.ui.main.team.forms.user

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.simenko.qmapp.domain.NoRecordStr
import com.simenko.qmapp.repository.UserError
import com.simenko.qmapp.ui.dialogs.scrollToSelectedStringItem
import com.simenko.qmapp.ui.user.registration.enterdetails.FillInError
import com.simenko.qmapp.ui.user.registration.enterdetails.FillInInitialState
import com.simenko.qmapp.ui.user.registration.enterdetails.FillInSuccess
import java.util.Locale

@Composable
fun AddRole(
    viewModel: UserViewModel,
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    onAddClick: () -> Unit
) {
    val functions by viewModel.roleFunctions.collectAsStateWithLifecycle()
    val levels by viewModel.roleLevels.collectAsStateWithLifecycle()
    val accesses by viewModel.roleAccesses.collectAsStateWithLifecycle()

    val userRoleToAddErrors by viewModel.userRoleToAddErrors.collectAsStateWithLifecycle()
    var error by rememberSaveable { mutableStateOf(UserError.NO_ERROR.error) }

    val fillInState by viewModel.roleFillInState.collectAsStateWithLifecycle()
    fillInState.let { state ->
        when (state) {
            is FillInSuccess -> onAddClick()
            is FillInError -> error = state.errorMsg
            is FillInInitialState -> error = UserError.NO_ERROR.error
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.padding(10.dp, 10.dp, 10.dp, 10.dp),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier.background(MaterialTheme.colorScheme.onPrimary),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Section(title = "Select role function", isError = userRoleToAddErrors.first) {
                    SelectionGrid(modifier = Modifier.padding(top = 0.dp), functions) { viewModel.setRoleFunction(it) }
                }
                Section(title = "Select role level", isError = userRoleToAddErrors.second) {
                    SelectionGrid(modifier = Modifier.padding(top = 0.dp), levels) { viewModel.setRoleLevel(it) }
                }
                Section(title = "Select access level", isError = userRoleToAddErrors.third, withDivider = false) {
                    SelectionGrid(modifier = Modifier.padding(top = 0.dp), accesses) { viewModel.setRoleAccess(it) }
                }
                if (error != UserError.NO_ERROR.error)
                    Text(
                        text = error,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 14.sp, color = MaterialTheme.colorScheme.error),
                        modifier = Modifier.padding(all = 5.dp),
                        textAlign = TextAlign.Center
                    )
                Spacer(modifier = Modifier.height(10.dp))

                //.......................................................................
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
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
                    Divider(
                        modifier = modifier
                            .width(1.dp)
                            .height(48.dp), color = MaterialTheme.colorScheme.onPrimary
                    )
                    TextButton(
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.validateUserRoleInput() },
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
fun Section(
    title: String,
    isError: Boolean,
    modifier: Modifier = Modifier,
    withDivider: Boolean = true,
    content: @Composable () -> Unit
) {
    Column(modifier) {
        Text(
            text = title.uppercase(Locale.getDefault()),
            style = MaterialTheme.typography.displayMedium.copy(fontSize = 18.sp),
            color = if(isError) MaterialTheme.colorScheme.error else Color.Unspecified,
            modifier = Modifier
                .paddingFromBaseline(top = 40.dp, bottom = 8.dp)
                .padding(horizontal = 16.dp)
        )
        content()
        Spacer(Modifier.height(16.dp))
        Divider(modifier = modifier.height(2.dp), color = if (withDivider) MaterialTheme.colorScheme.secondary else Color.Transparent)
    }
}

@Composable
fun SelectionGrid(
    modifier: Modifier = Modifier,
    items: List<Pair<String, Boolean>>,
    onSelect: (String) -> Unit
) {
    val gritState = rememberLazyGridState()
    var currentItem by rememberSaveable { mutableStateOf(NoRecordStr.str) }

    LaunchedEffect(items) {
        items.find { it.second }?.let {
            currentItem = it.first
        }
    }

    LaunchedEffect(currentItem) {
        if (currentItem != NoRecordStr.str)
            gritState.scrollToSelectedStringItem(
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
    item: Pair<String, Boolean>,
    onClick: (String) -> Unit
) {
    val btnColors = ButtonDefaults.buttonColors(
        contentColor = if (item.second) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
        containerColor = if (item.second) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
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
        ) { Text(text = item.first) }
    }
}