package com.simenko.qmapp.ui.navigation

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.simenko.qmapp.domain.EmptyString
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.domain.NoRecordStr

const val MAIN_ROUTE = "main"
const val PRODUCTS_ROUTE = "products"

object NavRouteName {
    //--------------------------------------------------------------------
    //--------------------------------------------------------------------

    //--------------------------------------------------------------------
    //--------------------------------------------------------------------

    const val product_lines = "product_lines"
    const val product_line_characteristics = "product_line_characteristics"
    const val product_kinds = "product_kinds"

    const val product_list = "product_list"
    const val version_tolerances = "version_tolerances"

    const val product_line_char_sub_group_add_edit = "product_line_char_sub_group_add_edit"

    //--------------------------------------------------------------------
    //--------------------------------------------------------------------
}

object NavArguments {
    const val domain = "https://qm.simple.com"

    const val fullName = "name"
    const val message = "message"

    const val companyId = "companyId"

    const val productLineId = "productLineId"

    const val charGroupId = "charGroupId"
    const val charSubGroupId = "charSubGroupId"
    const val characteristicId = "characteristicId"
    const val metricId = "metricId"

    const val productKindId = "productKindId"
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
}

sealed class Route(
    val link: String,
    val arguments: List<NamedNavArgument> = emptyList(),
    val deepLinks: List<NavDeepLink> = emptyList(),
    val route: String = EmptyString.str
) {
    data object Main : Route(link = MAIN_ROUTE) {
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
                    data object CharSubGroupAddEdit : Route(
                        link = "${NavRouteName.product_line_char_sub_group_add_edit}${arg(NavArguments.charGroupId)}${arg(NavArguments.charSubGroupId)}",
                        deepLinks = listOf(
                            navDeepLink {
                                uriPattern = "${NavArguments.domain}/${NavRouteName.product_line_characteristics}/${NavRouteName.product_line_char_sub_group_add_edit}${arg(NavArguments.charGroupId)}${
                                    arg(NavArguments.charSubGroupId)
                                }"
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



        }
    }

    fun withArgs(vararg args: String) = link.withArgs(*args)

    fun withOpts(vararg args: String) = link.withOpts(*args)

    companion object {
        fun opt(p: String) = "$p={$p}"
        fun arg(p: String) = "/{$p}"

        private fun String.withArgs(vararg args: String): String {
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

        private fun String.withOpts(vararg args: String): String {
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
