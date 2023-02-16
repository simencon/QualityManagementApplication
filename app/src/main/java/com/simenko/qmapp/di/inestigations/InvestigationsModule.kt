package com.simenko.qmapp.di.inestigations

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import com.simenko.qmapp.di.ViewModelKey
import com.simenko.qmapp.ui.QualityManagementViewModel
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap

@Module
class InvestigationsModule {
    @InvestigationsScope
    @Provides
    @IntoMap
    @ViewModelKey(QualityManagementViewModel::class)
    fun bindQualityManagementViewModel(context: Context): ViewModel {
        val application = context.applicationContext as Application
        return QualityManagementViewModel(application)
    }
}