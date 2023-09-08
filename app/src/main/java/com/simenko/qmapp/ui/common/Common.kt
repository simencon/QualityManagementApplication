package com.simenko.qmapp.ui.common

import android.graphics.Rect
import android.view.ViewTreeObserver
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.PopupProperties
import com.simenko.qmapp.R
import com.simenko.qmapp.domain.DomainBaseModel
import com.simenko.qmapp.domain.EmptyString
import com.simenko.qmapp.domain.NoRecord

@Composable
fun RecordFieldItem(
    valueParam: Triple<String, Boolean, (String) -> Unit>,
    keyboardNavigation: Pair<FocusRequester, () -> Unit>,
    keyBoardTypeAction: Pair<KeyboardType, ImeAction>,
    contentDescription: Triple<ImageVector, String, String>,
    isMandatoryField: Boolean = true,
    enabled: Boolean = true,
    trailingIcon: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
) {
    TextField(
        value = valueParam.first,
        onValueChange = valueParam.third,
        leadingIcon = {
            val tint = if (valueParam.second) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.surfaceTint
            Icon(imageVector = contentDescription.first, contentDescription = contentDescription.second, tint = tint)
        },
        label = { Text(text = "${contentDescription.second}${if (isMandatoryField) " *" else ""}") },
        isError = valueParam.second,
        placeholder = { Text(text = "${contentDescription.third}${if (isMandatoryField) " *" else ""}") },
        maxLines = 1,
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyBoardTypeAction.first, imeAction = keyBoardTypeAction.second),
        keyboardActions = KeyboardActions(onNext = { keyboardNavigation.second() }),
        enabled = enabled,
        trailingIcon = trailingIcon,
        visualTransformation = visualTransformation,
        modifier = Modifier
            .focusRequester(keyboardNavigation.first)
            .width(320.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun RecordFieldItemWithMenu(
    options: List<Triple<Int, String, Boolean>>,
    onDropdownMenuItemClick: (Int) -> Unit,
    keyBoardTypeAction: Pair<KeyboardType, ImeAction>,
    keyboardNavigation: Pair<FocusRequester, () -> Unit>,
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedName: String by rememberSaveable { mutableStateOf(EmptyString.str) }

    LaunchedEffect(key1 = options) {
        options.findLast { it.third }?.let { selectedName = it.second }
    }

    val isKeyboardOpen by keyboardAsState()

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            expanded = !expanded
        }
    ) {
        TextField(
            modifier = Modifier
                .menuAnchor()
                .focusRequester(keyboardNavigation.first)
                .width(320.dp),
            readOnly = false,
            value = selectedName,
            onValueChange = { value ->
                selectedName = value
                options.find { it.second == value }.let {
                    if(it == null) onDropdownMenuItemClick(NoRecord.num) else onDropdownMenuItemClick(it.first)
                }
                if (!expanded) expanded = true
            },
            label = { Text("Label") },
            keyboardOptions = KeyboardOptions(keyboardType = keyBoardTypeAction.first, imeAction = keyBoardTypeAction.second),
            keyboardActions = KeyboardActions(onNext = { keyboardNavigation.second() }),
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.textFieldColors()
        )
        ExposedDropdownMenu(
            modifier = Modifier.padding(horizontal = 4.dp),
            expanded = expanded,
            onDismissRequest = {
                println("RecordFieldItemWithMenu - $isKeyboardOpen")
                expanded = false
            }
        ) {
            val filteredOptions = options.filter { it.second.contains(selectedName, ignoreCase = true) }
            val items = if (selectedName == EmptyString.str) options else filteredOptions

            if (items.isNotEmpty()) {
                items.forEach { option ->
                    DropdownMenuItem(
                        modifier = Modifier
                            .background(color = MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(size = 12.dp)),
                        text = { Text(text = option.second) },
                        onClick = {
                            onDropdownMenuItemClick(option.first)
                            expanded = false
                        }
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }
    }
}

enum class Keyboard {
    Opened, Closed
}

@Composable
fun keyboardAsState(): State<Keyboard> {
    val keyboardState = remember { mutableStateOf(Keyboard.Closed) }
    val view = LocalView.current
    DisposableEffect(view) {
        val onGlobalListener = ViewTreeObserver.OnGlobalLayoutListener {
            val rect = Rect()
            view.getWindowVisibleDisplayFrame(rect)
            val screenHeight = view.rootView.height
            val keypadHeight = screenHeight - rect.bottom
            keyboardState.value = if (keypadHeight > screenHeight * 0.15) {
                Keyboard.Opened
            } else {
                Keyboard.Closed
            }
        }
        view.viewTreeObserver.addOnGlobalLayoutListener(onGlobalListener)

        onDispose {
            view.viewTreeObserver.removeOnGlobalLayoutListener(onGlobalListener)
        }
    }

    return keyboardState
}

@Composable
fun RecordActionTextBtn(
    text: String,
    onClick: () -> Unit,
    colors: Pair<ButtonColors, Color>,
    enabled: Boolean = true
) {
    TextButton(
        modifier = Modifier.width(150.dp),
        onClick = onClick,
        content = {
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 14.sp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .padding(all = 0.dp)
            )
        },
        colors = colors.first,
        border = BorderStroke(1.dp, colors.second),
        shape = MaterialTheme.shapes.medium,
        enabled = enabled
    )
}

@Composable
fun <T> TopLevelSingleRecordMainHeader(
    modifier: Modifier,
    value: DomainBaseModel<T>,
    detailsVisibility: Boolean,
    onClick: (String) -> Unit,
    title: String? = null
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = value.getName(),
            style = MaterialTheme.typography.titleMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp)
        )

        IconButton(onClick = { onClick(value.getRecordId().toString()) }) {
            Icon(
                imageVector = if (detailsVisibility) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                contentDescription = if (detailsVisibility) {
                    stringResource(R.string.show_less)
                } else {
                    stringResource(R.string.show_more)
                }
            )
        }
    }
}

@Composable
fun TopLevelSingleRecordHeader(title: String, value: String, titleWight: Float = 0.22f) {
    Row(
        modifier = Modifier.padding(
            top = 0.dp,
            start = 0.dp,
            end = 0.dp,
            bottom = 4.dp
        ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .weight(weight = titleWight)
                .padding(top = 7.dp, start = 0.dp, end = 0.dp, bottom = 0.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .weight(weight = 1 - titleWight)
                .padding(top = 0.dp, start = 3.dp, end = 0.dp, bottom = 0.dp)
        )
    }
}

@Composable
fun TopLevelSingleRecordDetails(title: String, value: String, modifier: Modifier, titleWight: Float = 0.35f) {
    Row(
        modifier = modifier.padding(start = 8.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.Bottom
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .weight(weight = titleWight)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .weight(weight = 1 - titleWight)
                .padding(start = 3.dp)
        )
    }
}

@Composable
fun SecondLevelSingleRecordHeader(title: String, value: String) {
    Row(
        modifier = Modifier.padding(
            top = 0.dp,
            start = 0.dp,
            end = 0.dp,
            bottom = 4.dp
        ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .weight(weight = 0.20f)
                .padding(top = 7.dp, start = 0.dp, end = 0.dp, bottom = 0.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .weight(weight = 0.80f)
                .padding(top = 0.dp, start = 3.dp, end = 0.dp, bottom = 0.dp)
        )
    }
}

@Composable
fun SecondLevelSingleRecordDetails(title: String, value: String) {
    Row(
        modifier = Modifier.padding(top = 0.dp, start = 8.dp, end = 0.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .weight(weight = 0.22f)
                .padding(top = 5.dp, start = 0.dp, end = 0.dp, bottom = 0.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .weight(weight = 0.78f)
                .padding(top = 0.dp, start = 3.dp, end = 0.dp, bottom = 0.dp)
        )
    }
}