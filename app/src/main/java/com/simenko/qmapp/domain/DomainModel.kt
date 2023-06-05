package com.simenko.qmapp.domain

import androidx.compose.runtime.Stable

@Stable
abstract class DomainModel {
    @Stable
    abstract fun getRecordId(): Any
    @Stable
    abstract fun getParentOneId(): Int
    @Stable
    open fun hasParentOneId(pId: Int): Boolean = false
    @Stable
    abstract fun setIsChecked(value: Boolean)
    @Stable
    open fun changeCheckedState(): Boolean = false
    @Stable
    open fun getName(): String = "will be returned any string"
}

@JvmInline
value class SelectedNumber(val num: Int)
val NoRecord = SelectedNumber(-1)
val OrderTypeProcessOnly = SelectedNumber(3)

@JvmInline
value class SelectedString(val str: String)
val NoSelectedString = SelectedString("-")