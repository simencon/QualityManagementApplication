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
annotation class ProductProjectIdParameter

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ProductKindIdParameter

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ComponentKindIdParameter

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ComponentStageKindIdParameter

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
    fun provideCompanyIdParameter(savedStateHandle: SavedStateHandle): Int {
        return savedStateHandle[NavArguments.companyId] ?: NoRecord.num
    }

    @Provides
    @DepartmentIdParameter
    @ViewModelScoped
    fun provideDepartmentIdParameter(savedStateHandle: SavedStateHandle): Int =
        savedStateHandle[NavArguments.departmentId] ?: NoRecord.num

    @Provides
    @SubDepartmentIdParameter
    @ViewModelScoped
    fun provideSubDepartmentIdParameter(savedStateHandle: SavedStateHandle): Int =
        savedStateHandle[NavArguments.subDepartmentId] ?: NoRecord.num

    @Provides
    @ChannelIdParameter
    @ViewModelScoped
    fun provideChannelIdParameter(savedStateHandle: SavedStateHandle): Int =
        savedStateHandle[NavArguments.channelId] ?: NoRecord.num

    @Provides
    @LineIdParameter
    @ViewModelScoped
    fun provideLineIdParameter(savedStateHandle: SavedStateHandle): Int =
        savedStateHandle[NavArguments.lineId] ?: NoRecord.num

    @Provides
    @OperationIdParameter
    @ViewModelScoped
    fun provideOperationIdParameter(savedStateHandle: SavedStateHandle): Int =
        savedStateHandle[NavArguments.operationId] ?: NoRecord.num

    @Provides
    @ProductProjectIdParameter
    @ViewModelScoped
    fun provideProductProjectIdParameter(savedStateHandle: SavedStateHandle): Int =
        savedStateHandle[NavArguments.productProjectId] ?: NoRecord.num

    @Provides
    @ProductKindIdParameter
    @ViewModelScoped
    fun provideProductKindIdParameter(savedStateHandle: SavedStateHandle): Int =
        savedStateHandle[NavArguments.productKindId] ?: NoRecord.num

    @Provides
    @ComponentKindIdParameter
    @ViewModelScoped
    fun provideComponentKindIdParameter(savedStateHandle: SavedStateHandle): Int =
        savedStateHandle[NavArguments.componentKindId] ?: NoRecord.num

    @Provides
    @ComponentStageKindIdParameter
    @ViewModelScoped
    fun provideComponentStageIdParameter(savedStateHandle: SavedStateHandle): Int =
        savedStateHandle[NavArguments.componentStageKindId] ?: NoRecord.num

    @Provides
    @IsProcessControlOnlyParameter
    @ViewModelScoped
    fun provideProcessControlOnlyParameter(savedStateHandle: SavedStateHandle): Boolean? =
        savedStateHandle[NavArguments.isProcessControlOnly]

    @Provides
    @OrderIdParameter
    @ViewModelScoped
    fun provideOrderIdParameter(savedStateHandle: SavedStateHandle): Int =
        savedStateHandle[NavArguments.orderId] ?: NoRecord.num

    @Provides
    @SubOrderIdParameter
    @ViewModelScoped
    fun provideSubOrderIdParameter(savedStateHandle: SavedStateHandle): Int =
        savedStateHandle[NavArguments.subOrderId] ?: NoRecord.num
}