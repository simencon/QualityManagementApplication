package com.simenko.qmapp.ui.neworder

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import com.simenko.qmapp.domain.NoSelectedRecord
import com.simenko.qmapp.ui.main.MainActivity
import com.simenko.qmapp.ui.neworder.assemblers.checkCurrentOrder
import com.simenko.qmapp.ui.neworder.assemblers.checkCurrentSubOrder
import com.simenko.qmapp.ui.neworder.assemblers.disassembleOrder
import com.simenko.qmapp.ui.neworder.steps.*
import com.simenko.qmapp.ui.theme.*
import com.simenko.qmapp.utils.StringUtils
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject

private const val TAG = "NewItemActivity"

enum class ActionType {
    ADD_ORDER,
    EDIT_ORDER,
    ADD_SUB_ORDER,
    EDIT_SUB_ORDER,
    ADD_SUB_ORDER_STAND_ALONE,
    EDIT_SUB_ORDER_STAND_ALONE
}

internal const val KEY_ARG_REQUEST_CODE = "KEY_ARG_REQUEST_CODE"
internal const val KEY_ARG_ORDER_ID = "KEY_ARG_ORDER_ID"
internal const val KEY_ARG_SUB_ORDER_ID = "KEY_ARG_SUB_ORDER_ID"

fun launchNewItemActivityForResult(
    activity: MainActivity,
    actionType: Int,
    orderId: Int = NoSelectedRecord.num,
    subOrderId: Int = NoSelectedRecord.num
) {
    activity.startActivityForResult(
        createNewItemActivityIntent(
            activity,
            actionType,
            orderId,
            subOrderId
        ), actionType
    )
}

fun createNewItemActivityIntent(
    context: Context,
    actionType: Int,
    orderId: Int,
    subOrderId: Int
): Intent {
    val intent = Intent(context, NewItemActivity::class.java)
    intent.putExtra(KEY_ARG_REQUEST_CODE, actionType)
    intent.putExtra(KEY_ARG_ORDER_ID, orderId)
    intent.putExtra(KEY_ARG_SUB_ORDER_ID, subOrderId)
    return intent
}

@AndroidEntryPoint
class NewItemActivity : ComponentActivity() {

    lateinit var actionTypeEnum: ActionType
    private var orderId = NoSelectedRecord.num
    private var subOrderId = NoSelectedRecord.num

    lateinit var viewModel: NewItemViewModel

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this)[NewItemViewModel::class.java]

        actionTypeEnum = ActionType.values()[intent.getIntExtra(KEY_ARG_REQUEST_CODE, -1)]

        orderId = intent.extras?.getInt(KEY_ARG_ORDER_ID) ?: NoSelectedRecord.num
        subOrderId = intent.extras?.getInt(KEY_ARG_SUB_ORDER_ID) ?: NoSelectedRecord.num

        setContent {
            QMAppTheme {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {
                                Text(
                                    StringUtils.getWithSpaces(actionTypeEnum.name),
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
                                if (checkCurrentOrder(viewModel) == null
                                    && checkCurrentSubOrder(viewModel) == null
                                ) {
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
                                        ActionType.ADD_SUB_ORDER -> {
                                            viewModel.postNewSubOrder(
                                                this,
                                                checkCurrentSubOrder(viewModel)!!
                                            )
                                        }
                                        ActionType.EDIT_SUB_ORDER -> {
                                            viewModel.editSubOrder(
                                                this,
                                                checkCurrentSubOrder(viewModel)!!
                                            )
                                        }
                                        ActionType.ADD_SUB_ORDER_STAND_ALONE -> {
                                            Log.d(
                                                TAG,
                                                "onCreate: ${viewModel.currentSubOrder.value?.order?.reasonId}"
                                            )
                                            viewModel.postNewOrderWithSubOrder(
                                                this,
                                                checkCurrentSubOrder(viewModel)!!
                                            )
                                        }
                                        ActionType.EDIT_SUB_ORDER_STAND_ALONE -> {
//                                            ToDo
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

                    when {
                        actionTypeEnum == ActionType.ADD_ORDER -> {
                            OrderForm(
                                modifier = Modifier.padding(padding),
                                viewModel = viewModel,
                                actionType = actionTypeEnum,
                                parentId = 0,
                            )
                        }
                        actionTypeEnum == ActionType.EDIT_ORDER -> {
                            OrderForm(
                                modifier = Modifier.padding(padding),
                                viewModel = viewModel,
                                actionType = actionTypeEnum,
                                parentId = 0,
                            )
                        }
                        (actionTypeEnum == ActionType.ADD_SUB_ORDER ||
                                actionTypeEnum == ActionType.ADD_SUB_ORDER_STAND_ALONE) -> {
                            SubOrderForm(
                                modifier = Modifier.padding(padding),
                                viewModel = viewModel,
                                actionType = actionTypeEnum,
                                parentId = 0,
                            )
                        }
                        (actionTypeEnum == ActionType.EDIT_SUB_ORDER ||
                                actionTypeEnum == ActionType.EDIT_SUB_ORDER_STAND_ALONE) -> {
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
                                                viewModel.itemVersionsComplete.observe(this) {
                                                    viewModel.operations.observe(this) {
                                                        viewModel.operationsFlows.observe(this) {
                                                            viewModel.characteristics.observe(this) {
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
                                                                            orderId
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
                                                                        viewModel.currentSubOrder.value?.subOrder?.orderId =
                                                                            orderId
                                                                        viewModel.departmentsMutable.performFiltration(
                                                                            s = viewModel.departments,
                                                                            action = FilteringMode.ADD_ALL_FROM_META_TABLE,
                                                                            trigger = viewModel.pairedTrigger,
                                                                            m = viewModel.inputForOrder
                                                                        )
                                                                    }

                                                                    ActionType.EDIT_SUB_ORDER -> {

                                                                        viewModel.subOrdersWithChildren.observe(
                                                                            this
                                                                        ) {
                                                                            viewModel.loadCurrentSubOrder(
                                                                                subOrderId
                                                                            )

                                                                            viewModel.departmentsMutable.performFiltration(
                                                                                s = viewModel.departments,
                                                                                action = FilteringMode.ADD_ALL_FROM_META_TABLE,
                                                                                trigger = viewModel.pairedTrigger,
                                                                                m = viewModel.inputForOrder
                                                                            )
                                                                            val currentSubOrder =
                                                                                viewModel.currentSubOrder.value!!
                                                                            filterAllAfterDepartments(
                                                                                viewModel,
                                                                                currentSubOrder.subOrder.departmentId
                                                                            )
                                                                            filterAllAfterSubDepartments(
                                                                                viewModel,
                                                                                currentSubOrder.subOrder.subDepartmentId
                                                                            )
                                                                            filterAllAfterSubOrderPlacers(
                                                                                viewModel,
                                                                                currentSubOrder.subOrder.orderedById
                                                                            )
                                                                            filterAllAfterChannels(
                                                                                viewModel,
                                                                                currentSubOrder.subOrder.channelId
                                                                            )
                                                                            filterAllAfterLines(
                                                                                viewModel,
                                                                                currentSubOrder.subOrder.lineId
                                                                            )
                                                                            filterAllAfterVersions(
                                                                                viewModel,
                                                                                StringUtils.concatTwoStrings4(
                                                                                    currentSubOrder.subOrder.itemPreffix,
                                                                                    currentSubOrder.subOrder.itemVersionId.toString()
                                                                                )
                                                                            )
                                                                            filterAllAfterOperations(
                                                                                viewModel,
                                                                                currentSubOrder.subOrder.operationId
                                                                            )
                                                                            filterAllAfterQuantity(
                                                                                viewModel,
                                                                                currentSubOrder.subOrder.samplesCount
                                                                                    ?: 0
                                                                            )
                                                                            filterAllAfterCharacteristics(
                                                                                viewModel
                                                                            )
                                                                        }
                                                                    }

                                                                    ActionType.ADD_SUB_ORDER_STAND_ALONE -> {
                                                                        viewModel.investigationReasonsMutable.performFiltration(
                                                                            viewModel.investigationReasons,
                                                                            FilteringMode.ADD_ALL,
                                                                            viewModel.pairedTrigger
                                                                        )
                                                                    }

                                                                    ActionType.EDIT_SUB_ORDER_STAND_ALONE -> {
//                                                                                ToDo
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

