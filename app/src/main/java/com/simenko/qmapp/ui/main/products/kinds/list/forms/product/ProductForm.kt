package com.simenko.qmapp.ui.main.products.kinds.list.forms.product

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
import androidx.compose.material.icons.outlined.Sell
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
import com.simenko.qmapp.domain.FillInErrorState
import com.simenko.qmapp.domain.FillInInitialState
import com.simenko.qmapp.domain.FillInSuccessState
import com.simenko.qmapp.other.Constants
import com.simenko.qmapp.repository.UserError
import com.simenko.qmapp.ui.common.InfoLine
import com.simenko.qmapp.ui.common.RecordFieldItemWithMenu
import com.simenko.qmapp.ui.common.RecordFieldItem
import com.simenko.qmapp.ui.navigation.Route

@Composable
fun ProductForm(
    modifier: Modifier = Modifier,
    viewModel: ProductViewModel,
    route: Route.Main.ProductLines.ProductKinds.Products.AddEditProduct
) {

    val productKindProduct by viewModel.productKindProduct.collectAsStateWithLifecycle()

    val fillInErrors by viewModel.fillInErrors.collectAsStateWithLifecycle()

    val productBases by viewModel.availableProductBases.collectAsStateWithLifecycle()
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

    val (productKeyFR) = FocusRequester.createRefs()
    val (productBaseFR) = FocusRequester.createRefs()
    val (descriptionFR) = FocusRequester.createRefs()

    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        viewModel.onEntered(route = route)
        productKeyFR.requestFocus()
    }

    Column(horizontalAlignment = Alignment.Start, verticalArrangement = Arrangement.Bottom) {
        Spacer(modifier = Modifier.height(10.dp))
        InfoLine(modifier = modifier.padding(start = Constants.DEFAULT_SPACE.dp), title = "Product kind", body = productKindProduct.productKind.productKindDesignation)
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
                isError = fillInErrors.productKeyError,
                onDropdownMenuItemClick = { viewModel.onSelectProductKey(it) },
                keyboardNavigation = Pair(productKeyFR) { productKeyFR.requestFocus() },
                keyBoardTypeAction = Pair(KeyboardType.Text, ImeAction.Next),
                contentDescription = Triple(Icons.Outlined.Info, "Product designation", "Select product designation"),
            )
            Spacer(modifier = Modifier.height(10.dp))
            RecordFieldItemWithMenu(
                modifier = Modifier.width(320.dp),
                options = productBases,
                isError = fillInErrors.productBaseError,
                onDropdownMenuItemClick = { viewModel.onSelectProductBase(it) },
                keyboardNavigation = Pair(productBaseFR) { productBaseFR.requestFocus() },
                keyBoardTypeAction = Pair(KeyboardType.Text, ImeAction.Next),
                contentDescription = Triple(Icons.Outlined.Sell, "Base product", "Select base product"),
            )
            Spacer(modifier = Modifier.height(10.dp))
            RecordFieldItem(
                modifier = Modifier.width(320.dp),
                valueParam = Triple(productKindProduct.product.product.productDesignation, fillInErrors.productDescriptionError) { viewModel.onSetProductDescription(it) },
                keyboardNavigation = Pair(descriptionFR) { keyboardController?.hide() },
                keyBoardTypeAction = Pair(KeyboardType.Text, ImeAction.Done),
                contentDescription = Triple(Icons.Outlined.Info, "Product name", "Enter product name")
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