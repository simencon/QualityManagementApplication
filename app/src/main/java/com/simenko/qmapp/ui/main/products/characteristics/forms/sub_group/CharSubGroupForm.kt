package com.simenko.qmapp.ui.main.products.characteristics.forms.sub_group

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.SquareFoot
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.simenko.qmapp.domain.EmptyString
import com.simenko.qmapp.domain.FillInErrorState
import com.simenko.qmapp.domain.FillInInitialState
import com.simenko.qmapp.domain.FillInSuccessState
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.domain.NoString
import com.simenko.qmapp.other.Constants
import com.simenko.qmapp.repository.UserError
import com.simenko.qmapp.ui.common.InfoLine
import com.simenko.qmapp.ui.common.RecordFieldItemWithMenu
import com.simenko.qmapp.ui.common.RecordFieldItem
import com.simenko.qmapp.ui.navigation.Route

@Composable
fun CharacteristicSubGroupForm(
    modifier: Modifier = Modifier,
    viewModel: CharSubGroupViewModel = hiltViewModel(),
    route: Route.Main.ProductLines.Characteristics.AddEditCharSubGroup
) {
    LaunchedEffect(Unit) { viewModel.onEntered(route = route) }

    val charSubGroup by viewModel.charSubGroup.collectAsStateWithLifecycle()
    val time by viewModel.subGroupRelatedTime.collectAsStateWithLifecycle()


    val fillInErrors by viewModel.fillInErrors.collectAsStateWithLifecycle()

    val charGroups by viewModel.charGroups.collectAsStateWithLifecycle()

    val fillInState by viewModel.fillInState.collectAsStateWithLifecycle()
    var error by rememberSaveable { mutableStateOf(UserError.NO_ERROR.error) }

    LaunchedEffect(fillInState) {
        fillInState.let { state ->
            when (state) {
                is FillInSuccessState -> viewModel.makeRecord()
                is FillInErrorState -> error = state.errorMsg
                is FillInInitialState -> error = UserError.NO_ERROR.error
            }
        }
    }

    val (groupFR) = FocusRequester.createRefs()
    val (descriptionFR) = FocusRequester.createRefs()
    val (timeFR) = FocusRequester.createRefs()

    val keyboardController = LocalSoftwareKeyboardController.current

    Column(horizontalAlignment = Alignment.Start, verticalArrangement = Arrangement.Bottom) {
        Spacer(modifier = Modifier.height(10.dp))
        InfoLine(modifier = modifier.padding(start = Constants.DEFAULT_SPACE.dp), title = "Product line", body = charSubGroup.charGroup.productLine.manufacturingProject.projectSubject ?: NoString.str)
        HorizontalDivider(modifier = Modifier.height(1.dp), color = MaterialTheme.colorScheme.secondary)
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
                .fillMaxSize()
                .padding(all = 0.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(10.dp))
            RecordFieldItemWithMenu(
                modifier = Modifier.width(320.dp),
                options = charGroups,
                isError = fillInErrors.charGroupError,
                onDropdownMenuItemClick = { viewModel.onSetCharGroup(it ?: NoRecord.num) },
                keyboardNavigation = Pair(groupFR) { keyboardController?.hide() },
                keyBoardTypeAction = Pair(KeyboardType.Ascii, ImeAction.Done),
                contentDescription = Triple(Icons.Outlined.SquareFoot, "Characteristic group", "Select group"),
            )
            Spacer(modifier = Modifier.height(10.dp))
            RecordFieldItem(
                modifier = Modifier.width(320.dp),
                valueParam = Triple(charSubGroup.charSubGroup.ishElement ?: EmptyString.str, fillInErrors.charSubGroupDescriptionError) { viewModel.onSetCharSubGroupDescription(it) },
                keyboardNavigation = Pair(descriptionFR) { keyboardController?.hide() },
                keyBoardTypeAction = Pair(KeyboardType.Ascii, ImeAction.Done),
                contentDescription = Triple(Icons.Outlined.Info, "Char. sub group description", "Enter description")
            )
            Spacer(modifier = Modifier.height(10.dp))
            RecordFieldItem(
                modifier = Modifier
                    .width(320.dp)
                    .onFocusChanged {
                        if (it.isFocused) {
                            if (time == NoString.str) viewModel.onSetCharSubGroupMeasurementTime(EmptyString.str)
                        } else {
                            if (time == EmptyString.str) viewModel.onSetCharSubGroupMeasurementTime(NoString.str)
                        }
                    },
                valueParam = Triple(time, fillInErrors.charSubGroupRelatedTimeError) { viewModel.onSetCharSubGroupMeasurementTime(it) },
                keyboardNavigation = Pair(timeFR) { keyboardController?.hide() },
                keyBoardTypeAction = Pair(KeyboardType.Number, ImeAction.Done),
                contentDescription = Triple(Icons.Outlined.Timer, "Sub group related time (minutes)", "Enter time in minutes"),
                isMandatoryField = false
            )
            Spacer(modifier = Modifier.height(10.dp))
            if (error != UserError.NO_ERROR.error)
                Text(
                    modifier = Modifier
                        .padding(all = 5.dp)
                        .width(320.dp),
                    text = error,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 14.sp, color = MaterialTheme.colorScheme.error),
                    textAlign = TextAlign.Center
                )
        }
    }
}