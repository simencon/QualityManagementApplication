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

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class CompanyIdParameter

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DepartmentIdParameter

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class SubDepartmentIdParameter

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ChannelIdParameter

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class LineIdParameter

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class OperationIdParameter

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class IsProcessControlOnlyParameter

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class OrderIdParameter

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class SubOrderIdParameter

@Module
@InstallIn(ViewModelComponent::class)
object ViewModelsModule {
    @Provides
    @UserEditModeParameter
    @ViewModelScoped
    fun provideUserEditModeParameter(savedStateHandle: SavedStateHandle): Boolean =
        savedStateHandle[NavArguments.userEditMode] ?: false

    @Provides
    @EmployeeIdParameter
    @ViewModelScoped
    fun provideEmployeeIdParameter(savedStateHandle: SavedStateHandle): Int =
        savedStateHandle[NavArguments.employeeId] ?: NoRecord.num

    @Provides
    @UserIdParameter
    @ViewModelScoped
    fun provideUserIdParameter(savedStateHandle: SavedStateHandle): String =
        savedStateHandle[NavArguments.userId] ?: NoRecordStr.str

    @Provides
    @CompanyIdParameter
    @ViewModelScoped
    fun provideCompanyIdParameterParameter(savedStateHandle: SavedStateHandle): Int {
        return savedStateHandle[NavArguments.companyId] ?: NoRecord.num
    }

    @Provides
    @DepartmentIdParameter
    @ViewModelScoped
    fun provideDepartmentIdParameterParameter(savedStateHandle: SavedStateHandle): Int =
        savedStateHandle[NavArguments.departmentId] ?: NoRecord.num

    @Provides
    @SubDepartmentIdParameter
    @ViewModelScoped
    fun provideSubDepartmentIdParameterParameter(savedStateHandle: SavedStateHandle): Int =
        savedStateHandle[NavArguments.subDepartmentId] ?: NoRecord.num

    @Provides
    @ChannelIdParameter
    @ViewModelScoped
    fun provideChannelIdParameterParameter(savedStateHandle: SavedStateHandle): Int =
        savedStateHandle[NavArguments.channelId] ?: NoRecord.num

    @Provides
    @LineIdParameter
    @ViewModelScoped
    fun provideLineIdParameterParameter(savedStateHandle: SavedStateHandle): Int =
        savedStateHandle[NavArguments.lineId] ?: NoRecord.num

    @Provides
    @OperationIdParameter
    @ViewModelScoped
    fun provideOperationIdParameterParameter(savedStateHandle: SavedStateHandle): Int =
        savedStateHandle[NavArguments.operationId] ?: NoRecord.num

    @Provides
    @IsProcessControlOnlyParameter
    @ViewModelScoped
    fun provideProcessControlOnlyParameter(savedStateHandle: SavedStateHandle): Boolean? =
        savedStateHandle[NavArguments.isProcessControlOnly]

    @Provides
    @OrderIdParameter
    @ViewModelScoped
    fun provideOrderIdParameterParameter(savedStateHandle: SavedStateHandle): Int =
        savedStateHandle[NavArguments.orderId] ?: NoRecord.num

    @Provides
    @SubOrderIdParameter
    @ViewModelScoped
    fun provideSubOrderIdParameterParameter(savedStateHandle: SavedStateHandle): Int =
        savedStateHandle[NavArguments.subOrderId] ?: NoRecord.num
}