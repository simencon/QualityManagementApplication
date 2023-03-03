package com.simenko.qmapp.ui.neworder

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import com.simenko.qmapp.BaseApplication
import com.simenko.qmapp.R
import com.simenko.qmapp.ui.neworder.assemblers.assembleOrder
import com.simenko.qmapp.ui.neworder.assemblers.disassembleOrder
import com.simenko.qmapp.ui.neworder.steps.*
import com.simenko.qmapp.ui.theme.*
import com.simenko.qmapp.utils.StringUtils
import com.simenko.qmapp.viewmodels.ViewModelProviderFactory
import java.util.*
import javax.inject.Inject
import kotlin.properties.Delegates

enum class ActionType() {
    ADD_ORDER,
    EDIT_ORDER,
    ADD_SUB_ORDER,
    EDIT_SUBORDER
}

internal const val KEY_ARG_ACTION_TYPE = "KEY_ARG_ACTION_TYPE"
internal const val KEY_ARG_RECORD_ID = "KEY_ARG_RECORD_ID"

fun launchNewItemActivity(context: Context, actionType: ActionType, recordId: Int = 0) {
    context.startActivity(createNewItemActivityIntent(context, actionType, recordId))
}

fun createNewItemActivityIntent(context: Context, actionType: ActionType, recordId: Int): Intent {
    val intent = Intent(context, NewItemActivity::class.java)
    intent.putExtra(KEY_ARG_ACTION_TYPE, actionType.name)
    intent.putExtra(KEY_ARG_RECORD_ID, recordId)
    return intent
}

private lateinit var actionType: String
private lateinit var actionTypeEnum: ActionType
private var recordId = 0

class NewItemActivity : ComponentActivity() {

    lateinit var viewModel: NewItemViewModel

    @Inject
    lateinit var providerFactory: ViewModelProviderFactory

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {

        (application as BaseApplication).appComponent.newItemComponent().create().inject(this)
        viewModel = ViewModelProvider(this, providerFactory)[NewItemViewModel::class.java]

        super.onCreate(savedInstanceState)
        actionType = intent.extras?.getString(KEY_ARG_ACTION_TYPE) ?: ""
        actionTypeEnum = when (actionType) {
            ActionType.ADD_ORDER.name -> {
                ActionType.ADD_ORDER
            }
            ActionType.EDIT_ORDER.name -> {
                ActionType.EDIT_ORDER
            }
            else -> {
                ActionType.ADD_ORDER
            }
        }

        recordId = intent.extras?.getInt(KEY_ARG_RECORD_ID) ?: 0

        setContent {
            QMAppTheme {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {
                                Text(
                                    StringUtils.getWithSpaces(actionType),
                                    color = Color.White
                                )
                            },
                            colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = Primary900)
                        )
                    },
                    floatingActionButton = {
                        FloatingActionButton(
                            modifier = Modifier.padding(end = 29.dp),
                            onClick = {
                                if (assembleOrder(viewModel) == null) {
                                    Toast.makeText(
                                        this,
                                        "Перед збереженням заповніть всі поля!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    return@FloatingActionButton
                                } else {
                                    when (actionTypeEnum) {
                                        ActionType.ADD_ORDER -> {
                                            viewModel.postNewOrder(
                                                this,
                                                assembleOrder(viewModel)!!
                                            )
                                        }
                                        ActionType.EDIT_ORDER -> {
                                            viewModel.editOrder(
                                                this,
                                                assembleOrder(viewModel)!!
                                            )
                                        }
                                        else -> {
                                            viewModel.postNewOrder(
                                                this,
                                                assembleOrder(viewModel)!!
                                            )
                                        }
                                    }
                                }
                            },
                            content = {
                                Icon(
                                    imageVector = Icons.Default.Save,
                                    contentDescription = null,
                                    tint = Primary900
                                )
                            }
                        )
                    },
                ) { padding ->

                    HomeScreen(
                        modifier = Modifier.padding(padding),
                        viewModel = viewModel,
                        actionType = actionTypeEnum,
                        parentId = 0,
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
            viewModel.investigationReasons.observe(this) {
                viewModel.customers.observe(this) {
                    viewModel.teamMembers.observe(this) {
                        viewModel.investigationOrders.observe(this) {
                            if (actionTypeEnum == ActionType.EDIT_ORDER)
                                disassembleOrder(viewModel, recordId)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    actionType: ActionType,
    viewModel: NewItemViewModel,
    parentId: Int
) {
    val observerLoadingProcess by viewModel.isLoadingInProgress.observeAsState()
    val observerIsNetworkError by viewModel.isNetworkError.observeAsState()

    val pullRefreshState = rememberPullRefreshState(
        refreshing = observerLoadingProcess!!,
        onRefresh = {
            viewModel.refreshDataFromRepository()
        }
    )

    Box(Modifier.pullRefresh(pullRefreshState)) {
        Column(
            modifier
                .verticalScroll(rememberScrollState())
        ) {
            ButtonsSection(title = R.string.select_type) {
                TypesSelection(
                    modifier = Modifier.padding(top = 0.dp),
                    appModel = viewModel,
                    actionType = actionType,
                )
            }
            ButtonsSection(title = R.string.select_reason) {
                ReasonsSelection(
                    modifier = Modifier.padding(top = 0.dp),
                    appModel = viewModel,
                    actionType = actionType,
                )
            }
            ButtonsSection(title = R.string.select_customer) {
                CustomersSelection(
                    modifier = Modifier.padding(top = 0.dp),
                    appModel = viewModel
                )
            }

            ButtonsSection(title = R.string.select_placer) {
                SearchBarProducts(Modifier.padding(horizontal = 16.dp))
                Spacer(Modifier.height(16.dp))
                PlacersSelection(
                    modifier = Modifier.padding(top = 0.dp),
                    appModel = viewModel
                )
            }

            ButtonsSection(title = R.string.select_item_type) {
                SearchBarProducts(Modifier.padding(horizontal = 16.dp))
                Spacer(Modifier.height(16.dp))
                ProductsSelection(
                    modifier = Modifier.padding(top = 0.dp),
                    appModel = viewModel,
                    parentId = parentId
                )
            }

            Spacer(Modifier.height((16 + 56).dp))
        }
        PullRefreshIndicator(
            observerLoadingProcess!!,
            pullRefreshState,
            modifier.align(Alignment.TopCenter),
            contentColor = ProgressIndicatorDefaults.circularColor
        )
    }
    if (observerIsNetworkError == true) {
        Toast.makeText(LocalContext.current, "Network error!", Toast.LENGTH_SHORT).show()
        viewModel.onNetworkErrorShown()
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
        Divider(
            modifier = modifier.height(2.dp),
            color = Accent200
        )
    }
}

