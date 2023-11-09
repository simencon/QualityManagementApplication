package com.simenko.qmapp.di

import android.content.Context
import androidx.navigation.NavHostController
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.DialogNavigator
import com.simenko.qmapp.ui.main.main.MainPageState
import com.simenko.qmapp.ui.main.main.MainPageStateImpl
import com.simenko.qmapp.ui.navigation.AppNavigator
import com.simenko.qmapp.ui.navigation.AppNavigatorImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityRetainedScoped

@Module
@InstallIn(ActivityRetainedComponent::class)
object ActivityRetainedModule {
    @ActivityRetainedScoped
    @Provides
    fun provideAppNavigator(): AppNavigator {
        val navigator = AppNavigatorImpl()
        println("provideAppNavigator instance = $navigator")
        return navigator
    }

    @ActivityRetainedScoped
    @Provides
    fun provideTopScreenState(): MainPageState {
        return MainPageStateImpl()
    }

    @ActivityRetainedScoped
    @Provides
    fun provideNavController(@ApplicationContext context: Context) = NavHostController(context).apply {
        navigatorProvider.addNavigator(ComposeNavigator())
        navigatorProvider.addNavigator(DialogNavigator())
    }
}