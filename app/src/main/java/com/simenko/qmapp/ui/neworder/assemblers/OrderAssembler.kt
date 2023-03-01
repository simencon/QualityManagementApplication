package com.simenko.qmapp.ui.neworder.assemblers

import com.simenko.qmapp.domain.DomainOrder
import com.simenko.qmapp.ui.neworder.NewItemViewModel

fun assembleOrder(viewModel: NewItemViewModel): DomainOrder? {
    val orderTypeId = viewModel.investigationTypes.value?.find { it.isSelected }?.id ?: return null
    val reasonId = viewModel.investigationReasons.value?.find { it.isSelected }?.id ?: return null
    val customerId = viewModel.customers.value?.find { it.isSelected }?.id ?: return null
    val orderedById = viewModel.teamMembers.value?.find { it.isSelected }?.id ?: return null

    return DomainOrder(
        id = 0,
        orderTypeId = orderTypeId,
        reasonId = reasonId,
        orderNumber = null,
        customerId = customerId,
        orderedById = orderedById,
        statusId = 1,//Means when created - to do!
        createdDate = "2022-12-15T22:24:43"//Will be changed anyhow by API but has to be with proper format
    )
}