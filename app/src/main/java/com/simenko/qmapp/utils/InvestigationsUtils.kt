package com.simenko.qmapp.utils

import com.simenko.qmapp.domain.*
import com.simenko.qmapp.retrofit.entities.NetworkOrder
import com.simenko.qmapp.works.SyncPeriods
import java.time.Instant

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
    fun List<DomainOrderComplete>.getDetailedOrdersRange(): Pair<Long, Long> =

        if (this.isNotEmpty())
            Pair(
                this.minBy { it.order.createdDate }.order.createdDate,
                this.maxBy { it.order.createdDate }.order.createdDate
            )
        else
            Pair(NoSelectedRecord.num.toLong(), NoSelectedRecord.num.toLong())

    /**
     * The first means top orderID
     * The second means btn orderID
     * */
    fun List<NetworkOrder>.getOrdersRange(): Pair<Long, Long> =
        if (this.isNotEmpty())
            Pair(
                this.minBy { it.createdDate }.createdDate,
                this.maxBy { it.createdDate }.createdDate
            )
        else
            Pair(NoSelectedRecord.num.toLong(), NoSelectedRecord.num.toLong())

    fun Pair<SelectedNumber, SelectedNumber>.setVisibility(
        dId: SelectedNumber,
        aId: SelectedNumber
    ): Pair<SelectedNumber, SelectedNumber> {
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

    fun getPeriodToSync(
        currentState: Pair<Long, Long>,
        latest: Long,
        exclude: Long
    ): Pair<Long, Long> {

        val thisMoment = Instant.now()

        val latestMillis = thisMoment.minusMillis(
            latest
        ).toEpochMilli()

        val excludedMillis = thisMoment.minusMillis(
            exclude
        ).toEpochMilli()

        return Pair(
            when {
                currentState.first == NoSelectedRecord.num.toLong() ->
                    if (latest != SyncPeriods.LAST_DAY.latestMillis)
                        thisMoment.minusMillis(SyncPeriods.LAST_HOUR.latestMillis).toEpochMilli()
                    else latestMillis
                currentState.first > latestMillis && currentState.first < excludedMillis ->
                    currentState.first
                currentState.first > excludedMillis ->
                    thisMoment.minusMillis(SyncPeriods.LAST_HOUR.latestMillis).toEpochMilli()
                else -> latestMillis
            },

            when {
                currentState.second == NoSelectedRecord.num.toLong() ->
                    if (exclude != SyncPeriods.LAST_DAY.excludedMillis)
                        thisMoment.minusMillis(SyncPeriods.LAST_HOUR.excludedMillis).toEpochMilli()
                    else excludedMillis
                currentState.first < excludedMillis && currentState.second > excludedMillis ->
                    excludedMillis
                currentState.second > excludedMillis ->
                    thisMoment.minusMillis(SyncPeriods.LAST_HOUR.excludedMillis).toEpochMilli()
                else -> excludedMillis
            }
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
                    (it.orderShort.order.orderNumber.toString()
                        .contains(subOrdersFilter.orderNumber)
                            ||
                            (subOrdersFilter.orderNumber == NoSelectedString.str))
        }
    }
}