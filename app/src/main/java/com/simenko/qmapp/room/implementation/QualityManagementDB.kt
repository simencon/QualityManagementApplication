package com.simenko.qmapp.room.implementation

import androidx.room.*
import com.simenko.qmapp.room.entities.*
import com.simenko.qmapp.room.implementation.dao.InvestigationsDao
import com.simenko.qmapp.room.implementation.dao.ManufacturingDao
import com.simenko.qmapp.room.implementation.dao.ProductsDao
import com.simenko.qmapp.room.implementation.dao.investigaions.*

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
        DatabaseReason::class,
        DatabaseOrdersType::class,
        DatabaseOrder::class,
        DatabaseSubOrder::class,
        DatabaseSubOrderTask::class,
        DatabaseSample::class,
        DatabaseResultsDecryption::class,
        DatabaseResult::class
    ],
    views = [
        DatabaseItem::class,
        DatabaseItemToLine::class,
        DatabaseItemComplete::class,
        DatabaseItemVersion::class,
        DatabaseItemVersionComplete::class,

        DatabaseItemTolerance::class,

        DatabaseCharacteristicComplete::class,

        DatabaseSubOrderTaskComplete::class,
        DatabaseResultComplete::class,

        DatabaseResultTolerance::class,

        DatabaseOrderResult::class,
        DatabaseSubOrderResult::class,
        DatabaseTaskResult::class,
        DatabaseSampleResult::class,

        DatabaseOrderShort::class,
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class QualityManagementDB : RoomDatabase() {
    abstract val manufacturingDao: ManufacturingDao
    abstract val productsDao: ProductsDao
    abstract val investigationsDao: InvestigationsDao

    abstract val inputForOrderDao: InputForOrderDao
    abstract val orderStatusDao: OrderStatusDao
    abstract val measurementReasonDao: MeasurementReasonDao
    abstract val investigationTypeDao: InvestigationTypeDao
    abstract val resultDecryptionDao: ResultDecryptionDao
}