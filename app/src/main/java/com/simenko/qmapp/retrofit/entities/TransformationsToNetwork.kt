package com.simenko.qmapp.retrofit.entities

import com.simenko.qmapp.domain.DomainOrder
import com.simenko.qmapp.utils.orderDomainToNetwork


fun DomainOrder.toNetworkOrder() = orderDomainToNetwork.transform(this)