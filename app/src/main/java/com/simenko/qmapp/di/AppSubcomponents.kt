package com.simenko.qmapp.di

import com.simenko.qmapp.ui.investigations.InvestigationsComponent
import dagger.Module

// This module tells a Component which are its subcomponents
@Module(
    subcomponents = [
        InvestigationsComponent::class
    ]
)
class AppSubcomponents