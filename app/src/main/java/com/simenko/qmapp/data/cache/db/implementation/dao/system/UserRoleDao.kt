package com.simenko.qmapp.data.cache.db.implementation.dao.system

import androidx.room.Dao
import androidx.room.Query
import com.simenko.qmapp.data.cache.db.contract.DaoBaseModel
import com.simenko.qmapp.data.cache.db.entities.DatabaseUserRole
import kotlinx.coroutines.flow.Flow

@Dao
abstract class UserRoleDao : DaoBaseModel<String, String, DatabaseUserRole> {
    @Query("SELECT * FROM user_roles ORDER BY function ASC")
    abstract override fun getRecords(): List<DatabaseUserRole>

    @Query("SELECT * FROM user_roles ORDER BY function ASC")
    abstract override fun getRecordsForUI(): Flow<List<DatabaseUserRole>>

    @Query("select * from user_roles where function != :parentId order by function asc")
    abstract fun getRecordsByParentId(parentId: String): List<DatabaseUserRole>

    @Query("SELECT * FROM user_roles WHERE (function || ':' || roleLevel || ':' || accessLevel) = :id")
    abstract fun getRecordById(id: String): DatabaseUserRole?
}