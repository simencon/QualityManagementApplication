package com.simenko.qmapp.room.implementation.dao

import androidx.room.*
import com.simenko.qmapp.room.entities.*

@Dao
interface ProductsDao {
    @Query("select m.* from items_tolerances as it " +
            "left join `8_metrixes` as m on it.metrixId = m.id " +
            "where " +
            "it.versionId = :versionId and " +
            "it.isActual = :actual and " +
            "charId = :charId and " +
            "substr(it.fId, 1, 1) = :prefix")
    suspend fun getMetricsByPrefixVersionIdActualityCharId(
        prefix: String,
        versionId: String,
        actual: String,
        charId: String
    ): List<DatabaseMetrix>
}