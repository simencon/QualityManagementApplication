package com.simenko.qmapp.domain

import com.simenko.qmapp.room.entities.*
import com.simenko.qmapp.utils.ListTransformer
import com.simenko.qmapp.utils.ObjectTransformer

fun DatabaseOrder.toDomainOrder() =
    ObjectTransformer(DatabaseOrder::class, DomainOrder::class).transform(this)

fun DatabaseOrdersType.toDomainType() =
    ObjectTransformer(DatabaseOrdersType::class, DomainOrdersType::class).transform(this)

fun DatabaseMeasurementReason.toDomainReason() =
    ObjectTransformer(DatabaseMeasurementReason::class, DomainMeasurementReason::class).transform(
        this
    )

fun DatabaseSubOrder.toDomainSubOrder() =
    ObjectTransformer(DatabaseSubOrder::class, DomainSubOrder::class).transform(this)

fun DatabaseTeamMember.toDomainTeamMember() =
    ObjectTransformer(DatabaseTeamMember::class, DomainTeamMember::class).transform(this)

fun DatabaseOrdersStatus.toDomainStatus() =
    ObjectTransformer(DatabaseOrdersStatus::class, DomainOrdersStatus::class).transform(this)

fun DatabaseDepartment.toDomainDepartment() =
    ObjectTransformer(DatabaseDepartment::class, DomainDepartment::class).transform(this)

fun DatabaseSubDepartment.toDomainSubDepartment() =
    ObjectTransformer(DatabaseSubDepartment::class, DomainSubDepartment::class).transform(this)

fun DatabaseManufacturingChannel.toDomainChannel() = ObjectTransformer(
    DatabaseManufacturingChannel::class, DomainManufacturingChannel::class).transform(this)

fun DatabaseManufacturingLine.toDomainLine() =
    ObjectTransformer(DatabaseManufacturingLine::class, DomainManufacturingLine::class).transform(
        this
    )

fun DatabaseManufacturingOperation.toDomainOperation() = ObjectTransformer(
    DatabaseManufacturingOperation::class, DomainManufacturingOperation::class
).transform(this)

fun DatabaseSubOrderTask.toDomainSubOrderTask() =
    ObjectTransformer(DatabaseSubOrderTask::class, DomainSubOrderTask::class).transform(this)

fun DatabaseCharacteristic.toDomainCharacteristic() =
    ObjectTransformer(DatabaseCharacteristic::class, DomainCharacteristic::class).transform(this)

fun DatabaseComponent.toDomainComponent() = ObjectTransformer(
    DatabaseComponent::class, DomainComponent::class).transform(this)

fun DatabaseVersionStatus.toVersionStatus() = ObjectTransformer(
    DatabaseVersionStatus::class, DomainVersionStatus::class).transform(this)

fun DatabaseComponentVersion.toDomainComponentVersion() = ObjectTransformer(
    DatabaseComponentVersion::class, DomainComponentVersion::class).transform(this)


fun List<DatabaseOrderComplete>.asDomainOrdersComplete(parentId: Int): List<DomainOrderComplete> {

    return map {
        DomainOrderComplete(
            order = it.order.toDomainOrder(),
            orderType = it.orderType.toDomainType(),
            orderReason = it.orderReason.toDomainReason(),
            customer = it.customer.toDomainDepartment(),
            orderPlacer = it.orderPlacer.toDomainTeamMember(),
            orderStatus = it.orderStatus.toDomainStatus()
        )
    }.sortedByDescending { it.order.orderNumber }
}

fun List<DatabaseCompleteSubOrder>.asDomainSubOrderDetailed(parentId: Int): List<DomainSubOrderComplete> {
    return filter { it.subOrder.orderId == parentId || parentId == -1 }.map {
        DomainSubOrderComplete(
            subOrder = it.subOrder.toDomainSubOrder(),
            orderedBy = it.orderedBy.toDomainTeamMember(),
            completedBy = it.completedBy?.toDomainTeamMember(),
            status = it.status.toDomainStatus(),
            department = it.department.toDomainDepartment(),
            subDepartment = it.subDepartment.toDomainSubDepartment(),
            channel = it.channel.toDomainChannel(),
            line = it.line.toDomainLine(),
            operation = it.operation.toDomainOperation()
        )
    }
}

fun List<DatabaseSubOrderTaskComplete>.asDomainSubOrderTask(parentId: Int): List<DomainSubOrderTaskComplete> {
    return filter { it.subOrderTask.subOrderId == parentId || parentId == -1 }.map {
        DomainSubOrderTaskComplete(
            subOrderTask = it.subOrderTask.toDomainSubOrderTask(),
            characteristic = it.characteristic.toDomainCharacteristic(),
            status = it.status.toDomainStatus()
        )
    }
}

fun List<DatabaseDepartmentsDetailed>.asDepartmentsDetailedDomainModel(): List<DomainDepartmentComplete> {
    return map {
        DomainDepartmentComplete(
            departments = ListTransformer(
                DatabaseDepartment::class,
                DomainDepartment::class
            ).transform(it.departments),
            depManagerDetails = ListTransformer(
                it.depManagerDetails,
                DatabaseTeamMember::class,
                DomainTeamMember::class
            ).generateList(),
            companies = ListTransformer(
                it.companies,
                DatabaseCompany::class,
                DomainCompany::class
            ).generateList()
        )
    }
}

fun List<DatabaseComponentVersionDetailed>.asComponentVersionDetailedDomainModel(): List<DomainComponentVersionDetailed> {
    return map {
        DomainComponentVersionDetailed(
            componentVersion = it.componentVersion.toDomainComponentVersion(),
            component = it.component.toDomainComponent(),
            versionStatus = it.versionStatus.toVersionStatus()
        )
    }
}