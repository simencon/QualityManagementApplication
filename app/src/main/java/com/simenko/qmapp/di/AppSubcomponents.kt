package com.simenko.qmapp.di

import com.simenko.qmapp.di.inestigations.InvestigationsComponent
import com.simenko.qmapp.usetesting.TestingComponent
import dagger.Module

// This module tells a Component which are its subcomponents
@Module(
    subcomponents = [
        InvestigationsComponent::class,
        TestingComponent::class
    ]
)
class AppSubcomponents