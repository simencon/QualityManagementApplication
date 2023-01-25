package com.simenko.qmapp.room

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*
import com.simenko.qmapp.database.DatabaseDepartment
import com.simenko.qmapp.database.DatabaseTeamMembers

@Dao
interface QualityManagementDao {
    @Query("SELECT * FROM `10_departments` ORDER BY depOrder ASC")
    fun getDepartments(): LiveData<List<DatabaseDepartment>>

    @Query("SELECT * FROM `8_team_members` ORDER BY id ASC")
    fun getTeamMembers(): LiveData<List<DatabaseTeamMembers>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDepartmentsAll(department: List<DatabaseDepartment>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTeamMembersAll(teamMember: List<DatabaseTeamMembers>)
}

@Database(
    entities = [
        DatabaseDepartment::class,
        DatabaseTeamMembers::class
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