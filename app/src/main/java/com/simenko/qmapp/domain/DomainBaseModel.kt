package com.simenko.qmapp.domain

import androidx.compose.runtime.Stable

@Stable
abstract class DomainBaseModel<out T> {
    @Stable
    abstract fun getRecordId(): Any

    @Stable
    abstract fun getParentId(): Int

    @Stable
    open fun hasParentId(pId: Int): Boolean = false

    @Stable
    abstract fun setIsSelected(value: Boolean)

    @Stable
    open fun changeCheckedState(): Boolean = false

    @Stable
    open fun getName(): String = "will be returned any string"

    @Stable
    abstract fun toDatabaseModel(): T
}

@JvmInline
value class SelectedNumber(val num: Int)

val NoRecord = SelectedNumber(-1)
val ZeroValue = SelectedNumber(0)
val ProcessControlOrderTypeId = SelectedNumber(3)

@JvmInline
value class SelectedString(val str: String)

val NoString = SelectedString("-")
val EmptyString = SelectedString("")
val FalseStr = SelectedString("false")
val TrueStr = SelectedString("true")
val NoRecordStr = SelectedString("-1")

val ToProcessControlScreen = SelectedString("investigationsKey")

val CurrentOrderIdKey = SelectedString("currentOrderId")
val CurrentSubOrderIdKey = SelectedString("currentSubOrderId")

val SubOrderAddEditModeKey = SelectedString("subOrderAddEditModeKey")
