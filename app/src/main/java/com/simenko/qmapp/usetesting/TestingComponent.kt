package com.simenko.qmapp.usetesting

import com.simenko.qmapp.di.inestigations.InvestigationsScope
import dagger.Subcomponent

@InvestigationsScope
@Subcomponent (modules = [StringProviderModule::class])
interface TestingComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): TestingComponent
    }

    fun inject(manager: CustomManager)
}