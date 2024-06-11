package com.simenko.qmapp.ui.main.products.forms

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simenko.qmapp.domain.EmptyString
import com.simenko.qmapp.domain.FillInErrorState
import com.simenko.qmapp.domain.FillInInitialState
import com.simenko.qmapp.domain.FillInState
import com.simenko.qmapp.domain.FillInSuccessState
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.domain.entities.products.DomainProductLine
import com.simenko.qmapp.other.Status
import com.simenko.qmapp.repository.ManufacturingRepository
import com.simenko.qmapp.repository.ProductsRepository
import com.simenko.qmapp.ui.main.main.MainPageHandler
import com.simenko.qmapp.ui.main.main.MainPageState
import com.simenko.qmapp.ui.main.main.content.Page
import com.simenko.qmapp.ui.navigation.AppNavigator
import com.simenko.qmapp.ui.navigation.Route
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ProductLineViewModel @Inject constructor(
    private val appNavigator: AppNavigator,
    private val mainPageState: MainPageState,
    private val repository: ProductsRepository,
    private val manufacturingRepository: ManufacturingRepository
) : ViewModel() {
    private val _productLine = MutableStateFlow(DomainProductLine.DomainProductLineComplete())

    /**
     * Main page setup -------------------------------------------------------------------------------------------------------------------------------
     * */
    private var mainPageHandler: MainPageHandler? = null

    fun onEntered(route: Route.Main.ProductLines.AddEditProductLine) {
        viewModelScope.launch(Dispatchers.IO) {
            if (route.productLineId == NoRecord.num) {
                prepareProductLine(route.companyId)
            } else {
                val productLine = repository.productLineById(route.productLineId)
                _productLine.value = productLine.copy(manufacturingProject = productLine.manufacturingProject.copy(revisionDate = DateTimeFormatter.ISO_INSTANT.format(Instant.now())))
            }

            mainPageHandler = MainPageHandler.Builder(if (route.productLineId == NoRecord.num) Page.ADD_PRODUCT_LINE else Page.EDIT_PRODUCT_LINE, mainPageState)
                .setOnNavMenuClickAction { appNavigator.navigateBack() }
                .setOnFabClickAction { validateInput() }
                .build()
                .apply { setupMainPage(0, true) }
        }
    }

    private suspend fun prepareProductLine(companyId: ID) {
        val company = manufacturingRepository.companyById(companyId)
        _productLine.value = DomainProductLine.DomainProductLineComplete(
            company = company,
            manufacturingProject = DomainProductLine(
                companyId = company.id,
                startDate = DateTimeFormatter.ISO_INSTANT.format(Instant.now()),
                revisionDate = DateTimeFormatter.ISO_INSTANT.format(Instant.now())
            ),
        )
    }

    /**
     * UI State --------------------------------------------------------------------------------------------------------------------------------------
     * */

    val productLine get() = _productLine.asStateFlow()

    private val _availableDepartments = _productLine.flatMapLatest { productLine ->
        manufacturingRepository.departmentsByParentId(productLine.getParentId())
    }.flowOn(Dispatchers.IO).conflate().stateIn(viewModelScope, SharingStarted.WhileSubscribed(), listOf())

    val availableDepartments: StateFlow<List<Triple<ID, String, Boolean>>> = _availableDepartments.flatMapLatest { departments ->
        _productLine.flatMapLatest { productLine ->
            val cpy = mutableListOf<Triple<ID, String, Boolean>>()
            departments.forEach { cpy.add(Triple(it.id, it.depAbbr ?: EmptyString.str, it.id == productLine.designDepartment.id)) }
            flow { emit(cpy) }
        }
    }.flowOn(Dispatchers.IO).conflate().stateIn(viewModelScope, SharingStarted.WhileSubscribed(), listOf())

    private val _availableEmployees = _productLine.flatMapLatest { productLine ->
        manufacturingRepository.employeesByParentId(productLine.designDepartment.id)
    }.flowOn(Dispatchers.IO).conflate().stateIn(viewModelScope, SharingStarted.WhileSubscribed(), listOf())

    val availableEmployees: StateFlow<List<Triple<ID, String, Boolean>>> = _availableEmployees.flatMapLatest { employees ->
        _productLine.flatMapLatest { productLine ->
            val cpy = mutableListOf<Triple<ID, String, Boolean>>()
            employees.forEach { cpy.add(Triple(it.id, it.fullName, it.id == productLine.designManager.id)) }
            flow { emit(cpy) }
        }
    }.flowOn(Dispatchers.IO).conflate().stateIn(viewModelScope, SharingStarted.WhileSubscribed(), listOf())


    fun onSetDesignation(designation: String) {
        if (_productLine.value.manufacturingProject.pfmeaNum != designation) {
            _productLine.value = _productLine.value.copy(manufacturingProject = _productLine.value.manufacturingProject.copy(pfmeaNum = designation))
            _fillInErrors.value = _fillInErrors.value.copy(productLineDesignationError = false)
            _fillInState.value = FillInInitialState
        }
    }

    fun onSetDescription(description: String) {
        if (_productLine.value.manufacturingProject.projectSubject != description) {
            _productLine.value = _productLine.value.copy(manufacturingProject = _productLine.value.manufacturingProject.copy(projectSubject = description))
            _fillInErrors.value = _fillInErrors.value.copy(productLineDescriptionError = false)
            _fillInState.value = FillInInitialState
        }
    }

    fun onSetDesignDepartment(id: ID) {
        if (_productLine.value.designDepartment.id != id) {
            _availableDepartments.value.find { it.id == id }?.let {
                _productLine.value = _productLine.value.copy(designDepartment = it, manufacturingProject = _productLine.value.manufacturingProject.copy(factoryLocationDep = it.id))
                _fillInErrors.value = _fillInErrors.value.copy(designDepartmentError = false)
                _fillInState.value = FillInInitialState
            }
        }
    }

    fun onSetDesignManager(id: ID) {
        if (_productLine.value.designManager.id != id) {
            _availableEmployees.value.find { it.id == id }?.let {
                _productLine.value = _productLine.value.copy(designManager = it, manufacturingProject = _productLine.value.manufacturingProject.copy(processOwner = it.id))
                _fillInErrors.value = _fillInErrors.value.copy(designManagerError = false)
                _fillInState.value = FillInInitialState
            }
        }
    }


    /**
     * Navigation ------------------------------------------------------------------------------------------------------------------------------------
     * */

    private val _fillInErrors = MutableStateFlow(FillInErrors())
    val fillInErrors get() = _fillInErrors.asStateFlow()
    private val _fillInState = MutableStateFlow<FillInState>(FillInInitialState)
    val fillInState get() = _fillInState.asStateFlow()

    private fun validateInput() {
        val errorMsg = buildString {
            with(_productLine.value.manufacturingProject) {
                if (pfmeaNum.isNullOrEmpty()) {
                    _fillInErrors.value = _fillInErrors.value.copy(productLineDesignationError = true)
                    append("Product line designation field is mandatory\n")
                }
                if (projectSubject.isNullOrEmpty()) {
                    _fillInErrors.value = _fillInErrors.value.copy(productLineDescriptionError = true)
                    append("Product line description field is mandatory\n")
                }
                if (factoryLocationDep == NoRecord.num) {
                    _fillInErrors.value = _fillInErrors.value.copy(designDepartmentError = true)
                    append("Design department field is mandatory\n")
                }
                if (processOwner == NoRecord.num) {
                    _fillInErrors.value = _fillInErrors.value.copy(designManagerError = true)
                    append("Design manager field is mandatory\n")
                }
            }
        }
        if (errorMsg.isNotEmpty()) _fillInState.value = FillInErrorState(errorMsg) else _fillInState.value = FillInSuccessState
    }

    fun makeRecord() = viewModelScope.launch(Dispatchers.IO) {
        with(repository) {
            if (_productLine.value.manufacturingProject.id == NoRecord.num) insertProductLine(_productLine.value.manufacturingProject) else updateProductLine(_productLine.value.manufacturingProject)
        }.consumeEach { event ->
            event.getContentIfNotHandled()?.let { resource ->
                when (resource.status) {
                    Status.LOADING -> mainPageHandler?.updateLoadingState?.invoke(Pair(true, null))
                    Status.SUCCESS -> navBackToRecord(resource.data?.id)
                    Status.ERROR -> {
                        mainPageHandler?.updateLoadingState?.invoke(Pair(true, resource.message))
                        _fillInState.value = FillInInitialState
                    }
                }
            }
        }
    }

    private suspend fun navBackToRecord(id: ID?) {
        mainPageHandler?.updateLoadingState?.invoke(Pair(false, null))
        withContext(Dispatchers.Main) {
            id?.let {
                val companyId = _productLine.value.manufacturingProject.companyId
                val productLineId = it
                appNavigator.tryNavigateTo(
                    route = Route.Main.ProductLines.ProductLinesList(companyId, productLineId),
                    popUpToRoute = Route.Main.ProductLines,
                    inclusive = true
                )
            }
        }
    }
}

data class FillInErrors(
    var productLineDesignationError: Boolean = false,
    var productLineDescriptionError: Boolean = false,

    var designDepartmentError: Boolean = false,
    var designManagerError: Boolean = false,
)