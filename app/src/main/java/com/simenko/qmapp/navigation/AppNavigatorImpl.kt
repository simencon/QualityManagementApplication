package com.simenko.qmapp.navigation

import androidx.navigation.NavHostController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class AppNavigatorImpl @Inject constructor() : AppNavigator {
    override var navigationSharedFlow = MutableSharedFlow<NavigationIntent>(
        extraBufferCapacity = Int.MAX_VALUE,
        onBufferOverflow = BufferOverflow.DROP_LATEST
    )

    override suspend fun navigateBack(route: Route?, inclusive: Boolean) {
        navigationSharedFlow.emit(
            NavigationIntent.NavigateBack(
                route = route,
                inclusive = inclusive
            )
        )
    }

    override fun tryNavigateBack(route: Route?, inclusive: Boolean) {
        navigationSharedFlow.tryEmit(
            NavigationIntent.NavigateBack(
                route = route,
                inclusive = inclusive
            )
        )
    }

    override suspend fun navigateTo(
        route: Route,
        popUpToRoute: Route?,
        popUpToId: Int?,
        inclusive: Boolean,
        isSingleTop: Boolean
    ) {
        navigationSharedFlow.emit(
            NavigationIntent.NavigateToRoute(
                route = route,
                popUpToRoute = popUpToRoute,
                popUpToId = popUpToId,
                inclusive = inclusive,
                isSingleTop = isSingleTop,
            )
        )
    }

    override fun tryNavigateTo(route: Route, popUpToRoute: Route?, popUpToId: Int?, inclusive: Boolean, isSingleTop: Boolean) {
        navigationSharedFlow.tryEmit(
            NavigationIntent.NavigateToRoute(
                route = route,
                popUpToRoute = popUpToRoute,
                popUpToId = popUpToId,
                inclusive = inclusive,
                isSingleTop = isSingleTop
            )
        )
    }

    private var job: Job = Job()

    override fun subscribeNavigationEvents(coroutineScope: CoroutineScope, navController: NavHostController) {
        job.cancel()
        job = coroutineScope.launch {
            navigationSharedFlow.collect { navIntent ->
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