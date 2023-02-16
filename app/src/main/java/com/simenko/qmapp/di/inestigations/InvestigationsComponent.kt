package com.simenko.qmapp.di.inestigations

import com.simenko.qmapp.fragments.Adapter____Order
import com.simenko.qmapp.ui.investigations.MainActivity
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
    fun inject(orderAdapter: Adapter____Order)
}