package com.simenko.qmapp.usetesting

import dagger.Subcomponent

@Subcomponent
interface TestingComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): TestingComponent
    }

    fun inject(manager: CustomManager)
}