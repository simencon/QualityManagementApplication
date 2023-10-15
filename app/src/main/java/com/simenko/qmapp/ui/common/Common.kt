package com.simenko.qmapp.ui.common

import android.graphics.Rect
import android.view.ViewTreeObserver
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simenko.qmapp.R
import com.simenko.qmapp.domain.DomainBaseModel
import com.simenko.qmapp.domain.EmptyString
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.domain.NoString
import kotlin.math.round

@Composable
fun RecordFieldItem(
    valueParam: Triple<String, Boolean, (String) -> Unit>,
    keyboardNavigation: Pair<FocusRequester, () -> Unit>,
    keyBoardTypeAction: Pair<KeyboardType, ImeAction>,
    contentDescription: Triple<ImageVector, String, String>,
    isMandatoryField: Boolean = true,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    trailingIcon: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
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
        readOnly = readOnly,
        textStyle = LocalTextStyle.current.copy(fontWeight = FontWeight.Medium),
        trailingIcon = trailingIcon,
        visualTransformation = visualTransformation,
        interactionSource = interactionSource,
        modifier = Modifier
            .focusRequester(keyboardNavigation.first)
            .width(320.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordFieldItemWithMenu(
    options: List<Triple<Int, String, Boolean>>,
    isError: Boolean,
    onDropdownMenuItemClick: (Int) -> Unit,
    keyboardNavigation: Pair<FocusRequester, () -> Unit>,
    keyBoardTypeAction: Pair<KeyboardType, ImeAction>,
    contentDescription: Triple<ImageVector, String, String>,
    isMandatoryField: Boolean = true,
) {
    var expanded by remember { mutableStateOf(false) }

    var selectedOptionText by rememberSaveable { mutableStateOf(EmptyString.str) }
    var searchedOption: String by rememberSaveable { mutableStateOf(EmptyString.str) }

    LaunchedEffect(key1 = options) {
        options.findLast { it.third }.let {
            selectedOptionText = it?.second ?: EmptyString.str
        }
    }

    var filteredOptions = mutableListOf<Triple<Int, String, Boolean>>()

    Box {
        RecordFieldItem(
            valueParam = Triple(selectedOptionText, isError) {},
            keyboardNavigation = keyboardNavigation,
            keyBoardTypeAction = keyBoardTypeAction,
            contentDescription = contentDescription,
            isMandatoryField = isMandatoryField,
            readOnly = true,
            trailingIcon = {
                IconToggleButton(
                    checked = expanded,
                    onCheckedChange = {
                        expanded = it
                        keyboardNavigation.second()
                    }
                ) { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }
            },
            interactionSource = remember { MutableInteractionSource() }
                .also { interactionSource ->
                    LaunchedEffect(interactionSource) {
                        interactionSource.interactions.collect {
                            if (it is PressInteraction.Release) expanded = !expanded
                        }
                    }
                }
        )

        if (expanded) {
            DropdownMenu(
                modifier = Modifier.fillMaxWidth(0.75f),
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        value = searchedOption,
                        onValueChange = { selectedSport ->
                            searchedOption = selectedSport
                            filteredOptions = options.filter { it.second.contains(searchedOption, ignoreCase = true) }.toMutableList()
                        },
                        leadingIcon = { Icon(imageVector = Icons.Outlined.Search, contentDescription = null) },
                        placeholder = { Text(text = "Search") }
                    )

                    val items = if (searchedOption == EmptyString.str) options else filteredOptions

                    DropdownMenuItem(
                        onClick = {},
                        text = { Text(text = "Add item") },
                        colors = MenuDefaults.itemColors(textColor = MaterialTheme.colorScheme.onSurfaceVariant),
                    )

                    items.forEach { selectedItem ->
                        DropdownMenuItem(
                            onClick = {
                                selectedOptionText = selectedItem.second
                                onDropdownMenuItemClick(selectedItem.first)
                                searchedOption = EmptyString.str
                                expanded = false
                            },
                            text = { Text(text = selectedItem.second) },
                            colors = MenuDefaults.itemColors(textColor = MaterialTheme.colorScheme.primary),
                        )
                    }
                    DropdownMenuItem(
                        onClick = {
                            selectedOptionText = EmptyString.str
                            onDropdownMenuItemClick(NoRecord.num)
                            searchedOption = EmptyString.str
                            expanded = false
                        },
                        text = { Text(text = "None") },
                        colors = MenuDefaults.itemColors(textColor = MaterialTheme.colorScheme.onSurfaceVariant),
                    )
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
    colors: Pair<ButtonColors, Color?>,
    enabled: Boolean = true,
    elevation: ButtonElevation? = null
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
        elevation = elevation,
        border = colors.second?.let { BorderStroke(1.dp, it) },
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
        modifier = modifier.padding(start = 8.dp),
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
fun InfoLine(
    modifier: Modifier,
    title: String,
    body: String
) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelMedium.copy(color = MaterialTheme.colorScheme.primary),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier.width(320.dp)
    )
    Text(
        text = body,
        style = MaterialTheme.typography.bodyMedium,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier.width(320.dp)
    )
    Spacer(modifier = Modifier.height(5.dp))
}

@Composable
fun HeaderWithTitle(
    modifier: Modifier = Modifier,
    titleWight: Float,
    title: String,
    text: String? = null,
    content: @Composable (() -> Unit)? = null,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.Start
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(weight = titleWight)
        )

        text?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.titleSmall.copy(fontSize = 14.sp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(weight = 1f - titleWight)
                    .padding(start = 3.dp)
            )
        }

        content?.let {
            Row(
                modifier = Modifier
                    .padding(start = 3.dp)
                    .weight(weight = 1 - titleWight),
                verticalAlignment = Alignment.CenterVertically
            ) {
                it()
            }
        }
    }
}

@Composable
fun ContentWithTitle(modifier: Modifier = Modifier, title: String, value: String, titleWight: Float) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.Start
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
                .weight(weight = 1f - titleWight)
                .padding(start = 3.dp)
        )
    }
}

@Composable
fun StatusWithPercentage(
    status: Pair<Int, String?>,
    result: Triple<Boolean?, Int?, Int?>,
    onlyInt: Boolean = false,
    percentageTextSize: TextUnit = 12.sp
) {
    if (status.second != EmptyString.str)
        Text(
            text = status.second ?: NoString.str,
            style = MaterialTheme.typography.titleSmall.copy(fontSize = 14.sp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    if (status.first == 3) {
        if (status.second != EmptyString.str)
            Text(
                text = "(",
                style = MaterialTheme.typography.titleSmall.copy(fontSize = 14.sp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Start,
                modifier = Modifier.padding(start = 3.dp)
            )
        Icon(
            imageVector = if (result.first != false) Icons.Filled.Check else Icons.Filled.Close,
            contentDescription = if (result.first != false) stringResource(R.string.show_less) else stringResource(R.string.show_more),
            modifier = Modifier.height(20.dp),
            tint = if (result.first != false) Color.Green else Color.Red,
        )
        val conformity = (result.second?.toFloat()?.let { result.third?.toFloat()?.div(it) }?.times(100)) ?: 0.0f
        Text(
            text = if (!conformity.isNaN()) (round(conformity * 10) / 10).let { (if (onlyInt) it.toInt() else it).toString() + "%" } else "",
            style = MaterialTheme.typography.titleSmall.copy(fontSize = percentageTextSize),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Start,
            modifier = Modifier.padding(start = 3.dp)
        )
        if (status.second != EmptyString.str)
            Text(
                text = ")",
                style = MaterialTheme.typography.titleSmall.copy(fontSize = 14.sp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Start,
                modifier = Modifier.padding(start = 3.dp)
            )
    }
}


@Composable
fun SecondLevelSingleRecordHeader(title: String, value: String) {
    Row(
        modifier = Modifier.padding(top = 0.dp, start = 0.dp, end = 0.dp, bottom = 4.dp),
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