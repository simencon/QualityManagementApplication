package com.simenko.qmapp.utils

import com.simenko.qmapp.domain.*
import com.simenko.qmapp.domain.entities.DomainOrderComplete
import com.simenko.qmapp.retrofit.entities.NetworkOrder
import com.simenko.qmapp.works.SyncPeriods
import java.time.Instant

open class BaseFilter constructor(
    open val typeId: Int? = null,
    open val statusId: Int? = null,
    open val stringToSearch: String? = null,
    open val newUsers: Boolean? = null
)

data class EmployeesFilter(
    override val stringToSearch: String = EmptyString.str,
) : BaseFilter()

data class UsersFilter(
    override val stringToSearch: String = EmptyString.str,
    override val newUsers: Boolean = false
) : BaseFilter()

data class OrdersFilter(
    override val typeId: Int = NoRecord.num,
    override val statusId: Int = NoRecord.num,
    override val stringToSearch: String = EmptyString.str
) : BaseFilter()

data class SubOrdersFilter(
    override val typeId: Int = NoRecord.num,
    override val statusId: Int = NoRecord.num,
    override val stringToSearch: String = EmptyString.str
) : BaseFilter()

data class NotificationData(
    val orderId: Int = NoRecord.num,
    val subOrderId: Int = NoRecord.num,
    val orderNumber: Int? = null,
    val subOrderStatus: String? = null,
    val departmentAbbr: String? = null,
    val channelAbbr: String? = null,
    val itemTypeCompleteDesignation: String = NoString.str,
    val notificationReason: NotificationReasons = NotificationReasons.DEFAULT
)

enum class NotificationReasons(val reason: String) {
    CREATED("New investigation: "),
    DELETED("Deleted: "),
    CHANGED("Changed: "),
    DEFAULT("no reason")
}

enum class InvStatuses(val statusId: Int) {
    TO_DO(1),
    IN_PROGRESS(2),
    DONE(3),
    REJECTED(4)
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
            Pair(NoRecord.num.toLong(), NoRecord.num.toLong())

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
            Pair(NoRecord.num.toLong(), NoRecord.num.toLong())

    fun Pair<SelectedNumber, SelectedNumber>.setVisibility(
        dId: SelectedNumber,
        aId: SelectedNumber
    ): Pair<SelectedNumber, SelectedNumber> =
        if (dId != NoRecord)
            Pair(
                if (this.first != dId) dId else NoRecord,
                this.second
            )
        else
            Pair(
                this.first,
                if (this.second != aId) aId else NoRecord
            )

    fun Pair<SelectedString, SelectedString>.setVisibility(
        dId: SelectedString,
        aId: SelectedString
    ): Pair<SelectedString, SelectedString> =
        if (dId != NoRecordStr)
            Pair(
                if (this.first != dId) dId else NoRecordStr,
                this.second
            )
        else
            Pair(
                this.first,
                if (this.second != aId) aId else NoRecordStr
            )


    fun getPeriodToSync(currentState: Pair<Long, Long>, latest: Long, exclude: Long): Pair<Long, Long> {
        val thisMoment = Instant.now()
        val latestMillis = thisMoment.minusMillis(latest).toEpochMilli()
        val excludedMillis = thisMoment.minusMillis(exclude).toEpochMilli()

        val specificPair = Pair(
            when {
                currentState.first == NoRecord.num.toLong() -> //when local is empty
                    if (latest != SyncPeriods.LAST_DAY.latestMillis) thisMoment.minusMillis(SyncPeriods.LAST_HOUR.latestMillis).toEpochMilli() else latestMillis

                currentState.first in (latestMillis + 1) until excludedMillis -> //when local start is within latest and exclude
                    if (latest == SyncPeriods.LAST_HOUR.latestMillis || latest == SyncPeriods.LAST_DAY.latestMillis) latestMillis else currentState.first

                currentState.first > excludedMillis -> //when local is over exclude
                    when (latest) {
                        SyncPeriods.LAST_HOUR.latestMillis -> thisMoment.minusMillis(SyncPeriods.LAST_HOUR.latestMillis).toEpochMilli()
                        SyncPeriods.LAST_DAY.latestMillis -> thisMoment.minusMillis(SyncPeriods.LAST_DAY.latestMillis).toEpochMilli()
                        else -> thisMoment.minusMillis(SyncPeriods.LAST_HOUR.latestMillis).toEpochMilli()
                    }

                else -> latestMillis
            },
            when {
                currentState.second == NoRecord.num.toLong() -> //when local is empty
                    if (exclude != SyncPeriods.LAST_DAY.excludeMillis) thisMoment.minusMillis(SyncPeriods.LAST_HOUR.excludeMillis).toEpochMilli() else excludedMillis

                currentState.first < excludedMillis && currentState.second > excludedMillis -> //when local is within latest and exclude
                    excludedMillis

                currentState.second > excludedMillis ->
                    when (exclude) {
                        SyncPeriods.LAST_HOUR.excludeMillis -> thisMoment.minusMillis(SyncPeriods.LAST_HOUR.excludeMillis).toEpochMilli()
                        SyncPeriods.LAST_DAY.excludeMillis -> thisMoment.minusMillis(SyncPeriods.LAST_DAY.excludeMillis).toEpochMilli()
                        else -> thisMoment.minusMillis(SyncPeriods.LAST_HOUR.excludeMillis).toEpochMilli()
                    }

                else -> excludedMillis
            }
        )

        val infPair = Pair(
            when {
                currentState.first == NoRecord.num.toLong() -> thisMoment.minusMillis(SyncPeriods.LAST_HOUR.latestMillis).toEpochMilli()
                currentState.first > thisMoment.minusMillis(SyncPeriods.LAST_YEAR.latestMillis).toEpochMilli() -> thisMoment.minusMillis(SyncPeriods.LAST_HOUR.latestMillis).toEpochMilli()
                else -> currentState.first
            },
            when {
                currentState.second == NoRecord.num.toLong() -> thisMoment.minusMillis(SyncPeriods.LAST_HOUR.excludeMillis).toEpochMilli()
                currentState.first > thisMoment.minusMillis(SyncPeriods.LAST_YEAR.latestMillis).toEpochMilli() -> thisMoment.minusMillis(SyncPeriods.LAST_HOUR.excludeMillis).toEpochMilli()
                else -> excludedMillis
            }
        )

        return if (exclude == SyncPeriods.COMPLETE_PERIOD.excludeMillis) infPair else specificPair
    }
}