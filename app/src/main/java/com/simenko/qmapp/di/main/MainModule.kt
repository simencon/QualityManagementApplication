package com.simenko.qmapp.di.main

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import com.simenko.qmapp.di.ViewModelKey
import com.simenko.qmapp.ui.main.QualityManagementViewModel
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap

@Module
class MainModule {
    @MainScope
    @Provides
    @IntoMap
    @ViewModelKey(QualityManagementViewModel::class)
    fun bindQualityManagementViewModel(context: Context): ViewModel {
        val application = context.applicationContext as Application
        return QualityManagementViewModel(application)
    }
}