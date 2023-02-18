package com.simenko.qmapp.viewmodels

import java.util.Map
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

//Class<out ViewModel> - means any class which extends viewModel
//ToDo one more time learn the mechanism how it works (multibinding) - Singleton doesn't work
class ViewModelProviderFactory @Inject constructor(private val creators: Map<Class<out ViewModel>, Provider<ViewModel>>) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        var creator: Provider<out ViewModel>? = creators[modelClass]

        if (creator == null) {// if the viewModel has not been created
            // loop through the allowable keys (aka allowed classes with the @ViewModelKey)
            run myBlock@{
                creators.entrySet().forEach {
                    if (modelClass.isAssignableFrom(it.key)) {
                        creator = it.value
                        return@myBlock //stops forEach
                    }
                }
            }
        }

        // if this is not one of the allowed keys, throw exception
        if(creator == null) throw IllegalArgumentException("unknown model class $modelClass")

        // return the Provider
        try {
            return creator?.get() as T
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }
}