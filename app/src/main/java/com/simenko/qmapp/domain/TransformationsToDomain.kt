package com.simenko.qmapp.domain

import com.simenko.qmapp.retrofit.entities.NetworkOrder
import com.simenko.qmapp.retrofit.entities.NetworkSubOrder
import com.simenko.qmapp.retrofit.entities.NetworkSubOrderTask
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

fun NetworkOrder.toDomainOrder() =
    ObjectTransformer(NetworkOrder::class, DomainOrder::class).transform(this)

fun NetworkSubOrder.toDomainSubOrder() =
    ObjectTransformer(NetworkSubOrder::class, DomainSubOrder::class).transform(this)

fun DatabaseSubOrderTask.toDomainSubOrderTask() =
    ObjectTransformer(DatabaseSubOrderTask::class, DomainSubOrderTask::class).transform(this)

fun NetworkSubOrderTask.toDomainSubOrderTask() =
    ObjectTransformer(NetworkSubOrderTask::class, DomainSubOrderTask::class).transform(this)

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


fun List<DatabaseOrderComplete>.asDomainOrdersComplete(lastSelectedId: Int = 0): List<DomainOrderComplete> {
    return map {
        var detailsVisibility = false

        if (lastSelectedId == it.order.id)
            detailsVisibility = true

        DomainOrderComplete(
            order = it.order.toDomainOrder(),
            orderType = it.orderType.toDomainType(),
            orderReason = it.orderReason.toDomainReason(),
            customer = it.customer.toDomainDepartment(),
            orderPlacer = it.orderPlacer.toDomainTeamMember(),
            orderStatus = it.orderStatus.toDomainStatus(),
            orderResult = it.orderResult.toDomainOrderResult(),
            detailsVisibility = detailsVisibility
        )
    }.sortedByDescending { it.order.orderNumber }
}

fun List<DatabaseSubOrderComplete>.asDomainSubOrderDetailed(lastSelectedId: Int = 0): List<DomainSubOrderComplete> {
    return map {
        var detailsVisibility = false

        if (lastSelectedId == it.subOrder.id)
            detailsVisibility = true

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
            detailsVisibility = detailsVisibility
        )
    }.sortedByDescending { it.orderShort.order.orderNumber }
}

fun List<DatabaseSubOrderTaskComplete>.asDomainSubOrderTask(lastSelectedId: Int = 0): List<DomainSubOrderTaskComplete> {
    return map {
        var detailsVisibility = false

        if (lastSelectedId == it.subOrderTask.id)
            detailsVisibility = true

        DomainSubOrderTaskComplete(
            subOrderTask = it.subOrderTask.toDomainSubOrderTask(),
            characteristic = it.characteristic.toDomainCharacteristicComplete(),
            subOrder = it.subOrder.toDomainSubOrder(),
            status = it.status.toDomainStatus(),
            taskResult = it.taskResult.toDomainTaskResult(),
            detailsVisibility = detailsVisibility
        )
    }
}

fun List<DatabaseSampleComplete>.asDomainSamples(lastSelectedId: Int = 0): List<DomainSampleComplete> {
    return map {
        var detailsVisibility = false

        if (lastSelectedId == it.sampleResult.id)
            detailsVisibility = true

        DomainSampleComplete(
            sampleResult = it.sampleResult.toDomainSampleResult(),
            sample = it.sample.toDomainSample(),
            detailsVisibility = detailsVisibility
        )
    }
}

fun List<DatabaseResultComplete>.asDomainResults(lastSelectedId: Int = 0): List<DomainResultComplete> {
    return map {
        var detailsVisibility = false

        if (lastSelectedId == it.result.id)
            detailsVisibility = true

        DomainResultComplete(
            result = it.result.toDomainResult(),
            resultsDecryption = it.resultsDecryption.toDomainResult(),
            metrix = it.metrix.toDomainMetrix(),
            resultTolerance = it.resultTolerance.toDomainResultTolerance(),
            detailsVisibility = detailsVisibility
        )
    }
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

fun List<DomainTeamMemberComplete>.changeOrderVisibility(id: Int): List<DomainTeamMemberComplete> {
    return map {
        if (id == it.teamMember.id) {
            it.detailsVisibility = !it.detailsVisibility
        } else {
            it.detailsVisibility = false
        }
        it
    }
}

private const val TAG = "TransformationsToDomain"
fun List<DomainOrderComplete>.changeOrderVisibility(
    detailsId: Int,
    actionsId: Int
): List<DomainOrderComplete> {
    return map {

        it.detailsVisibility = detailsId == it.order.id
        it.isExpanded = actionsId == it.order.id

        it
    }
}

fun List<DomainOrderComplete>.filterByStatusAndNumber(
    statusId: Int,
    orderNumber: String
): List<DomainOrderComplete> {
    return filter {
        (it.order.statusId == statusId || statusId == -1)
                &&
                (it.order.orderNumber.toString().contains(orderNumber)
                        ||
                        (orderNumber == "0"))
    }
}

fun List<DomainSubOrderComplete>.changeSubOrderVisibility(
    detailsId: Int,
    actionsId: Int
): List<DomainSubOrderComplete> {
    return map {

        it.detailsVisibility = detailsId == it.subOrder.id
        it.isExpanded = actionsId == it.subOrder.id

        it
    }
}

fun List<DomainSubOrderComplete>.filterSubOrderByStatusAndNumber(
    statusId: Int,
    orderNumber: String
): List<DomainSubOrderComplete> {
    return filter {
        (it.subOrder.statusId == statusId || statusId == -1)
                &&
                (it.orderShort.order.orderNumber.toString().contains(orderNumber)
                        ||
                        (orderNumber == "0"))
    }
}

fun List<DomainSubOrderTaskComplete>.changeTaskVisibility(
    detailsId: Int,
    actionsId: Int
): List<DomainSubOrderTaskComplete> {
    return map {

        it.detailsVisibility = detailsId == it.subOrderTask.id
        it.isExpanded = actionsId == it.subOrderTask.id

        it
    }
}

fun List<DomainSampleComplete>.changeSampleVisibility(
    detailsId: Int
): List<DomainSampleComplete> {
    return map {

        it.detailsVisibility = detailsId == it.sample.id

        it
    }
}

fun List<DomainResultComplete>.changeResultVisibility(
    detailsId: Int
): List<DomainResultComplete> {
    return map {

        it.detailsVisibility = detailsId == it.result.id

        it
    }
}