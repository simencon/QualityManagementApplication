package com.simenko.qmapp.room.implementation.dao.manufacturing

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.simenko.qmapp.room.contract.DaoBaseModel
import com.simenko.qmapp.room.entities.DatabaseCompany
import kotlinx.coroutines.flow.Flow

@Dao
abstract class CompanyDao: DaoBaseModel<DatabaseCompany> {
    @Query("SELECT * FROM `0_companies` ORDER BY companyOrder ASC")
    abstract override fun getRecords(): List<DatabaseCompany>

    @Query("select * from `0_companies` where companyManagerId = :parentId order by companyOrder asc")
    abstract override fun getRecordsByParentId(parentId: Int): List<DatabaseCompany>

    @Query("SELECT * FROM `0_companies` WHERE id = :id")
    abstract override fun getRecordById(id: String): DatabaseCompany?

    @Query("SELECT * FROM `0_companies` ORDER BY companyOrder ASC")
    abstract override fun getRecordsForUI(): LiveData<List<DatabaseCompany>>

    @Query("SELECT * FROM `0_companies` ORDER BY companyOrder ASC")
    abstract fun getRecordsFlowForUI(): Flow<List<DatabaseCompany>>
}