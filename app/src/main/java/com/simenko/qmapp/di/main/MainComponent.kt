package com.simenko.qmapp.di.main

import com.simenko.qmapp.ui.main.MainActivity
import com.simenko.qmapp.ui.main.investigations.InvestigationsContainerFragment
import com.simenko.qmapp.ui.main.investigations.orders.OrdersFragment
import com.simenko.qmapp.ui.main.manufacturing.ManufacturingFragment
import com.simenko.qmapp.ui.main.neworder.PlaceOrderFragment
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

    fun inject(fragment: InvestigationsContainerFragment)
    fun inject(fragment: OrdersFragment)

    fun inject(fragment: PlaceOrderFragment)
}