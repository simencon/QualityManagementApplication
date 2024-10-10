package com.simenko.qmapp.ui.main.structure.products.product_lines

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simenko.qmapp.domain.EmptyString
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.domain.SelectedNumber
import com.simenko.qmapp.domain.entities.DomainDepartment
import com.simenko.qmapp.domain.entities.products.DomainProductLineToDepartment
import com.simenko.qmapp.other.Status
import com.simenko.qmapp.repository.ManufacturingRepository
import com.simenko.qmapp.repository.ProductsRepository
import com.simenko.qmapp.ui.main.main.MainPageHandler
import com.simenko.qmapp.ui.main.main.MainPageState
import com.simenko.qmapp.ui.main.main.content.Page
import com.simenko.qmapp.ui.navigation.AppNavigator
import com.simenko.qmapp.ui.navigation.Route.Main.CompanyStructure.DepartmentProductLines
import com.simenko.qmapp.utils.InvestigationsUtils.setVisibility
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
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
    private val _allProductLines = _route.flatMapLatest { route ->
        productRepository.productLines(route.companyId)
    }
    private val _department = MutableStateFlow(DomainDepartment.DomainDepartmentComplete())
    private val _departmentProductLines = _route.flatMapLatest { route ->
        repository.departmentProductLines(route.departmentId).flatMapLatest { departmentProductLines ->
            flow { emit(departmentProductLines) }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), listOf())
    private val _productLinesVisibility = MutableStateFlow(Pair(SelectedNumber(NoRecord.num), NoRecord))

    private val _isAddItemDialogVisible = MutableStateFlow(false)
    private val _itemToAddId: MutableStateFlow<ID> = MutableStateFlow(NoRecord.num)
    private val _itemToAddSearchStr: MutableStateFlow<String> = MutableStateFlow(EmptyString.str)

    /**
     * Main page setup -------------------------------------------------------------------------------------------------------------------------------
     * */
    private var mainPageHandler: MainPageHandler? = null

    fun onEntered(route: DepartmentProductLines) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _route.value = route
                _department.value = repository.departmentById(route.departmentId)
                mainPageHandler = MainPageHandler.Builder(Page.DEPARTMENT_PRODUCT_LINES, mainPageState)
                    .setOnNavMenuClickAction { appNavigator.navigateBack() }
                    .setOnFabClickAction { setAddItemDialogVisibility(true) }
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

    val productLines = _departmentProductLines.flatMapLatest { productLinesIds ->
        _productLinesVisibility.flatMapLatest { visibility ->
            _allProductLines.flatMapLatest { allProductLines ->
                flow {
                    emit(allProductLines
                        .filter { productLinesIds.map { it.productLineId }.contains(it.manufacturingProject.id) }
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

    val availableProductLines = _departmentProductLines.flatMapLatest { productLinesIds ->
        _itemToAddId.flatMapLatest { selectedId ->
            _allProductLines.flatMapLatest { allProductLines ->
                flow {
                    emit(
                        allProductLines
                            .filter { item -> !productLinesIds.map { it.productLineId }.contains(item.manufacturingProject.id) }
                            .map { it.copy(isSelected = it.manufacturingProject.id == selectedId) }
                    )
                }
            }
        }
    }

    val isAddItemDialogVisible = _isAddItemDialogVisible.asStateFlow()
    fun setAddItemDialogVisibility(value: Boolean) {
        if (!value) {
            _itemToAddSearchStr.value = EmptyString.str
            _itemToAddId.value = NoRecord.num
        }
        _isAddItemDialogVisible.value = value
    }

    val itemToAddSearchStr = _itemToAddSearchStr.asStateFlow()
    fun setItemToAddSearchStr(value: String) {
        if (_itemToAddSearchStr.value != value) _itemToAddSearchStr.value = value
    }

    fun onItemSelect(id: ID) {
        _itemToAddId.value = id
    }

    /**
     * Navigation ------------------------------------------------------------------------------------------------------------------------------------
     * */

    fun onAddProductLine() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.run {
                insertDepartmentProductLine(DomainProductLineToDepartment(depId = _route.value.departmentId, productLineId = _itemToAddId.value)).consumeEach { event ->
                    event.getContentIfNotHandled()?.let { resource ->
                        when (resource.status) {
                            Status.LOADING -> mainPageHandler?.updateLoadingState?.invoke(Triple(false, true, null))

                            Status.SUCCESS -> {
                                setAddItemDialogVisibility(false); mainPageHandler?.updateLoadingState?.invoke(Triple(false, false, null))
                            }

                            Status.ERROR -> {
                                mainPageHandler?.updateLoadingState?.invoke(Triple(false, false, resource.message))
                            }
                        }
                    }
                }
            }
        }
    }

    fun onDeleteProductLineClick(productLineId: ID) {
        _departmentProductLines.value.find { it.productLineId == productLineId }?.let { depProductLine ->
            viewModelScope.launch(Dispatchers.IO) {
                repository.run {
                    deleteDepartmentProductLine(depProductLine.id).consumeEach { event ->
                        event.getContentIfNotHandled()?.let { resource ->
                            when (resource.status) {
                                Status.LOADING -> mainPageHandler?.updateLoadingState?.invoke(Triple(false, true, null))

                                Status.SUCCESS -> {
                                    setAddItemDialogVisibility(false); mainPageHandler?.updateLoadingState?.invoke(Triple(false, false, null))
                                }

                                Status.ERROR -> {
                                    mainPageHandler?.updateLoadingState?.invoke(Triple(false, false, resource.message))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}