package com.simenko.qmapp.room.implementation.dao.manufacturing

import androidx.room.Dao
import androidx.room.Query
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.room.contract.DaoBaseModel
import com.simenko.qmapp.room.entities.DatabaseCompany
import kotlinx.coroutines.flow.Flow

@Dao
abstract class CompanyDao: DaoBaseModel<DatabaseCompany> {
    @Query("SELECT * FROM `0_companies` ORDER BY companyOrder ASC")
    abstract override fun getRecords(): List<DatabaseCompany>

    @Query("select * from `0_companies` where companyManagerId = :parentId order by companyOrder asc")
    abstract override fun getRecordsByParentId(parentId: ID): List<DatabaseCompany>

    @Query("SELECT * FROM `0_companies` WHERE id = :id")
    abstract override fun getRecordById(id: String): DatabaseCompany?

    @Query("SELECT * FROM `0_companies` ORDER BY companyOrder ASC")
    abstract override fun getRecordsForUI(): Flow<List<DatabaseCompany>>

    @Query("SELECT * FROM `0_companies` WHERE companyName = :name")
    abstract fun getRecordByName(name: String): DatabaseCompany?
}