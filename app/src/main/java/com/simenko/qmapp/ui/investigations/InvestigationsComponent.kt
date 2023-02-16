package com.simenko.qmapp.ui.investigations

import com.simenko.qmapp.di.inestigations.InvestigationsModule
import com.simenko.qmapp.di.inestigations.InvestigationsScope
import com.simenko.qmapp.ui.MainActivity
import dagger.Subcomponent

@InvestigationsScope
@Subcomponent(
    modules = [
        InvestigationsModule::class
    ]
)
interface InvestigationsComponent {
    @Subcomponent.Factory
    interface Factory{
        fun create(): InvestigationsComponent
    }
    fun inject(activity: MainActivity)
}