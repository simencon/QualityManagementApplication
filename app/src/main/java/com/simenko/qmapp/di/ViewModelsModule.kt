package com.simenko.qmapp.di

import androidx.lifecycle.SavedStateHandle
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.domain.NoRecordStr
import com.simenko.qmapp.ui.navigation.NavArguments
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class UserEditModeParameter

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class EmployeeIdParameter

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class UserIdParameter

@Module
@InstallIn(ViewModelComponent::class)
object ViewModelsModule {
    @Provides
    @UserEditModeParameter
    @ViewModelScoped
    fun provideUserEditModeParameter(savedStateHandle: SavedStateHandle): Boolean =
        savedStateHandle.get<Boolean>(NavArguments.userEditMode) ?: false

    @Provides
    @EmployeeIdParameter
    @ViewModelScoped
    fun provideEmployeeIdParameter(savedStateHandle: SavedStateHandle): Int =
        savedStateHandle.get<Int>(NavArguments.employeeId) ?: NoRecord.num

    @Provides
    @UserIdParameter
    @ViewModelScoped
    fun provideUserIdParameter(savedStateHandle: SavedStateHandle): String =
        savedStateHandle.get<String>(NavArguments.userId) ?: NoRecordStr.str
}