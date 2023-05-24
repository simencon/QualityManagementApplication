package com.simenko.qmapp.utils

import com.simenko.qmapp.domain.*
import com.simenko.qmapp.retrofit.entities.NetworkOrder

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

    /**
     * The first means top orderID
     * The second means btn orderID
     * */
    fun List<DomainOrderComplete>.getDetailedOrdersRange(): Pair<Int, Int> =

        if (this.isNotEmpty())
            Pair(this.minBy { it.order.id }.order.id, this.maxBy { it.order.id }.order.id)
        else
            Pair(NoSelectedRecord.num, NoSelectedRecord.num)

    /**
     * The first means top orderID
     * The second means btn orderID
     * */
    fun List<NetworkOrder>.getOrdersRange(): Pair<Int, Int> =
        if (this.isNotEmpty())
            Pair(this.maxBy { it.id }.id, this.minBy { it.id }.id)
        else
            Pair(NoSelectedRecord.num, NoSelectedRecord.num)

    fun Pair<SelectedNumber, SelectedNumber>.setVisibility(dId: SelectedNumber, aId: SelectedNumber): Pair<SelectedNumber, SelectedNumber> {
        return if (dId != NoSelectedRecord)
            Pair(
                if (this.first != dId) dId else NoSelectedRecord,
                this.second
            ) else
            Pair(
                this.first,
                if (this.second != aId) aId else NoSelectedRecord
            )
    }

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