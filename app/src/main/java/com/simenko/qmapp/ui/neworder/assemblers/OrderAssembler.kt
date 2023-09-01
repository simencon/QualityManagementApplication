package com.simenko.qmapp.ui.neworder.assemblers

import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.domain.ZeroValue
import com.simenko.qmapp.domain.entities.DomainOrder
import com.simenko.qmapp.domain.entities.DomainSubOrder
import com.simenko.qmapp.ui.neworder.NewItemViewModel

fun checkIfPossibleToSave(record: DomainOrder): Boolean {
    if (record.orderTypeId == NoRecord.num) return false
    if (record.reasonId == NoRecord.num) return false
    if (record.customerId == NoRecord.num) return false
    if (record.orderedById == NoRecord.num) return false

    return true
}

fun disassembleOrder(viewModel: NewItemViewModel, orderId: Int) {
//    viewModel.currentOrder.value = viewModel.investigationOrders.value?.find { it.id == orderId }
}

fun checkIfPossibleToSave(record: Triple<DomainOrder, DomainSubOrder, Int>): Boolean {

    if (record.first.reasonId == NoRecord.num) return false

    if (record.second.orderedById == NoRecord.num) return false
    if (record.second.departmentId == NoRecord.num) return false
    if (record.second.subDepartmentId == NoRecord.num) return false
    if (record.second.channelId == NoRecord.num) return false
    if (record.second.lineId == NoRecord.num) return false
    if (record.second.operationId == NoRecord.num) return false
    if (record.second.itemVersionId == NoRecord.num) return false
    if (record.second.samplesCount == ZeroValue.num) return false

    if (record.third == ZeroValue.num) return false

    return true
}