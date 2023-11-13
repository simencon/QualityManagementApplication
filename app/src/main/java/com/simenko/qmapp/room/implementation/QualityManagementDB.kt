package com.simenko.qmapp.room.implementation

import androidx.room.*
import com.simenko.qmapp.room.entities.*
import com.simenko.qmapp.room.entities.products.*
import com.simenko.qmapp.room.implementation.dao.Converters
import com.simenko.qmapp.room.implementation.dao.investigaions.*
import com.simenko.qmapp.room.implementation.dao.manufacturing.*
import com.simenko.qmapp.room.implementation.dao.products.*
import com.simenko.qmapp.room.implementation.dao.products.characteristics.*
import com.simenko.qmapp.room.implementation.dao.products.manufacturing.*
import com.simenko.qmapp.room.implementation.dao.products.specification.*
import com.simenko.qmapp.room.implementation.dao.system.*

@Database(
    entities = [
        DatabaseUserRole::class,
        DatabaseUser::class,

        DatabaseEmployee::class,
        DatabaseCompany::class,
        DatabaseJobRole::class,
        DatabaseDepartment::class,
        DatabaseSubDepartment::class,
        DatabaseManufacturingChannel::class,
        DatabaseManufacturingLine::class,
        DatabaseManufacturingOperation::class,
        DatabaseOperationsFlow::class,

        DatabaseProductLine::class,
        DatabaseKey::class,
        DatabaseProductBase::class,
        DatabaseCharGroup::class,
        DatabaseCharSubGroup::class,
        DatabaseCharacteristic::class,
        DatabaseMetrix::class,
        DatabaseVersionStatus::class,

        DatabaseProductKind::class,
        DatabaseComponentKind::class,
        DatabaseComponentStageKind::class,

        DatabaseProductKindKey::class,
        DatabaseComponentKindKey::class,
        DatabaseComponentStageKindKey::class,

        DatabaseCharacteristicProductKind::class,
        DatabaseCharacteristicComponentKind::class,
        DatabaseCharacteristicComponentStageKind::class,

        DatabaseProduct::class,
        DatabaseComponent::class,
        DatabaseComponentInStage::class,

        DatabaseProductToLine::class,
        DatabaseComponentToLine::class,
        DatabaseComponentInStageToLine::class,

        DatabaseProductKindProduct::class,
        DatabaseComponentKindComponent::class,
        DatabaseComponentStageKindComponentStage::class,

        DatabaseProductComponent::class,
        DatabaseComponentComponentStage::class,

        DatabaseProductVersion::class,
        DatabaseComponentVersion::class,
        DatabaseComponentInStageVersion::class,

        DatabaseProductTolerance::class,
        DatabaseComponentTolerance::class,
        DatabaseComponentInStageTolerance::class,

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
        DatabaseSubDepartment.DatabaseSubDepartmentWithParents::class,
        DatabaseManufacturingChannel.DatabaseManufacturingChannelWithParents::class,
        DatabaseManufacturingLine.DatabaseManufacturingLineWithParents::class,
        DatabaseOperationsFlow.DatabaseOperationsFlowComplete::class,

        DatabaseItem::class,
        DatabaseItemToLine::class,
        DatabaseItemComplete::class,
        DatabaseItemVersion::class,
        DatabaseItemVersionComplete::class,

        DatabaseItemTolerance::class,

        DatabaseKey.DatabaseKeyComplete::class,
        DatabaseProductLine.DatabaseProductLineComplete::class,

        DatabaseCharGroup.DatabaseCharGroupComplete::class,
        DatabaseCharSubGroup.DatabaseCharSubGroupComplete::class,
        DatabaseCharacteristic.DatabaseCharacteristicComplete::class,

        DatabaseProductKind.DatabaseProductKindComplete::class,
        DatabaseProductKindKey.DatabaseProductKindKeyComplete::class,

        DatabaseComponentKind.DatabaseComponentKindComplete::class,
        DatabaseComponentKindKey.DatabaseComponentKindKeyComplete::class,

        DatabaseComponentStageKind.DatabaseComponentStageKindComplete::class,
        DatabaseComponentStageKindKey.DatabaseComponentStageKindKeyComplete::class,

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
    abstract val userRoleDao: UserRoleDao
    abstract val userDao: UserDao

    abstract val employeeDao: EmployeeDao
    abstract val companyDao: CompanyDao
    abstract val jobRoleDao: JobRoleDao
    abstract val departmentDao: DepartmentDao
    abstract val subDepartmentDao: SubDepartmentDao
    abstract val channelDao: ChannelDao
    abstract val lineDao: LineDao
    abstract val operationDao: OperationDao
    abstract val operationsFlowDao: OperationsFlowDao

    abstract val manufacturingProjectDao: ManufacturingProjectDao
    abstract val productKeyDao: ProductKeyDao
    abstract val productBaseDao: ProductBaseDao
    abstract val characteristicGroupDao: CharacteristicGroupDao
    abstract val characteristicSubGroupDao: CharacteristicSubGroupDao
    abstract val characteristicDao: CharacteristicDao
    abstract val metricDao: MetricDao
    abstract val versionStatusDao: VersionStatusDao

    abstract val productKindDao: ProductKindDao
    abstract val componentKindDao: ComponentKindDao
    abstract val componentStageKindDao: ComponentStageKindDao

    abstract val productKindKeyDao: ProductKindKeyDao
    abstract val componentKindKeyDao: ComponentKindKeyDao
    abstract val componentStageKindKeyDao: ComponentStageKindKeyDao

    abstract val characteristicProductKindDao: CharacteristicProductKindDao
    abstract val characteristicComponentKindDao: CharacteristicComponentKindDao
    abstract val characteristicComponentStageKindDao: CharacteristicComponentStageKindDao


    abstract val productDao: ProductDao
    abstract val componentDao: ComponentDao
    abstract val componentStageDao: ComponentStageDao

    abstract val productToLineDao: ProductToLineDao
    abstract val componentToLineDao: ComponentToLineDao
    abstract val componentStageToLineDao: ComponentStageToLineDao

    abstract val productKindProductDao: ProductKindProductDao
    abstract val componentKindComponentDao: ComponentKindComponentDao
    abstract val componentStageKindComponentStageDao: ComponentStageKindComponentStageDao

    abstract val productComponentDao: ProductComponentDao
    abstract val componentComponentStageDao: ComponentComponentStageDao

    abstract val productVersionDao: ProductVersionDao
    abstract val componentVersionDao: ComponentVersionDao
    abstract val componentStageVersionDao: ComponentStageVersionDao

    abstract val productToleranceDao: ProductToleranceDao
    abstract val componentToleranceDao: ComponentToleranceDao
    abstract val componentStageToleranceDao: ComponentStageToleranceDao


    abstract val inputForOrderDao: InputForOrderDao
    abstract val orderStatusDao: OrderStatusDao
    abstract val measurementReasonDao: MeasurementReasonDao
    abstract val investigationTypeDao: InvestigationTypeDao
    abstract val resultDecryptionDao: ResultDecryptionDao

    abstract val resultDao: ResultDao
    abstract val sampleDao: SampleDao
    abstract val taskDao: TaskDao
    abstract val subOrderDao: SubOrderDao
    abstract val orderDao: OrderDao
}