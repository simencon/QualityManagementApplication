package com.simenko.qmapp.ui.neworder.assemblers

import com.simenko.qmapp.domain.DomainOrder
import com.simenko.qmapp.domain.DomainSubOrder
import com.simenko.qmapp.domain.DomainSubOrderWithTasks
import com.simenko.qmapp.ui.neworder.NewItemViewModel

fun checkCurrentOrder(viewModel: NewItemViewModel): DomainOrder? {
    if (viewModel.currentOrder.value?.orderTypeId == 0) return null
    if (viewModel.currentOrder.value?.reasonId == 0) return null
    if (viewModel.currentOrder.value?.customerId == 0) return null
    if (viewModel.currentOrder.value?.orderedById == 0) return null

    return viewModel.currentOrder.value
}

fun disassembleOrder(viewModel: NewItemViewModel, orderId: Int) {
    viewModel.currentOrder.value = viewModel.investigationOrders.value?.find { it.id == orderId }
}

fun checkCurrentSubOrder(viewModel: NewItemViewModel): DomainSubOrderWithTasks? {

    val subOrder = viewModel.currentSubOrder.value!!.subOrder

    if (subOrder.orderedById == 0) return null
    if (subOrder.departmentId == 0) return null
    if (subOrder.subDepartmentId == 0) return null
    if (subOrder.channelId == 0) return null
    if (subOrder.lineId == 0) return null
    if (subOrder.operationId == 0) return null
    if (subOrder.itemVersionId == 0) return null
    if (subOrder.samplesCount == 0) return null

    return viewModel.currentSubOrder.value

}