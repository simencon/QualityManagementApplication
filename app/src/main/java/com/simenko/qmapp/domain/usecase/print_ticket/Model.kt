package com.simenko.qmapp.domain.usecase.print_ticket

import com.simenko.qmapp.domain.entities.DomainCompany
import com.simenko.qmapp.domain.entities.DomainDepartment
import com.simenko.qmapp.domain.entities.DomainEmployee
import com.simenko.qmapp.domain.entities.DomainManufacturingChannel
import com.simenko.qmapp.domain.entities.DomainManufacturingLine
import com.simenko.qmapp.domain.entities.DomainManufacturingOperation
import com.simenko.qmapp.domain.entities.DomainOrder
import com.simenko.qmapp.domain.entities.DomainOrderShort
import com.simenko.qmapp.domain.entities.DomainOrdersStatus
import com.simenko.qmapp.domain.entities.DomainOrdersType
import com.simenko.qmapp.domain.entities.DomainReason
import com.simenko.qmapp.domain.entities.DomainResult
import com.simenko.qmapp.domain.entities.DomainResultComplete
import com.simenko.qmapp.domain.entities.DomainResultsDecryption
import com.simenko.qmapp.domain.entities.DomainSample
import com.simenko.qmapp.domain.entities.DomainSampleComplete
import com.simenko.qmapp.domain.entities.DomainSampleResult
import com.simenko.qmapp.domain.entities.DomainSubDepartment
import com.simenko.qmapp.domain.entities.DomainSubOrder
import com.simenko.qmapp.domain.entities.DomainSubOrderComplete
import com.simenko.qmapp.domain.entities.DomainSubOrderResult
import com.simenko.qmapp.domain.entities.DomainSubOrderTask
import com.simenko.qmapp.domain.entities.DomainSubOrderTaskComplete
import com.simenko.qmapp.domain.entities.DomainTaskResult
import com.simenko.qmapp.domain.entities.products.DomainCharGroup
import com.simenko.qmapp.domain.entities.products.DomainCharSubGroup
import com.simenko.qmapp.domain.entities.products.DomainCharacteristic
import com.simenko.qmapp.domain.entities.products.DomainItem
import com.simenko.qmapp.domain.entities.products.DomainItemComplete
import com.simenko.qmapp.domain.entities.products.DomainItemToLine
import com.simenko.qmapp.domain.entities.products.DomainItemVersion
import com.simenko.qmapp.domain.entities.products.DomainItemVersionComplete
import com.simenko.qmapp.domain.entities.products.DomainKey
import com.simenko.qmapp.domain.entities.products.DomainMetrix
import com.simenko.qmapp.domain.entities.products.DomainProductLine
import com.simenko.qmapp.domain.entities.products.DomainResultTolerance
import com.simenko.qmapp.domain.entities.products.DomainVersionStatus

val results: List<DomainResultComplete> = listOf(
    DomainResultComplete(
        result = DomainResult(id = 552788, sampleId = 36298, metrixId = 43, result = 0.056f, isOk = false, resultDecryptionId = 2, taskId = 82678),
        resultsDecryption = DomainResultsDecryption(id = 2, resultDecryption = "Too big"),
        metrix = DomainMetrix(id = 43, charId = 8, metrixOrder = 9, metrixDesignation = "ΔriGL", metrixDescription = null, units = "1 / мм", detailsVisibility = false, isExpanded = false),
        resultTolerance = DomainResultTolerance(id = 552788, nominal = 0.0f, lsl = -0.05f, usl = 0.05f),
        detailsVisibility = false,
        isExpanded = false
    ),
    DomainResultComplete(
        result = DomainResult(id = 552823, sampleId = 36299, metrixId = 43, result = 0.065f, isOk = false, resultDecryptionId = 2, taskId = 82678),
        resultsDecryption = DomainResultsDecryption(id = 2, resultDecryption = "Too big"),
        metrix = DomainMetrix(id = 43, charId = 8, metrixOrder = 9, metrixDesignation = "ΔriGL", metrixDescription = null, units = "1 / мм", detailsVisibility = false, isExpanded = false),
        resultTolerance = DomainResultTolerance(id = 552823, nominal = 0.0f, lsl = -0.05f, usl = 0.05f),
        detailsVisibility = false,
        isExpanded = false
    ),
    DomainResultComplete(
        result = DomainResult(id = 552824, sampleId = 36299, metrixId = 44, result = 0.022f, isOk = false, resultDecryptionId = 2, taskId = 82678),
        resultsDecryption = DomainResultsDecryption(id = 2, resultDecryption = "Too big"),
        metrix = DomainMetrix(id = 44, charId = 8, metrixOrder = 10, metrixDesignation = "ΔriG", metrixDescription = null, units = "1 / мм", detailsVisibility = false, isExpanded = false),
        resultTolerance = DomainResultTolerance(id = 552824, nominal = 0.0f, lsl = -0.02f, usl = 0.02f),
        detailsVisibility = false,
        isExpanded = false
    )
)

val samples: List<DomainSampleComplete> = listOf(
    DomainSampleComplete(
        sampleResult = DomainSampleResult(id = 36298, taskId = 82674, isOk = true, good = 5, total = 5),
        sample = DomainSample(id = 36298, subOrderId = 26124, sampleNumber = 1, isNewRecord = false, toBeDeleted = false),
        detailsVisibility = false,
        isExpanded = false
    ),
    DomainSampleComplete(
        sampleResult = DomainSampleResult(id = 36298, taskId = 82675, isOk = true, good = 1, total = 1),
        sample = DomainSample(id = 36298, subOrderId = 26124, sampleNumber = 1, isNewRecord = false, toBeDeleted = false),
        detailsVisibility = false,
        isExpanded = false
    ),
    DomainSampleComplete(
        sampleResult = DomainSampleResult(id = 36298, taskId = 82676, isOk = true, good = 1, total = 1),
        sample = DomainSample(id = 36298, subOrderId = 26124, sampleNumber = 1, isNewRecord = false, toBeDeleted = false),
        detailsVisibility = false,
        isExpanded = false
    ),
    DomainSampleComplete(
        sampleResult = DomainSampleResult(id = 36298, taskId = 82677, isOk = true, good = 8, total = 8),
        sample = DomainSample(id = 36298, subOrderId = 26124, sampleNumber = 1, isNewRecord = false, toBeDeleted = false),
        detailsVisibility = false,
        isExpanded = false
    ),
    DomainSampleComplete(
        sampleResult = DomainSampleResult(id = 36298, taskId = 82678, isOk = false, good = 11, total = 12),
        sample = DomainSample(id = 36298, subOrderId = 26124, sampleNumber = 1, isNewRecord = false, toBeDeleted = false),
        detailsVisibility = false,
        isExpanded = false
    ),
    DomainSampleComplete(
        sampleResult = DomainSampleResult(id = 36298, taskId = 82679, isOk = true, good = 1, total = 1),
        sample = DomainSample(id = 36298, subOrderId = 26124, sampleNumber = 1, isNewRecord = false, toBeDeleted = false),
        detailsVisibility = false,
        isExpanded = false
    ),
    DomainSampleComplete(
        sampleResult = DomainSampleResult(id = 36298, taskId = 82680, isOk = true, good = 7, total = 7),
        sample = DomainSample(id = 36298, subOrderId = 26124, sampleNumber = 1, isNewRecord = false, toBeDeleted = false),
        detailsVisibility = false,
        isExpanded = false
    ),
    DomainSampleComplete(
        sampleResult = DomainSampleResult(id = 36299, taskId = 82674, isOk = true, good = 5, total = 5),
        sample = DomainSample(id = 36299, subOrderId = 26124, sampleNumber = 2, isNewRecord = false, toBeDeleted = false),
        detailsVisibility = false,
        isExpanded = false
    ),
    DomainSampleComplete(
        sampleResult = DomainSampleResult(id = 36299, taskId = 82675, isOk = true, good = 1, total = 1),
        sample = DomainSample(id = 36299, subOrderId = 26124, sampleNumber = 2, isNewRecord = false, toBeDeleted = false),
        detailsVisibility = false,
        isExpanded = false
    ),
    DomainSampleComplete(
        sampleResult = DomainSampleResult(id = 36299, taskId = 82676, isOk = true, good = 1, total = 1),
        sample = DomainSample(id = 36299, subOrderId = 26124, sampleNumber = 2, isNewRecord = false, toBeDeleted = false),
        detailsVisibility = false,
        isExpanded = false
    ),
    DomainSampleComplete(
        sampleResult = DomainSampleResult(id = 36299, taskId = 82677, isOk = true, good = 8, total = 8),
        sample = DomainSample(id = 36299, subOrderId = 26124, sampleNumber = 2, isNewRecord = false, toBeDeleted = false),
        detailsVisibility = false,
        isExpanded = false
    ),
    DomainSampleComplete(
        sampleResult = DomainSampleResult(id = 36299, taskId = 82678, isOk = false, good = 10, total = 12),
        sample = DomainSample(id = 36299, subOrderId = 26124, sampleNumber = 2, isNewRecord = false, toBeDeleted = false),
        detailsVisibility = false,
        isExpanded = false
    ),
    DomainSampleComplete(
        sampleResult = DomainSampleResult(id = 36299, taskId = 82679, isOk = true, good = 1, total = 1),
        sample = DomainSample(id = 36299, subOrderId = 26124, sampleNumber = 2, isNewRecord = false, toBeDeleted = false),
        detailsVisibility = false,
        isExpanded = false
    ),
    DomainSampleComplete(
        sampleResult = DomainSampleResult(id = 36299, taskId = 82680, isOk = true, good = 7, total = 7),
        sample = DomainSample(id = 36299, subOrderId = 26124, sampleNumber = 2, isNewRecord = false, toBeDeleted = false),
        detailsVisibility = false,
        isExpanded = false
    ),
)

val subOrderTasks: List<DomainSubOrderTaskComplete> = listOf(
    DomainSubOrderTaskComplete(
        subOrderTask = DomainSubOrderTask(
            id = 82674,
            subOrderId = 26124,
            charId = 16,
            statusId = 3,
            createdDate = 1692054679021,
            completedDate = 1692054679021,
            orderedById = 6,
            completedById = 9,
            isNewRecord = false,
            toBeDeleted = false
        ),
        characteristic = DomainCharacteristic.DomainCharacteristicComplete(
            characteristic = DomainCharacteristic(
                id = 16,
                ishSubCharId = 4,
                charOrder = 7,
                charDesignation = null,
                charDescription = "Профіль опорного борта",
                sampleRelatedTime = 0.7,
                measurementRelatedTime = 4.9,
                isSelected = false,
                detailsVisibility = false,
                isExpanded = false
            ), characteristicSubGroup = DomainCharSubGroup.DomainCharSubGroupComplete(
                charSubGroup = DomainCharSubGroup(id = 4, charGroupId = 7, ishElement = "Profile", measurementGroupRelatedTime = 1.23, detailsVisibility = false, isExpanded = false),
                charGroup = DomainCharGroup.DomainCharGroupComplete(
                    charGroup = DomainCharGroup(id = 7, productLineId = 1, ishElement = "Microgeomentry", detailsVisibility = false, isExpanded = false),
                    productLine = DomainProductLine.DomainProductLineComplete(
                        manufacturingProject = DomainProductLine(
                            id = 1,
                            companyId = 1,
                            factoryLocationDep = 8,
                            factoryLocationDetails = null,
                            customerName = null,
                            team = 0,
                            modelYear = null,
                            projectSubject = "Продукція підрпиємства",
                            startDate = "2022-07-19",
                            revisionDate = "2022-07-26",
                            refItem = null,
                            pfmeaNum = "M001",
                            processOwner = 18,
                            confLevel = 0
                        ),
                        company = DomainCompany(
                            id = 1,
                            companyName = "PrJSC \"SKF Ukraine\"",
                            companyCountry = "Ukraine",
                            companyCity = "Lutsk",
                            companyAddress = "Bozhenko 34",
                            companyPhoneNo = "+380332783302",
                            companyPostCode = "43017",
                            companyRegion = "Volyn",
                            companyOrder = 1,
                            companyIndustrialClassification = "Automotive, Agry",
                            companyManagerId = 67,
                            isSelected = false
                        ),
                        designDepartment = DomainDepartment(
                            id = 8,
                            depAbbr = "ТКВ",
                            depName = "Технологічно - конструкторський відділ",
                            depManager = 18,
                            depOrganization = "Design and Process",
                            depOrder = 12,
                            companyId = 1,
                            isSelected = false
                        ),
                        designManager = DomainEmployee(
                            id = 18,
                            fullName = "Роман Семенишин",
                            companyId = 1,
                            departmentId = 4,
                            subDepartmentId = null,
                            department = "УЯк",
                            jobRoleId = 1,
                            jobRole = "QM Deputy",
                            email = "roman.semenyshyn@skf.com",
                            passWord = "1305051301",
                            isSelected = false
                        ),
                        detailsVisibility = false,
                        isExpanded = false,
                        isSelected = false
                    ),
                    detailsVisibility = false,
                    isExpanded = false
                ),
                detailsVisibility = false,
                isExpanded = false
            ), detailsVisibility = false, isExpanded = false
        ),
        status = DomainOrdersStatus(id = 3, statusDescription = "Done", isSelected = false),
        subOrder = DomainSubOrder(
            id = 26124,
            orderId = 26276,
            subOrderNumber = 1,
            orderedById = 6,
            completedById = 9,
            statusId = 3,
            createdDate = 1692054679021,
            completedDate = 1692054679021,
            departmentId = 2,
            subDepartmentId = 11,
            channelId = 12,
            lineId = 5,
            operationId = 44,
            itemPreffix = "c388",
            itemTypeId = 629,
            itemVersionId = 388,
            samplesCount = 2,
            remarkId = 2
        ),
        taskResult = DomainTaskResult(id = 82674, isOk = true, good = 10, total = 10),
        detailsVisibility = false,
        isExpanded = false
    ),
    DomainSubOrderTaskComplete(
        subOrderTask = DomainSubOrderTask(
            id = 82675,
            subOrderId = 26124,
            charId = 18,
            statusId = 3,
            createdDate = 1692054679021,
            completedDate = 1692054679021,
            orderedById = 6,
            completedById = 9,
            isNewRecord = false,
            toBeDeleted = false
        ),
        characteristic = DomainCharacteristic.DomainCharacteristicComplete(
            characteristic = DomainCharacteristic(
                id = 18,
                ishSubCharId = 2,
                charOrder = 3,
                charDesignation = "Ra d",
                charDescription = "Шорсткість отвору",
                sampleRelatedTime = 0.5,
                measurementRelatedTime = 0.43,
                isSelected = false,
                detailsVisibility = false,
                isExpanded = false
            ), characteristicSubGroup = DomainCharSubGroup.DomainCharSubGroupComplete(
                charSubGroup = DomainCharSubGroup(id = 2, charGroupId = 7, ishElement = "Roughness", measurementGroupRelatedTime = 0.4, detailsVisibility = false, isExpanded = false),
                charGroup = DomainCharGroup.DomainCharGroupComplete(
                    charGroup = DomainCharGroup(id = 7, productLineId = 1, ishElement = "Microgeomentry", detailsVisibility = false, isExpanded = false),
                    productLine = DomainProductLine.DomainProductLineComplete(
                        manufacturingProject = DomainProductLine(
                            id = 1,
                            companyId = 1,
                            factoryLocationDep = 8,
                            factoryLocationDetails = null,
                            customerName = null,
                            team = 0,
                            modelYear = null,
                            projectSubject = "Продукція підрпиємства",
                            startDate = "2022-07-19",
                            revisionDate = "2022-07-26",
                            refItem = null,
                            pfmeaNum = "M001",
                            processOwner = 18,
                            confLevel = 0
                        ),
                        company = DomainCompany(
                            id = 1,
                            companyName = "PrJSC \"SKF Ukraine\"",
                            companyCountry = "Ukraine",
                            companyCity = "Lutsk",
                            companyAddress = "Bozhenko 34",
                            companyPhoneNo = "+380332783302",
                            companyPostCode = "43017",
                            companyRegion = "Volyn",
                            companyOrder = 1,
                            companyIndustrialClassification = "Automotive, Agry",
                            companyManagerId = 67,
                            isSelected = false
                        ),
                        designDepartment = DomainDepartment(
                            id = 8,
                            depAbbr = "ТКВ",
                            depName = "Технологічно - конструкторський відділ",
                            depManager = 18,
                            depOrganization = "Design and Process",
                            depOrder = 12,
                            companyId = 1,
                            isSelected = false
                        ),
                        designManager = DomainEmployee(
                            id = 18,
                            fullName = "Роман Семенишин",
                            companyId = 1,
                            departmentId = 4,
                            subDepartmentId = null,
                            department = "УЯк",
                            jobRoleId = 1,
                            jobRole = "QM Deputy",
                            email = "roman.semenyshyn@skf.com",
                            passWord = "1305051301",
                            isSelected = false
                        ),
                        detailsVisibility = false,
                        isExpanded = false,
                        isSelected = false
                    ),
                    detailsVisibility = false,
                    isExpanded = false
                ),
                detailsVisibility = false,
                isExpanded = false
            ), detailsVisibility = false, isExpanded = false
        ),
        status = DomainOrdersStatus(id = 3, statusDescription = "Done", isSelected = false),
        subOrder = DomainSubOrder(
            id = 26124,
            orderId = 26276,
            subOrderNumber = 1,
            orderedById = 6,
            completedById = 9,
            statusId = 3,
            createdDate = 1692054679021,
            completedDate = 1692054679021,
            departmentId = 2,
            subDepartmentId = 11,
            channelId = 12,
            lineId = 5,
            operationId = 44,
            itemPreffix = "c388",
            itemTypeId = 629,
            itemVersionId = 388,
            samplesCount = 2,
            remarkId = 2
        ),
        taskResult = DomainTaskResult(id = 82675, isOk = true, good = 2, total = 2),
        detailsVisibility = false,
        isExpanded = false
    ),
    DomainSubOrderTaskComplete(
        subOrderTask = DomainSubOrderTask(
            id = 82676,
            subOrderId = 26124,
            charId = 6,
            statusId = 3,
            createdDate = 1692054679021,
            completedDate = 1692054679021,
            orderedById = 6,
            completedById = 9,
            isNewRecord = false,
            toBeDeleted = false
        ),
        characteristic = DomainCharacteristic.DomainCharacteristicComplete(
            characteristic = DomainCharacteristic(
                id = 6,
                ishSubCharId = 2,
                charOrder = 1,
                charDesignation = null,
                charDescription = "Шорсткість доріжки кочення IR",
                sampleRelatedTime = 0.33,
                measurementRelatedTime = 0.25,
                isSelected = false,
                detailsVisibility = false,
                isExpanded = false
            ), characteristicSubGroup = DomainCharSubGroup.DomainCharSubGroupComplete(
                charSubGroup = DomainCharSubGroup(id = 2, charGroupId = 7, ishElement = "Roughness", measurementGroupRelatedTime = 0.4, detailsVisibility = false, isExpanded = false),
                charGroup = DomainCharGroup.DomainCharGroupComplete(
                    charGroup = DomainCharGroup(id = 7, productLineId = 1, ishElement = "Microgeomentry", detailsVisibility = false, isExpanded = false),
                    productLine = DomainProductLine.DomainProductLineComplete(
                        manufacturingProject = DomainProductLine(
                            id = 1,
                            companyId = 1,
                            factoryLocationDep = 8,
                            factoryLocationDetails = null,
                            customerName = null,
                            team = 0,
                            modelYear = null,
                            projectSubject = "Продукція підрпиємства",
                            startDate = "2022-07-19",
                            revisionDate = "2022-07-26",
                            refItem = null,
                            pfmeaNum = "M001",
                            processOwner = 18,
                            confLevel = 0
                        ),
                        company = DomainCompany(
                            id = 1,
                            companyName = "PrJSC \"SKF Ukraine\"",
                            companyCountry = "Ukraine",
                            companyCity = "Lutsk",
                            companyAddress = "Bozhenko 34",
                            companyPhoneNo = "+380332783302",
                            companyPostCode = "43017",
                            companyRegion = "Volyn",
                            companyOrder = 1,
                            companyIndustrialClassification = "Automotive, Agry",
                            companyManagerId = 67,
                            isSelected = false
                        ),
                        designDepartment = DomainDepartment(
                            id = 8,
                            depAbbr = "ТКВ",
                            depName = "Технологічно - конструкторський відділ",
                            depManager = 18,
                            depOrganization = "Design and Process",
                            depOrder = 12,
                            companyId = 1,
                            isSelected = false
                        ),
                        designManager = DomainEmployee(
                            id = 18,
                            fullName = "Роман Семенишин",
                            companyId = 1,
                            departmentId = 4,
                            subDepartmentId = null,
                            department = "УЯк",
                            jobRoleId = 1,
                            jobRole = "QM Deputy",
                            email = "roman.semenyshyn@skf.com",
                            passWord = "1305051301",
                            isSelected = false
                        ),
                        detailsVisibility = false,
                        isExpanded = false,
                        isSelected = false
                    ),
                    detailsVisibility = false,
                    isExpanded = false
                ),
                detailsVisibility = false,
                isExpanded = false
            ), detailsVisibility = false, isExpanded = false
        ),
        status = DomainOrdersStatus(id = 3, statusDescription = "Done", isSelected = false),
        subOrder = DomainSubOrder(
            id = 26124,
            orderId = 26276,
            subOrderNumber = 1,
            orderedById = 6,
            completedById = 9,
            statusId = 3,
            createdDate = 1692054679021,
            completedDate = 1692054679021,
            departmentId = 2,
            subDepartmentId = 11,
            channelId = 12,
            lineId = 5,
            operationId = 44,
            itemPreffix = "c388",
            itemTypeId = 629,
            itemVersionId = 388,
            samplesCount = 2,
            remarkId = 2
        ),
        taskResult = DomainTaskResult(id = 82676, isOk = true, good = 2, total = 2),
        detailsVisibility = false,
        isExpanded = false
    ),
    DomainSubOrderTaskComplete(
        subOrderTask = DomainSubOrderTask(
            id = 82677,
            subOrderId = 26124,
            charId = 7,
            statusId = 3,
            createdDate = 1692054679021,
            completedDate = 1692054679021,
            orderedById = 6,
            completedById = 9,
            isNewRecord = false,
            toBeDeleted = false
        ),
        characteristic = DomainCharacteristic.DomainCharacteristicComplete(
            characteristic = DomainCharacteristic(
                id = 7,
                ishSubCharId = 3,
                charOrder = 8,
                charDesignation = null,
                charDescription = "Хвилястість доріжки кочення IR",
                sampleRelatedTime = 0.57,
                measurementRelatedTime = 0.87,
                isSelected = false,
                detailsVisibility = false,
                isExpanded = false
            ),
            characteristicSubGroup = DomainCharSubGroup.DomainCharSubGroupComplete(
                charSubGroup = DomainCharSubGroup(id = 3, charGroupId = 7, ishElement = "Waveness", measurementGroupRelatedTime = 2.02, detailsVisibility = false, isExpanded = false),
                charGroup = DomainCharGroup.DomainCharGroupComplete(
                    charGroup = DomainCharGroup(id = 7, productLineId = 1, ishElement = "Microgeomentry", detailsVisibility = false, isExpanded = false),
                    productLine = DomainProductLine.DomainProductLineComplete(
                        manufacturingProject = DomainProductLine(
                            id = 1,
                            companyId = 1,
                            factoryLocationDep = 8,
                            factoryLocationDetails = null,
                            customerName = null,
                            team = 0,
                            modelYear = null,
                            projectSubject = "Продукція підрпиємства",
                            startDate = "2022 - 07-19",
                            revisionDate = "2022-07-26",
                            refItem = null,
                            pfmeaNum = "M001",
                            processOwner = 18,
                            confLevel = 0
                        ),
                        company = DomainCompany(
                            id = 1,
                            companyName = "PrJSC \"SKF Ukraine\"",
                            companyCountry = "Ukraine",
                            companyCity = "Lutsk",
                            companyAddress = "Bozhenko 34",
                            companyPhoneNo = "+380332783302",
                            companyPostCode = "43017",
                            companyRegion = "Volyn",
                            companyOrder = 1,
                            companyIndustrialClassification = "Automotive, Agry",
                            companyManagerId = 67,
                            isSelected = false
                        ),
                        designDepartment = DomainDepartment(
                            id = 8,
                            depAbbr = "ТКВ",
                            depName = "Технологічно - конструкторський відділ",
                            depManager = 18,
                            depOrganization = "Design and Process",
                            depOrder = 12,
                            companyId = 1,
                            isSelected = false
                        ),
                        designManager = DomainEmployee(
                            id = 18,
                            fullName = "Роман Семенишин",
                            companyId = 1,
                            departmentId = 4,
                            subDepartmentId = null,
                            department = "УЯк",
                            jobRoleId = 1,
                            jobRole = "QM Deputy",
                            email = "roman.semenyshyn@skf.com",
                            passWord = "1305051301",
                            isSelected = false
                        ),
                        detailsVisibility = false,
                        isExpanded = false,
                        isSelected = false
                    ),
                    detailsVisibility = false,
                    isExpanded = false
                ),
                detailsVisibility = false,
                isExpanded = false
            ), detailsVisibility = false, isExpanded = false
        ),
        status = DomainOrdersStatus(id = 3, statusDescription = "Done", isSelected = false),
        subOrder = DomainSubOrder(
            id = 26124,
            orderId = 26276,
            subOrderNumber = 1,
            orderedById = 6,
            completedById = 9,
            statusId = 3,
            createdDate = 1692054679021,
            completedDate = 1692054679021,
            departmentId = 2,
            subDepartmentId = 11,
            channelId = 12,
            lineId = 5,
            operationId = 44,
            itemPreffix = "c388",
            itemTypeId = 629,
            itemVersionId = 388,
            samplesCount = 2,
            remarkId = 2
        ),
        taskResult = DomainTaskResult(id = 82677, isOk = true, good = 16, total = 16),
        detailsVisibility = false,
        isExpanded = false
    ),
    DomainSubOrderTaskComplete(
        subOrderTask = DomainSubOrderTask(
            id = 82678,
            subOrderId = 26124,
            charId = 8,
            statusId = 3,
            createdDate = 1692054679021,
            completedDate = 1692054679021,
            orderedById = 6,
            completedById = 9,
            isNewRecord = false,
            toBeDeleted = false
        ),
        characteristic = DomainCharacteristic.DomainCharacteristicComplete(
            characteristic = DomainCharacteristic(
                id = 8,
                ishSubCharId = 4,
                charOrder = 6,
                charDesignation = null,
                charDescription = "Профіль доріжки кочення(логарифм.) IR",
                sampleRelatedTime = 0.95,
                measurementRelatedTime = 3.9,
                isSelected = false,
                detailsVisibility = false,
                isExpanded = false
            ), characteristicSubGroup = DomainCharSubGroup.DomainCharSubGroupComplete(
                charSubGroup = DomainCharSubGroup(id = 4, charGroupId = 7, ishElement = "Profile", measurementGroupRelatedTime = 1.23, detailsVisibility = false, isExpanded = false),
                charGroup = DomainCharGroup.DomainCharGroupComplete(
                    charGroup = DomainCharGroup(id = 7, productLineId = 1, ishElement = "Microgeomentry", detailsVisibility = false, isExpanded = false),
                    productLine = DomainProductLine.DomainProductLineComplete(
                        manufacturingProject = DomainProductLine(
                            id = 1,
                            companyId = 1,
                            factoryLocationDep = 8,
                            factoryLocationDetails = null,
                            customerName = null,
                            team = 0,
                            modelYear = null,
                            projectSubject = "Продукція підрпиємства",
                            startDate = "2022-07-19",
                            revisionDate = "2022-07-26",
                            refItem = null,
                            pfmeaNum = "M001",
                            processOwner = 18,
                            confLevel = 0
                        ),
                        company = DomainCompany(
                            id = 1,
                            companyName = "PrJSC \"SKF Ukraine\"",
                            companyCountry = "Ukraine",
                            companyCity = "Lutsk",
                            companyAddress = "Bozhenko 34",
                            companyPhoneNo = "+380 332 783 302",
                            companyPostCode = "43017",
                            companyRegion = "Volyn",
                            companyOrder = 1,
                            companyIndustrialClassification = "Automotive, Agry",
                            companyManagerId = 67,
                            isSelected = false
                        ),
                        designDepartment = DomainDepartment(
                            id = 8,
                            depAbbr = "ТКВ",
                            depName = "Технологічно - конструкторський відділ",
                            depManager = 18,
                            depOrganization = "Design and Process",
                            depOrder = 12,
                            companyId = 1,
                            isSelected = false
                        ),
                        designManager = DomainEmployee(
                            id = 18,
                            fullName = "Роман Семенишин",
                            companyId = 1,
                            departmentId = 4,
                            subDepartmentId = null,
                            department = "УЯк",
                            jobRoleId = 1,
                            jobRole = "QM Deputy",
                            email = "roman.semenyshyn@skf.com",
                            passWord = "1305051301",
                            isSelected = false
                        ),
                        detailsVisibility = false,
                        isExpanded = false,
                        isSelected = false
                    ),
                    detailsVisibility = false,
                    isExpanded = false
                ),
                detailsVisibility = false,
                isExpanded = false
            ), detailsVisibility = false, isExpanded = false
        ),
        status = DomainOrdersStatus(id = 3, statusDescription = "Done", isSelected = false),
        subOrder = DomainSubOrder(
            id = 26124,
            orderId = 26276,
            subOrderNumber = 1,
            orderedById = 6,
            completedById = 9,
            statusId = 3,
            createdDate = 1692054679021,
            completedDate = 1692054679021,
            departmentId = 2,
            subDepartmentId = 11,
            channelId = 12,
            lineId = 5,
            operationId = 44,
            itemPreffix = "c388",
            itemTypeId = 629,
            itemVersionId = 388,
            samplesCount = 2,
            remarkId = 2
        ),
        taskResult = DomainTaskResult(id = 82678, isOk = false, good = 21, total = 24),
        detailsVisibility = false,
        isExpanded = false
    ),
    DomainSubOrderTaskComplete(
        subOrderTask = DomainSubOrderTask(
            id = 82679,
            subOrderId = 26124,
            charId = 14,
            statusId = 3,
            createdDate = 1692054679021,
            completedDate = 1692054679021,
            orderedById = 6,
            completedById = 9,
            isNewRecord = false,
            toBeDeleted = false
        ),
        characteristic = DomainCharacteristic.DomainCharacteristicComplete(
            characteristic = DomainCharacteristic(
                id = 14,
                ishSubCharId = 2,
                charOrder = 2,
                charDesignation = null,
                charDescription = "Шорсткість опорного борта",
                sampleRelatedTime = 0.37,
                measurementRelatedTime = 0.37,
                isSelected = false,
                detailsVisibility = false,
                isExpanded = false
            ), characteristicSubGroup = DomainCharSubGroup.DomainCharSubGroupComplete(
                charSubGroup = DomainCharSubGroup(id = 2, charGroupId = 7, ishElement = "Roughness", measurementGroupRelatedTime = 0.4, detailsVisibility = false, isExpanded = false),
                charGroup = DomainCharGroup.DomainCharGroupComplete(
                    charGroup = DomainCharGroup(id = 7, productLineId = 1, ishElement = "Microgeomentry", detailsVisibility = false, isExpanded = false),
                    productLine = DomainProductLine.DomainProductLineComplete(
                        manufacturingProject = DomainProductLine(
                            id = 1,
                            companyId = 1,
                            factoryLocationDep = 8,
                            factoryLocationDetails = null,
                            customerName = null,
                            team = 0,
                            modelYear = null,
                            projectSubject = "Продукція підрпиємства",
                            startDate = "2022-07-19",
                            revisionDate = "2022-07-26",
                            refItem = null,
                            pfmeaNum = "M001",
                            processOwner = 18,
                            confLevel = 0
                        ),
                        company = DomainCompany(
                            id = 1,
                            companyName = "PrJSC \"SKF Ukraine\"",
                            companyCountry = "Ukraine",
                            companyCity = "Lutsk",
                            companyAddress = "Bozhenko 34",
                            companyPhoneNo = "+380 332 783 302",
                            companyPostCode = "43017",
                            companyRegion = "Volyn",
                            companyOrder = 1,
                            companyIndustrialClassification = "Automotive, Agry",
                            companyManagerId = 67,
                            isSelected = false
                        ),
                        designDepartment = DomainDepartment(
                            id = 8,
                            depAbbr = "ТКВ",
                            depName = "Технологічно - конструкторський відділ",
                            depManager = 18,
                            depOrganization = "Design and Process",
                            depOrder = 12,
                            companyId = 1,
                            isSelected = false
                        ),
                        designManager = DomainEmployee(
                            id = 18,
                            fullName = "Роман Семенишин",
                            companyId = 1,
                            departmentId = 4,
                            subDepartmentId = null,
                            department = "УЯк",
                            jobRoleId = 1,
                            jobRole = "QM Deputy",
                            email = "roman.semenyshyn@skf.com",
                            passWord = "1305051301",
                            isSelected = false
                        ),
                        detailsVisibility = false,
                        isExpanded = false,
                        isSelected = false
                    ),
                    detailsVisibility = false,
                    isExpanded = false
                ),
                detailsVisibility = false,
                isExpanded = false
            ), detailsVisibility = false, isExpanded = false
        ),
        status = DomainOrdersStatus(id = 3, statusDescription = "Done", isSelected = false),
        subOrder = DomainSubOrder(
            id = 26124,
            orderId = 26276,
            subOrderNumber = 1,
            orderedById = 6,
            completedById = 9,
            statusId = 3,
            createdDate = 1692054679021,
            completedDate = 1692054679021,
            departmentId = 2,
            subDepartmentId = 11,
            channelId = 12,
            lineId = 5,
            operationId = 44,
            itemPreffix = "c388",
            itemTypeId = 629,
            itemVersionId = 388,
            samplesCount = 2,
            remarkId = 2
        ),
        taskResult = DomainTaskResult(id = 82679, isOk = true, good = 2, total = 2),
        detailsVisibility = false,
        isExpanded = false
    ),
    DomainSubOrderTaskComplete(
        subOrderTask = DomainSubOrderTask(
            id = 82680,
            subOrderId = 26124,
            charId = 15,
            statusId = 3,
            createdDate = 1692054679021,
            completedDate = 1692054679021,
            orderedById = 6,
            completedById = 9,
            isNewRecord = false,
            toBeDeleted = false
        ),
        characteristic = DomainCharacteristic.DomainCharacteristicComplete(
            characteristic = DomainCharacteristic(
                id = 15,
                ishSubCharId = 3,
                charOrder = 9,
                charDesignation = null,
                charDescription = "Хвилястість опорного борта",
                sampleRelatedTime = 0.37,
                measurementRelatedTime = 1.72,
                isSelected = false,
                detailsVisibility = false,
                isExpanded = false
            ), characteristicSubGroup = DomainCharSubGroup.DomainCharSubGroupComplete(
                charSubGroup = DomainCharSubGroup(id = 3, charGroupId = 7, ishElement = "Waveness", measurementGroupRelatedTime = 2.02, detailsVisibility = false, isExpanded = false),
                charGroup = DomainCharGroup.DomainCharGroupComplete(
                    charGroup = DomainCharGroup(id = 7, productLineId = 1, ishElement = "Microgeomentry", detailsVisibility = false, isExpanded = false),
                    productLine = DomainProductLine.DomainProductLineComplete(
                        manufacturingProject = DomainProductLine(
                            id = 1,
                            companyId = 1,
                            factoryLocationDep = 8,
                            factoryLocationDetails = null,
                            customerName = null,
                            team = 0,
                            modelYear = null,
                            projectSubject = "Продукція підрпиємства",
                            startDate = "2022-07-19",
                            revisionDate = "2022-07-26",
                            refItem = null,
                            pfmeaNum = "M001",
                            processOwner = 18,
                            confLevel = 0
                        ),
                        company = DomainCompany(
                            id = 1,
                            companyName = "PrJSC \"SKF Ukraine\"",
                            companyCountry = "Ukraine",
                            companyCity = "Lutsk",
                            companyAddress = "Bozhenko 34",
                            companyPhoneNo = "+380 332 783 302",
                            companyPostCode = "43017",
                            companyRegion = "Volyn",
                            companyOrder = 1,
                            companyIndustrialClassification = "Automotive, Agry",
                            companyManagerId = 67,
                            isSelected = false
                        ),
                        designDepartment = DomainDepartment(
                            id = 8,
                            depAbbr = "ТКВ",
                            depName = "Технологічно - конструкторський відділ",
                            depManager = 18,
                            depOrganization = "Design and Process",
                            depOrder = 12,
                            companyId = 1,
                            isSelected = false
                        ),
                        designManager = DomainEmployee(
                            id = 18,
                            fullName = "Роман Семенишин",
                            companyId = 1,
                            departmentId = 4,
                            subDepartmentId = null,
                            department = "УЯк",
                            jobRoleId = 1,
                            jobRole = "QM Deputy",
                            email = "roman.semenyshyn@skf.com",
                            passWord = "1305051301",
                            isSelected = false
                        ),
                        detailsVisibility = false,
                        isExpanded = false,
                        isSelected = false
                    ),
                    detailsVisibility = false,
                    isExpanded = false
                ),
                detailsVisibility = false,
                isExpanded = false
            ), detailsVisibility = false, isExpanded = false
        ),
        status = DomainOrdersStatus(id = 3, statusDescription = "Done", isSelected = false),
        subOrder = DomainSubOrder(
            id = 26124,
            orderId = 26276,
            subOrderNumber = 1,
            orderedById = 6,
            completedById = 9,
            statusId = 3,
            createdDate = 1692054679021,
            completedDate = 1692054679021,
            departmentId = 2,
            subDepartmentId = 11,
            channelId = 12,
            lineId = 5,
            operationId = 44,
            itemPreffix = "c388",
            itemTypeId = 629,
            itemVersionId = 388,
            samplesCount = 2,
            remarkId = 2
        ),
        taskResult = DomainTaskResult(id = 82680, isOk = true, good = 14, total = 14),
        detailsVisibility = false,
        isExpanded = false
    )
)


internal val subOrderComplete = DomainSubOrderComplete(
    subOrder = DomainSubOrder(
        id = 26124,
        orderId = 26276,
        subOrderNumber = 1,
        orderedById = 6,
        completedById = 9,
        statusId = 3,
        createdDate = 1692054679021,
        completedDate = 1692054679021,
        departmentId = 2,
        subDepartmentId = 11,
        channelId = 12,
        lineId = 5,
        operationId = 44,
        itemPreffix = "c388",
        itemTypeId = 629,
        itemVersionId = 388,
        samplesCount = 2,
        remarkId = 2
    ),
    orderShort = DomainOrderShort(
        order = DomainOrder(
            id = 26276,
            orderTypeId = 3,
            reasonId = 2,
            orderNumber = 25269,
            customerId = 4,
            orderedById = 18,
            statusId = 3,
            createdDate = 1692054679021,
            completedDate = 1692054679021
        ),
        orderType = DomainOrdersType(id = 3, typeDescription = "Process Control", isSelected = false),
        orderReason = DomainReason(id = 2, reasonDescription = "Налагоджувальник", reasonFormalDescript = "PCP", reasonOrder = 1, isSelected = false)
    ),
    orderedBy = DomainEmployee(
        id = 6, fullName = "Андрій Грисюк", companyId = 1, departmentId = 2, subDepartmentId = null, department = "ГШСК №2",
        jobRoleId = 1,
        jobRole = "Начальник ГШСК №2",
        email = "andriy.grysyuk@skf.com",
        passWord = "2022091601",
        isSelected = false
    ),
    completedBy = DomainEmployee(
        id = 9,
        fullName = "Роман Дмитришин",
        companyId = 1,
        departmentId = 4,
        subDepartmentId = null,
        department = "УЯк",
        jobRoleId = 2,
        jobRole = "Testing Center & Metrology Manager",
        email = "roman.dmytryshyn@skf.com",
        passWord = "Qwerty20200",
        isSelected = false
    ),
    status = DomainOrdersStatus(id = 3, statusDescription = "Done", isSelected = false),
    department = DomainDepartment(
        id = 2, depAbbr = "ГШСК №2",
        depName = "Група шліфувально -складальних каналів №2",
        depManager = 6,
        depOrganization = "Manufacturing",
        depOrder = 6,
        companyId = 1,
        isSelected = false
    ),
    subDepartment = DomainSubDepartment(
        id = 11,
        depId = 2,
        subDepAbbr = "ШК",
        subDepDesignation = "Шліфувальні канали",
        subDepOrder = 2,
        isSelected = false,
        detailsVisibility = false,
        isExpanded = false
    ),
    channel = DomainManufacturingChannel(
        id = 12, subDepId = 11, channelAbbr = "Канал 2", channelDesignation = "Канал №2",
        channelOrder = 2,
        isSelected = false,
        detailsVisibility = false,
        isExpanded = false
    ),
    line = DomainManufacturingLine(
        id = 5,
        chId = 12,
        lineAbbr = "T21 (IR)",
        lineDesignation = "Лінія шліфувальної обробки внутрішнього кільця",
        lineOrder = 3,
        isSelected = false,
        detailsVisibility = false,
        isExpanded = false
    ),
    operation = DomainManufacturingOperation(
        id = 44,
        lineId = 5,
        operationAbbr = "035 Пара 1(п.п.8)",
        operationDesignation = "Суперфініш д.к. та опорного борта",
        operationOrder = 2,
        equipment = "FBM65",
        isSelected = false
    ),
    itemVersionComplete = DomainItemVersionComplete(
        itemVersion = DomainItemVersion(
            id = 388,
            fId = "c388",
            itemId = 629,
            fItemId = "c629",
            versionDescription = "V.5",
            versionDate = 1674165600000,
            statusId = 1,
            isDefault = true
        ),
        versionStatus = DomainVersionStatus(id = 1, statusDescription = "In Creation"),
        itemComplete = DomainItemComplete(
            item = DomainItem(id = 629, fId = "c629", keyId = 9, itemDesignation = "32009X/Q"),
            key = DomainKey(
                id = 9,
                projectId = 1,
                componentKey = "IR",
                componentKeyDescription = "Внутрішнє кільце після шліфувальної обробки",
                detailsVisibility = false,
                isExpanded = false,
                isSelected = false
            ),
            itemToLines = listOf(DomainItemToLine(id = 1493, fId = "c1493", lineId = 5, itemId = 629, fItemId = "c629"))
        ),
        isSelected = false,
        detailsVisibility = false,
        isExpanded = false
    ),
    subOrderResult = DomainSubOrderResult(id = 26124, isOk = false, good = 67, total = 70),
    detailsVisibility = false,
    isExpanded = false
)