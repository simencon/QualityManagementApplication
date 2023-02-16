package com.simenko.qmapp.di.inestigations

import com.simenko.qmapp.fragments.Adapter____Order
import com.simenko.qmapp.ui.investigations.MainActivity
import com.simenko.qmapp.usetesting.StringProviderModule
import dagger.Subcomponent

@InvestigationsScope
@Subcomponent(
    modules = [
        InvestigationsModule::class,
        StringProviderModule::class
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