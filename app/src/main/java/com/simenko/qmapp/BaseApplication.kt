package com.simenko.qmapp

import android.app.Application
import com.simenko.qmapp.di.AppComponent
import com.simenko.qmapp.di.DaggerAppComponent

class BaseApplication:Application() {

    // Instance of the AppComponent that will be used by all the Activities in the project
    val appComponent by lazy {
        initializeComponent()
    }

    open fun initializeComponent(): AppComponent {
        // Creates an instance of AppComponent using its Factory constructor
        // We pass the applicationContext that will be used as Context in the graph
        return DaggerAppComponent.factory().create(applicationContext)
    }
}