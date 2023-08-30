package com.simenko.qmapp.ui.neworder

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModelProvider
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.ui.main.AddEditMode
import com.simenko.qmapp.ui.main.MainActivity
import com.simenko.qmapp.ui.neworder.steps.*
import com.simenko.qmapp.ui.theme.*
import com.simenko.qmapp.utils.StringUtils
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

private const val TAG = "NewItemActivity"

internal const val KEY_ARG_REQUEST_CODE = "KEY_ARG_REQUEST_CODE"
internal const val KEY_ARG_ORDER_ID = "KEY_ARG_ORDER_ID"
internal const val KEY_ARG_SUB_ORDER_ID = "KEY_ARG_SUB_ORDER_ID"

fun launchNewItemActivityForResult(
    activity: MainActivity,
    actionType: Int,
    orderId: Int = NoRecord.num,
    subOrderId: Int = NoRecord.num
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

    lateinit var addEditModeEnum: AddEditMode
    private var orderId = NoRecord.num
    private var subOrderId = NoRecord.num

    lateinit var viewModel: NewItemViewModel

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this)[NewItemViewModel::class.java]

        addEditModeEnum = AddEditMode.values()[intent.getIntExtra(KEY_ARG_REQUEST_CODE, NoRecord.num)]

        orderId = intent.extras?.getInt(KEY_ARG_ORDER_ID) ?: NoRecord.num
        subOrderId = intent.extras?.getInt(KEY_ARG_SUB_ORDER_ID) ?: NoRecord.num

        setContent {
            QMAppTheme {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {
                                Text(
                                    StringUtils.getWithSpaces(addEditModeEnum.name),
                                    color = Color.White
                                )
                            },
                            colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = Primary)
                        )
                    },
                    floatingActionButton = {
                        /*FloatingActionButton(
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
                                    when (addEditModeEnum) {
                                        AddEditMode.NO_MODE -> {
                                            viewModel.postNewSubOrder(
                                                this,
                                                checkCurrentSubOrder(viewModel)!!
                                            )
                                        }
                                        AddEditMode.ADD_ORDER -> {
//                                            viewModel.postNewOrder(
//                                                this,
//                                                checkCurrentOrder(viewModel)!!
//                                            )
                                        }
                                        AddEditMode.EDIT_ORDER -> {
                                            viewModel.editOrder(
                                                this,
                                                checkCurrentOrder(viewModel)!!
                                            )
                                        }
                                        AddEditMode.ADD_SUB_ORDER -> {
                                            viewModel.postNewSubOrder(
                                                this,
                                                checkCurrentSubOrder(viewModel)!!
                                            )
                                        }
                                        AddEditMode.EDIT_SUB_ORDER -> {
                                            viewModel.editSubOrder(
                                                this,
                                                checkCurrentSubOrder(viewModel)!!
                                            )
                                        }
                                        AddEditMode.ADD_SUB_ORDER_STAND_ALONE -> {
                                            viewModel.postNewOrderWithSubOrder(
                                                this,
                                                checkCurrentSubOrder(viewModel)!!
                                            )
                                        }
                                        AddEditMode.EDIT_SUB_ORDER_STAND_ALONE -> {
//                                            ToDo
                                        }
                                    }
                                }
                            },
                            content = {
                                Icon(
                                    imageVector = Icons.Default.Save,
                                    contentDescription = null,
                                    tint = Primary
                                )
                            }
                        )*/
                    },
                ) { padding ->

                    when {
//                        actionTypeEnum == ActionType.ADD_ORDER -> {
//                            OrderForm(
//                                modifier = Modifier.padding(padding),
//                                actionType = actionTypeEnum,
//                                parentId = NoRecord.num,
//                            )
//                        }
//                        actionTypeEnum == ActionType.EDIT_ORDER -> {
//                            OrderForm(
//                                modifier = Modifier.padding(padding),
//                                actionType = actionTypeEnum,
//                                parentId = NoRecord.num,
//                            )
//                        }
                        (addEditModeEnum == AddEditMode.ADD_SUB_ORDER ||
                                addEditModeEnum == AddEditMode.ADD_SUB_ORDER_STAND_ALONE) -> {
                            SubOrderForm(
                                modifier = Modifier.padding(padding),
                                viewModel = viewModel,
                                addEditMode = addEditModeEnum,
                                parentId = NoRecord.num,
                            )
                        }
                        (addEditModeEnum == AddEditMode.EDIT_SUB_ORDER ||
                                addEditModeEnum == AddEditMode.EDIT_SUB_ORDER_STAND_ALONE) -> {
                            SubOrderForm(
                                modifier = Modifier.padding(padding),
                                viewModel = viewModel,
                                addEditMode = addEditModeEnum,
                                parentId = NoRecord.num,
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        /*viewModel.investigationTypes.observe(this) {
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
                                                                    ActionType.DEFAULT -> {
                                                                        viewModel.currentSubOrder.value?.subOrder?.orderId =
                                                                            orderId
                                                                        viewModel.departmentsMutable.performFiltration(
                                                                            s = viewModel.departments,
                                                                            action = FilteringMode.ADD_ALL_FROM_META_TABLE,
                                                                            trigger = viewModel.pairedTrigger,
                                                                            m = viewModel.inputForOrder
                                                                        )
                                                                    }

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
                                                                        lifecycleScope.launch {
                                                                            viewModel.prepareCurrentSubOrder(orderId, subOrderId)
                                                                            viewModel.departmentsMutable.performFiltration(
                                                                                s = viewModel.departments,
                                                                                action = FilteringMode.ADD_ALL_FROM_META_TABLE,
                                                                                trigger = viewModel.pairedTrigger,
                                                                                m = viewModel.inputForOrder
                                                                            )
                                                                        }
                                                                    }

                                                                    ActionType.EDIT_SUB_ORDER -> {
                                                                        lifecycleScope.launch {
                                                                            viewModel.prepareCurrentSubOrder(orderId, subOrderId)

                                                                            viewModel.departmentsMutable.performFiltration(
                                                                                s = viewModel.departments,
                                                                                action = FilteringMode.ADD_ALL_FROM_META_TABLE,
                                                                                trigger = viewModel.pairedTrigger,
                                                                                m = viewModel.inputForOrder
                                                                            )
                                                                            val currentSubOrder = viewModel.currentSubOrder.value!!
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
                                                                                currentSubOrder.subOrder.itemPreffix

                                                                            )
                                                                            filterAllAfterOperations(
                                                                                viewModel,
                                                                                currentSubOrder.subOrder.operationId
                                                                            )
                                                                            filterAllAfterQuantity(
                                                                                viewModel,
                                                                                currentSubOrder.subOrder.samplesCount ?: ZeroValue.num
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
        }*/
    }
}

