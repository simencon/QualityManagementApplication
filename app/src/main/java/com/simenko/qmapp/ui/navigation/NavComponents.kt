package com.simenko.qmapp.ui.navigation

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation

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
        arguments = startDestination.arguments,
        deepLinks = startDestination.deepLinks,
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