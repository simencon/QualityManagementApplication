package com.simenko.qmapp.domain

import com.simenko.qmapp.room.entities.*
import com.simenko.qmapp.utils.ListTransformer
import com.simenko.qmapp.utils.ObjectTransformer

fun DatabaseOrder.toDomainOrder() =
    ObjectTransformer(DatabaseOrder::class, DomainOrder::class).transform(this)

fun DatabaseOrderResult.toDomainOrderResult() =
    ObjectTransformer(DatabaseOrderResult::class, DomainOrderResult::class).transform(this)

fun DatabaseOrdersType.toDomainType() =
    ObjectTransformer(DatabaseOrdersType::class, DomainOrdersType::class).transform(this)

fun DatabaseReason.toDomainReason() =
    ObjectTransformer(DatabaseReason::class, DomainReason::class).transform(this)

fun DatabaseSubOrder.toDomainSubOrder() =
    ObjectTransformer(DatabaseSubOrder::class, DomainSubOrder::class).transform(this)

fun DatabaseSubOrderTask.toDomainTask() =
    ObjectTransformer(DatabaseSubOrderTask::class, DomainSubOrderTask::class).transform(this)

fun DatabaseSubOrderResult.toDomainSubOrderResult() =
    ObjectTransformer(DatabaseSubOrderResult::class, DomainSubOrderResult::class).transform(this)

fun DatabaseSubOrder.toDomainSubOrderTask() =
    ObjectTransformer(DatabaseSubOrder::class, DomainSubOrder::class).transform(this)

fun DatabaseTeamMember.toDomainTeamMember() =
    ObjectTransformer(DatabaseTeamMember::class, DomainTeamMember::class).transform(this)

fun DatabaseOrdersStatus.toDomainStatus() =
    ObjectTransformer(DatabaseOrdersStatus::class, DomainOrdersStatus::class).transform(this)

fun DatabaseCompany.toDomainCompany() =
    ObjectTransformer(DatabaseCompany::class, DomainCompany::class).transform(this)

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

fun DatabaseResult.toDomainResult() =
    ObjectTransformer(DatabaseResult::class, DomainResult::class).transform(this)

fun DatabaseResultsDecryption.toDomainResult() =
    ObjectTransformer(DatabaseResultsDecryption::class, DomainResultsDecryption::class).transform(
        this
    )

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
    ObjectTransformer(
        DatabaseIshSubCharacteristic::class,
        DomainIshSubCharacteristic::class
    ).transform(this)

fun DatabaseKey.toDomainKey() =
    ObjectTransformer(DatabaseKey::class, DomainKey::class).transform(this)

fun DatabaseVersionStatus.toVersionStatus() =
    ObjectTransformer(DatabaseVersionStatus::class, DomainVersionStatus::class).transform(this)


fun List<DatabaseOrderComplete>.asDomainOrdersComplete(): List<DomainOrderComplete> {
    return map {
        DomainOrderComplete(
            order = it.order.toDomainOrder(),
            orderType = it.orderType.toDomainType(),
            orderReason = it.orderReason.toDomainReason(),
            customer = it.customer.toDomainDepartment(),
            orderPlacer = it.orderPlacer.toDomainTeamMember(),
            orderStatus = it.orderStatus.toDomainStatus(),
            orderResult = it.orderResult.toDomainOrderResult(),
            detailsVisibility = false
        )
    }.sortedByDescending { it.order.createdDate }
}

fun List<DatabaseSubOrderComplete>.asDomainSubOrderDetailed(): List<DomainSubOrderComplete> {
    return map {
        DomainSubOrderComplete(
            subOrder = it.subOrder.toDomainSubOrder(),
            orderShort = DomainOrderShort(
                order = it.orderShort.order.toDomainOrder(),
                orderType = it.orderShort.orderType.toDomainType(),
                orderReason = it.orderShort.orderReason.toDomainReason()
            ),
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
            subOrderResult = it.subOrderResult.toDomainSubOrderResult(),
            detailsVisibility = false
        )
    }.sortedByDescending { it.orderShort.order.createdDate }
}

fun List<DatabaseSubOrderTaskComplete>.asDomainSubOrderTask(): List<DomainSubOrderTaskComplete> {
    return map {
        DomainSubOrderTaskComplete(
            subOrderTask = it.subOrderTask.toDomainSubOrderTask(),
            characteristic = it.characteristic.toDomainCharacteristicComplete(),
            subOrder = it.subOrder.toDomainSubOrder(),
            status = it.status.toDomainStatus(),
            taskResult = it.taskResult.toDomainTaskResult(),
            detailsVisibility = false
        )
    }.sortedBy { it.characteristic.characteristic.charOrder }
}

fun List<DatabaseSampleComplete>.asDomainSamples(): List<DomainSampleComplete> {
    return map {
        DomainSampleComplete(
            sampleResult = it.sampleResult.toDomainSampleResult(),
            sample = it.sample.toDomainSample(),
            detailsVisibility = false
        )
    }.sortedBy { it.sample.sampleNumber }
}

fun List<DatabaseResultComplete>.asDomainResults(): List<DomainResultComplete> {
    return map {
        DomainResultComplete(
            result = it.result.toDomainResult(),
            resultsDecryption = it.resultsDecryption.toDomainResult(),
            metrix = it.metrix.toDomainMetrix(),
            resultTolerance = it.resultTolerance.toDomainResultTolerance(),
            detailsVisibility = false
        )
    }.sortedBy { it.metrix.metrixOrder }
}

fun List<DatabaseTeamMemberComplete>.asTeamCompleteDomainModel(): List<DomainTeamMemberComplete> {
    return map {
        DomainTeamMemberComplete(
            teamMember = it.teamMember.toDomainTeamMember(),
            department = it.department.let { dep ->
                dep?.toDomainDepartment()
                    ?: DomainDepartment(
                        id = 0,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null
                    )

            },
            company = it.company.let { comp ->
                comp?.toDomainCompany()
                    ?: DomainCompany(
                        id = 0,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        0,
                        null,
                        0
                    )
            }
        )
    }
}

fun List<DatabaseDepartmentsComplete>.asDepartmentsDetailedDomainModel(): List<DomainDepartmentComplete> {
    return map {
        DomainDepartmentComplete(
            department = ObjectTransformer(
                DatabaseDepartment::class,
                DomainDepartment::class
            ).transform(it.department),
            depManager = ObjectTransformer(
                DatabaseTeamMember::class,
                DomainTeamMember::class
            ).transform(it.depManager),
            company = ObjectTransformer(
                DatabaseCompany::class,
                DomainCompany::class
            ).transform(it.company)
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

fun List<DatabaseSubOrderShort>.toDomainSubOrderShort(): List<DomainSubOrderShort> {
    return map {
        DomainSubOrderShort(
            subOrder = it.subOrder.toDomainSubOrder(),
            order = it.order.toDomainOrder(),
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