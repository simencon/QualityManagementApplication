package com.simenko.qmapp.ui.navigation

import kotlinx.coroutines.channels.Channel

interface AppNavigator {
    val navigationChannel: Channel<NavigationIntent>

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