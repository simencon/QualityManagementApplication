package com.simenko.qmapp.data.cache.db.implementation.dao.products.characteristics

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.simenko.qmapp.data.cache.db.contract.DaoBaseModel
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.data.cache.db.entities.products.DatabaseCharacteristic
import kotlinx.coroutines.flow.Flow

@Dao
abstract class CharacteristicDao : DaoBaseModel<ID, ID, DatabaseCharacteristic> {
    @Query("SELECT * FROM `7_characteristics` ORDER BY charOrder ASC")
    abstract override fun getRecords(): List<DatabaseCharacteristic>

    @Query("SELECT * FROM `7_characteristics` ORDER BY charOrder ASC")
    abstract override fun getRecordsForUI(): Flow<List<DatabaseCharacteristic>>

    @Query("select * from `7_characteristics` where ishSubCharId = :parentId order by charOrder  asc")
    abstract fun getRecordsByParentId(parentId: ID): List<DatabaseCharacteristic>

    @Query("SELECT * FROM `7_characteristics` WHERE id = :id")
    abstract fun getRecordById(id: ID): DatabaseCharacteristic?

    @Transaction
    @Query("select * from characteristic_complete where ishSubCharId = :parentId ")
    abstract fun getRecordsCompleteForUI(parentId: ID): Flow<List<DatabaseCharacteristic.DatabaseCharacteristicComplete>>

    @Transaction
    @Query("SELECT * FROM `7_characteristics` WHERE id = :id")
    abstract fun getRecordCompleteById(id: ID): DatabaseCharacteristic.DatabaseCharacteristicComplete

    @Query("select * from characteristicWithParents where productLineId = :parentId ")
    abstract fun getAllCharacteristicsPerProductLine(parentId: ID): Flow<List<DatabaseCharacteristic.DatabaseCharacteristicWithParents>>

    @Transaction
    @Query(
        """select ch.*
            from `characteristicWithParents` ch
                join `8_metrixes` m on ch.charId = m.charID
                join `9_8_product_tolerances` pt on m.ID = pt.metrixID
                join `9_products_versions` pv on pt.versionID = pv.ID
                join `13_1_products_to_lines` p_l on pv.productID = p_l.productID
            where p_l.lineId = :lineId
    """
    )
    abstract fun getAllProductCharsByLineId(lineId: ID): Flow<List<DatabaseCharacteristic.DatabaseCharacteristicWithParents>>

    @Transaction
    @Query(
        """select ch.*
            from `characteristicWithParents` ch
                join `8_metrixes` m on ch.charId = m.charID
                join `10_8_component_tolerances` pt on m.ID = pt.metrixID
                join `10_components_versions` pv on pt.versionID = pv.ID
                join `13_3_components_to_lines` p_l on pv.componentId = p_l.componentId
            where p_l.lineId = :lineId
    """
    )
    abstract fun getAllComponentCharsByLineId(lineId: ID): Flow<List<DatabaseCharacteristic.DatabaseCharacteristicWithParents>>

    @Transaction
    @Query(
        """select ch.*
            from `characteristicWithParents` ch
                join `8_metrixes` m on ch.charId = m.charID
                join `11_8_component_in_stage_tolerances` pt on m.ID = pt.metrixID
                join `11_component_in_stage_versions` pv on pt.versionID = pv.ID
                join `13_5_component_in_stages_to_lines` p_l on pv.componentInStageId = p_l.componentInStageId
            where p_l.lineId = :lineId
    """
    )
    abstract fun getAllStageCharsByLineId(lineId: ID): Flow<List<DatabaseCharacteristic.DatabaseCharacteristicWithParents>>
}