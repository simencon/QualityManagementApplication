package com.simenko.qmapp.domain

import androidx.compose.runtime.Stable

typealias ID = Long

@Stable
abstract class DomainBaseModel<out T> {
    open var detailsVisibility: Boolean = false
    open var isExpanded: Boolean = false
    @Stable
    abstract fun getRecordId(): Any

    @Stable
    abstract fun getParentId(): ID

    @Stable
    open fun getParentIdStr(): String = NoRecordStr.str

    @Stable
    open fun hasParentId(pId: ID): Boolean = false

    @Stable
    abstract fun setIsSelected(value: Boolean)

    @Stable
    open fun getName(): String = "will be returned any string"

    @Stable
    abstract fun toDatabaseModel(): T
}

sealed class FillInState
data object FillInInitialState : FillInState()
data object FillInSuccessState : FillInState()
data class FillInErrorState(val errorMsg: String) : FillInState()

@JvmInline
value class SelectedNumber(val num: Long)

val NoRecord = SelectedNumber(-1)
val ZeroValue = SelectedNumber(0)
val StatusDoneId = SelectedNumber(3)
val ProcessControlOrderTypeId = SelectedNumber(3)

val FirstTabId = NoRecord
val SecondTabId = SelectedNumber(1)
val ThirdTabId = SelectedNumber(2)
val FourthTabId = SelectedNumber(3)

@JvmInline
value class SelectedString(val str: String)

val NoString = SelectedString("-")
val EmptyString = SelectedString("")
val FalseStr = SelectedString("false")
val TrueStr = SelectedString("true")
val NoRecordStr = SelectedString("-1")

@JvmInline
value class SelectedChar(val char: Char)

val ProductPref = SelectedChar('p')
val ComponentPref = SelectedChar('c')
val ComponentStagePref = SelectedChar('s')

@JvmInline
value class DefinedFloat(val float: Float)

val ZeroFloat = DefinedFloat(0.0f)

@JvmInline
value class DefinedDouble(val double: Double)

val ZeroDouble = DefinedDouble(0.0)


