package com.simenko.qmapp.presentation.ui.main.products.kinds.list.forms.component.new_component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.ShoppingCart
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
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.simenko.qmapp.domain.EmptyString
import com.simenko.qmapp.domain.FillInErrorState
import com.simenko.qmapp.domain.FillInInitialState
import com.simenko.qmapp.domain.FillInSuccessState
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.domain.NoString
import com.simenko.qmapp.domain.ZeroValue
import com.simenko.qmapp.other.Constants
import com.simenko.qmapp.data.repository.UserError
import com.simenko.qmapp.presentation.ui.common.InfoLine
import com.simenko.qmapp.presentation.ui.common.RecordFieldItemWithMenu
import com.simenko.qmapp.presentation.ui.common.RecordFieldItem
import com.simenko.qmapp.navigation.Route
import com.simenko.qmapp.utils.StringUtils

@Composable
fun ComponentForm(
    modifier: Modifier = Modifier,
    viewModel: ComponentViewModel,
    route: Route.Main.ProductLines.ProductKinds.Products.AddEditComponent
) {

    val productKind by viewModel.productKind.collectAsStateWithLifecycle()
    val productComponent by viewModel.productComponent.collectAsStateWithLifecycle()

    val fillInErrors by viewModel.fillInErrors.collectAsStateWithLifecycle()

    val keys by viewModel.availableKeys.collectAsStateWithLifecycle()

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

    val (componentKeyFR) = FocusRequester.createRefs()
    val (productComponentQntFR) = FocusRequester.createRefs()
    val (descriptionFR) = FocusRequester.createRefs()

    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        viewModel.onEntered(route = route)
        componentKeyFR.requestFocus()
    }

    Column(horizontalAlignment = Alignment.Start, verticalArrangement = Arrangement.Bottom) {
        Spacer(modifier = Modifier.height(10.dp))
        InfoLine(modifier = modifier.padding(start = Constants.DEFAULT_SPACE.dp).fillMaxWidth(), title = "Product", body = productKind.productKind.productKindDesignation)
        InfoLine(
            modifier = modifier.padding(start = Constants.DEFAULT_SPACE.dp).fillMaxWidth(),
            title = "Product item",
            body = buildString {
                append(productComponent.product.let { StringUtils.concatTwoStrings3(it.key.componentKey, it.product.productDesignation) })
                append(" (${productComponent.product.productBase.componentBaseDesignation ?: NoString.str})")
            }
        )
        InfoLine(modifier = modifier.padding(start = Constants.DEFAULT_SPACE.dp).fillMaxWidth(), title = "Component", body = productComponent.component.componentKind.componentKindDescription)
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
                options = keys,
                isError = fillInErrors.componentKeyError,
                onDropdownMenuItemClick = { viewModel.onSelectProductKey(it ?: NoRecord.num) },
                keyboardNavigation = Pair(componentKeyFR) { componentKeyFR.requestFocus() },
                keyBoardTypeAction = Pair(KeyboardType.Text, ImeAction.Next),
                contentDescription = Triple(Icons.Outlined.Info, "Component designation", "Select designation"),
            )
            Spacer(modifier = Modifier.height(10.dp))
            RecordFieldItem(
                modifier = Modifier.width(320.dp),
                valueParam = Triple(productComponent.component.component.component.componentDesignation, fillInErrors.componentDescriptionError) { viewModel.onSetComponentDescription(it) },
                keyboardNavigation = Pair(productComponentQntFR) { descriptionFR.requestFocus() },
                keyBoardTypeAction = Pair(KeyboardType.Text, ImeAction.Done),
                contentDescription = Triple(Icons.Outlined.Info, "Component name", "Enter component name")
            )
            Spacer(modifier = Modifier.height(10.dp))
            RecordFieldItem(
                modifier = Modifier.width(320.dp),
                valueParam = Triple(
                    productComponent.productComponent.quantity.let { if (it == ZeroValue.num.toFloat()) EmptyString.str else it.toString() },
                    fillInErrors.productComponentQntError
                ) { viewModel.onSetProductComponentQuantity(it) },
                keyboardNavigation = Pair(descriptionFR) { keyboardController?.hide() },
                keyBoardTypeAction = Pair(KeyboardType.Decimal, ImeAction.Done),
                contentDescription = Triple(Icons.Outlined.ShoppingCart, "Quantity in product", "Enter quantity")
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