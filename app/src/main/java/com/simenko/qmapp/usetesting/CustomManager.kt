package com.simenko.qmapp.usetesting

import com.simenko.qmapp.BaseApplication
import javax.inject.Inject

class CustomManager(application: BaseApplication) {

    private var testingComponent: TestingComponent
    init {
        testingComponent = (application as BaseApplication).appComponent.testingComponent().create()
        testingComponent.inject(this)
    }

    var messageFromCustomManager = "This is a custom manager"
    @Inject
    lateinit var globalMessage: String

    fun changeToGlobalMessage() {
        messageFromCustomManager = globalMessage
    }
}