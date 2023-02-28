package com.simenko.qmapp.ui.neworder

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material3.Divider
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import com.simenko.qmapp.BaseApplication
import com.simenko.qmapp.R
import com.simenko.qmapp.ui.neworder.steps.*
import com.simenko.qmapp.ui.theme.*
import com.simenko.qmapp.viewmodels.ViewModelProviderFactory
import java.util.*
import javax.inject.Inject

enum class NewItemType() {
    NEW_INVESTIGATION,
    NEW_PROCESS_INVESTIGATION,
    NEW_CHARACTERISTIC
}

internal const val KEY_ARG_NEW_ITEM_TYPE = "KEY_ARG_NEW_ITEM_TYPE"

fun launchNewItemActivity(context: Context, orderType: NewItemType) {
    context.startActivity(createNewItemActivityIntent(context, orderType))
}

fun createNewItemActivityIntent(context: Context, orderType: NewItemType): Intent {
    val intent = Intent(context, NewItemActivity::class.java)
    intent.putExtra(KEY_ARG_NEW_ITEM_TYPE, orderType.name)
    return intent
}

class NewItemActivity : ComponentActivity() {

    lateinit var viewModel: NewItemViewModel

    @Inject
    lateinit var providerFactory: ViewModelProviderFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as BaseApplication).appComponent.newItemComponent().create().inject(this)
        viewModel = ViewModelProvider(this, providerFactory)[NewItemViewModel::class.java]

        super.onCreate(savedInstanceState)
        setContent {

            QMAppTheme {
                Scaffold(
                    bottomBar = {}
                ) { padding ->

                    HomeScreen(
                        modifier = Modifier.padding(padding),
                        viewModel = viewModel,
                        parentId = 0
                    )
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.investigationTypes.observe(
            this
        ) {
            viewModel.filterWithOneParent(
                viewModel.investigationTypesMutable,
                viewModel.investigationTypes,
                -2
            )
        }
        viewModel.investigationReasons.observe(this) {}
        viewModel.customers.observe(this) {}
        viewModel.teamMembers.observe(this) {}
    }

}

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: NewItemViewModel,
    parentId: Int
) {
    Column(
        modifier
            .verticalScroll(rememberScrollState())
    ) {
        ButtonsSection(title = R.string.select_type) {
            TypesSelection(
                modifier = modifier,
                appModel = viewModel,
                parentId = parentId
            )
        }
        ButtonsSection(title = R.string.select_reason) {
            ReasonsSelection(
                modifier = modifier,
                appModel = viewModel,
                parentId = parentId
            )
        }
        ButtonsSection(title = R.string.select_customer) {
            CustomersSelection(
                modifier = modifier,
                appModel = viewModel,
                parentId = parentId
            )
        }

        ButtonsSection(title = R.string.select_placer) {
            SearchBarProducts(Modifier.padding(horizontal = 16.dp))
            Spacer(Modifier.height(16.dp))
            PlacersSelection(
                modifier = modifier,
                appModel = viewModel,
                parentId = parentId
            )
        }

        ButtonsSection(title = R.string.select_item_type) {
            SearchBarProducts(Modifier.padding(horizontal = 16.dp))
            Spacer(Modifier.height(16.dp))
            ProductsSelection(
                modifier = modifier,
                appModel = viewModel,
                parentId = parentId
            )
        }

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
fun ButtonsSection(
    @StringRes title: Int,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Column(modifier) {
        androidx.compose.material.Text(
            text = stringResource(title).uppercase(Locale.getDefault()),
            style = MaterialTheme.typography.h2.copy(fontSize = 18.sp),
            modifier = Modifier
                .paddingFromBaseline(top = 40.dp, bottom = 8.dp)
                .padding(horizontal = 16.dp)
        )
        content()
        Spacer(Modifier.height(16.dp))
        Divider(
            modifier = modifier.height(2.dp),
            color = Accent200
        )
    }
}

