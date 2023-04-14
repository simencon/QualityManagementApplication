package com.simenko.qmapp.di.main


import androidx.lifecycle.ViewModel
import com.simenko.qmapp.di.ViewModelKey
import com.simenko.qmapp.ui.main.QualityManagementViewModel
import com.simenko.qmapp.ui.main.investigations.InvestigationsViewModel
import com.simenko.qmapp.ui.main.team.TeamViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class MainModule {
    @MainScope
    @Binds
    @IntoMap
    @ViewModelKey(QualityManagementViewModel::class)
    abstract fun bindQualityManagementViewModel(context: QualityManagementViewModel): ViewModel

    @MainScope
    @Binds
    @IntoMap
    @ViewModelKey(TeamViewModel::class)
    abstract fun bindTeamViewModel(context: TeamViewModel): ViewModel

    @MainScope
    @Binds
    @IntoMap
    @ViewModelKey(InvestigationsViewModel::class)
    abstract fun bindInvestigationsViewModel(context: InvestigationsViewModel): ViewModel
}