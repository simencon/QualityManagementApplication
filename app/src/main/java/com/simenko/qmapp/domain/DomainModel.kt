package com.simenko.qmapp.domain

abstract class DomainModel {
    abstract fun getRecordId(): Any
    abstract fun getParentOneId(): Int
    open fun hasParentOneId(pId: Int): Boolean = false
    abstract fun setIsChecked(value: Boolean)
    open fun changeCheckedState(): Boolean = false
    open fun getName(): String = "will be returned any string"
}