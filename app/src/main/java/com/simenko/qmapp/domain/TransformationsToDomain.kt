package com.simenko.qmapp.domain

import com.simenko.qmapp.retrofit.entities.NetworkSubOrderTask
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

fun DatabaseSubOrderResult.toDomainSubOrderResult() =
    ObjectTransformer(DatabaseSubOrderResult::class, DomainSubOrderResult::class).transform(this)

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
    DatabaseManufacturingChannel::class, DomainManufacturingChannel::class).transform(this)

fun DatabaseManufacturingLine.toDomainLine() =
    ObjectTransformer(DatabaseManufacturingLine::class, DomainManufacturingLine::class).transform(this)

fun DatabaseManufacturingOperation.toDomainOperation() = ObjectTransformer(
    DatabaseManufacturingOperation::class, DomainManufacturingOperation::class).transform(this)

fun DatabaseSubOrderTask.toDomainSubOrderTask() =
    ObjectTransformer(DatabaseSubOrderTask::class, DomainSubOrderTask::class).transform(this)

fun NetworkSubOrderTask.toDomainSubOrderTask() =
    ObjectTransformer(NetworkSubOrderTask::class, DomainSubOrderTask::class).transform(this)

fun DatabaseResult.toDomainResult() =
    ObjectTransformer(DatabaseResult::class, DomainResult::class).transform(this)

fun DatabaseResultsDecryption.toDomainResult() =
    ObjectTransformer(DatabaseResultsDecryption::class, DomainResultsDecryption::class).transform(this)

fun DatabaseTaskResult.toDomainTaskResult() =
    ObjectTransformer(DatabaseTaskResult::class, DomainTaskResult::class).transform(this)

fun DatabaseMetrix.toDomainMetrix() =
    ObjectTransformer(DatabaseMetrix::class, DomainMetrix::class).transform(this)

fun DatabaseResultTolerance.toDomainResultTolerance() =
    ObjectTransformer(DatabaseResultTolerance::class, DomainResultTolerance::class).transform(this)

fun DatabaseSample.toDomainSample() =
    ObjectTransformer(DatabaseSample::class, DomainSample::class).transform(this)

fun DatabaseSampleResult.toDomainSampleResult() =
    ObjectTransformer(DatabaseSampleResult::class, DomainSampleResult::class).transform(this)

fun DatabaseCharacteristic.toDomainCharacteristic() =
    ObjectTransformer(DatabaseCharacteristic::class, DomainCharacteristic::class).transform(this)

fun DatabaseElementIshModel.toDomainCharacteristicGroup() =
    ObjectTransformer(DatabaseElementIshModel::class, DomainElementIshModel::class).transform(this)

fun DatabaseIshSubCharacteristic.toDomainCharacteristicSubGroup() =
    ObjectTransformer(DatabaseIshSubCharacteristic::class, DomainIshSubCharacteristic::class).transform(this)

fun DatabaseKey.toDomainKey() =
    ObjectTransformer(DatabaseKey::class, DomainKey::class).transform(this)

fun DatabaseVersionStatus.toVersionStatus() =
    ObjectTransformer(DatabaseVersionStatus::class, DomainVersionStatus::class).transform(this)


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

fun List<DatabaseSubOrderComplete>.asDomainSubOrderDetailed(parentId: Int): List<DomainSubOrderComplete> {
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
            operation = it.operation.toDomainOperation(),
            itemVersionComplete = DomainItemVersionComplete(
                itemVersion = it.itemVersionComplete.itemVersion.toDomainItemVersion(),
                versionStatus = it.itemVersionComplete.versionStatus.toVersionStatus(),
                itemComplete = it.itemVersionComplete.itemComplete.toDomainItemComplete()
            ),
            subOrderResult = it.subOrderResult.toDomainSubOrderResult()
        )
    }
}

fun List<DatabaseSubOrderTaskComplete>.asDomainSubOrderTask(parentId: Int): List<DomainSubOrderTaskComplete> {
    return filter { it.subOrderTask.subOrderId == parentId || parentId == -1 }.map {
        DomainSubOrderTaskComplete(
            subOrderTask = it.subOrderTask.toDomainSubOrderTask(),
            characteristic = it.characteristic.toDomainCharacteristicComplete(),
            subOrder = it.subOrder.toDomainSubOrder(),
            status = it.status.toDomainStatus(),
            taskResult = it.taskResult.toDomainTaskResult()
        )
    }
}

fun List<DatabaseSampleComplete>.asDomainSamples(): List<DomainSampleComplete> {
    return map {
        DomainSampleComplete(
            sampleResult = it.sampleResult.toDomainSampleResult(),
            sample = it.sample.toDomainSample()
        )
    }
}

fun List<DatabaseResultComplete>.asDomainResults(): List<DomainResultComplete> {
    return map {
        DomainResultComplete(
            result = it.result.toDomainResult(),
            resultsDecryption = it.resultsDecryption.toDomainResult(),
            metrix = it.metrix.toDomainMetrix(),
            resultTolerance = it.resultTolerance.toDomainResultTolerance()
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

fun DatabaseItemVersion.toDomainItemVersion() = ObjectTransformer(
    DatabaseItemVersion::class, DomainItemVersion::class
).transform(this)


fun DatabaseItem.toDomainItem() = ObjectTransformer(
    DatabaseItem::class, DomainItem::class
).transform(this)

fun DatabaseItemComplete.toDomainItemComplete() = DomainItemComplete(
    item = this.item.toDomainItem(),
    key = this.key.toDomainKey(),
    itemToLines = ListTransformer(
        this.itemToLines,
        DatabaseItemToLine::class,
        DomainItemToLine::class
    ).generateList()
)

fun List<DatabaseItemVersionComplete>.asDomainItem(): List<DomainItemVersionComplete> {
    return map {
        DomainItemVersionComplete(
            itemVersion = it.itemVersion.toDomainItemVersion(),
            versionStatus = it.versionStatus.toVersionStatus(),
            itemComplete = it.itemComplete.toDomainItemComplete()
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