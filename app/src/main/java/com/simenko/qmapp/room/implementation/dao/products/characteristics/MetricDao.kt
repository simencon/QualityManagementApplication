package com.simenko.qmapp.room.implementation.dao.products.characteristics

import androidx.room.Dao
import androidx.room.Query
import com.simenko.qmapp.room.contract.DaoBaseModel
import com.simenko.qmapp.room.entities.products.DatabaseMetrix
import kotlinx.coroutines.flow.Flow

@Dao
abstract class MetricDao: DaoBaseModel<DatabaseMetrix> {
    @Query("SELECT * FROM `8_metrixes` ORDER BY metrixOrder ASC")
    abstract override fun getRecords(): List<DatabaseMetrix>

    @Query("select * from `8_metrixes` where charId = :parentId order by metrixOrder  asc")
    abstract override fun getRecordsByParentId(parentId: Int): List<DatabaseMetrix>

    @Query("SELECT * FROM `8_metrixes` WHERE id = :id")
    abstract override fun getRecordById(id: String): DatabaseMetrix?

    @Query("SELECT * FROM `8_metrixes` ORDER BY metrixOrder ASC")
    abstract override fun getRecordsForUI(): Flow<List<DatabaseMetrix>>

    @Query("select m.* from items_tolerances as it " +
            "left join `8_metrixes` as m on it.metrixId = m.id " +
            "where " +
            "it.versionId = :versionId and " +
            "it.isActual = :actual and " +
            "charId = :charId and " +
            "substr(it.fId, 1, 1) = :prefix")
    abstract suspend fun getMetricsByPrefixVersionIdActualityCharId(prefix: String, versionId: String, actual: String, charId: String): List<DatabaseMetrix>
}