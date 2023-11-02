package com.simenko.qmapp.room.implementation.dao.system

import androidx.room.Dao
import androidx.room.Query
import com.simenko.qmapp.room.contract.DaoBaseModel
import com.simenko.qmapp.room.entities.DatabaseUserRole
import kotlinx.coroutines.flow.Flow

@Dao
abstract class UserRoleDao: DaoBaseModel<DatabaseUserRole> {
    @Query("SELECT * FROM user_roles ORDER BY function ASC")
    abstract override fun getRecords(): List<DatabaseUserRole>

    @Query("select * from user_roles where function != :parentId order by function asc")
    abstract override fun getRecordsByParentId(parentId: Int): List<DatabaseUserRole>

    @Query("SELECT * FROM user_roles WHERE (function || ':' || roleLevel || ':' || accessLevel) = :id")
    abstract override fun getRecordById(id: String): DatabaseUserRole?

    @Query("SELECT * FROM user_roles ORDER BY function ASC")
    abstract override fun getRecordsForUI(): Flow<List<DatabaseUserRole>>
}