package com.simenko.qmapp.room.implementation

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*
import com.simenko.qmapp.room.entities.*

@Dao
interface QualityManagementManufacturingDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPositionLevelsAll(teamMember: List<DatabasePositionLevel>)

    @Query("SELECT * FROM `0_position_levels` ORDER BY id ASC")
    fun getPositionLevels(): LiveData<List<DatabasePositionLevel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTeamMembersAll(teamMember: List<DatabaseTeamMember>)

    @Query("SELECT * FROM `8_team_members` ORDER BY id ASC")
    fun getTeamMembers(): LiveData<List<DatabaseTeamMember>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCompaniesAll(company: List<DatabaseCompany>)

    @Query("SELECT * FROM `0_companies` ORDER BY id ASC")
    fun getCompanies(): LiveData<List<DatabaseCompany>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDepartmentsAll(department: List<DatabaseDepartment>)

    @Query("SELECT * FROM `10_departments` ORDER BY depOrder ASC")
    fun getDepartments(): LiveData<List<DatabaseDepartment>>

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

    @Transaction
    @Query(
        "SELECT dp.* FROM '8_team_members' AS tm " +
                "JOIN '10_departments' AS dp ON tm.id = dp.depManager " +
                "ORDER BY dp.depOrder ASC"
    )
    fun getDepartmentsDetailed(): LiveData<List<DatabaseDepartmentsDetailed>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOperationsFlowsAll(department: List<DatabaseOperationsFlow>)

    @Query("SELECT * FROM `14_14_manufacturing_operations_flow` ORDER BY currentOperationId ASC")
    fun getOperationsFlows(): LiveData<List<DatabaseOperationsFlow>>
}

@Dao
interface QualityManagementProductsDao {

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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertComponentTolerancesAll(list: List<DatabaseComponentTolerance>)

    @Query("SELECT * FROM `10_8_component_tolerances` ORDER BY id ASC")
    fun getComponentTolerances(): LiveData<List<DatabaseComponentTolerance>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertComponentInStageTolerancesAll(list: List<DatabaseComponentInStageTolerance>)

    @Query("SELECT * FROM `11_8_component_in_stage_tolerances` ORDER BY id ASC")
    fun getComponentInStageTolerances(): LiveData<List<DatabaseComponentInStageTolerance>>

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

    @Query("SELECT * FROM components_complete")
    fun getComponentsComplete(): LiveData<List<DatabaseComponentComplete>>

    @Query("SELECT * FROM product_versions_complete")
    fun getProductVersionsComplete(): LiveData<List<DatabaseProductVersionComplete>>

    @Query("SELECT * FROM component_versions_complete")
    fun getComponentVersionsComplete(): LiveData<List<DatabaseComponentVersionComplete>>

    @Query("SELECT * FROM component_in_stage_versions_complete")
    fun getComponentInStageVersionsComplete(): LiveData<List<DatabaseComponentInStageVersionComplete>>
}

@Dao
interface QualityManagementInvestigationsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertInputForOrderAll(company: List<DatabaseInputForOrder>)

    @Query("SELECT * FROM `1_1_inputForMeasurementRegister` ORDER BY charOrder ASC")
    fun getInputForOrder(): LiveData<List<DatabaseInputForOrder>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrdersStatusesAll(company: List<DatabaseOrdersStatus>)

    @Query("SELECT * FROM `0_orders_statuses` ORDER BY id ASC")
    fun getOrdersStatuses(): LiveData<List<DatabaseOrdersStatus>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMeasurementReasonsAll(company: List<DatabaseMeasurementReason>)

    @Query("SELECT * FROM `0_measurement_reasons` ORDER BY reasonOrder ASC")
    fun getMeasurementReasons(): LiveData<List<DatabaseMeasurementReason>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrdersTypesAll(company: List<DatabaseOrdersType>)

    @Query("SELECT * FROM `0_orders_types` ORDER BY id ASC")
    fun getOrdersTypes(): LiveData<List<DatabaseOrdersType>>


    @Query("DELETE FROM `12_orders`")
    fun deleteOrdersAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrdersAll(company: List<DatabaseOrder>)

    @Query("SELECT * FROM `12_orders` ORDER BY orderNumber ASC")
    fun getOrders(): LiveData<List<DatabaseOrder>>

    @Query("SELECT * FROM `12_orders` ORDER BY orderNumber ASC")
    fun getOrdersByList(): List<DatabaseOrder>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrder(order: DatabaseOrder)

    @Delete
    fun deleteOrder(order: DatabaseOrder)


    @Query("DELETE FROM `13_sub_orders`")
    fun deleteSubOrdersAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSubOrdersAll(company: List<DatabaseSubOrder>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSubOrder(order: DatabaseSubOrder)

    @Delete
    fun deleteSubOrder(order: DatabaseSubOrder)

    @Query("SELECT * FROM `13_sub_orders` ORDER BY subOrderNumber ASC")
    fun getSubOrders(): LiveData<List<DatabaseSubOrder>>

    @Query("SELECT * FROM `13_sub_orders` ORDER BY subOrderNumber ASC")
    fun getSubOrdersByList(): List<DatabaseSubOrder>


    @Query("DELETE FROM `13_7_sub_order_tasks`")
    fun deleteSubOrderTasksAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSubOrderTask(record: DatabaseSubOrderTask)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSubOrderTasksAll(company: List<DatabaseSubOrderTask>)

    @Query("SELECT * FROM `13_7_sub_order_tasks` ORDER BY charId ASC")
    fun getSubOrderTasks(): LiveData<List<DatabaseSubOrderTask>>


    @Query("DELETE FROM `14_samples`")
    fun deleteSamplesAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSample(record: DatabaseSample)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSamplesAll(company: List<DatabaseSample>)

    @Query("SELECT * FROM `14_samples` ORDER BY sampleNumber ASC")
    fun getSamples(): LiveData<List<DatabaseSample>>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertResultsDecryptionsAll(company: List<DatabaseResultsDecryption>)

    @Query("SELECT * FROM `0_results_decryptions` ORDER BY id ASC")
    fun getResultsDecryptions(): LiveData<List<DatabaseResultsDecryption>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertResultsAll(company: List<DatabaseResult>)

    @Query("SELECT * FROM `14_8_results` ORDER BY id ASC")
    fun getResults(): LiveData<List<DatabaseResult>>

    @Transaction
    @Query("SELECT * FROM '12_orders' ORDER BY orderNumber;")
    fun getOrdersDetailed(): LiveData<List<DatabaseOrderComplete>>

    @Transaction
    @Query("SELECT * FROM `13_sub_orders` ORDER BY subOrderNumber;")
    fun getSubOrdersDetailed(): LiveData<List<DatabaseCompleteSubOrder>>

    @Transaction
    @Query("SELECT * FROM `13_sub_orders`")
    fun getSubOrderWithChildren(): LiveData<List<DatabaseSubOrderWithChildren>>

    @Query("SELECT * FROM `12_orders` WHERE id=:id ")
    fun getOrder(id: String): LiveData<DatabaseOrder>

    @Query("SELECT * FROM '12_orders' WHERE id = (SELECT MAX(id) FROM '12_orders')")
    fun getLatestOrder(): LiveData<DatabaseOrder>

    @Transaction
    @Query("SELECT * FROM `13_7_sub_order_tasks` AS soTasks ORDER BY id;")
    fun getSubOrderTasksDetailed(): LiveData<List<DatabaseSubOrderTaskComplete>>
}

@Database(
    entities = [
        DatabasePositionLevel::class,
        DatabaseTeamMember::class,
        DatabaseCompany::class,
        DatabaseDepartment::class,
        DatabaseSubDepartment::class,
        DatabaseManufacturingChannel::class,
        DatabaseManufacturingLine::class,
        DatabaseManufacturingOperation::class,
        DatabaseOperationsFlow::class,

        DatabaseElementIshModel::class,
        DatabaseIshSubCharacteristic::class,
        DatabaseManufacturingProject::class,
        DatabaseCharacteristic::class,
        DatabaseMetrix::class,
        DatabaseKey::class,
        DatabaseProductBase::class,
        DatabaseProduct::class,
        DatabaseComponent::class,
        DatabaseComponentInStage::class,
        DatabaseVersionStatus::class,
        DatabaseProductVersion::class,
        DatabaseComponentVersion::class,
        DatabaseComponentInStageVersion::class,
        DatabaseProductTolerance::class,
        DatabaseComponentTolerance::class,
        DatabaseComponentInStageTolerance::class,
        DatabaseProductToLine::class,
        DatabaseComponentToLine::class,
        DatabaseComponentInStageToLine::class,

        DatabaseInputForOrder::class,
        DatabaseOrdersStatus::class,
        DatabaseMeasurementReason::class,
        DatabaseOrdersType::class,
        DatabaseOrder::class,
        DatabaseSubOrder::class,
        DatabaseSubOrderTask::class,
        DatabaseSample::class,
        DatabaseResultsDecryption::class,
        DatabaseResult::class
    ],
    views = [
        DatabaseProductComplete::class,
        DatabaseProductVersionComplete::class,

        DatabaseComponentComplete::class,
        DatabaseComponentVersionComplete::class,

        DatabaseComponentInStageComplete::class,
        DatabaseComponentInStageVersionComplete::class
    ],
    version = 1,
    exportSchema = true
)
abstract class QualityManagementDB : RoomDatabase() {
    abstract val qualityManagementManufacturingDao: QualityManagementManufacturingDao
    abstract val qualityManagementProductsDao: QualityManagementProductsDao
    abstract val qualityManagementInvestigationsDao: QualityManagementInvestigationsDao
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