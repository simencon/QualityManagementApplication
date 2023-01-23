package com.simenko.qmapp.room

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*
import com.simenko.qmapp.database.DatabaseDepartment

@Dao
interface DepartmentsDao {
    @Query("SELECT * FROM `10_departments` ORDER BY depOrder ASC")
    fun getDepartments(): LiveData<List<DatabaseDepartment>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDepartmentsAll(department: List<DatabaseDepartment>)
}

@Database(entities = [DatabaseDepartment::class], version = 1)
abstract class QualityManagementDB: RoomDatabase() {
    abstract val departmentDao: DepartmentsDao
}

private lateinit var INSTANCE: QualityManagementDB

fun getDatabase(context: Context): QualityManagementDB {
    synchronized(QualityManagementDB::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(context.applicationContext,
                QualityManagementDB::class.java,
                "QualityManagementDB").build()
        }
    }
    return INSTANCE
}