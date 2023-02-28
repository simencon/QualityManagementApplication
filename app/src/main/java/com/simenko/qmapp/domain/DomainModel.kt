package com.simenko.qmapp.domain

abstract class DomainModel {
    abstract fun getRecordId(): Int
    abstract fun setIsChecked(value: Boolean)
}