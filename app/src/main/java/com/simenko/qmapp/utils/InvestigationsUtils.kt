package com.simenko.qmapp.utils

import com.simenko.qmapp.domain.DomainOrderComplete
import com.simenko.qmapp.domain.DomainSubOrderComplete
import com.simenko.qmapp.domain.NoSelectedRecord
import com.simenko.qmapp.domain.NoSelectedString

data class OrdersFilter(
    val typeId: Int = NoSelectedRecord.num,
    val statusId: Int = NoSelectedRecord.num,
    val orderNumber: String = NoSelectedString.str
)

data class SubOrdersFilter(
    val typeId: Int = NoSelectedRecord.num,
    val statusId: Int = NoSelectedRecord.num,
    val orderNumber: String = NoSelectedString.str
)

object InvestigationsUtils {
    fun List<DomainOrderComplete>.getCurrentOrdersRange(): Pair<Int, Int> =
        if (this.isNotEmpty())
            Pair(this[lastIndex].order.id, this[0].order.id)
        else
            Pair(NoSelectedRecord.num, NoSelectedRecord.num)

    fun List<DomainOrderComplete>.filterByStatusAndNumber(
        ordersFilter: OrdersFilter = OrdersFilter()
    ): List<DomainOrderComplete> {
        return filter {
            (it.order.orderTypeId == ordersFilter.typeId || ordersFilter.typeId == NoSelectedRecord.num)
                    &&
                    (it.order.statusId == ordersFilter.statusId || ordersFilter.statusId == NoSelectedRecord.num)
                    &&
                    (it.order.orderNumber.toString().contains(ordersFilter.orderNumber)
                            ||
                            (ordersFilter.orderNumber == NoSelectedString.str))
        }
    }

    fun List<DomainSubOrderComplete>.filterSubOrderByStatusAndNumber(
        subOrdersFilter: SubOrdersFilter = SubOrdersFilter()
    ): List<DomainSubOrderComplete> {
        return filter {
            (it.orderShort.order.orderTypeId == subOrdersFilter.typeId || subOrdersFilter.typeId == NoSelectedRecord.num)
                    &&
                    (it.subOrder.statusId == subOrdersFilter.statusId || subOrdersFilter.statusId == NoSelectedRecord.num)
                    &&
                    (it.orderShort.order.orderNumber.toString().contains(subOrdersFilter.orderNumber)
                            ||
                            (subOrdersFilter.orderNumber == NoSelectedString.str))
        }
    }
}