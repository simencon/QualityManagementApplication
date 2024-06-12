package com.simenko.qmapp.ui.navigation

import androidx.navigation.NavHostController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow

interface AppNavigator {
    var navigationSharedFlow: MutableSharedFlow<NavigationIntent>

    suspend fun navigateBack(
        route: Route? = null,
        inclusive: Boolean = false,
    )

    fun tryNavigateBack(
        route: Route? = null,
        inclusive: Boolean = false,
    )

    suspend fun navigateTo(
        route: Route,
        popUpToRoute: Route?,
        popUpToId: Int?,
        inclusive: Boolean,
        isSingleTop: Boolean
    )

    fun tryNavigateTo(
        route: Route,
        popUpToRoute: Route? = null,
        popUpToId: Int? = null,
        inclusive: Boolean = false,
        isSingleTop: Boolean = false,
    )

    fun subscribeNavigationEvents(coroutineScope: CoroutineScope, navController: NavHostController)
}

sealed class NavigationIntent {
    data class NavigateBack(
        val route: Route? = null,
        val inclusive: Boolean = false,
    ) : NavigationIntent()

    data class NavigateTo(
        val route: String,
        val popUpToRoute: String? = null,
        val popUpToId: Int? = null,
        val inclusive: Boolean = false,
        val isSingleTop: Boolean = false,
    ) : NavigationIntent()

    data class NavigateToRoute(
        val route: Route,
        val popUpToRoute: Route? = null,
        val popUpToId: Int? = null,
        val inclusive: Boolean = false,
        val isSingleTop: Boolean = false,
    ) : NavigationIntent()
}