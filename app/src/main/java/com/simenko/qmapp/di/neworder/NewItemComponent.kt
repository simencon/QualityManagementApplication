package com.simenko.qmapp.di.neworder

import com.simenko.qmapp.ui.neworder.NewItemActivity
import dagger.Subcomponent

@NewItemScope
@Subcomponent(
    modules = [
        NewItemModule::class
    ]
)
interface NewItemComponent {
    @Subcomponent.Factory
    interface Factory{
        fun create(): NewItemComponent
    }
    fun inject(activity: NewItemActivity)
}