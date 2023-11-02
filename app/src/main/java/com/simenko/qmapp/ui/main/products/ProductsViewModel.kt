package com.simenko.qmapp.ui.main.products

import androidx.lifecycle.ViewModel
import com.simenko.qmapp.di.CompanyIdParameter
import com.simenko.qmapp.di.ComponentKindIdParameter
import com.simenko.qmapp.di.ComponentStageKindIdParameter
import com.simenko.qmapp.di.ProductKindIdParameter
import com.simenko.qmapp.di.ProductProjectIdParameter
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.domain.SelectedNumber
import com.simenko.qmapp.repository.ProductsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class ProductsViewModel @Inject constructor(
    val repository: ProductsRepository,
    @CompanyIdParameter val companyId: Int,
    @ProductProjectIdParameter val productProjectId: Int,
    @ProductKindIdParameter val productKindId: Int,
    @ComponentKindIdParameter val componentKindId: Int,
    @ComponentStageKindIdParameter val componentStageKindId: Int
) : ViewModel() {
    private val _productProjectsVisibility = MutableStateFlow(Pair(SelectedNumber(productProjectId), NoRecord))
    private val _productKindsVisibility = MutableStateFlow(Pair(SelectedNumber(productKindId), NoRecord))
    private val _componentKindsVisibility = MutableStateFlow(Pair(SelectedNumber(componentKindId), NoRecord))
    private val _componentStageKindsVisibility = MutableStateFlow(Pair(SelectedNumber(componentStageKindId), NoRecord))
//    private val _departments = repository.departmentsComplete(companyId)
}