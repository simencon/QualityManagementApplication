package com.simenko.qmapp.ui.common

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.VectorProperty
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.simenko.qmapp.other.Constants

@Preview(
    device = Devices.PIXEL_TABLET,
    showSystemUi = true,
    uiMode = Configuration.ORIENTATION_LANDSCAPE.and(Configuration.UI_MODE_NIGHT_YES)
)
@Composable
fun DropDownPreviewer() {
    Column {
        Spacer(modifier = Modifier.height(Constants.DEFAULT_SPACE.dp))
        Row {
            Spacer(modifier = Modifier.width(Constants.DEFAULT_SPACE.dp))
            RecordFieldItemWithMenu(
                modifier = Modifier.weight(0.5f),
                options = listOf(Triple(0, "First", false), Triple(0, "Second", true), Triple(0, "Third", false)),
                isError = false,
                onDropdownMenuItemClick = {},
                keyboardNavigation = Pair(FocusRequester(), {}),
                keyBoardTypeAction = Pair(KeyboardType.Ascii, ImeAction.Done),
                contentDescription = Triple(null, null, "Select correct option")
            )
            Spacer(modifier = Modifier.width(Constants.DEFAULT_SPACE.dp))
            RecordFieldItemWithMenu(
                modifier = Modifier.weight(0.5f),
                options = listOf(Triple(0, "First", false), Triple(0, "Second", false), Triple(0, "Third", false)),
                isError = false,
                onDropdownMenuItemClick = {},
                keyboardNavigation = Pair(FocusRequester(), {}),
                keyBoardTypeAction = Pair(KeyboardType.Ascii, ImeAction.Done),
                contentDescription = Triple(null, null, "Select correct option")
            )
            Spacer(modifier = Modifier.width(Constants.DEFAULT_SPACE.dp))
        }
        Spacer(modifier = Modifier.height(Constants.DEFAULT_SPACE.dp))
        Row(horizontalArrangement = Arrangement.End) {
            Spacer(modifier = Modifier.width(Constants.DEFAULT_SPACE.dp))
            RecordFieldItemWithMenu(
                modifier = Modifier.weight(0.5f),
                options = listOf(Triple(0, "First", true), Triple(0, "Second", false), Triple(0, "Third", false)),
                isError = false,
                onDropdownMenuItemClick = {},
                keyboardNavigation = Pair(FocusRequester(), {}),
                keyBoardTypeAction = Pair(KeyboardType.Ascii, ImeAction.Done),
                contentDescription = Triple(null, null, "Select correct option")
            )
            Spacer(modifier = Modifier.width(Constants.DEFAULT_SPACE.dp))
            TrueFalseField(
                modifier = Modifier.weight(0.5f),
                frontImage = Icons.Default.AdminPanelSettings,
                enabled = true,
                description = "Id default",
                containerColor = Color.Transparent,
                isError = false,
                onSwitch = {}
            )
            Spacer(modifier = Modifier.width(Constants.DEFAULT_SPACE.dp))
        }
    }
}