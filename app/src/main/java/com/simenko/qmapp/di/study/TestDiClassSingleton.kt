package com.simenko.qmapp.di.study

import javax.inject.Inject

class TestDiClassSingleton @Inject constructor() {
    var name: String = ""
         set(value) {
            field = "\"{$value}\""
        }


    fun getOwnerName(): String {
        return "The owner is: $name; class instance: $this"
    }
}