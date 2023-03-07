package com.simenko.qmapp.ui.neworder

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import com.simenko.qmapp.BaseApplication
import com.simenko.qmapp.ui.neworder.assemblers.checkCurrentOrder
import com.simenko.qmapp.ui.neworder.assemblers.disassembleOrder
import com.simenko.qmapp.ui.neworder.steps.*
import com.simenko.qmapp.ui.theme.*
import com.simenko.qmapp.utils.StringUtils
import com.simenko.qmapp.viewmodels.ViewModelProviderFactory
import java.util.*
import javax.inject.Inject

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
            ActionType.ADD_SUB_ORDER.name -> {
                ActionType.ADD_SUB_ORDER
            }
            ActionType.EDIT_SUBORDER.name -> {
                ActionType.EDIT_SUBORDER
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
                                if (checkCurrentOrder(viewModel) == null) {
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
                                                checkCurrentOrder(viewModel)!!
                                            )
                                        }
                                        ActionType.EDIT_ORDER -> {
                                            viewModel.editOrder(
                                                this,
                                                checkCurrentOrder(viewModel)!!
                                            )
                                        }
                                        ActionType.ADD_SUB_ORDER -> {}
                                        ActionType.EDIT_SUBORDER -> {}
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

                    when (actionTypeEnum) {
                        ActionType.ADD_ORDER -> {
                            OrderForm(
                                modifier = Modifier.padding(padding),
                                viewModel = viewModel,
                                actionType = actionTypeEnum,
                                parentId = 0,
                            )
                        }
                        ActionType.EDIT_ORDER -> {
                            OrderForm(
                                modifier = Modifier.padding(padding),
                                viewModel = viewModel,
                                actionType = actionTypeEnum,
                                parentId = 0,
                            )
                        }
                        ActionType.ADD_SUB_ORDER -> {
                            SubOrderForm(
                                modifier = Modifier.padding(padding),
                                viewModel = viewModel,
                                actionType = actionTypeEnum,
                                parentId = 0,
                            )
                        }
                        ActionType.EDIT_SUBORDER -> {
                            SubOrderForm(
                                modifier = Modifier.padding(padding),
                                viewModel = viewModel,
                                actionType = actionTypeEnum,
                                parentId = 0,
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.investigationTypes.observe(this) {
            viewModel.investigationReasons.observe(this) {
                viewModel.customers.observe(this) {
                    viewModel.teamMembers.observe(this) {
                        viewModel.investigationOrders.observe(this) {
                            viewModel.departments.observe(this) {
                                viewModel.inputForOrder.observe(this) {
                                    viewModel.subDepartments.observe(this) {
                                        viewModel.channels.observe(this) {
                                            viewModel.lines.observe(this) {
                                                viewModel.keys.observe(this) {
                                                    viewModel.components.observe(this) {
                                                        viewModel.componentsToLines.observe(this) {
                                                            viewModel.statuses.observe(this) {
                                                                viewModel.componentVersions.observe(this) {

                                                                    when (actionTypeEnum) {
                                                                        ActionType.ADD_ORDER -> {
                                                                            viewModel.investigationTypesMutable.performFiltration(
                                                                                viewModel.investigationTypes,
                                                                                FilteringMode.ADD_ALL,
                                                                                viewModel.pairedTrigger
                                                                            )
                                                                        }

                                                                        ActionType.EDIT_ORDER -> {
                                                                            viewModel.investigationTypesMutable.performFiltration(
                                                                                viewModel.investigationTypes,
                                                                                FilteringMode.ADD_ALL,
                                                                                viewModel.pairedTrigger
                                                                            )

                                                                            disassembleOrder(
                                                                                viewModel,
                                                                                recordId
                                                                            )
                                                                            filterAllAfterTypes(
                                                                                viewModel,
                                                                                viewModel.currentOrder.value?.orderTypeId!!
                                                                            )
                                                                            filterAllAfterReasons(
                                                                                viewModel,
                                                                                viewModel.currentOrder.value?.reasonId!!
                                                                            )
                                                                            filterAllAfterCustomers(
                                                                                viewModel,
                                                                                viewModel.currentOrder.value?.customerId!!
                                                                            )
                                                                            filterAllAfterPlacers(
                                                                                viewModel,
                                                                                viewModel.currentOrder.value?.orderedById!!
                                                                            )
                                                                        }

                                                                        ActionType.ADD_SUB_ORDER -> {
                                                                            viewModel.departmentsMutable.performFiltration(
                                                                                s = viewModel.departments,
                                                                                action = FilteringMode.ADD_ALL_FROM_META_TABLE,
                                                                                trigger = viewModel.pairedTrigger,
                                                                                m = viewModel.inputForOrder
                                                                            )
                                                                        }
                                                                        else -> {}
                                                                    }

                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
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

