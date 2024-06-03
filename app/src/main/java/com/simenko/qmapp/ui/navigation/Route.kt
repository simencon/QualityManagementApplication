package com.simenko.qmapp.ui.navigation

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavDeepLink
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.simenko.qmapp.domain.EmptyString
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.domain.NoRecordStr

const val MAIN_ROUTE = "main"
const val TEAM_ROUTE = "team"
const val COMPANY_STRUCTURE_ROUTE = "company_structure"
const val PRODUCTS_ROUTE = "products"
const val SETTINGS_ROUTE = "settings"

object NavRouteName {
    //--------------------------------------------------------------------
    //--------------------------------------------------------------------

    //--------------------------------------------------------------------
    //--------------------------------------------------------------------
    const val company_profile = "company_profile"

    const val employees = "employees"
    const val employee_add_edit = "employee_add_edit"
    const val users = "users"
    const val edit_user = "edit_user"
    const val requests = "requests"
    const val authorize_user = "authorize_user"

    const val structure_view = "structure_view"
    const val department_add_edit = "department_add_edit"
    const val sub_department_add_edit = "sub_department_add_edit"
    const val channel_add_edit = "channel_add_edit"
    const val line_add_edit = "line_add_edit"
    const val operation_add_edit = "operation_add_edit"

    const val product_lines = "product_lines"
    const val product_line_characteristics = "product_line_characteristics"
    const val product_line_keys = "product_line_keys"
    const val product_kinds = "product_kinds"
    const val product_kind_keys = "product_kind_keys"
    const val product_kind_characteristics = "product_kind_characteristics"
    const val product_specification = "product_specification"
    const val component_kind_keys = "component_kind_keys"
    const val component_kind_characteristics = "component_kind_characteristics"
    const val component_stage_kind_keys = "component_kind_keys"
    const val component_stage_kind_characteristics = "component_stage_kind_characteristics"

    const val product_list = "product_list"
    const val version_tolerances = "version_tolerances"

    const val product_line_add_edit = "product_line_add_edit"
    const val product_kind_add_edit = "product_kind_add_edit"
    const val component_kind_add_edit = "component_kind_add_edit"
    const val component_stage_kind_add_edit = "component_stage_kind_add_edit"
    const val product_line_char_sub_group_add_edit = "product_line_char_sub_group_add_edit"


    const val scrap_level = "scrap_level"

    //    const val settings = "settings"
    const val user_details = "user_details"
    const val edit_user_details = "edit_user_details"
    //--------------------------------------------------------------------
    //--------------------------------------------------------------------
}

object NavArguments {
    const val domain = "https://qm.simple.com"

    const val userEditMode = "userEditMode"

    const val fullName = "name"
    const val message = "message"

    const val employeeId = "employeeId"
    const val userId = "userId"

    const val companyId = "companyId"
    const val departmentId = "departmentId"
    const val subDepartmentId = "subDepartmentId"
    const val channelId = "channelId"
    const val lineId = "lineId"
    const val operationId = "operationId"

    const val productLineId = "productLineId"

    const val charGroupId = "charGroupId"
    const val charSubGroupId = "charSubGroupId"
    const val characteristicId = "characteristicId"
    const val metricId = "metricId"
    const val productLineKeyId = "productLineKeyId"

    const val productKindId = "productKindId"
    const val productKindKeyId = "productKindKeyId"
    const val componentKindId = "componentKindId"
    const val componentKindKeyId = "componentKindKeyId"
    const val componentStageKindId = "componentStageKindId"
    const val componentStageKindKeyId = "componentKindKeyId"

    const val productId = "productId"
    const val componentId = "componentId"
    const val componentStageId = "componentStageId"
    const val versionFId = "versionFId"
    const val versionEditMode = "versionEditMode"
    const val toleranceId = "toleranceId"

    const val orderId = "orderId"
    const val subOrderId = "subOrderId"
}

sealed class Route(
    val link: String,
    val arguments: List<NamedNavArgument> = emptyList(),
    val deepLinks: List<NavDeepLink> = emptyList(),
    val route: String = EmptyString.str
) {
    data object Main : Route(link = MAIN_ROUTE) {
        data object CompanyProfile : Route(link = NavRouteName.company_profile, route = MAIN_ROUTE)
        data object Team : Route(link = TEAM_ROUTE, route = MAIN_ROUTE) {
            data object Employees : Route(
                link = "${NavRouteName.employees}${arg(NavArguments.employeeId)}",
                arguments = listOf(
                    navArgument(name = NavArguments.employeeId) {
                        type = NavType.LongType
                        defaultValue = NoRecord.num
                    }
                ),
                route = TEAM_ROUTE
            )

            data object EmployeeAddEdit : Route(
                link = "${NavRouteName.employee_add_edit}${arg(NavArguments.employeeId)}",
                arguments = listOf(
                    navArgument(name = NavArguments.employeeId) {
                        type = NavType.LongType
                        defaultValue = NoRecord.num
                    }
                ),
                route = TEAM_ROUTE
            )

            data object Users : Route(
                link = "${NavRouteName.users}${arg(NavArguments.userId)}",
                arguments = listOf(
                    navArgument(name = NavArguments.userId) {
                        type = NavType.StringType
                        defaultValue = NoRecordStr.str
                    }
                ),
                route = TEAM_ROUTE
            )

            data object EditUser : Route(
                link = "${NavRouteName.edit_user}${arg(NavArguments.userId)}",
                deepLinks = listOf(
                    navDeepLink {
                        uriPattern = "${NavArguments.domain}/$TEAM_ROUTE/${NavRouteName.users}/${NavRouteName.edit_user}${arg(NavArguments.userId)}"
                        action = Intent.ACTION_VIEW
                    }
                ),
                arguments = listOf(
                    navArgument(name = NavArguments.userId) {
                        type = NavType.StringType
                        defaultValue = NoRecordStr.str
                    }
                ),
                route = TEAM_ROUTE
            )

            data object Requests : Route(
                link = "${NavRouteName.requests}${arg(NavArguments.userId)}",
                arguments = listOf(
                    navArgument(name = NavArguments.userId) {
                        type = NavType.StringType
                        defaultValue = NoRecordStr.str
                    }
                ),
                route = TEAM_ROUTE
            )

            data object AuthorizeUser : Route(
                link = "${NavRouteName.authorize_user}${arg(NavArguments.userId)}",
                deepLinks = listOf(
                    navDeepLink {
                        uriPattern = "${NavArguments.domain}/$TEAM_ROUTE/${NavRouteName.requests}/${NavRouteName.authorize_user}${arg(NavArguments.userId)}"
                        action = Intent.ACTION_VIEW
                    }
                ),
                arguments = listOf(
                    navArgument(name = NavArguments.userId) {
                        type = NavType.StringType
                        defaultValue = NoRecordStr.str
                    }
                ),
                route = TEAM_ROUTE
            )
        }

        data object CompanyStructure : Route(link = COMPANY_STRUCTURE_ROUTE + "?${opt(NavArguments.companyId)}", route = MAIN_ROUTE) {
            data object StructureView : Route(
                link = NavRouteName.structure_view +
                        "?${opt(NavArguments.companyId)}&${opt(NavArguments.departmentId)}&${opt(NavArguments.subDepartmentId)}" +
                        "&${opt(NavArguments.channelId)}&${opt(NavArguments.lineId)}&${opt(NavArguments.operationId)}",
                deepLinks = listOf(
                    navDeepLink {
                        uriPattern = "${NavArguments.domain}/${NavRouteName.structure_view}" +
                                "?${opt(NavArguments.companyId)}&${opt(NavArguments.departmentId)}&${opt(NavArguments.subDepartmentId)}" +
                                "&${opt(NavArguments.channelId)}&${opt(NavArguments.lineId)}&${opt(NavArguments.operationId)}"
                        action = Intent.ACTION_VIEW
                    }
                ),
                arguments = listOf(
                    navArgument(NavArguments.companyId) {
                        type = NavType.LongType
                        defaultValue = NoRecord.num
                    },
                    navArgument(NavArguments.departmentId) {
                        type = NavType.LongType
                        defaultValue = NoRecord.num
                    },
                    navArgument(NavArguments.subDepartmentId) {
                        type = NavType.LongType
                        defaultValue = NoRecord.num
                    },
                    navArgument(NavArguments.channelId) {
                        type = NavType.LongType
                        defaultValue = NoRecord.num
                    },
                    navArgument(NavArguments.lineId) {
                        type = NavType.LongType
                        defaultValue = NoRecord.num
                    },
                    navArgument(NavArguments.operationId) {
                        type = NavType.LongType
                        defaultValue = NoRecord.num
                    }
                ),
                route = COMPANY_STRUCTURE_ROUTE + "?${opt(NavArguments.companyId)}"
            )

            data object DepartmentAddEdit : Route(
                link = "${NavRouteName.department_add_edit}${arg(NavArguments.companyId)}${arg(NavArguments.departmentId)}",
                deepLinks = listOf(
                    navDeepLink {
                        uriPattern = "${NavArguments.domain}/$COMPANY_STRUCTURE_ROUTE/${NavRouteName.department_add_edit}${arg(NavArguments.companyId)}${arg(NavArguments.departmentId)}"
                        action = Intent.ACTION_VIEW
                    }
                ),
                arguments = listOf(
                    navArgument(NavArguments.companyId) {
                        type = NavType.LongType
                        defaultValue = NoRecord.num
                    },
                    navArgument(NavArguments.departmentId) {
                        type = NavType.LongType
                        defaultValue = NoRecord.num
                    }
                ),
                route = COMPANY_STRUCTURE_ROUTE + "?${opt(NavArguments.companyId)}"
            )

            data object SubDepartmentAddEdit : Route(
                link = "${NavRouteName.sub_department_add_edit}${arg(NavArguments.departmentId)}${arg(NavArguments.subDepartmentId)}",
                deepLinks = listOf(
                    navDeepLink {
                        uriPattern = "${NavArguments.domain}/$COMPANY_STRUCTURE_ROUTE/${NavRouteName.sub_department_add_edit}${arg(NavArguments.departmentId)}${arg(NavArguments.subDepartmentId)}"
                        action = Intent.ACTION_VIEW
                    }
                ),
                arguments = listOf(
                    navArgument(NavArguments.departmentId) {
                        type = NavType.LongType
                        defaultValue = NoRecord.num
                    },
                    navArgument(NavArguments.subDepartmentId) {
                        type = NavType.LongType
                        defaultValue = NoRecord.num
                    }
                ),
                route = COMPANY_STRUCTURE_ROUTE + "?${opt(NavArguments.companyId)}"
            )

            data object ChannelAddEdit : Route(
                link = "${NavRouteName.channel_add_edit}${arg(NavArguments.subDepartmentId)}${arg(NavArguments.channelId)}",
                deepLinks = listOf(
                    navDeepLink {
                        uriPattern = "${NavArguments.domain}/$COMPANY_STRUCTURE_ROUTE/${NavRouteName.channel_add_edit}${arg(NavArguments.subDepartmentId)}${arg(NavArguments.channelId)}"
                        action = Intent.ACTION_VIEW
                    }
                ),
                arguments = listOf(
                    navArgument(NavArguments.subDepartmentId) {
                        type = NavType.LongType
                        defaultValue = NoRecord.num
                    },
                    navArgument(NavArguments.channelId) {
                        type = NavType.LongType
                        defaultValue = NoRecord.num
                    }
                ),
                route = COMPANY_STRUCTURE_ROUTE + "?${opt(NavArguments.companyId)}"
            )

            data object LineAddEdit : Route(
                link = "${NavRouteName.line_add_edit}${arg(NavArguments.channelId)}${arg(NavArguments.lineId)}",
                deepLinks = listOf(
                    navDeepLink {
                        uriPattern = "${NavArguments.domain}/$COMPANY_STRUCTURE_ROUTE/${NavRouteName.line_add_edit}${arg(NavArguments.channelId)}${arg(NavArguments.lineId)}"
                        action = Intent.ACTION_VIEW
                    }
                ),
                arguments = listOf(
                    navArgument(NavArguments.channelId) {
                        type = NavType.LongType
                        defaultValue = NoRecord.num
                    },
                    navArgument(NavArguments.lineId) {
                        type = NavType.LongType
                        defaultValue = NoRecord.num
                    }
                ),
                route = COMPANY_STRUCTURE_ROUTE + "?${opt(NavArguments.companyId)}"
            )

            data object OperationAddEdit : Route(
                link = "${NavRouteName.operation_add_edit}${arg(NavArguments.lineId)}${arg(NavArguments.operationId)}",
                deepLinks = listOf(
                    navDeepLink {
                        uriPattern = "${NavArguments.domain}/$COMPANY_STRUCTURE_ROUTE/${NavRouteName.operation_add_edit}${arg(NavArguments.lineId)}${arg(NavArguments.operationId)}"
                        action = Intent.ACTION_VIEW
                    }
                ),
                arguments = listOf(
                    navArgument(NavArguments.lineId) {
                        type = NavType.LongType
                        defaultValue = NoRecord.num
                    },
                    navArgument(NavArguments.operationId) {
                        type = NavType.LongType
                        defaultValue = NoRecord.num
                    }
                ),
                route = COMPANY_STRUCTURE_ROUTE + "?${opt(NavArguments.companyId)}"
            )
        }

        data object Products : Route(link = PRODUCTS_ROUTE + "?${opt(NavArguments.companyId)}", route = MAIN_ROUTE) {
            data object ProductLines : Route(
                link = NavRouteName.product_lines + "?${opt(NavArguments.companyId)}&${opt(NavArguments.productLineId)}",
                deepLinks = listOf(
                    navDeepLink {
                        uriPattern = "${NavArguments.domain}/${NavRouteName.product_lines}" + "?${opt(NavArguments.companyId)}&${opt(NavArguments.productLineId)}"
                        action = Intent.ACTION_VIEW
                    }
                ),
                arguments = listOf(
                    navArgument(NavArguments.companyId) {
                        type = NavType.LongType
                        defaultValue = NoRecord.num
                    },
                    navArgument(NavArguments.productLineId) {
                        type = NavType.LongType
                        defaultValue = NoRecord.num
                    }
                ),
                route = PRODUCTS_ROUTE + "?${opt(NavArguments.companyId)}"
            ) {
                data object Characteristics : Route(
                    link = NavRouteName.product_line_characteristics +
                            "?${opt(NavArguments.productLineId)}&${opt(NavArguments.charGroupId)}&${opt(NavArguments.charSubGroupId)}" +
                            "&${opt(NavArguments.characteristicId)}&${opt(NavArguments.metricId)}",
                    deepLinks = listOf(
                        navDeepLink {
                            uriPattern = "${NavArguments.domain}/${NavRouteName.product_lines}/${NavRouteName.product_line_characteristics}" +
                                    "?${opt(NavArguments.productLineId)}&${opt(NavArguments.charGroupId)}&${opt(NavArguments.charSubGroupId)}" +
                                    "&${opt(NavArguments.characteristicId)}&${opt(NavArguments.metricId)}"
                            action = Intent.ACTION_VIEW
                        }
                    ),
                    arguments = listOf(
                        navArgument(NavArguments.productLineId) {
                            type = NavType.LongType
                            defaultValue = NoRecord.num
                        },
                        navArgument(NavArguments.charGroupId) {
                            type = NavType.LongType
                            defaultValue = NoRecord.num
                        },
                        navArgument(NavArguments.charSubGroupId) {
                            type = NavType.LongType
                            defaultValue = NoRecord.num
                        },
                        navArgument(NavArguments.characteristicId) {
                            type = NavType.LongType
                            defaultValue = NoRecord.num
                        },
                        navArgument(NavArguments.metricId) {
                            type = NavType.LongType
                            defaultValue = NoRecord.num
                        }
                    ),
                    route = NavRouteName.product_lines
                ) {
                    data object CharSubGroupAddEdit: Route(
                        link = "${NavRouteName.product_line_char_sub_group_add_edit}${arg(NavArguments.charGroupId)}${arg(NavArguments.charSubGroupId)}",
                        deepLinks = listOf(
                            navDeepLink {
                                uriPattern = "${NavArguments.domain}/${NavRouteName.product_line_characteristics}/${NavRouteName.product_line_char_sub_group_add_edit}${arg(NavArguments.charGroupId)}${arg(NavArguments.charSubGroupId)}"
                                action = Intent.ACTION_VIEW
                            }
                        ),
                        arguments = listOf(
                            navArgument(NavArguments.charGroupId) {
                                type = NavType.LongType
                                defaultValue = NoRecord.num
                            },
                            navArgument(NavArguments.charSubGroupId) {
                                type = NavType.LongType
                                defaultValue = NoRecord.num
                            }
                        ),
                        route = NavRouteName.product_line_characteristics
                    )
                }

                data object ProductLineKeys : Route(
                    link = NavRouteName.product_line_keys + "?${opt(NavArguments.productLineId)}&${opt(NavArguments.productLineKeyId)}",
                    deepLinks = listOf(
                        navDeepLink {
                            uriPattern =
                                "${NavArguments.domain}/${NavRouteName.product_lines}/${NavRouteName.product_line_keys}?${opt(NavArguments.productLineId)}&${opt(NavArguments.productLineKeyId)}"
                            action = Intent.ACTION_VIEW
                        }
                    ),
                    arguments = listOf(
                        navArgument(NavArguments.productLineId) {
                            type = NavType.LongType
                            defaultValue = NoRecord.num
                        },
                        navArgument(NavArguments.productLineKeyId) {
                            type = NavType.LongType
                            defaultValue = NoRecord.num
                        }
                    ),
                    route = NavRouteName.product_lines
                )

                data object ProductKinds : Route(
                    link = NavRouteName.product_kinds + "?${opt(NavArguments.productLineId)}&${opt(NavArguments.productKindId)}",
                    deepLinks = listOf(
                        navDeepLink {
                            uriPattern = "${NavArguments.domain}/${NavRouteName.product_lines}/${NavRouteName.product_kinds}" +
                                    "?${opt(NavArguments.productLineId)}&${opt(NavArguments.productKindId)}"
                            action = Intent.ACTION_VIEW
                        }
                    ),
                    arguments = listOf(
                        navArgument(NavArguments.productLineId) {
                            type = NavType.LongType
                            defaultValue = NoRecord.num
                        },
                        navArgument(NavArguments.productKindId) {
                            type = NavType.LongType
                            defaultValue = NoRecord.num
                        }
                    ),
                    route = NavRouteName.product_lines
                ) {
                    data object ProductKindKeys : Route(
                        link = NavRouteName.product_kind_keys + "?${opt(NavArguments.productKindId)}&${opt(NavArguments.productKindKeyId)}",
                        deepLinks = listOf(
                            navDeepLink {
                                uriPattern = "${NavArguments.domain}/${NavRouteName.product_lines}/${NavRouteName.product_kinds}/${NavRouteName.product_kind_keys}" +
                                        "?${opt(NavArguments.productKindId)}&${opt(NavArguments.productKindKeyId)}"
                                action = Intent.ACTION_VIEW
                            }
                        ),
                        arguments = listOf(
                            navArgument(NavArguments.productKindId) {
                                type = NavType.LongType
                                defaultValue = NoRecord.num
                            },
                            navArgument(NavArguments.productKindKeyId) {
                                type = NavType.LongType
                                defaultValue = NoRecord.num
                            }
                        ),
                        route = NavRouteName.product_kinds
                    )

                    data object ProductKindCharacteristics : Route(
                        link = NavRouteName.product_kind_characteristics + "?${opt(NavArguments.productKindId)}&${opt(NavArguments.characteristicId)}",
                        deepLinks = listOf(
                            navDeepLink {
                                uriPattern = "${NavArguments.domain}/${NavRouteName.product_lines}/${NavRouteName.product_kinds}/${NavRouteName.product_kind_characteristics}" +
                                        "?${opt(NavArguments.productKindId)}&${opt(NavArguments.characteristicId)}"
                                action = Intent.ACTION_VIEW
                            }
                        ),
                        arguments = listOf(
                            navArgument(NavArguments.productKindId) {
                                type = NavType.LongType
                                defaultValue = NoRecord.num
                            },
                            navArgument(NavArguments.characteristicId) {
                                type = NavType.LongType
                                defaultValue = NoRecord.num
                            }
                        ),
                        route = NavRouteName.product_kinds
                    )

                    data object ProductSpecification : Route(
                        link = NavRouteName.product_specification + "?${opt(NavArguments.productKindId)}&${opt(NavArguments.componentKindId)}&${opt(NavArguments.componentStageKindId)}",
                        deepLinks = listOf(
                            navDeepLink {
                                uriPattern = "${NavArguments.domain}/${NavRouteName.product_lines}/${NavRouteName.product_kinds}/${NavRouteName.product_specification}" +
                                        "?${opt(NavArguments.productKindId)}&${opt(NavArguments.componentKindId)}&${opt(NavArguments.componentStageKindId)}"
                                action = Intent.ACTION_VIEW
                            }
                        ),
                        arguments = listOf(
                            navArgument(NavArguments.productKindId) {
                                type = NavType.LongType
                                defaultValue = NoRecord.num
                            },
                            navArgument(NavArguments.componentKindId) {
                                type = NavType.LongType
                                defaultValue = NoRecord.num
                            },
                            navArgument(NavArguments.componentStageKindId) {
                                type = NavType.LongType
                                defaultValue = NoRecord.num
                            }
                        ),
                        route = NavRouteName.product_kinds
                    ) {
                        data object ComponentKindKeys : Route(
                            link = NavRouteName.component_kind_keys + "?${opt(NavArguments.componentKindId)}&${opt(NavArguments.componentKindKeyId)}",
                            deepLinks = listOf(
                                navDeepLink {
                                    uriPattern =
                                        "${NavArguments.domain}/${NavRouteName.product_lines}/${NavRouteName.product_kinds}/${NavRouteName.product_specification}/${NavRouteName.component_kind_keys}" +
                                                "?${opt(NavArguments.componentKindId)}&${opt(NavArguments.componentKindKeyId)}"
                                    action = Intent.ACTION_VIEW
                                }
                            ),
                            arguments = listOf(
                                navArgument(NavArguments.componentKindId) {
                                    type = NavType.LongType
                                    defaultValue = NoRecord.num
                                },
                                navArgument(NavArguments.componentKindKeyId) {
                                    type = NavType.LongType
                                    defaultValue = NoRecord.num
                                }
                            ),
                            route = NavRouteName.product_specification
                        )

                        data object ComponentKindCharacteristics : Route(
                            link = NavRouteName.component_kind_characteristics + "?${opt(NavArguments.componentKindId)}&${opt(NavArguments.characteristicId)}",
                            deepLinks = listOf(
                                navDeepLink {
                                    uriPattern = "${NavArguments.domain}/${NavRouteName.product_lines}/${NavRouteName.product_kinds}/${NavRouteName.component_kind_characteristics}" +
                                            "?${opt(NavArguments.componentKindId)}&${opt(NavArguments.characteristicId)}"
                                    action = Intent.ACTION_VIEW
                                }
                            ),
                            arguments = listOf(
                                navArgument(NavArguments.componentKindId) {
                                    type = NavType.LongType
                                    defaultValue = NoRecord.num
                                },
                                navArgument(NavArguments.characteristicId) {
                                    type = NavType.LongType
                                    defaultValue = NoRecord.num
                                }
                            ),
                            route = NavRouteName.product_specification
                        )

                        data object ComponentStageKindKeys : Route(
                            link = NavRouteName.component_stage_kind_keys + "?${opt(NavArguments.componentStageKindId)}&${opt(NavArguments.componentStageKindKeyId)}",
                            deepLinks = listOf(
                                navDeepLink {
                                    uriPattern =
                                        "${NavArguments.domain}/${NavRouteName.product_lines}/${NavRouteName.product_kinds}/${NavRouteName.product_specification}/${NavRouteName.component_stage_kind_keys}" +
                                                "?${opt(NavArguments.componentStageKindId)}&${opt(NavArguments.componentStageKindKeyId)}"
                                    action = Intent.ACTION_VIEW
                                }
                            ),
                            arguments = listOf(
                                navArgument(NavArguments.componentStageKindId) {
                                    type = NavType.LongType
                                    defaultValue = NoRecord.num
                                },
                                navArgument(NavArguments.componentStageKindKeyId) {
                                    type = NavType.LongType
                                    defaultValue = NoRecord.num
                                }
                            ),
                            route = NavRouteName.product_specification
                        )

                        data object ComponentStageKindCharacteristics : Route(
                            link = NavRouteName.component_stage_kind_characteristics + "?${opt(NavArguments.componentStageKindId)}&${opt(NavArguments.characteristicId)}",
                            deepLinks = listOf(
                                navDeepLink {
                                    uriPattern = "${NavArguments.domain}/${NavRouteName.product_lines}/${NavRouteName.product_kinds}/${NavRouteName.component_stage_kind_characteristics}" +
                                            "?${opt(NavArguments.componentStageKindId)}&${opt(NavArguments.characteristicId)}"
                                    action = Intent.ACTION_VIEW
                                }
                            ),
                            arguments = listOf(
                                navArgument(NavArguments.componentStageKindId) {
                                    type = NavType.LongType
                                    defaultValue = NoRecord.num
                                },
                                navArgument(NavArguments.characteristicId) {
                                    type = NavType.LongType
                                    defaultValue = NoRecord.num
                                }
                            ),
                            route = NavRouteName.product_specification
                        )
                    }

                    data object ProductList : Route(
                        link = NavRouteName.product_list +
                                "?${opt(NavArguments.productKindId)}&${opt(NavArguments.productId)}" +
                                "&${opt(NavArguments.componentKindId)}&${opt(NavArguments.componentId)}&${opt(NavArguments.componentStageKindId)}&${opt(NavArguments.componentStageId)}" +
                                "&${opt(NavArguments.versionFId)}",
                        deepLinks = listOf(
                            navDeepLink {
                                uriPattern = "${NavArguments.domain}/${NavRouteName.product_lines}/${NavRouteName.product_kinds}/${NavRouteName.product_list}" +
                                        "?${opt(NavArguments.productKindId)}&${opt(NavArguments.productId)}" +
                                        "&${opt(NavArguments.componentKindId)}&${opt(NavArguments.componentId)}&${opt(NavArguments.componentStageKindId)}&${opt(NavArguments.componentStageId)}" +
                                        "&${opt(NavArguments.versionFId)}"
                                action = Intent.ACTION_VIEW
                            }
                        ),
                        arguments = listOf(
                            navArgument(NavArguments.productKindId) {
                                type = NavType.LongType
                                defaultValue = NoRecord.num
                            },
                            navArgument(NavArguments.productId) {
                                type = NavType.LongType
                                defaultValue = NoRecord.num
                            },
                            navArgument(NavArguments.componentKindId) {
                                type = NavType.LongType
                                defaultValue = NoRecord.num
                            },
                            navArgument(NavArguments.componentId) {
                                type = NavType.LongType
                                defaultValue = NoRecord.num
                            },
                            navArgument(NavArguments.componentStageKindId) {
                                type = NavType.LongType
                                defaultValue = NoRecord.num
                            },
                            navArgument(NavArguments.componentStageId) {
                                type = NavType.LongType
                                defaultValue = NoRecord.num
                            },
                            navArgument(NavArguments.versionFId) {
                                type = NavType.StringType
                                defaultValue = NoRecordStr.str
                            }
                        ),
                        route = NavRouteName.product_kinds
                    ) {
                        data object VersionTolerances : Route(
                            link = NavRouteName.version_tolerances +
                                    "?${opt(NavArguments.versionFId)}&${opt(NavArguments.versionEditMode)}" +
                                    "&${opt(NavArguments.charGroupId)}&${opt(NavArguments.charSubGroupId)}&${opt(NavArguments.characteristicId)}&${opt(NavArguments.toleranceId)}",
                            deepLinks = listOf(
                                navDeepLink {
                                    uriPattern = NavArguments.domain +
                                            "/${NavRouteName.product_lines}/${NavRouteName.product_kinds}/${NavRouteName.product_list}/${NavRouteName.version_tolerances}" +
                                            "?${opt(NavArguments.versionFId)}&${opt(NavArguments.versionEditMode)}" +
                                            "&${opt(NavArguments.charGroupId)}&${opt(NavArguments.charSubGroupId)}&${opt(NavArguments.characteristicId)}&${opt(NavArguments.toleranceId)}"
                                    action = Intent.ACTION_VIEW
                                }
                            ),
                            arguments = listOf(
                                navArgument(NavArguments.versionFId) {
                                    type = NavType.StringType
                                    defaultValue = NoRecordStr.str
                                },
                                navArgument(NavArguments.versionEditMode) {
                                    type = NavType.BoolType
                                    defaultValue = false
                                },
                                navArgument(NavArguments.charGroupId) {
                                    type = NavType.LongType
                                    defaultValue = NoRecord.num
                                },
                                navArgument(NavArguments.charSubGroupId) {
                                    type = NavType.LongType
                                    defaultValue = NoRecord.num
                                },
                                navArgument(NavArguments.characteristicId) {
                                    type = NavType.LongType
                                    defaultValue = NoRecord.num
                                },
                                navArgument(NavArguments.toleranceId) {
                                    type = NavType.LongType
                                    defaultValue = NoRecord.num
                                }
                            ),
                            route = NavRouteName.product_list
                        )
                    }
                }
            }

            data object ProductLineAddEdit : Route(
                link = "${NavRouteName.product_line_add_edit}${arg(NavArguments.companyId)}${arg(NavArguments.productLineId)}",
                deepLinks = listOf(
                    navDeepLink {
                        uriPattern = "${NavArguments.domain}/$PRODUCTS_ROUTE/${NavRouteName.product_line_add_edit}${arg(NavArguments.companyId)}${arg(NavArguments.productLineId)}"
                        action = Intent.ACTION_VIEW
                    }
                ),
                arguments = listOf(
                    navArgument(NavArguments.companyId) {
                        type = NavType.LongType
                        defaultValue = NoRecord.num
                    },
                    navArgument(NavArguments.productLineId) {
                        type = NavType.LongType
                        defaultValue = NoRecord.num
                    }
                ),
                route = PRODUCTS_ROUTE + "?${opt(NavArguments.companyId)}"
            )

            data object ProductKindAddEdit : Route(
                link = "${NavRouteName.product_kind_add_edit}${arg(NavArguments.productLineId)}${arg(NavArguments.productKindId)}",
                deepLinks = listOf(
                    navDeepLink {
                        uriPattern = "${NavArguments.domain}/$PRODUCTS_ROUTE/${NavRouteName.product_kind_add_edit}${arg(NavArguments.productLineId)}${arg(NavArguments.productKindId)}"
                        action = Intent.ACTION_VIEW
                    }
                ),
                arguments = listOf(
                    navArgument(NavArguments.productLineId) {
                        type = NavType.LongType
                        defaultValue = NoRecord.num
                    },
                    navArgument(NavArguments.productKindId) {
                        type = NavType.LongType
                        defaultValue = NoRecord.num
                    }
                ),
                route = PRODUCTS_ROUTE + "?${opt(NavArguments.companyId)}"
            )

            data object ComponentKindAddEdit : Route(
                link = "${NavRouteName.component_kind_add_edit}${arg(NavArguments.productKindId)}${arg(NavArguments.componentKindId)}",
                deepLinks = listOf(
                    navDeepLink {
                        uriPattern = "${NavArguments.domain}/$PRODUCTS_ROUTE/${NavRouteName.component_kind_add_edit}${arg(NavArguments.productKindId)}${arg(NavArguments.componentKindId)}"
                        action = Intent.ACTION_VIEW
                    }
                ),
                arguments = listOf(
                    navArgument(NavArguments.productKindId) {
                        type = NavType.LongType
                        defaultValue = NoRecord.num
                    },
                    navArgument(NavArguments.componentKindId) {
                        type = NavType.LongType
                        defaultValue = NoRecord.num
                    }
                ),
                route = PRODUCTS_ROUTE + "?${opt(NavArguments.companyId)}"
            )

            data object ComponentStageKindAddEdit : Route(
                link = "${NavRouteName.component_stage_kind_add_edit}${arg(NavArguments.componentKindId)}${arg(NavArguments.componentStageKindId)}",
                deepLinks = listOf(
                    navDeepLink {
                        uriPattern =
                            "${NavArguments.domain}/$PRODUCTS_ROUTE/${NavRouteName.component_stage_kind_add_edit}${arg(NavArguments.componentKindId)}${arg(NavArguments.componentStageKindId)}"
                        action = Intent.ACTION_VIEW
                    }
                ),
                arguments = listOf(
                    navArgument(NavArguments.componentKindId) {
                        type = NavType.LongType
                        defaultValue = NoRecord.num
                    },
                    navArgument(NavArguments.componentStageKindId) {
                        type = NavType.LongType
                        defaultValue = NoRecord.num
                    }
                ),
                route = PRODUCTS_ROUTE + "?${opt(NavArguments.companyId)}"
            )
        }

        data object ScrapLevel : Route(link = NavRouteName.scrap_level, route = MAIN_ROUTE)

        data object Settings : Route(link = SETTINGS_ROUTE, route = MAIN_ROUTE) {
            data object UserDetails : Route(link = NavRouteName.user_details, route = SETTINGS_ROUTE)
            data object EditUserDetails : Route(
                link = "${NavRouteName.edit_user_details}${arg(NavArguments.userEditMode)}",
                arguments = listOf(
                    navArgument(name = NavArguments.userEditMode) {
                        type = NavType.BoolType
                        defaultValue = true
                    }
                ),
                route = SETTINGS_ROUTE
            )
        }
    }

    fun withArgs(vararg args: String) = link.withArgs(*args)

    fun withOpts(vararg args: String) = link.withOpts(*args)

    companion object {
        fun opt(p: String) = "$p={$p}"
        fun arg(p: String) = "/{$p}"

        fun String.withArgs(vararg args: String): String {
            val link = this
            return buildString {
                append(link.split("/")[0])
                args.forEach { arg ->
                    append("/$arg")
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
    }
}

@Composable
inline fun <reified T : ViewModel> NavBackStackEntry?.viewModel(): T? = this?.let {
    viewModel(viewModelStoreOwner = it)
}
