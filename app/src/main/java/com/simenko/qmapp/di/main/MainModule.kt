package com.simenko.qmapp.di.main

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.simenko.qmapp.BaseApplication
import com.simenko.qmapp.di.ViewModelKey
import com.simenko.qmapp.room.implementation.getDatabase
import com.simenko.qmapp.ui.main.QualityManagementViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap

@Module
abstract class MainModule {
    @MainScope
    @Binds
    @IntoMap
    @ViewModelKey(QualityManagementViewModel::class)
    abstract fun bindQualityManagementViewModel(context: QualityManagementViewModel): ViewModel
}