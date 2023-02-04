package com.simenko.qmapp.room_implementation

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*
import com.simenko.qmapp.room_entities.*

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
}

@Dao
interface QualityManagementProductsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertElementIshModelsAll(company: List<DatabaseElementIshModel>)

    @Query("SELECT * FROM `10_1_d_element_ish_model` ORDER BY id ASC")
    fun getElementIshModels(): LiveData<List<DatabaseElementIshModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertIshSubCharacteristicsAll(company: List<DatabaseIshSubCharacteristic>)

    @Query("SELECT * FROM `0_ish_sub_characteristics` ORDER BY id ASC")
    fun getIshSubCharacteristics(): LiveData<List<DatabaseIshSubCharacteristic>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertManufacturingProjectsAll(company: List<DatabaseManufacturingProject>)

    @Query("SELECT * FROM `0_manufacturing_project` ORDER BY id ASC")
    fun geManufacturingProjects(): LiveData<List<DatabaseManufacturingProject>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCharacteristicsAll(company: List<DatabaseCharacteristic>)

    @Query("SELECT * FROM `7_characteristics` ORDER BY charOrder ASC")
    fun getCharacteristics(): LiveData<List<DatabaseCharacteristic>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMetrixesAll(company: List<DatabaseMetrix>)

    @Query("SELECT * FROM `8_metrixes` ORDER BY metrixOrder ASC")
    fun getMetrixes(): LiveData<List<DatabaseMetrix>>
}

@Dao
interface QualityManagementInvestigationsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertInputForOrderAll(company: List<DatabaseInputForOrder>)

    @Query("SELECT * FROM `1_1_inputForMeasurementRegister` ORDER BY recordId ASC")
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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrdersAll(company: List<DatabaseOrder>)

    @Query("SELECT * FROM `12_orders` ORDER BY orderNumber ASC")
    fun getOrders(): LiveData<List<DatabaseOrder>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSubOrdersAll(company: List<DatabaseSubOrder>)

    @Query("SELECT * FROM `13_sub_orders` ORDER BY subOrderNumber ASC")
    fun getSubOrders(): LiveData<List<DatabaseSubOrder>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSubOrderTasksAll(company: List<DatabaseSubOrderTask>)

    @Query("SELECT * FROM `13_7_sub_order_tasks` ORDER BY charId ASC")
    fun getSubOrderTasks(): LiveData<List<DatabaseSubOrderTask>>

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
    @Query(
        "SELECT orders.* " +
                "FROM '12_orders' AS orders " +
                "INNER JOIN '13_sub_orders' AS sub_orders " +
                "ON orders.ID = sub_orders.orderID;"
    )
    fun getOrdersDetailed(): LiveData<List<DatabaseCompleteOrder>>
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

        DatabaseElementIshModel::class,
        DatabaseIshSubCharacteristic::class,
        DatabaseManufacturingProject::class,
        DatabaseCharacteristic::class,
        DatabaseMetrix::class,

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
    version = 1
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