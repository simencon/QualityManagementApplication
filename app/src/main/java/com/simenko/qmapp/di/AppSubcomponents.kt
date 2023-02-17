package com.simenko.qmapp.di

import com.simenko.qmapp.di.main.MainComponent
import dagger.Module

// This module tells a Component which are its subcomponents
@Module(
    subcomponents = [
        MainComponent::class
    ]
)
class AppSubcomponents