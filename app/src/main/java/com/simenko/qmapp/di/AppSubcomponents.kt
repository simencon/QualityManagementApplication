package com.simenko.qmapp.di

import com.simenko.qmapp.di.main.MainComponent
import com.simenko.qmapp.di.neworder.NewItemComponent
import dagger.Module

// This module tells a Component which are its subcomponents
@Module(
    subcomponents = [
        MainComponent::class,
        NewItemComponent::class
    ]
)
class AppSubcomponents