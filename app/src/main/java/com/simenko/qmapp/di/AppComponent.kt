package com.simenko.qmapp.di


import android.content.Context
import com.simenko.qmapp.di.inestigations.InvestigationsComponent
import com.simenko.qmapp.usetesting.StringProviderModule
import com.simenko.qmapp.usetesting.TestingComponent
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

// Scope annotation that the AppComponent uses
// Classes annotated with @Singleton will have a unique instance in this Component
@Singleton
// Definition of a Dagger component that adds info from the different modules to the graph
@Component(
    modules = [
        ViewModelFactoryModule::class,
        AppSubcomponents::class,
    ]
)
interface AppComponent {

    // Factory to create instances of the AppComponent
    @Component.Factory
    interface Factory {
        // With @BindsInstance, the Context passed in will be available in the graph
        fun create(@BindsInstance context: Context): AppComponent
    }

    fun investigationsComponent(): InvestigationsComponent.Factory
    fun testingComponent(): TestingComponent.Factory

}