package com.simenko.qmapp.di

import com.simenko.qmapp.di.study.TestDiClassActivityRetainedScope
import com.simenko.qmapp.ui.main.main.MainPageState
import com.simenko.qmapp.ui.main.main.MainPageStateImpl
import com.simenko.qmapp.ui.navigation.AppNavigator
import com.simenko.qmapp.ui.navigation.AppNavigatorImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import javax.inject.Singleton

@Module
@InstallIn(ActivityRetainedComponent::class)
object ActivityRetainedModule {
    @ActivityRetainedScoped
    @Provides
    fun provideTestDi(): TestDiClassActivityRetainedScope {
        val instance = TestDiClassActivityRetainedScope()
        instance.name = "Roman Semenyshyn"
        return instance
    }

    @ActivityRetainedScoped
    @Provides
    fun provideAppNavigatorWithinMainActivity(): AppNavigator {
        return AppNavigatorImpl()
    }

    @ActivityRetainedScoped
    @Provides
    fun provideTopScreenState(): MainPageState {
        return MainPageStateImpl()
    }
}