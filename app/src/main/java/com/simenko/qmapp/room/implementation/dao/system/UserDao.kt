package com.simenko.qmapp.room.implementation.dao.system

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.simenko.qmapp.room.contract.DaoBaseModel
import com.simenko.qmapp.room.entities.DatabaseUser
import kotlinx.coroutines.flow.Flow

@Dao
abstract class UserDao: DaoBaseModel<DatabaseUser> {
    @Query("SELECT * FROM users ORDER BY email ASC")
    abstract override fun getRecords(): List<DatabaseUser>

    @Query("select * from users where companyId = :parentId order by email asc")
    abstract override fun getRecordsByParentId(parentId: Int): List<DatabaseUser>

    @Query("SELECT * FROM users WHERE email = :id")
    abstract override fun getRecordById(id: String): DatabaseUser?

    @Query("SELECT * FROM users ORDER BY email ASC")
    abstract override fun getRecordsForUI(): LiveData<List<DatabaseUser>>

    @Query("SELECT * FROM users ORDER BY email ASC")
    abstract fun getRecordsFlowForUI(): Flow<List<DatabaseUser>>
}