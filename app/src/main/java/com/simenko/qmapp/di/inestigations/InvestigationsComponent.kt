package com.simenko.qmapp.di.inestigations

import com.simenko.qmapp.fragments.Adapter____Order
import com.simenko.qmapp.ui.main.MainActivity
import dagger.Subcomponent

@Subcomponent
interface InvestigationsComponent {
    @Subcomponent.Factory
    interface Factory{
        fun create(): InvestigationsComponent
    }

    fun inject(activity: MainActivity)
    fun inject(orderAdapter: Adapter____Order)
}