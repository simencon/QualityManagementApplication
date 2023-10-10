package com.simenko.qmapp.di

import androidx.lifecycle.SavedStateHandle
import com.simenko.qmapp.di.study.TestDiClassViewModelScope
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.ui.navigation.NavArguments
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class EmployeeIdParameter

@Module
@InstallIn(ViewModelComponent::class)
object ViewModelsModule {
    @Provides
    @ViewModelScoped
    fun provideTestDi(): TestDiClassViewModelScope {
        val instance = TestDiClassViewModelScope()
        instance.name = "Roman Semenyshyn"
        return instance
    }

    @Provides
    @EmployeeIdParameter
    @ViewModelScoped
    fun provideEmployeeIdParameter(savedStateHandle: SavedStateHandle): Int =
        savedStateHandle.get<Int>(NavArguments.employeeId) ?: NoRecord.num
}