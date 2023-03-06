package com.simenko.qmapp.domain

abstract class DomainModel {
    abstract fun getRecordId(): Int
    abstract fun getParentOneId(): Int
    open fun getSecondParentId() = 0
    abstract fun setIsChecked(value: Boolean)
}