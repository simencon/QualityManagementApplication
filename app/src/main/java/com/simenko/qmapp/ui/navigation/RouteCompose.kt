package com.simenko.qmapp.ui.navigation

import androidx.navigation.NavDeepLink
import com.simenko.qmapp.domain.EmptyString
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.domain.NoRecordStr
import kotlinx.serialization.Serializable

sealed interface RouteCompose {

    val deepLinks: List<NavDeepLink> get() = emptyList()

    @Serializable
    data object LoggedOut : RouteCompose {
        @Serializable
        data object InitialScreen : RouteCompose

        @Serializable
        data object Registration : RouteCompose {
            @Serializable
            data class EnterDetails(val userEditMode: Boolean = false) : RouteCompose

            @Serializable
            data class TermsAndConditions(val fullName: String = EmptyString.str) : RouteCompose
        }

        @Serializable
        data class WaitingForValidation(val message: String = EmptyString.str) : RouteCompose

        @Serializable
        data object LogIn : RouteCompose
    }

    @Serializable
    data object Main : RouteCompose {
        @Serializable
        data object CompanyProfile : RouteCompose

        @Serializable
        data object Team : RouteCompose {
            @Serializable
            data class Employees(val employeeId: ID = NoRecord.num) : RouteCompose

            @Serializable
            data class EmployeeAddEdit(val employeeId: ID = NoRecord.num) : RouteCompose

            @Serializable
            data class Users(val userId: String = NoRecordStr.str) : RouteCompose

            @Serializable
            data class EditUser(val userId: String = NoRecordStr.str) : RouteCompose

            @Serializable
            data class Requests(val userId: String = NoRecordStr.str) : RouteCompose

            @Serializable
            data class AuthorizeUser(val userId: String = NoRecordStr.str) : RouteCompose
        }

        @Serializable

        data object CompanyStructure : RouteCompose {
            @Serializable
            data class StructureView(
                val companyId: ID = NoRecord.num, val departmentId: ID = NoRecord.num, val subDepartmentId: ID = NoRecord.num,
                val channelId: ID = NoRecord.num, val lineId: ID = NoRecord.num, val operationId: ID = NoRecord.num
            ) : RouteCompose

            @Serializable
            data class DepartmentAddEdit(val companyId: ID = NoRecord.num, val departmentId: ID = NoRecord.num) : RouteCompose

            @Serializable
            data class SubDepartmentAddEdit(val departmentId: ID = NoRecord.num, val subDepartmentId: ID = NoRecord.num) : RouteCompose

            @Serializable
            data class ChannelAddEdit(val subDepartmentId: ID = NoRecord.num, val channelId: ID = NoRecord.num) : RouteCompose

            @Serializable
            data class LineAddEdit(val channelId: ID = NoRecord.num, val lineId: ID = NoRecord.num) : RouteCompose

            @Serializable
            data class OperationAddEdit(val lineId: ID = NoRecord.num, val operationId: ID = NoRecord.num) : RouteCompose
        }

        @Serializable
        data object ProductLines : RouteCompose {
            @Serializable
            data class ProductLinesList(val companyId: ID = NoRecord.num, val productLineId: ID = NoRecord.num) : RouteCompose

            @Serializable
            data object Characteristics : RouteCompose {
                @Serializable
                data class CharacteristicsList(val productLineId: ID, val charGroupId: ID, val charSubGroupId: ID, val characteristicId: ID, val metricId: ID) : RouteCompose

                @Serializable
                data class CharSubGroupAddEdit(val charGroupId: ID, val charSubGroupId: ID = NoRecord.num) : RouteCompose
            }

            @Serializable
            data object ProductLineKeys : RouteCompose {
                @Serializable
                data class ProductLineKeysList(val productLineId: ID, val productLineKeyId: ID) : RouteCompose
            }

            @Serializable
            data object ProductKinds : RouteCompose {
                @Serializable
                data class ProductKindsList(val productLineId: ID, val productKindId: ID) : RouteCompose

                @Serializable
                data object ProductKindKeys : RouteCompose {
                    @Serializable
                    data class ProductKindKeysList(val productKindId: ID, val productKindKeyId: ID) : RouteCompose
                }

                @Serializable
                data object ProductKindCharacteristics : RouteCompose {
                    @Serializable
                    data class ProductKindCharacteristicsList(val productKindId: ID, val characteristicId: ID) : RouteCompose
                }

                @Serializable
                data object ProductSpecification : RouteCompose {
                    @Serializable
                    data class ProductSpecificationList(val productKindId: ID, val componentKindId: ID, val componentStageKindId: ID) : RouteCompose

                    @Serializable
                    data class ProductKindAddEdit(val productLineId: ID, val productKindId: ID = NoRecord.num) : RouteCompose

                    @Serializable
                    data class ComponentKindAddEdit(val productKindId: ID, val componentKindId: ID = NoRecord.num) : RouteCompose

                    @Serializable
                    data class ComponentStageKindAddEdit(val componentKindId: ID, val componentStageKindId: ID = NoRecord.num) : RouteCompose

                    @Serializable
                    data object ComponentKindKeys : RouteCompose {
                        @Serializable
                        data class ComponentKindKeysList(val componentKindId: ID, val componentKindKeyId: ID) : RouteCompose
                    }

                    @Serializable
                    data object ComponentKindCharacteristics : RouteCompose {
                        @Serializable
                        data class ProductSpecificationList(val componentKindId: ID, val characteristicId: ID) : RouteCompose
                    }

                    @Serializable
                    data object ComponentStageKindKeys : RouteCompose {
                        @Serializable
                        data class ComponentStageKindKeysList(val componentStageKindId: ID, val componentStageKindKeyId: ID) : RouteCompose
                    }

                    @Serializable
                    data object ComponentStageKindCharacteristics : RouteCompose {
                        @Serializable
                        data class ComponentStageKindCharacteristicsList(val componentStageKindId: ID, val characteristicId: ID) : RouteCompose
                    }
                }

                @Serializable
                data object Products : RouteCompose {
                    @Serializable
                    data class ProductsList(
                        val productKindId: ID, val productId: ID,
                        val componentKindId: ID, val componentId: ID, val componentStageKindId: ID, val componentStageId: ID,
                        val versionFId: String
                    ) : RouteCompose

                    @Serializable
                    data object VersionTolerances : RouteCompose {
                        @Serializable
                        data class VersionTolerancesDetails(
                            val versionFId: String, val versionEditMode: Boolean = false,
                            val charGroupId: ID, val charSubGroupId: ID, val characteristicId: ID, val toleranceId: ID
                        ) : RouteCompose
                    }
                }
            }
        }

        @Serializable
        data object AllInvestigations : RouteCompose {
            @Serializable
            data class AllInvestigationsList(val orderId: ID = NoRecord.num, val subOrderId: ID = NoRecord.num) : RouteCompose

            @Serializable
            data class OrderAddEdit(val orderId: ID = NoRecord.num) : RouteCompose

            @Serializable
            data class SubOrderAddEdit(val orderId: ID, val subOrderId: ID = NoRecord.num) : RouteCompose
        }

        @Serializable
        data object ProcessControl : RouteCompose {
            @Serializable
            data class ProcessControlList(val orderId: ID = NoRecord.num, val subOrderId: ID = NoRecord.num) : RouteCompose

            @Serializable
            data class SubOrderAddEdit(val orderId: ID, val subOrderId: ID = NoRecord.num) : RouteCompose
        }

        @Serializable
        data object ScrapLevel : RouteCompose {
            @Serializable
            data object ScrapLevelList : RouteCompose
        }

        @Serializable
        data object Settings : RouteCompose {
            @Serializable
            data object UserDetails : RouteCompose

            @Serializable
            data class EditUserDetails(val userEditMode: Boolean = false) : RouteCompose
        }
    }
}