package com.simenko.qmapp.ui.navigation

import com.simenko.qmapp.domain.EmptyString
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.domain.NoRecordStr
import kotlinx.serialization.Serializable

sealed interface Route {
    @Serializable
    data object LoggedOut : Route {
        @Serializable
        data object InitialScreen : Route

        @Serializable
        data object Registration : Route {
            @Serializable
            data class EnterDetails(val userEditMode: Boolean = false) : Route

            @Serializable
            data class TermsAndConditions(val fullName: String = EmptyString.str) : Route
        }

        @Serializable
        data class WaitingForValidation(val message: String = EmptyString.str) : Route

        @Serializable
        data object LogIn : Route
    }

    @Serializable
    data object Main : Route {
        @Serializable
        data object CompanyProfile : Route

        @Serializable
        data object Team : Route {
            @Serializable
            data class Employees(val employeeId: ID = NoRecord.num) : Route

            @Serializable
            data class EmployeeAddEdit(val employeeId: ID = NoRecord.num) : Route

            @Serializable
            data class Users(val userId: String = NoRecordStr.str) : Route

            @Serializable
            data class EditUser(val userId: String = NoRecordStr.str) : Route

            @Serializable
            data class Requests(val userId: String = NoRecordStr.str) : Route

            @Serializable
            data class AuthorizeUser(val userId: String) : Route
        }

        @Serializable

        data object CompanyStructure : Route {
            @Serializable
            data class StructureView(
                val companyId: ID = NoRecord.num, val departmentId: ID = NoRecord.num, val subDepartmentId: ID = NoRecord.num,
                val channelId: ID = NoRecord.num, val lineId: ID = NoRecord.num, val operationId: ID = NoRecord.num
            ) : Route

            @Serializable
            data class DepartmentAddEdit(val companyId: ID = NoRecord.num, val departmentId: ID = NoRecord.num) : Route

            @Serializable
            data class DepartmentProductLines(val companyId: ID, val departmentId: ID) : Route

            @Serializable
            data class SubDepartmentAddEdit(val departmentId: ID = NoRecord.num, val subDepartmentId: ID = NoRecord.num) : Route

            @Serializable
            data class SubDepartmentItemKinds(val departmentId: ID, val subDepartmentId: ID) : Route

            @Serializable
            data class ChannelAddEdit(val subDepartmentId: ID = NoRecord.num, val channelId: ID = NoRecord.num) : Route

            @Serializable
            data class ChannelItemKeys(val subDepartmentId: ID, val channelId: ID) : Route

            @Serializable
            data class LineAddEdit(val channelId: ID = NoRecord.num, val lineId: ID = NoRecord.num) : Route

            @Serializable
            data class LineItems(val subDepartmentId: ID, val channelId: ID, val lineId: ID ) : Route

            @Serializable
            data class OperationAddEdit(val lineId: ID = NoRecord.num, val operationId: ID = NoRecord.num) : Route

            @Serializable
            data class OperationCharacteristics(val lineId: ID, val operationId: ID) : Route
        }

        @Serializable
        data object ProductLines : Route {
            @Serializable
            data class ProductLinesList(val companyId: ID = NoRecord.num, val productLineId: ID = NoRecord.num) : Route

            @Serializable
            data class AddEditProductLine(val companyId: ID = NoRecord.num, val productLineId: ID = NoRecord.num) : Route

            @Serializable
            data object ProductLineKeys : Route {
                @Serializable
                data class ProductLineKeysList(val productLineId: ID = NoRecord.num, val productLineKeyId: ID = NoRecord.num) : Route

                @Serializable
                data class AddEditProductLineKey(val productLineId: ID, val productLineKeyId: ID = NoRecord.num) : Route
            }

            @Serializable
            data object Characteristics : Route {
                @Serializable
                data class CharacteristicGroupList(
                    val productLineId: ID = NoRecord.num,
                    val charGroupId: ID = NoRecord.num,
                    val charSubGroupId: ID = NoRecord.num,
                    val characteristicId: ID = NoRecord.num,
                    val metricId: ID = NoRecord.num
                ) : Route

                @Serializable
                data class AddEditCharGroup(val productLineId: ID = NoRecord.num, val charGroupId: ID = NoRecord.num) : Route

                @Serializable
                data class AddEditCharSubGroup(val charGroupId: ID, val charSubGroupId: ID = NoRecord.num) : Route

                @Serializable
                data class AddEditChar(val charSubGroupId: ID, val characteristicId: ID = NoRecord.num) : Route

                @Serializable
                data class AddEditMetric(val characteristicId: ID, val metricId: ID = NoRecord.num) : Route
            }

            @Serializable
            data object ProductKinds : Route {
                @Serializable
                data class ProductKindsList(val productLineId: ID = NoRecord.num, val productKindId: ID = NoRecord.num) : Route

                @Serializable
                data class AddEditProductKind(val productLineId: ID = NoRecord.num, val productKindId: ID = NoRecord.num) : Route

                @Serializable
                data object ProductKindKeys : Route {
                    @Serializable
                    data class ProductKindKeysList(val productKindId: ID = NoRecord.num, val productKindKeyId: ID = NoRecord.num) : Route
                }

                @Serializable
                data object ProductKindCharacteristics : Route {
                    @Serializable
                    data class ProductKindCharacteristicsList(val productKindId: ID = NoRecord.num, val characteristicId: ID = NoRecord.num) : Route
                }

                @Serializable
                data object ProductSpecification : Route {
                    @Serializable
                    data class ProductSpecificationList(val productKindId: ID = NoRecord.num, val componentKindId: ID = NoRecord.num, val componentStageKindId: ID = NoRecord.num) : Route

                    @Serializable
                    data class AddEditComponentKind(val productKindId: ID, val componentKindId: ID = NoRecord.num) : Route

                    @Serializable
                    data class AddEditComponentStageKind(val componentKindId: ID, val componentStageKindId: ID = NoRecord.num) : Route

                    @Serializable
                    data object ComponentKindKeys : Route {
                        @Serializable
                        data class ComponentKindKeysList(val componentKindId: ID = NoRecord.num, val componentKindKeyId: ID = NoRecord.num) : Route
                    }

                    @Serializable
                    data object ComponentKindCharacteristics : Route {
                        @Serializable
                        data class ComponentKindCharacteristicsList(val componentKindId: ID = NoRecord.num, val characteristicId: ID = NoRecord.num) : Route
                    }

                    @Serializable
                    data object ComponentStageKindKeys : Route {
                        @Serializable
                        data class ComponentStageKindKeysList(val componentStageKindId: ID = NoRecord.num, val componentStageKindKeyId: ID = NoRecord.num) : Route
                    }

                    @Serializable
                    data object ComponentStageKindCharacteristics : Route {
                        @Serializable
                        data class ComponentStageKindCharacteristicsList(val componentStageKindId: ID = NoRecord.num, val characteristicId: ID = NoRecord.num) : Route
                    }
                }

                @Serializable
                data object Products : Route {
                    @Serializable
                    data class ProductsList(
                        val productKindId: ID = NoRecord.num, val productId: ID = NoRecord.num,
                        val componentKindId: ID = NoRecord.num, val componentId: ID = NoRecord.num,
                        val componentStageKindId: ID = NoRecord.num, val componentStageId: ID = NoRecord.num,
                        val versionFId: String = NoRecordStr.str
                    ) : Route

                    @Serializable
                    data class AddEditProduct(val productKindId: ID = NoRecord.num, val productId: ID = NoRecord.num) : Route

                    @Serializable
                    data class AddProductKindProduct(val productKindId: ID = NoRecord.num) : Route

                    @Serializable
                    data class AddEditComponent(
                        val productKindId: ID,
                        val productId: ID,
                        val componentKindId: ID,
                        val componentId: ID = NoRecord.num
                    ) : Route

                    @Serializable
                    data class AddProductComponent(val productId: ID, val componentKindId: ID) : Route

                    @Serializable
                    data class AddEditComponentStage(
                        val productKindId: ID,
                        val productId: ID,
                        val componentKindId: ID,
                        val componentId: ID,
                        val componentStageKindId: ID,
                        val componentStageId: ID = NoRecord.num
                    ) : Route

                    @Serializable
                    data class AddComponentComponentStage(
                        val productKindId: ID,
                        val productId: ID,
                        val componentKindId: ID,
                        val componentId: ID,
                        val componentStageKindId: ID
                    ) : Route

                    @Serializable
                    data class CopyItemVersion(
                        val itemKindID: ID,
                        val itemFId: String = NoRecordStr.str,
                    ) : Route

                    @Serializable
                    data class VersionTolerancesDetails(
                        val itemKindId: ID,
                        val itemFId: String = NoRecordStr.str,
                        val referenceVersionFId: String = NoRecordStr.str,
                        val versionFId: String = NoRecordStr.str, val versionEditMode: Boolean = false,
                        val charGroupId: ID = NoRecord.num, val charSubGroupId: ID = NoRecord.num, val characteristicId: ID = NoRecord.num, val toleranceId: ID = NoRecord.num
                    ) : Route
                }
            }
        }

        @Serializable
        data object AllInvestigations : Route {
            @Serializable
            data class AllInvestigationsList(val orderId: ID = NoRecord.num, val subOrderId: ID = NoRecord.num) : Route

            @Serializable
            data class OrderAddEdit(val orderId: ID = NoRecord.num) : Route

            @Serializable
            data class SubOrderAddEdit(val orderId: ID, val subOrderId: ID = NoRecord.num) : Route
        }

        @Serializable
        data object ProcessControl : Route {
            @Serializable
            data class ProcessControlList(val orderId: ID = NoRecord.num, val subOrderId: ID = NoRecord.num) : Route

            @Serializable
            data class SubOrderAddEdit(val orderId: ID, val subOrderId: ID = NoRecord.num) : Route
        }

        @Serializable
        data object ScrapLevel : Route {
            @Serializable
            data object ScrapLevelList : Route
        }

        @Serializable
        data object Settings : Route {
            @Serializable
            data object UserDetails : Route

            @Serializable
            data class EditUserDetails(val userEditMode: Boolean = false) : Route
        }
    }

    companion object {

        const val DOMAIN = "https://qm.simple.com"

        fun String.withArgs(vararg args: String): String {
            val link = this
            return buildString {
                append(link.split("/{")[0])
                args.forEach { arg ->
                    append("/$arg")
                }
            }
        }

        fun String.withOpts(vararg args: String): String {
            val link = this
            val list = link.getParamsNames()
            var index = 0

            return buildString {
                append(link.split("?")[0])
                args.forEach { arg ->
                    if (index == 0) {
                        append("?${list[index]}=$arg")
                        index++
                    } else {
                        append("&${list[index]}=$arg")
                        index++
                    }
                }
            }
        }

        private fun String.getParamsNames(): List<String> {
            val list = mutableListOf<String>()
            val rawList = this.split('?')[1].split('&')
            rawList.forEach { item ->
                if (item.find { it == '{' } != null) {
                    list.add(item.substringAfter('{').substringBefore('}'))
                }
            }
            return list.toList()
        }
    }
}