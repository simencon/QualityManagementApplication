package com.simenko.qmapp.di

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModelProvider
import androidx.room.RoomDatabase
import com.simenko.qmapp.room.implementation.getDatabase
import com.simenko.qmapp.viewmodels.ViewModelProviderFactory
import dagger.Binds
import dagger.Module
import javax.inject.Singleton


@Module
abstract class AppModule {
    @Singleton
    @Binds
    abstract fun bindViewModelFactory(modelProviderFactory: ViewModelProviderFactory): ViewModelProvider.Factory

//    @Singleton fun provideDatabase(context: Context): RoomDatabase {
//        return getDatabase(context.applicationContext)
//    }
}