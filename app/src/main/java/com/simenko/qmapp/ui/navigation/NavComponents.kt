package com.simenko.qmapp.ui.navigation

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation

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