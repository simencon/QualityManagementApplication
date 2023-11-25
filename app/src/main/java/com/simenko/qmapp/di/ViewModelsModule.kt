package com.simenko.qmapp.di

import androidx.lifecycle.SavedStateHandle
import com.simenko.qmapp.domain.ID
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
annotation class ProductLineIdParameter
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class CharGroupIdParameter
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class CharSubGroupIdParameter

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class CharacteristicIdParameter
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class MetricIdParameter
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ProductLineKeyIdParameter

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ProductKindIdParameter
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ProductKindKeyIdParameter

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ComponentKindIdParameter
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ComponentKindKeyIdParameter

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ComponentStageKindIdParameter
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ComponentStageKindKeyIdParameter
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ProductIdParameter
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ComponentIdParameter
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ComponentStageIdParameter
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class VersionFIdParameter

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
    fun provideEmployeeIdParameter(savedStateHandle: SavedStateHandle): ID =
        savedStateHandle[NavArguments.employeeId] ?: NoRecord.num

    @Provides
    @UserIdParameter
    @ViewModelScoped
    fun provideUserIdParameter(savedStateHandle: SavedStateHandle): String =
        savedStateHandle[NavArguments.userId] ?: NoRecordStr.str

    @Provides
    @CompanyIdParameter
    @ViewModelScoped
    fun provideCompanyIdParameter(savedStateHandle: SavedStateHandle): ID {
        return savedStateHandle[NavArguments.companyId] ?: NoRecord.num
    }

    @Provides
    @DepartmentIdParameter
    @ViewModelScoped
    fun provideDepartmentIdParameter(savedStateHandle: SavedStateHandle): ID =
        savedStateHandle[NavArguments.departmentId] ?: NoRecord.num

    @Provides
    @SubDepartmentIdParameter
    @ViewModelScoped
    fun provideSubDepartmentIdParameter(savedStateHandle: SavedStateHandle): ID =
        savedStateHandle[NavArguments.subDepartmentId] ?: NoRecord.num

    @Provides
    @ChannelIdParameter
    @ViewModelScoped
    fun provideChannelIdParameter(savedStateHandle: SavedStateHandle): ID =
        savedStateHandle[NavArguments.channelId] ?: NoRecord.num

    @Provides
    @LineIdParameter
    @ViewModelScoped
    fun provideLineIdParameter(savedStateHandle: SavedStateHandle): ID =
        savedStateHandle[NavArguments.lineId] ?: NoRecord.num

    @Provides
    @OperationIdParameter
    @ViewModelScoped
    fun provideOperationIdParameter(savedStateHandle: SavedStateHandle): ID =
        savedStateHandle[NavArguments.operationId] ?: NoRecord.num

    @Provides
    @ProductLineIdParameter
    @ViewModelScoped
    fun provideProductLineIdParameter(savedStateHandle: SavedStateHandle): ID =
        savedStateHandle[NavArguments.productLineId] ?: NoRecord.num
    @Provides
    @CharGroupIdParameter
    @ViewModelScoped
    fun provideCharGroupIdParameter(savedStateHandle: SavedStateHandle): ID =
        savedStateHandle[NavArguments.charGroupId] ?: NoRecord.num
    @Provides
    @CharSubGroupIdParameter
    @ViewModelScoped
    fun provideSubCharGroupIdParameter(savedStateHandle: SavedStateHandle): ID =
        savedStateHandle[NavArguments.charSubGroupId] ?: NoRecord.num

    @Provides
    @CharacteristicIdParameter
    @ViewModelScoped
    fun provideCharacteristicIdParameter(savedStateHandle: SavedStateHandle): ID =
        savedStateHandle[NavArguments.characteristicId] ?: NoRecord.num
    @Provides
    @MetricIdParameter
    @ViewModelScoped
    fun provideMetricIdParameter(savedStateHandle: SavedStateHandle): ID =
        savedStateHandle[NavArguments.metricId] ?: NoRecord.num

    @Provides
    @ProductLineKeyIdParameter
    @ViewModelScoped
    fun provideProductLineKeyIdParameter(savedStateHandle: SavedStateHandle): ID =
        savedStateHandle[NavArguments.productLineKeyId] ?: NoRecord.num


    @Provides
    @ProductKindIdParameter
    @ViewModelScoped
    fun provideProductKindIdParameter(savedStateHandle: SavedStateHandle): ID =
        savedStateHandle[NavArguments.productKindId] ?: NoRecord.num

    @Provides
    @ProductKindKeyIdParameter
    @ViewModelScoped
    fun provideProductKindKeyIdParameter(savedStateHandle: SavedStateHandle): ID =
        savedStateHandle[NavArguments.productKindKeyId] ?: NoRecord.num

    @Provides
    @ComponentKindIdParameter
    @ViewModelScoped
    fun provideComponentKindIdParameter(savedStateHandle: SavedStateHandle): ID =
        savedStateHandle[NavArguments.componentKindId] ?: NoRecord.num
    @Provides
    @ComponentKindKeyIdParameter
    @ViewModelScoped
    fun provideComponentKindKeyIdParameter(savedStateHandle: SavedStateHandle): ID =
        savedStateHandle[NavArguments.componentKindKeyId] ?: NoRecord.num

    @Provides
    @ComponentStageKindIdParameter
    @ViewModelScoped
    fun provideComponentStageKindIdParameter(savedStateHandle: SavedStateHandle): ID =
        savedStateHandle[NavArguments.componentStageKindId] ?: NoRecord.num
    @Provides
    @ComponentStageKindKeyIdParameter
    @ViewModelScoped
    fun provideComponentStageKindKeyIdParameter(savedStateHandle: SavedStateHandle): ID =
        savedStateHandle[NavArguments.componentStageKindKeyId] ?: NoRecord.num
    @Provides
    @ProductIdParameter
    @ViewModelScoped
    fun provideProductIdParameter(savedStateHandle: SavedStateHandle): ID =
        savedStateHandle[NavArguments.productId] ?: NoRecord.num
    @Provides
    @ComponentIdParameter
    @ViewModelScoped
    fun provideComponentIdParameter(savedStateHandle: SavedStateHandle): ID =
        savedStateHandle[NavArguments.componentId] ?: NoRecord.num
    @Provides
    @ComponentStageIdParameter
    @ViewModelScoped
    fun provideComponentStageIdParameter(savedStateHandle: SavedStateHandle): ID =
        savedStateHandle[NavArguments.componentStageId] ?: NoRecord.num

    @Provides
    @VersionFIdParameter
    @ViewModelScoped
    fun provideVersionFIdParameter(savedStateHandle: SavedStateHandle): String =
        savedStateHandle[NavArguments.versionFId] ?: NoRecordStr.str

    @Provides
    @IsProcessControlOnlyParameter
    @ViewModelScoped
    fun provideProcessControlOnlyParameter(savedStateHandle: SavedStateHandle): Boolean? =
        savedStateHandle[NavArguments.isProcessControlOnly]

    @Provides
    @OrderIdParameter
    @ViewModelScoped
    fun provideOrderIdParameter(savedStateHandle: SavedStateHandle): ID =
        savedStateHandle[NavArguments.orderId] ?: NoRecord.num

    @Provides
    @SubOrderIdParameter
    @ViewModelScoped
    fun provideSubOrderIdParameter(savedStateHandle: SavedStateHandle): ID =
        savedStateHandle[NavArguments.subOrderId] ?: NoRecord.num
}