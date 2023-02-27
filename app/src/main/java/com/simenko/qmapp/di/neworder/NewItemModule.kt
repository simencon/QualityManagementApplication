package com.simenko.qmapp.di.neworder

import androidx.lifecycle.ViewModel
import com.simenko.qmapp.di.ViewModelKey
import com.simenko.qmapp.ui.neworder.NewItemViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class NewItemModule {
    @NewItemScope
    @Binds
    @IntoMap
    @ViewModelKey(NewItemViewModel::class)
    abstract fun bindNewItemViewModel(context: NewItemViewModel): ViewModel
}