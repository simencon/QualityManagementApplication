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
annotation class CompanyIdParameter

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
annotation class ProductKindIdParameter

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
annotation class VersionEditMode

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ToleranceIdParameter

@Module
@InstallIn(ViewModelComponent::class)
object ViewModelsModule {
    @Provides
    @CompanyIdParameter
    @ViewModelScoped
    fun provideCompanyIdParameter(savedStateHandle: SavedStateHandle): ID {
        return savedStateHandle[NavArguments.companyId] ?: NoRecord.num
    }

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
    @ProductKindIdParameter
    @ViewModelScoped
    fun provideProductKindIdParameter(savedStateHandle: SavedStateHandle): ID =
        savedStateHandle[NavArguments.productKindId] ?: NoRecord.num

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
    @VersionEditMode
    @ViewModelScoped
    fun provideVersionEditModeParameter(savedStateHandle: SavedStateHandle): Boolean =
        savedStateHandle[NavArguments.versionEditMode] ?: false

    @Provides
    @ToleranceIdParameter
    @ViewModelScoped
    fun provideToleranceIdParameter(savedStateHandle: SavedStateHandle): ID =
        savedStateHandle[NavArguments.toleranceId] ?: NoRecord.num
}