package com.simenko.qmapp.di

import androidx.lifecycle.ViewModelProvider
import com.simenko.qmapp.viewmodels.ViewModelProviderFactory
import dagger.Binds
import dagger.Module
import javax.inject.Singleton


@Module
abstract class AppModule {
    @Singleton
    @Binds
    abstract fun bindViewModelFactory(modelProviderFactory: ViewModelProviderFactory): ViewModelProvider.Factory
}