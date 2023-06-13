package com.simenko.qmapp.ui.neworder.assemblers

import android.util.Log
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.domain.ZeroValue
import com.simenko.qmapp.domain.entities.DomainOrder
import com.simenko.qmapp.domain.entities.DomainSubOrderShort
import com.simenko.qmapp.ui.neworder.NewItemViewModel

private const val TAG = "OrderAssembler"

fun checkCurrentOrder(viewModel: NewItemViewModel): DomainOrder? {
    if (viewModel.currentOrder.value?.orderTypeId == NoRecord.num) return null
    if (viewModel.currentOrder.value?.reasonId == NoRecord.num) return null
    if (viewModel.currentOrder.value?.customerId == NoRecord.num) return null
    if (viewModel.currentOrder.value?.orderedById == NoRecord.num) return null

    return viewModel.currentOrder.value
}

fun disassembleOrder(viewModel: NewItemViewModel, orderId: Int) {
    viewModel.currentOrder.value = viewModel.investigationOrders.value?.find { it.id == orderId }
}

fun checkCurrentSubOrder(viewModel: NewItemViewModel): DomainSubOrderShort? {

    Log.d(TAG, "checkCurrentSubOrder: ${viewModel.currentSubOrder.value?.subOrder}")
    Log.d(TAG, "checkCurrentSubOrder: ${viewModel.currentSubOrder.value!!.order.reasonId}")

    if(viewModel.currentSubOrder.value!!.order.reasonId == NoRecord.num) return null

    val subOrder = viewModel.currentSubOrder.value!!.subOrder

    if (subOrder.orderedById == NoRecord.num) return null
    if (subOrder.departmentId == NoRecord.num) return null
    if (subOrder.subDepartmentId == NoRecord.num) return null
    if (subOrder.channelId == NoRecord.num) return null
    if (subOrder.lineId == NoRecord.num) return null
    if (subOrder.operationId == NoRecord.num) return null
    if (subOrder.itemVersionId == NoRecord.num) return null
    if (subOrder.samplesCount == ZeroValue.num) return null

    return viewModel.currentSubOrder.value

}