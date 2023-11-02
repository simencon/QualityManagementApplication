package com.simenko.qmapp.room.implementation

import androidx.room.*
import com.simenko.qmapp.room.entities.*
import com.simenko.qmapp.room.implementation.dao.Converters
import com.simenko.qmapp.room.implementation.dao.investigaions.*
import com.simenko.qmapp.room.implementation.dao.manufacturing.ChannelDao
import com.simenko.qmapp.room.implementation.dao.manufacturing.CompanyDao
import com.simenko.qmapp.room.implementation.dao.manufacturing.DepartmentDao
import com.simenko.qmapp.room.implementation.dao.manufacturing.JobRoleDao
import com.simenko.qmapp.room.implementation.dao.manufacturing.LineDao
import com.simenko.qmapp.room.implementation.dao.manufacturing.OperationDao
import com.simenko.qmapp.room.implementation.dao.manufacturing.OperationsFlowDao
import com.simenko.qmapp.room.implementation.dao.manufacturing.SubDepartmentDao
import com.simenko.qmapp.room.implementation.dao.manufacturing.EmployeeDao
import com.simenko.qmapp.room.implementation.dao.products.CharacteristicDao
import com.simenko.qmapp.room.implementation.dao.products.CharacteristicGroupDao
import com.simenko.qmapp.room.implementation.dao.products.CharacteristicSubGroupDao
import com.simenko.qmapp.room.implementation.dao.products.ComponentDao
import com.simenko.qmapp.room.implementation.dao.products.ComponentStageDao
import com.simenko.qmapp.room.implementation.dao.products.ComponentStageToLineDao
import com.simenko.qmapp.room.implementation.dao.products.ComponentStageToleranceDao
import com.simenko.qmapp.room.implementation.dao.products.ComponentStageVersionDao
import com.simenko.qmapp.room.implementation.dao.products.ComponentToLineDao
import com.simenko.qmapp.room.implementation.dao.products.ComponentToleranceDao
import com.simenko.qmapp.room.implementation.dao.products.ComponentVersionDao
import com.simenko.qmapp.room.implementation.dao.products.ManufacturingProjectDao
import com.simenko.qmapp.room.implementation.dao.products.MetricDao
import com.simenko.qmapp.room.implementation.dao.products.ProductBaseDao
import com.simenko.qmapp.room.implementation.dao.products.ProductDao
import com.simenko.qmapp.room.implementation.dao.products.ProductKeyDao
import com.simenko.qmapp.room.implementation.dao.products.ProductToLineDao
import com.simenko.qmapp.room.implementation.dao.products.ProductToleranceDao
import com.simenko.qmapp.room.implementation.dao.products.ProductVersionDao
import com.simenko.qmapp.room.implementation.dao.products.VersionStatusDao
import com.simenko.qmapp.room.implementation.dao.system.UserDao
import com.simenko.qmapp.room.implementation.dao.system.UserRoleDao

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

    abstract val characteristicGroupDao: CharacteristicGroupDao
    abstract val characteristicSubGroupDao: CharacteristicSubGroupDao
    abstract val manufacturingProjectDao: ManufacturingProjectDao
    abstract val characteristicDao: CharacteristicDao
    abstract val metricDao: MetricDao
    abstract val productKeyDao: ProductKeyDao
    abstract val productBaseDao: ProductBaseDao
    abstract val productDao: ProductDao
    abstract val componentDao: ComponentDao
    abstract val componentStageDao: ComponentStageDao
    abstract val versionStatusDao: VersionStatusDao
    abstract val productVersionDao: ProductVersionDao
    abstract val componentVersionDao: ComponentVersionDao
    abstract val componentStageVersionDao: ComponentStageVersionDao
    abstract val productToleranceDao: ProductToleranceDao
    abstract val componentToleranceDao: ComponentToleranceDao
    abstract val componentStageToleranceDao: ComponentStageToleranceDao
    abstract val productToLineDao: ProductToLineDao
    abstract val componentToLineDao: ComponentToLineDao
    abstract val componentStageToLineDao: ComponentStageToLineDao

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