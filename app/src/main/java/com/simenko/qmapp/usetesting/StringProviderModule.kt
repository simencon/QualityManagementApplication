package com.simenko.qmapp.usetesting

import com.simenko.qmapp.di.inestigations.InvestigationsScope
import dagger.Module
import dagger.Provides
import javax.inject.Inject
import javax.inject.Singleton

@Module
class StringProviderModule {
    companion object {
        @InvestigationsScope
        @Provides
        fun globalMassage(): String = "This is global Message"
    }
}