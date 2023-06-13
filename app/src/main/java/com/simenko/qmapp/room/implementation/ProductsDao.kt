package com.simenko.qmapp.room.implementation

import androidx.lifecycle.LiveData
import androidx.room.*
import com.simenko.qmapp.room.entities.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertElementIshModelsAll(list: List<DatabaseElementIshModel>)

    @Query("SELECT * FROM `10_1_d_element_ish_model` ORDER BY id ASC")
    fun getElementIshModels(): LiveData<List<DatabaseElementIshModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertIshSubCharacteristicsAll(list: List<DatabaseIshSubCharacteristic>)

    @Query("SELECT * FROM `0_ish_sub_characteristics` ORDER BY id ASC")
    fun getIshSubCharacteristics(): LiveData<List<DatabaseIshSubCharacteristic>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertManufacturingProjectsAll(list: List<DatabaseManufacturingProject>)

    @Query("SELECT * FROM `0_manufacturing_project` ORDER BY id ASC")
    fun geManufacturingProjects(): LiveData<List<DatabaseManufacturingProject>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCharacteristicsAll(list: List<DatabaseCharacteristic>)

    @Query("SELECT * FROM `7_characteristics` ORDER BY charOrder ASC")
    fun getCharacteristics(): LiveData<List<DatabaseCharacteristic>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMetrixesAll(list: List<DatabaseMetrix>)

    @Query("SELECT * FROM `8_metrixes` ORDER BY metrixOrder ASC")
    fun getMetrixes(): LiveData<List<DatabaseMetrix>>

    @Query("SELECT * FROM `8_metrixes` ORDER BY metrixOrder ASC")
    fun getMetrixesFlow(): Flow<List<DatabaseMetrix>>

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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertKeysAll(list: List<DatabaseKey>)

    @Query("SELECT * FROM `0_keys` ORDER BY id ASC")
    fun getKeys(): LiveData<List<DatabaseKey>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertProductBasesAll(list: List<DatabaseProductBase>)

    @Query("SELECT * FROM `0_products_bases` ORDER BY id ASC")
    fun getProductBases(): LiveData<List<DatabaseProductBase>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertProductsAll(list: List<DatabaseProduct>)

    @Query("SELECT * FROM `2_products` ORDER BY id ASC")
    fun getProducts(): LiveData<List<DatabaseProduct>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertComponentsAll(list: List<DatabaseComponent>)

    @Query("SELECT * FROM `4_components` ORDER BY id ASC")
    fun getComponents(): LiveData<List<DatabaseComponent>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertComponentInStagesAll(list: List<DatabaseComponentInStage>)

    @Query("SELECT * FROM `6_components_in_stages` ORDER BY id ASC")
    fun getComponentInStages(): LiveData<List<DatabaseComponentInStage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertVersionStatusesAll(list: List<DatabaseVersionStatus>)

    @Query("SELECT * FROM `0_versions_status` ORDER BY id ASC")
    fun getVersionStatuses(): LiveData<List<DatabaseVersionStatus>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertProductVersionsAll(list: List<DatabaseProductVersion>)

    @Query("SELECT * FROM `9_products_versions` ORDER BY id ASC")
    fun getProductVersions(): LiveData<List<DatabaseProductVersion>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertComponentVersionsAll(list: List<DatabaseComponentVersion>)

    @Query("SELECT * FROM `10_components_versions` ORDER BY id ASC")
    fun getComponentVersions(): LiveData<List<DatabaseComponentVersion>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertComponentInStageVersionsAll(list: List<DatabaseComponentInStageVersion>)

    @Query("SELECT * FROM `11_component_in_stage_versions` ORDER BY id ASC")
    fun getComponentInStageVersions(): LiveData<List<DatabaseComponentInStageVersion>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertProductTolerancesAll(list: List<DatabaseProductTolerance>)

    @Query("SELECT * FROM `9_8_product_tolerances` ORDER BY id ASC")
    fun getProductTolerances(): LiveData<List<DatabaseProductTolerance>>

    @Query("SELECT * FROM `9_8_product_tolerances` ORDER BY id ASC")
    fun getProductTolerancesFlow(): Flow<List<DatabaseProductTolerance>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertComponentTolerancesAll(list: List<DatabaseComponentTolerance>)

    @Query("SELECT * FROM `10_8_component_tolerances` ORDER BY id ASC")
    fun getComponentTolerances(): LiveData<List<DatabaseComponentTolerance>>

    @Query("SELECT * FROM `10_8_component_tolerances` ORDER BY id ASC")
    fun getComponentTolerancesFlow(): Flow<List<DatabaseComponentTolerance>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertComponentInStageTolerancesAll(list: List<DatabaseComponentInStageTolerance>)

    @Query("SELECT * FROM `11_8_component_in_stage_tolerances` ORDER BY id ASC")
    fun getComponentInStageTolerances(): LiveData<List<DatabaseComponentInStageTolerance>>

    @Query("SELECT * FROM `11_8_component_in_stage_tolerances` ORDER BY id ASC")
    fun getComponentInStageTolerancesFlow(): Flow<List<DatabaseComponentInStageTolerance>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertProductsToLinesAll(list: List<DatabaseProductToLine>)

    @Query("SELECT * FROM `13_1_products_to_lines` ORDER BY id ASC")
    fun getProductsToLines(): LiveData<List<DatabaseProductToLine>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertComponentsToLinesAll(list: List<DatabaseComponentToLine>)

    @Query("SELECT * FROM `13_3_components_to_lines` ORDER BY id ASC")
    fun getComponentsToLines(): LiveData<List<DatabaseComponentToLine>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertComponentInStagesToLinesAll(list: List<DatabaseComponentInStageToLine>)

    @Query("SELECT * FROM `13_5_component_in_stages_to_lines` ORDER BY id ASC")
    fun getComponentInStagesToLines(): LiveData<List<DatabaseComponentInStageToLine>>

    @Transaction
    @Query("SELECT * FROM item_versions_complete")
    fun getItemVersionsComplete(): LiveData<List<DatabaseItemVersionComplete>>

    @Transaction
    @Query("SELECT * FROM characteristic_complete")
    fun getCharacteristicsComplete(): LiveData<List<DatabaseCharacteristicComplete>>

    @Transaction
    @Query("SELECT * FROM items_tolerances")
    fun getItemsTolerances(): LiveData<List<DatabaseItemTolerance>>
}