package com.simenko.qmapp.room.implementation.dao.products.specification

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.room.contract.DaoBaseModel
import com.simenko.qmapp.room.entities.products.DatabaseComponentKind
import kotlinx.coroutines.flow.Flow

@Dao
abstract class ComponentKindDao : DaoBaseModel<ID, ID, DatabaseComponentKind> {
    @Query("SELECT * FROM `3_component_kinds` ORDER BY id ASC")
    abstract override fun getRecords(): List<DatabaseComponentKind>

    @Query("SELECT * FROM `3_component_kinds` ORDER BY id ASC")
    abstract override fun getRecordsForUI(): Flow<List<DatabaseComponentKind>>

    @Query("select * from `3_component_kinds` where productKindId = :parentId order by id  asc")
    abstract fun getRecordsByParentId(parentId: ID): List<DatabaseComponentKind>

    @Query("SELECT * FROM `3_component_kinds` WHERE id = :id")
    abstract fun getRecordById(id: ID): DatabaseComponentKind?

    @Transaction
    @Query("SELECT * FROM `component_kinds_complete` WHERE id = :id")
    abstract fun getRecordCompleteById(id: ID): DatabaseComponentKind.DatabaseComponentKindComplete?
    @Transaction
    @Query("select * from component_kinds_complete where productKindId = :pId")
    abstract fun getRecordsCompleteForUI(pId: ID): Flow<List<DatabaseComponentKind.DatabaseComponentKindComplete>>

    @Transaction
    @Query(
        """select ck.*, pl.projectSubject, pl_to_dep.depID
            from `3_component_kinds` ck 
                join `1_product_kinds` pk on ck.productKindID = pk.ID
                join `0_manufacturing_project` pl on pk.projectID = pl.ID 
                join `10_0_prod_projects_to_departments` pl_to_dep on pl.ID = pl_to_dep.productLineId
                where pl_to_dep.depId = :depId
        """
    )
    abstract fun getRecordsByDepartmentId(depId: ID): Flow<List<DatabaseComponentKind.DatabaseComponentKindComplete>>
}