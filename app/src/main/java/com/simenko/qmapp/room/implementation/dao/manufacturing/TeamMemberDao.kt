package com.simenko.qmapp.room.implementation.dao.manufacturing

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.simenko.qmapp.room.contract.DaoBaseModel
import com.simenko.qmapp.room.entities.DatabaseTeamMember
import com.simenko.qmapp.room.entities.DatabaseTeamMemberComplete
import kotlinx.coroutines.flow.Flow

@Dao
abstract class TeamMemberDao : DaoBaseModel<DatabaseTeamMember> {
    @Query("SELECT * FROM `8_team_members` ORDER BY fullName ASC")
    abstract override fun getRecords(): List<DatabaseTeamMember>

    /**
     * as parent is used lineId but in fact should be companyId in future
     * */
    @Query("select * from `8_team_members` where departmentId = :parentId order by fullName asc")
    abstract override fun getRecordsByParentId(parentId: Int): List<DatabaseTeamMember>

    @Query("SELECT * FROM `8_team_members` WHERE id = :id")
    abstract override fun getRecordById(id: String): DatabaseTeamMember?

    @Query("SELECT * FROM `8_team_members` ORDER BY fullName ASC")
    abstract override fun getRecordsForUI(): LiveData<List<DatabaseTeamMember>>

    @Transaction
    @Query("SELECT * FROM '8_team_members' ORDER BY fullName ASC")
    abstract fun getRecordsFlowForUI(): Flow<List<DatabaseTeamMemberComplete>>
}