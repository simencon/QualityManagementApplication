package com.simenko.qmapp.ui.navigation

import androidx.navigation.NavHostController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

interface AppNavigator {
    val navigationChannel: Channel<NavigationIntent>

    suspend fun navigateBack(
        route: String? = null,
        inclusive: Boolean = false,
    )

    fun tryNavigateBack(
        route: String? = null,
        inclusive: Boolean = false,
    )

    suspend fun navigateTo(
        route: String,
        popUpToRoute: String? = null,
        popUpToId: Int? = null,
        inclusive: Boolean = false,
        isSingleTop: Boolean = false,
    )

    fun tryNavigateTo(
        route: String,
        popUpToRoute: String? = null,
        popUpToId: Int? = null,
        inclusive: Boolean = false,
        isSingleTop: Boolean = false,
    )
}

sealed class NavigationIntent {
    data class NavigateBack(
        val route: String? = null,
        val inclusive: Boolean = false,
    ) : NavigationIntent()

    data class NavigateTo(
        val route: String,
        val popUpToRoute: String? = null,
        val popUpToId: Int? = null,
        val inclusive: Boolean = false,
        val isSingleTop: Boolean = false,
    ) : NavigationIntent()
}

fun Flow<NavigationIntent>.subscribeNavigationEvents(viewModelScope: CoroutineScope, navHostController: NavHostController) {
    viewModelScope.launch {
        collectLatest { intent ->
            when (intent) {
                is NavigationIntent.NavigateBack -> {
                    if (intent.route != null) {
                        navHostController.popBackStack(intent.route, intent.inclusive)
                    } else {
                        navHostController.popBackStack()
                    }
                }

                is NavigationIntent.NavigateTo -> {
                    navHostController.navigate(intent.route) {
                        launchSingleTop = intent.isSingleTop
                        if (intent.popUpToRoute != null) {
                            popUpTo(intent.popUpToRoute) { inclusive = intent.inclusive }
                        } else if (intent.popUpToId != null) {
                            popUpTo(intent.popUpToId) { inclusive = intent.inclusive }
                        }
                    }
                }
            }
        }
    }
}