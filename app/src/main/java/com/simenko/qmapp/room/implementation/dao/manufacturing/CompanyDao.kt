package com.simenko.qmapp.room.implementation.dao.manufacturing

import androidx.room.Dao
import androidx.room.Query
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.room.contract.DaoBaseModel
import com.simenko.qmapp.room.entities.DatabaseCompany
import kotlinx.coroutines.flow.Flow

@Dao
abstract class CompanyDao : DaoBaseModel<ID, ID, DatabaseCompany> {
    @Query("SELECT * FROM `0_companies` ORDER BY companyOrder ASC")
    abstract override fun getRecords(): List<DatabaseCompany>

    @Query("SELECT * FROM `0_companies` ORDER BY companyOrder ASC")
    abstract override fun getRecordsForUI(): Flow<List<DatabaseCompany>>

    @Query("select * from `0_companies` where companyManagerId = :parentId order by companyOrder asc")
    abstract fun getRecordsByParentId(parentId: ID): List<DatabaseCompany>

    @Query("SELECT * FROM `0_companies` WHERE id = :id")
    abstract fun getRecordById(id: ID): DatabaseCompany?

    @Query("SELECT * FROM `0_companies` WHERE companyName = :name")
    abstract fun getRecordByName(name: String): DatabaseCompany?
}