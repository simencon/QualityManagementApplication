package com.simenko.qmapp.di.main

import com.simenko.qmapp.ui.main.MainActivity
import com.simenko.qmapp.ui.main.investigations.___InvestigationsContainerFragment
import com.simenko.qmapp.ui.main.investigations._InvestigationsFragment
import com.simenko.qmapp.ui.main.manufacturing.ManufacturingFragment
import com.simenko.qmapp.ui.neworder.PlaceOrderFragment
import com.simenko.qmapp.ui.main.team.TeamFragment
import dagger.Subcomponent

@MainScope
@Subcomponent(
    modules = [
        MainModule::class
    ]
)
interface MainComponent {
    @Subcomponent.Factory
    interface Factory{
        fun create(): MainComponent
    }
    fun inject(activity: MainActivity)

    fun inject(fragment: TeamFragment)

    fun inject(fragment: ManufacturingFragment)

    fun inject(fragment: ___InvestigationsContainerFragment)
    fun inject(fragment: _InvestigationsFragment)

    fun inject(fragment: PlaceOrderFragment)
}