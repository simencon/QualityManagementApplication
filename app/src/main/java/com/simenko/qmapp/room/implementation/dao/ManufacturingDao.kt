package com.simenko.qmapp.room.implementation.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.simenko.qmapp.room.entities.*

@Dao
interface ManufacturingDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPositionLevelsAll(teamMember: List<DatabasePositionLevel>)

    @Query("SELECT * FROM `0_position_levels` ORDER BY id ASC")
    fun getPositionLevels(): LiveData<List<DatabasePositionLevel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCompaniesAll(company: List<DatabaseCompany>)

    @Query("SELECT * FROM `0_companies` ORDER BY id ASC")
    fun getCompanies(): LiveData<List<DatabaseCompany>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSubDepartmentsAll(department: List<DatabaseSubDepartment>)

    @Query("SELECT * FROM `11_sub_departments` ORDER BY subDepOrder ASC")
    fun getSubDepartments(): LiveData<List<DatabaseSubDepartment>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertManufacturingChannelsAll(department: List<DatabaseManufacturingChannel>)

    @Query("SELECT * FROM `12_manufacturing_channels` ORDER BY channelOrder ASC")
    fun getManufacturingChannels(): LiveData<List<DatabaseManufacturingChannel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertManufacturingLinesAll(department: List<DatabaseManufacturingLine>)

    @Query("SELECT * FROM `13_manufacturing_lines` ORDER BY lineOrder ASC")
    fun getManufacturingLines(): LiveData<List<DatabaseManufacturingLine>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertManufacturingOperationsAll(department: List<DatabaseManufacturingOperation>)

    @Query("SELECT * FROM `14_manufacturing_operations` ORDER BY operationOrder ASC")
    fun getManufacturingOperations(): LiveData<List<DatabaseManufacturingOperation>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOperationsFlowsAll(department: List<DatabaseOperationsFlow>)

    @Query("SELECT * FROM `14_14_manufacturing_operations_flow` ORDER BY currentOperationId ASC")
    fun getOperationsFlows(): LiveData<List<DatabaseOperationsFlow>>
}