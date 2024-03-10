package com.simenko.qmapp.ui.main.products.kinds.list.versions

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.simenko.qmapp.domain.EmptyString
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.domain.entities.products.DomainItemVersionComplete
import com.simenko.qmapp.other.Constants.DEFAULT_SPACE
import com.simenko.qmapp.ui.common.AppDialogDatePicker
import com.simenko.qmapp.ui.common.InfoLine
import com.simenko.qmapp.ui.common.RecordFieldItem
import com.simenko.qmapp.ui.common.RecordFieldItemWithMenu
import com.simenko.qmapp.ui.common.TrueFalseField
import com.simenko.qmapp.ui.common.animation.HorizonteAnimationImp
import com.simenko.qmapp.utils.StringUtils
import com.simenko.qmapp.utils.StringUtils.getStringDate
import com.simenko.qmapp.utils.dp
import com.simenko.qmapp.utils.observeAsState

@Composable
fun VersionTolerances(
    mainScreenPadding: PaddingValues,
    viewModel: VersionTolerancesViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    val screenWidthPhysical = screenWidth.toFloat().dp()
    val localDensity = LocalDensity.current
    var titleHeightDp by remember { mutableStateOf(0.dp) }
    val screenHeight = configuration.screenHeightDp.dp - mainScreenPadding.calculateTopPadding() - titleHeightDp

    val animator = HorizonteAnimationImp(screenWidth, scope)

    val itemVersion by viewModel.itemVersion.collectAsStateWithLifecycle()
    val isEditMode by viewModel.versionEditMode.collectAsStateWithLifecycle()
    val itemVersionErrors by viewModel.itemVersionErrors.collectAsStateWithLifecycle()
    val versionStatuses by viewModel.versionStatuses.collectAsStateWithLifecycle(emptyList())

    val isSecondRowVisible by viewModel.isSecondColumnVisible.collectAsStateWithLifecycle(false)
    val listsIsInitialized by viewModel.listsIsInitialized.collectAsStateWithLifecycle(Pair(false, false))

    /**
     * TotalScreenWidth, FirstColumnWidth, SecondColumnWidth
     * */
    var screenSizes: Triple<Dp, Dp, Dp> by remember { mutableStateOf(animator.getRequiredScreenWidth(if (isSecondRowVisible) 1 else 0)) }

    val verticalScrollState = rememberScrollState()
    val horizontalScrollState = rememberScrollState()

    LaunchedEffect(Unit) { viewModel.mainPageHandler.setupMainPage(0, true) }

    val lifecycleState = LocalLifecycleOwner.current.lifecycle.observeAsState()

    LaunchedEffect(lifecycleState.value) {
        when (lifecycleState.value) {
            Lifecycle.Event.ON_RESUME -> viewModel.setViewState(true)
            Lifecycle.Event.ON_STOP -> viewModel.setViewState(false)
            else -> {}
        }
    }

    LaunchedEffect(isSecondRowVisible) {
        if (isSecondRowVisible) {
            animator.setRequiredScreenWidth(1) { screenSizes = it }
        } else {
            animator.run { horizontalScrollState.animateScroll(0) }
            animator.setRequiredScreenWidth(0) { screenSizes = it }
        }
    }

    var isDatePickerVisible by rememberSaveable { mutableStateOf(false) }

    Box {
        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start, verticalArrangement = Arrangement.Bottom) {
            VersionSpecificationHeader(
                localDensity = localDensity,
                onHeaderHeightCalculated = { titleHeightDp = it },
                isEditMode = isEditMode,
                itemVersion = itemVersion,
                itemVersionErrors = itemVersionErrors,
                versionStatuses = versionStatuses,
                onShowDatePicker = { isDatePickerVisible = it },
                setVersionIsDefault = viewModel::setVersionIsDefault,
                setItemVersionDescription = viewModel::setItemVersionDescription,
                setVersionStatus = viewModel::setVersionStatus
            )
            Row(
                Modifier
                    .verticalScroll(verticalScrollState)
                    .horizontalScroll(horizontalScrollState, isSecondRowVisible)
                    .onSizeChanged { if (isSecondRowVisible && it.width > screenWidthPhysical) animator.run { horizontalScrollState.animateScroll(1) } }
                    .width(screenSizes.first)
                    .height(screenHeight)
            ) {
                if (listsIsInitialized.first) {
                    CharGroups(modifier = Modifier.width(screenSizes.second), viewModel = viewModel)
                }
                if (isSecondRowVisible /*&& listsIsInitialized.second*/) {
                    Tolerances(modifier = Modifier.width(screenSizes.third), viewModel = viewModel)
                }
            }
        }
        if (isDatePickerVisible) {
            AppDialogDatePicker(
                initialDateMillis = itemVersion.itemVersion.versionDate,
                onDateSelected = { viewModel.setItemVersionDate(it) },
                onDismiss = { isDatePickerVisible = false }
            )
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun VersionSpecificationHeader(
    localDensity: Density,
    onHeaderHeightCalculated: (Dp) -> Unit,
    isEditMode: Boolean,
    itemVersion: DomainItemVersionComplete,
    itemVersionErrors: ItemVersionErrors,
    versionStatuses: List<Triple<ID, String, Boolean>>,
    onShowDatePicker: (Boolean) -> Unit,
    setVersionIsDefault: (Boolean) -> Unit,
    setItemVersionDescription: (String) -> Unit,
    setVersionStatus: (ID) -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    val (versionDescriptionFR) = FocusRequester.createRefs()
    val (versionDateFR) = FocusRequester.createRefs()
    val (versionStatusFR) = FocusRequester.createRefs()

    val interactionSource = remember { MutableInteractionSource() }

    LaunchedEffect(Unit) {
        interactionSource.interactions.collect { if (it is PressInteraction.Release) onShowDatePicker(true) }
    }

    Column(modifier = Modifier.onGloballyPositioned { onHeaderHeightCalculated(with(localDensity) { it.size.height.toDp() }) }) {
        val itemDesignationTitle = when (itemVersion.itemVersion.fId[0]) {
            'p' -> "Product designation"
            'c' -> "Component designation"
            's' -> "Component stage designation"
            else -> "Unknown item"
        }
        val itemDesignation = itemVersion.itemComplete.run { StringUtils.concatTwoStrings3(key.componentKey, item.itemDesignation) }
        Spacer(modifier = Modifier.height(DEFAULT_SPACE.dp))
        Row(horizontalArrangement = Arrangement.SpaceBetween) {
            Column(modifier = Modifier.weight(0.55f)) {
                InfoLine(modifier = Modifier.padding(start = DEFAULT_SPACE.dp), title = itemDesignationTitle, body = itemDesignation)
            }
            Spacer(modifier = Modifier.width(DEFAULT_SPACE.dp))
            TrueFalseField(
                modifier = Modifier.weight(0.45f),
                value = itemVersion.itemVersion.isDefault,
                description = "Default?",
                containerColor = Color.Transparent,
                enabled = isEditMode,
                isError = itemVersionErrors.versionStatusError,
                onSwitch = { setVersionIsDefault(it) }
            )
            Spacer(modifier = Modifier.width(DEFAULT_SPACE.dp))
        }
        Spacer(modifier = Modifier.height(DEFAULT_SPACE.dp))
        Row(
            modifier = Modifier
                .wrapContentWidth()
                .horizontalScroll(rememberScrollState()),

            ) {
            Spacer(modifier = Modifier.width(DEFAULT_SPACE.dp))
            RecordFieldItem(
                modifier = Modifier.width(120.dp),
                valueParam = Triple(itemVersion.itemVersion.versionDescription ?: EmptyString.str, itemVersionErrors.versionDescriptionError) { setItemVersionDescription(it) },
                enabled = isEditMode,
                keyboardNavigation = Pair(versionDescriptionFR) { keyboardController?.hide() },
                keyBoardTypeAction = Pair(KeyboardType.Ascii, ImeAction.Done),
                contentDescription = Triple(null, "Version ID", "version ID")
            )
            Spacer(modifier = Modifier.width(DEFAULT_SPACE.dp))
            RecordFieldItem(
                modifier = Modifier.width(120.dp),
                valueParam = Triple(getStringDate(itemVersion.itemVersion.versionDate, 6), itemVersionErrors.versionDescriptionError) {},
                enabled = isEditMode,
                keyboardNavigation = Pair(versionDateFR) { keyboardController?.hide() },
                keyBoardTypeAction = Pair(KeyboardType.Ascii, ImeAction.Done),
                contentDescription = Triple(null, "Version date", "pick date"),
                readOnly = true,
                interactionSource = interactionSource
            )
            Spacer(modifier = Modifier.width(DEFAULT_SPACE.dp))
            RecordFieldItemWithMenu(
                modifier = Modifier.width(160.dp),
                options = versionStatuses,
                enabled = isEditMode,
                isError = itemVersionErrors.versionStatusError,
                onDropdownMenuItemClick = { setVersionStatus(it) },
                keyboardNavigation = Pair(versionStatusFR) { keyboardController?.hide() },
                keyBoardTypeAction = Pair(KeyboardType.Ascii, ImeAction.Done),
                contentDescription = Triple(null, "Status", "status")
            )
            Spacer(modifier = Modifier.width(DEFAULT_SPACE.dp))
        }
        Spacer(modifier = Modifier.height(DEFAULT_SPACE.dp))
        HorizontalDivider(modifier = Modifier.height(1.dp), color = MaterialTheme.colorScheme.secondary)
    }
}