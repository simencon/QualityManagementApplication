package com.simenko.qmapp.ui.navigation

import android.app.Activity
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

@Composable
fun NavHost(
    navController: NavHostController,
    startDestination: Route,
    modifier: Modifier = Modifier,
    builder: NavGraphBuilder.() -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = startDestination.link,
        modifier = modifier,
        route = startDestination.route,
        builder = builder
    )
}

fun NavGraphBuilder.navigation(
    startDestination: Route,
    builder: NavGraphBuilder.() -> Unit
) {
    navigation(
        startDestination = startDestination.link,
        route = startDestination.route,
        builder = builder
    )
}

fun NavGraphBuilder.composable(
    destination: Route,
    content: @Composable AnimatedContentScope.(NavBackStackEntry) -> Unit
) {
    composable(
        route = destination.link,
        arguments = destination.arguments,
        deepLinks = destination.deepLinks,
        content = content
    )
}

@Composable
fun NavigationEffects(
    navigationChannel: Channel<NavigationIntent>,
    navHostController: NavHostController
) {
    val activity = (LocalContext.current as? Activity)
    LaunchedEffect(activity, navHostController, navigationChannel) {
        navigationChannel.receiveAsFlow().collect { intent ->
            if (activity?.isFinishing == true) {
                return@collect
            }
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