package com.simenko.qmapp.room.implementation.dao.system

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
    abstract override fun getRecordsForUI(): Flow<List<DatabaseUser>>

    @Query(
        """
            SELECT * FROM users
            WHERE ((:newUsers = 1 AND ((restApiUrl is NULL) OR (restApiUrl = ''))) OR (:newUsers = 0 AND ((restApiUrl is not NULL) OR (restApiUrl != ''))))
            AND (:fullName = '' or fullName like :fullName)
            ORDER BY email ASC
        """
    )
    abstract fun getRecordsFlowForUI(newUsers: Boolean, fullName: String): Flow<List<DatabaseUser>>
}