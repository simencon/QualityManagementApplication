package com.simenko.qmapp.di

import androidx.lifecycle.ViewModelProvider
import com.simenko.qmapp.viewmodels.ViewModelProviderFactory
import dagger.Binds
import dagger.Module


@Module
abstract class ViewModelFactoryModule {
    @Binds
    abstract fun bindViewModelFactory(modelProviderFactory: ViewModelProviderFactory): ViewModelProvider.Factory
}