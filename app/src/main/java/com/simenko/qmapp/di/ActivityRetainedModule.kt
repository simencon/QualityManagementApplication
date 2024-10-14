package com.simenko.qmapp.di

import com.simenko.qmapp.presentation.ui.main.main.MainPageState
import com.simenko.qmapp.presentation.ui.main.main.MainPageStateImpl
import com.simenko.qmapp.navigation.AppNavigator
import com.simenko.qmapp.navigation.AppNavigatorImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped

@Module
@InstallIn(ActivityRetainedComponent::class)
object ActivityRetainedModule {
    @ActivityRetainedScoped
    @Provides
    fun provideAppNavigator(): AppNavigator {
        return AppNavigatorImpl()
    }

    @ActivityRetainedScoped
    @Provides
    fun provideTopScreenState(): MainPageState {
        return MainPageStateImpl()
    }
}