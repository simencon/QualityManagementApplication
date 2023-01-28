package com.simenko.qmapp.room_implementation

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*
import com.simenko.qmapp.room_entities.DatabaseCompanies
import com.simenko.qmapp.room_entities.DatabaseDepartment
import com.simenko.qmapp.room_entities.DatabaseDepartmentsDetailed
import com.simenko.qmapp.room_entities.DatabaseTeamMember

@Dao
interface QualityManagementDao {
    @Transaction
    @Query(
        "SELECT dp.* FROM '8_team_members' AS tm " +
                "JOIN '10_departments' AS dp ON tm.id = dp.depManager " +
                "ORDER BY dp.depOrder ASC"
    )
    fun getDepartmentsDetailed(): LiveData<List<DatabaseDepartmentsDetailed>>

    @Query("SELECT * FROM '10_departments' ORDER BY depOrder ASC")
    fun getDepartments(): LiveData<List<DatabaseDepartment>>

    @Query("SELECT * FROM `8_team_members` ORDER BY id ASC")
    fun getTeamMembers(): LiveData<List<DatabaseTeamMember>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDepartmentsAll(department: List<DatabaseDepartment>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTeamMembersAll(teamMember: List<DatabaseTeamMember>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCompaniesAll(company: List<DatabaseCompanies>)
}

@Database(
    entities = [
        DatabaseDepartment::class,
        DatabaseTeamMember::class,
        DatabaseCompanies::class
    ],
    version = 1
)
abstract class QualityManagementDB : RoomDatabase() {
    abstract val qualityManagementDao: QualityManagementDao
}

private lateinit var INSTANCE: QualityManagementDB

fun getDatabase(context: Context): QualityManagementDB {
    synchronized(QualityManagementDB::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(
                context.applicationContext,
                QualityManagementDB::class.java,
                "QualityManagementDB"
            ).build()
        }
    }
    return INSTANCE
}