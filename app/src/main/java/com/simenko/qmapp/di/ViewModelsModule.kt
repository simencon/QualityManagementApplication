package com.simenko.qmapp.di

import com.simenko.qmapp.di.study.TestDiClassViewModelScope
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object ViewModelsModule {
    @ViewModelScoped
    @Provides
    fun provideTestDi(): TestDiClassViewModelScope {
        val instance = TestDiClassViewModelScope()
        instance.name = "Roman Semenyshyn"
        return instance
    }
}