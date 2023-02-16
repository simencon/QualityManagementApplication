package com.simenko.qmapp.usetesting

import dagger.Module
import dagger.Provides
import javax.inject.Inject

@Module
class StringProviderModule @Inject constructor() {
    companion object {
        @Provides
        fun globalMassage(): String = "This is global Message"
    }
}