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

data class NotificationData(
    val orderId: Int = NoSelectedRecord.num,
    val subOrderId: Int = NoSelectedRecord.num,
    val departmentAbbr: String? = null,
    val channelAbbr: String? = null,
    val itemTypeCompleteDesignation: String = NoSelectedString.str,
    val notificationReason: NotificationReasons = NotificationReasons.DEFAULT
)

enum class NotificationReasons (val reason: String) {
    CREATED("created new one"),
    DELETED("deleted"),
    CHANGED("changed"),
    DEFAULT("no reason")
}

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
    ):
            Pair<SelectedNumber, SelectedNumber> {
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

    fun getPeriodToSync(currentState: Pair<Long, Long>, latest: Long, exclude: Long):
            Pair<Long, Long> {

        val thisMoment = Instant.now()

        val latestMillis = thisMoment.minusMillis(
            latest
        ).toEpochMilli()

        val excludedMillis = thisMoment.minusMillis(
            exclude
        ).toEpochMilli()

        val specificPair = Pair(
            when {
                currentState.first == NoSelectedRecord.num.toLong() -> //when local is empty
                    if (latest != SyncPeriods.LAST_DAY.latestMillis)
                        thisMoment.minusMillis(SyncPeriods.LAST_HOUR.latestMillis).toEpochMilli()
                    else latestMillis
                currentState.first > latestMillis && currentState.first < excludedMillis -> //when local start is within latest and exclude
                    if (latest == SyncPeriods.LAST_HOUR.latestMillis || latest == SyncPeriods.LAST_DAY.latestMillis)
                        latestMillis
                    else
                        currentState.first
                currentState.first > excludedMillis -> //when local is over exclude
                    if (latest == SyncPeriods.LAST_HOUR.latestMillis)
                        thisMoment.minusMillis(SyncPeriods.LAST_HOUR.latestMillis).toEpochMilli()
                    else if (latest == SyncPeriods.LAST_DAY.latestMillis)
                        thisMoment.minusMillis(SyncPeriods.LAST_DAY.latestMillis).toEpochMilli()
                    else
                        thisMoment.minusMillis(SyncPeriods.LAST_HOUR.latestMillis).toEpochMilli()
                else -> latestMillis
            },
            when {
                currentState.second == NoSelectedRecord.num.toLong() -> //when local is empty
                    if (exclude != SyncPeriods.LAST_DAY.excludeMillis)
                        thisMoment.minusMillis(SyncPeriods.LAST_HOUR.excludeMillis).toEpochMilli()
                    else excludedMillis
                currentState.first < excludedMillis && currentState.second > excludedMillis -> //when local is within latest and exclude
                    excludedMillis
                currentState.second > excludedMillis ->
                    if (exclude == SyncPeriods.LAST_HOUR.excludeMillis)
                        thisMoment.minusMillis(SyncPeriods.LAST_HOUR.excludeMillis).toEpochMilli()
                    else if (exclude == SyncPeriods.LAST_DAY.excludeMillis)
                        thisMoment.minusMillis(SyncPeriods.LAST_DAY.excludeMillis).toEpochMilli()
                    else
                        thisMoment.minusMillis(SyncPeriods.LAST_HOUR.excludeMillis).toEpochMilli()
                else -> excludedMillis
            }
        )

        val infPair = Pair(
            when {
                currentState.first == NoSelectedRecord.num.toLong() ->
                    thisMoment.minusMillis(SyncPeriods.LAST_HOUR.latestMillis).toEpochMilli()
                currentState.first > thisMoment.minusMillis(SyncPeriods.LAST_YEAR.latestMillis)
                    .toEpochMilli() ->
                    thisMoment.minusMillis(SyncPeriods.LAST_HOUR.latestMillis).toEpochMilli()
                else ->
                    currentState.first
            },
            when {
                currentState.second == NoSelectedRecord.num.toLong() ->
                    thisMoment.minusMillis(SyncPeriods.LAST_HOUR.excludeMillis).toEpochMilli()
                currentState.first > thisMoment.minusMillis(SyncPeriods.LAST_YEAR.latestMillis)
                    .toEpochMilli() ->
                    thisMoment.minusMillis(SyncPeriods.LAST_HOUR.excludeMillis).toEpochMilli()
                else ->
                    excludedMillis
            }
        )

        return if (exclude == SyncPeriods.COMPLETE_PERIOD.excludeMillis)
            infPair
        else
            specificPair
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