package com.simenko.qmapp.ui.navigation

import androidx.navigation.NavHostController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import javax.inject.Inject

class AppNavigatorImpl @Inject constructor() : AppNavigator {
    override val navigationChannel = Channel<NavigationIntent>(
        capacity = Int.MAX_VALUE,
        onBufferOverflow = BufferOverflow.DROP_LATEST,
    )

    override suspend fun navigateBack(route: String?, inclusive: Boolean) {
        navigationChannel.send(
            NavigationIntent.NavigateBack(
                route = route,
                inclusive = inclusive
            )
        )
    }

    override fun tryNavigateBack(route: String?, inclusive: Boolean) {
        navigationChannel.trySend(
            NavigationIntent.NavigateBack(
                route = route,
                inclusive = inclusive
            )
        )
    }

    override suspend fun navigateTo(
        route: String,
        popUpToRoute: String?,
        popUpToId: Int?,
        inclusive: Boolean,
        isSingleTop: Boolean
    ) {
        navigationChannel.send(
            NavigationIntent.NavigateTo(
                route = route,
                popUpToRoute = popUpToRoute,
                popUpToId = popUpToId,
                inclusive = inclusive,
                isSingleTop = isSingleTop,
            )
        )
    }

    override fun tryNavigateTo(
        route: String,
        popUpToRoute: String?,
        popUpToId: Int?,
        inclusive: Boolean,
        isSingleTop: Boolean
    ) {
        navigationChannel.trySend(
            NavigationIntent.NavigateTo(
                route = route,
                popUpToRoute = popUpToRoute,
                popUpToId = popUpToId,
                inclusive = inclusive,
                isSingleTop = isSingleTop,
            )
        )
    }

    override fun tryNavigateTo(route: RouteCompose, popUpToRoute: RouteCompose?, popUpToId: Int?, inclusive: Boolean, isSingleTop: Boolean) {
        navigationChannel.trySend(
            NavigationIntent.NavigateToRoute(
                route = route,
                popUpToRoute = popUpToRoute,
                popUpToId = popUpToId,
                inclusive = inclusive,
                isSingleTop = isSingleTop
            )
        )
    }

    companion object {
        fun Channel<NavigationIntent>.subscribeNavigationEvents(viewModelScope: CoroutineScope, navController: NavHostController) {
            viewModelScope.launch {
                consumeEach { navIntent ->
                    when (navIntent) {
                        is NavigationIntent.NavigateBack -> {
                            if (navIntent.route != null) {
                                navController.popBackStack(navIntent.route, navIntent.inclusive)
                            } else {
                                navController.popBackStack()
                            }
                        }

                        is NavigationIntent.NavigateTo -> {
                            navController.navigate(navIntent.route) {
                                launchSingleTop = navIntent.isSingleTop
                                if (navIntent.popUpToRoute != null) {
                                    popUpTo(navIntent.popUpToRoute) { inclusive = navIntent.inclusive }
                                } else if (navIntent.popUpToId != null) {
                                    popUpTo(navIntent.popUpToId) { inclusive = navIntent.inclusive }
                                }
                            }
                        }

                        is NavigationIntent.NavigateToRoute -> {
                            navController.navigate(navIntent.route) {
                                launchSingleTop = navIntent.isSingleTop
                                if (navIntent.popUpToRoute != null) {
                                    popUpTo(navIntent.popUpToRoute) { inclusive = navIntent.inclusive }
                                } else if (navIntent.popUpToId != null) {
                                    popUpTo(navIntent.popUpToId) { inclusive = navIntent.inclusive }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}