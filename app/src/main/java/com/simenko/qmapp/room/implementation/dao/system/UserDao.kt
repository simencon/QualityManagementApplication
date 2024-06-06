package com.simenko.qmapp.room.implementation.dao.system

import androidx.room.Dao
import androidx.room.Query
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.room.contract.DaoBaseModel
import com.simenko.qmapp.room.entities.DatabaseUser
import kotlinx.coroutines.flow.Flow

@Dao
abstract class UserDao : DaoBaseModel<String, ID, DatabaseUser> {
    @Query("SELECT * FROM users ORDER BY email ASC")
    abstract override fun getRecords(): List<DatabaseUser>

    @Query("SELECT * FROM users ORDER BY email ASC")
    abstract override fun getRecordsForUI(): Flow<List<DatabaseUser>>

    @Query("select * from users where companyId = :parentId order by email asc")
    abstract fun getRecordsByParentId(parentId: ID): List<DatabaseUser>

    @Query("SELECT * FROM users WHERE email = :id")
    abstract fun getRecordById(id: String): DatabaseUser?

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