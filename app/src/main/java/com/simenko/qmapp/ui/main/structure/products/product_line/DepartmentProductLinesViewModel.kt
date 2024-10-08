package com.simenko.qmapp.ui.main.structure.products.product_line

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.domain.SelectedNumber
import com.simenko.qmapp.domain.entities.DomainDepartment
import com.simenko.qmapp.repository.ManufacturingRepository
import com.simenko.qmapp.repository.ProductsRepository
import com.simenko.qmapp.ui.main.main.MainPageHandler
import com.simenko.qmapp.ui.main.main.MainPageState
import com.simenko.qmapp.ui.main.main.content.Page
import com.simenko.qmapp.ui.navigation.AppNavigator
import com.simenko.qmapp.ui.navigation.Route
import com.simenko.qmapp.ui.navigation.Route.Main.CompanyStructure.DepartmentProductLines
import com.simenko.qmapp.utils.InvestigationsUtils.setVisibility
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class DepartmentProductLinesViewModel @Inject constructor(
    private val appNavigator: AppNavigator,
    private val mainPageState: MainPageState,
    private val repository: ManufacturingRepository,
    private val productRepository: ProductsRepository,
) : ViewModel() {
    private val _route = MutableStateFlow(DepartmentProductLines(companyId = NoRecord.num, departmentId = NoRecord.num))
    private val _department = MutableStateFlow(DomainDepartment.DomainDepartmentComplete())
    private val _departmentProductLinesIds = MutableStateFlow<List<ID>>(emptyList())
    private val _productLinesVisibility = MutableStateFlow(Pair(SelectedNumber(NoRecord.num), NoRecord))

    /**
     * Main page setup -------------------------------------------------------------------------------------------------------------------------------
     * */
    private var mainPageHandler: MainPageHandler? = null

    fun onEntered(route: DepartmentProductLines) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _route.value = route
                _department.value = repository.departmentById(route.departmentId)
                _departmentProductLinesIds.value = productRepository.departmentProductLines(route.departmentId).map { it.productLineId }
                mainPageHandler = MainPageHandler.Builder(Page.DEPARTMENT_PRODUCT_LINES, mainPageState)
                    .setOnNavMenuClickAction { appNavigator.navigateBack() }
                    .setOnFabClickAction { onAddProductLine() }
                    .build()
                    .apply { setupMainPage(0, true) }
            }
        }
    }

    /**
     * UI operations ---------------------------------------------------------------------------------------------------------------------------------
     * */
    fun setProductLinesVisibility(dId: SelectedNumber = NoRecord, aId: SelectedNumber = NoRecord) {
        _productLinesVisibility.value = _productLinesVisibility.value.setVisibility(dId, aId)
    }

    /**
     * UI State --------------------------------------------------------------------------------------------------------------------------------------
     * */
    val department get() = _department.asStateFlow()

    val productLines = _route.flatMapLatest { route ->
        _departmentProductLinesIds.flatMapLatest { productLinesIds ->
            _productLinesVisibility.flatMapLatest { visibility ->
                productRepository.productLines(route.companyId).flatMapLatest { allProductLines ->
                    flow {
                        emit(allProductLines
                            .filter { productLinesIds.contains(it.manufacturingProject.id) }
                            .map {
                                it.copy(
                                    detailsVisibility = it.manufacturingProject.id == visibility.first.num,
                                    isExpanded = it.manufacturingProject.id == visibility.second.num
                                )
                            }
                        )
                    }
                }
            }
        }
    }

    /**
     * Navigation ------------------------------------------------------------------------------------------------------------------------------------
     * */

    private fun onAddProductLine() {

    }

    fun onDeleteProductLineClick(id: ID) {

    }

    private suspend fun navBackToRecord() {
        mainPageHandler?.updateLoadingState?.invoke(Pair(false, null))
        withContext(Dispatchers.Main) {
            appNavigator.navigateTo(
                route = Route.Main.CompanyStructure.StructureView(companyId = _route.value.companyId, departmentId = _route.value.departmentId),
                popUpToRoute = Route.Main.CompanyStructure,
                inclusive = true
            )
        }
    }
}