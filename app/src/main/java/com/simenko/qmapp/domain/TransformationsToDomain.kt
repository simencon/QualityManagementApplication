package com.simenko.qmapp.domain

import com.simenko.qmapp.retrofit.entities.NetworkSubOrderTask
import com.simenko.qmapp.room.entities.*
import com.simenko.qmapp.utils.ItemTransformer
import com.simenko.qmapp.utils.ListTransformer
import com.simenko.qmapp.utils.ItemListTransformer
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

fun DatabaseSubOrder.toDomainSubOrderTask() =
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
    DatabaseManufacturingChannel::class, DomainManufacturingChannel::class
).transform(this)

fun DatabaseManufacturingLine.toDomainLine() =
    ObjectTransformer(DatabaseManufacturingLine::class, DomainManufacturingLine::class).transform(
        this
    )

fun DatabaseManufacturingOperation.toDomainOperation() = ObjectTransformer(
    DatabaseManufacturingOperation::class, DomainManufacturingOperation::class
).transform(this)

fun DatabaseSubOrderTask.toDomainSubOrderTask() =
    ObjectTransformer(DatabaseSubOrderTask::class, DomainSubOrderTask::class).transform(this)

fun NetworkSubOrderTask.toDomainSubOrderTask() =
    ObjectTransformer(NetworkSubOrderTask::class, DomainSubOrderTask::class).transform(this)

fun DatabaseResult.toDomainResult() =
    ObjectTransformer(DatabaseResult::class, DomainResult::class).transform(this)

fun DatabaseResultsDecryption.toDomainResult() =
    ObjectTransformer(DatabaseResultsDecryption::class, DomainResultsDecryption::class).transform(this)

fun DatabaseMetrix.toDomainMetrix() =
    ObjectTransformer(DatabaseMetrix::class, DomainMetrix::class).transform(this)

fun DatabaseSample.toDomainSample() =
    ObjectTransformer(DatabaseSample::class, DomainSample::class).transform(this)

fun DatabaseCharacteristic.toDomainCharacteristic() =
    ObjectTransformer(DatabaseCharacteristic::class, DomainCharacteristic::class).transform(this)

fun DatabaseElementIshModel.toDomainCharacteristicGroup() =
    ObjectTransformer(
        DatabaseElementIshModel::class,
        DomainElementIshModel::class
    ).transform(this)

fun DatabaseIshSubCharacteristic.toDomainCharacteristicSubGroup() =
    ObjectTransformer(
        DatabaseIshSubCharacteristic::class,
        DomainIshSubCharacteristic::class
    ).transform(this)

fun DatabaseKey.toDomainKey() = ObjectTransformer(
    DatabaseKey::class, DomainKey::class
).transform(this)

fun DatabaseVersionStatus.toVersionStatus() = ObjectTransformer(
    DatabaseVersionStatus::class, DomainVersionStatus::class
).transform(this)


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
            characteristic = it.characteristic.toDomainCharacteristicComplete(),
            subOrder = it.subOrder.toDomainSubOrder(),
            status = it.status.toDomainStatus()
        )
    }
}

fun List<DatabaseResultComplete>.asDomainResults(): List<DomainResultComplete> {
    return map {
        DomainResultComplete(
            result = it.result.toDomainResult(),
            resultsDecryption = it.resultsDecryption.toDomainResult(),
            metrix = it.metrix.toDomainMetrix(),
            subOrderTask = DomainSubOrderTaskComplete(
                subOrderTask = it.subOrderTask.subOrderTask.toDomainSubOrderTask(),
                characteristic = it.subOrderTask.characteristic.toDomainCharacteristicComplete(),
                subOrder = it.subOrderTask.subOrder.toDomainSubOrder(),
                status = it.subOrderTask.status.toDomainStatus()
            )
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

fun DatabaseProductVersion.toDomainItemVersion() = ItemTransformer(
    DatabaseProductVersion::class, DomainItemVersion::class
).transform(this)

fun DatabaseComponentVersion.toDomainItemVersion() = ItemTransformer(
    DatabaseComponentVersion::class, DomainItemVersion::class
).transform(this)

fun DatabaseComponentInStageVersion.toDomainItemVersion() = ItemTransformer(
    DatabaseComponentInStageVersion::class, DomainItemVersion::class
).transform(this)

fun DatabaseProduct.toDomainItem() = ItemTransformer(
    DatabaseProduct::class, DomainItem::class
).transform(this)

fun DatabaseComponent.toDomainItem() = ItemTransformer(
    DatabaseComponent::class, DomainItem::class
).transform(this)

fun DatabaseComponentInStage.toDomainItem() = ItemTransformer(
    DatabaseComponentInStage::class, DomainItem::class
).transform(this)

fun DatabaseProductComplete.toDomainItemComplete() = DomainItemComplete(
    item = this.product.toDomainItem(),
    key = this.key.toDomainKey(),
    itemToLines = ItemListTransformer(
        this.productToLines,
        DatabaseProductToLine::class,
        DomainItemToLine::class
    ).generateList()
)

fun DatabaseComponentComplete.toDomainItemComplete() = DomainItemComplete(
    item = this.component.toDomainItem(),
    key = this.key.toDomainKey(),
    itemToLines = ItemListTransformer(
        this.componentToLines,
        DatabaseComponentToLine::class,
        DomainItemToLine::class
    ).generateList()
)

fun DatabaseComponentInStageComplete.toDomainItemComplete() = DomainItemComplete(
    item = this.componentInStage.toDomainItem(),
    key = this.key.toDomainKey(),
    itemToLines = ItemListTransformer(
        this.componentInStageToLines,
        DatabaseComponentInStageToLine::class,
        DomainItemToLine::class
    ).generateList()
)

fun List<DatabaseProductVersionComplete>.asDomainItemFromProduct(): List<DomainItemVersionComplete> {
    return map {
        DomainItemVersionComplete(
            itemPrefix = ItemType.PRODUCT,
            itemVersion = it.productVersion.toDomainItemVersion(),
            versionStatus = it.versionStatus.toVersionStatus(),
            itemComplete = it.productComplete.toDomainItemComplete()
        )
    }
}

fun List<DatabaseComponentVersionComplete>.asDomainItemFromComponent(): List<DomainItemVersionComplete> {
    return map {
        DomainItemVersionComplete(
            itemPrefix = ItemType.COMPONENT,
            itemVersion = it.componentVersion.toDomainItemVersion(),
            versionStatus = it.versionStatus.toVersionStatus(),
            itemComplete = it.componentComplete.toDomainItemComplete()
        )
    }
}

fun List<DatabaseComponentInStageVersionComplete>.asDomainItemFromStage(): List<DomainItemVersionComplete> {
    return map {
        DomainItemVersionComplete(
            itemPrefix = ItemType.COMPONENT_IN_STAGE,
            itemVersion = it.componentInStageVersion.toDomainItemVersion(),
            versionStatus = it.versionStatus.toVersionStatus(),
            itemComplete = it.componentInStageComplete.toDomainItemComplete()
        )
    }
}

fun List<DatabaseSubOrderWithChildren>.toDomainSubOrderWithChildren(): List<DomainSubOrderWithChildren> {
    return map {
        DomainSubOrderWithChildren(
            subOrder = it.subOrder.toDomainSubOrder(),
            samples = mutableListOf<DomainSample>().apply {
                addAll(
                    ListTransformer(
                        it.samples,
                        DatabaseSample::class,
                        DomainSample::class
                    ).generateList()
                )
            },
            subOrderTasks = mutableListOf<DomainSubOrderTask>().apply {
                addAll(
                    ListTransformer(
                        it.subOrderTasks,
                        DatabaseSubOrderTask::class,
                        DomainSubOrderTask::class
                    ).generateList()
                )
            }
        )
    }
}

fun DatabaseCharacteristicComplete.toDomainCharacteristicComplete(): DomainCharacteristicComplete {
    return DomainCharacteristicComplete(
        characteristic = characteristic.toDomainCharacteristic(),
        characteristicGroup = characteristicGroup.toDomainCharacteristicGroup(),
        characteristicSubGroup = characteristicSubGroup.toDomainCharacteristicSubGroup()
    )
}