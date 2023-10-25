package com.simenko.qmapp.ui.main.structure.forms.department

import androidx.lifecycle.ViewModel
import com.simenko.qmapp.di.DepartmentIdParameter
import com.simenko.qmapp.repository.ManufacturingRepository
import com.simenko.qmapp.ui.main.main.MainPageState
import com.simenko.qmapp.ui.navigation.AppNavigator
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DepartmentViewModel @Inject constructor(
    private val appNavigator: AppNavigator,
    private val mainPageState: MainPageState,
    private val repository: ManufacturingRepository,
    @DepartmentIdParameter private val depId: Int
) : ViewModel() {
}