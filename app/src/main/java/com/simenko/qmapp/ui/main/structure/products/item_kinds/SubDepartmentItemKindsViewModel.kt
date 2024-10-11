package com.simenko.qmapp.ui.main.structure.products.item_kinds

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simenko.qmapp.domain.ComponentPref
import com.simenko.qmapp.domain.ComponentStagePref
import com.simenko.qmapp.domain.DomainBaseModel
import com.simenko.qmapp.domain.EmptyString
import com.simenko.qmapp.domain.FirstTabId
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.domain.ProductPref
import com.simenko.qmapp.domain.SecondTabId
import com.simenko.qmapp.domain.SelectedNumber
import com.simenko.qmapp.domain.ThirdTabId
import com.simenko.qmapp.domain.entities.DomainSubDepartment
import com.simenko.qmapp.domain.entities.products.DomainComponentKindToSubDepartment
import com.simenko.qmapp.domain.entities.products.DomainProductKindToSubDepartment
import com.simenko.qmapp.domain.entities.products.DomainStageKindToSubDepartment
import com.simenko.qmapp.other.Status
import com.simenko.qmapp.repository.ManufacturingRepository
import com.simenko.qmapp.repository.ProductsRepository
import com.simenko.qmapp.ui.main.main.MainPageHandler
import com.simenko.qmapp.ui.main.main.MainPageState
import com.simenko.qmapp.ui.main.main.content.Page
import com.simenko.qmapp.ui.navigation.AppNavigator
import com.simenko.qmapp.ui.navigation.Route.Main.CompanyStructure.SubDepartmentItemKinds
import com.simenko.qmapp.utils.InvestigationsUtils.setVisibility
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.Flow
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
class SubDepartmentItemKindsViewModel @Inject constructor(
    private val appNavigator: AppNavigator,
    private val mainPageState: MainPageState,
    private val repository: ManufacturingRepository,
    private val productRepository: ProductsRepository,
) : ViewModel() {
    private val _itemKindPref = MutableStateFlow(ProductPref.char)
    private val _route = MutableStateFlow(SubDepartmentItemKinds(departmentId = NoRecord.num, subDepartmentId = NoRecord.num))
    private val _subDepartment = MutableStateFlow(DomainSubDepartment.DomainSubDepartmentComplete())


    private val _allProductKinds = _route.flatMapLatest { route ->
        productRepository.productKindsByDepartmentId(route.departmentId)
    }
    private val _allComponentKinds = _route.flatMapLatest { route ->
        productRepository.componentKindsByDepartmentId(route.departmentId)
    }
    private val _allStageKinds = _route.flatMapLatest { route ->
        productRepository.stageKindsByDepartmentId(route.departmentId)
    }


    private val _subDepartmentProductKinds = _route.flatMapLatest { route ->
        repository.subDepartmentProductKinds(route.subDepartmentId).flatMapLatest { subDepartmentProductKinds ->
            flow { emit(subDepartmentProductKinds) }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), listOf())
    private val _subDepartmentComponentKinds = _route.flatMapLatest { route ->
        repository.subDepartmentComponentKinds(route.subDepartmentId).flatMapLatest { subDepartmentProductKinds ->
            flow { emit(subDepartmentProductKinds) }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), listOf())
    private val _subDepartmentStageKinds = _route.flatMapLatest { route ->
        repository.subDepartmentStageKinds(route.subDepartmentId).flatMapLatest { subDepartmentProductKinds ->
            flow { emit(subDepartmentProductKinds) }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), listOf())


    private val _itemKindVisibility = MutableStateFlow(Pair(SelectedNumber(NoRecord.num), NoRecord))

    private val _isAddItemDialogVisible = MutableStateFlow(false)
    private val _itemToAddId: MutableStateFlow<ID> = MutableStateFlow(NoRecord.num)
    private val _itemToAddSearchStr: MutableStateFlow<String> = MutableStateFlow(EmptyString.str)

    /**
     * Main page setup -------------------------------------------------------------------------------------------------------------------------------
     * */
    private var mainPageHandler: MainPageHandler? = null

    fun onEntered(route: SubDepartmentItemKinds) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _route.value = route
                _subDepartment.value = repository.subDepartmentById(route.subDepartmentId)
                mainPageHandler = MainPageHandler.Builder(Page.SUB_DEPARTMENT_ITEM_KINDS, mainPageState)
                    .setOnNavMenuClickAction { appNavigator.navigateBack() }
                    .setOnFabClickAction { setAddItemDialogVisibility(true) }
                    .setOnTabSelectAction {
                        when (it) {
                            FirstTabId -> {
                                _itemKindPref.value = ProductPref.char
                            }

                            SecondTabId -> {
                                _itemKindPref.value = ComponentPref.char
                            }

                            ThirdTabId -> {
                                _itemKindPref.value = ComponentStagePref.char
                            }
                        }
                        _itemKindVisibility.value = Pair(SelectedNumber(NoRecord.num), NoRecord)
                    }
                    .build()
                    .apply { setupMainPage(0, true) }
            }
        }
    }

    /**
     * UI operations ---------------------------------------------------------------------------------------------------------------------------------
     * */
    fun setProductLinesVisibility(dId: SelectedNumber = NoRecord, aId: SelectedNumber = NoRecord) {
        _itemKindVisibility.value = _itemKindVisibility.value.setVisibility(dId, aId)
    }

    /**
     * UI State --------------------------------------------------------------------------------------------------------------------------------------
     * */
    val itemKindPref get() = _itemKindPref.asStateFlow()
    val subDepartment get() = _subDepartment.asStateFlow()

    val productKinds = _subDepartmentProductKinds.flatMapLatest { productKinds ->
        _itemKindVisibility.flatMapLatest { visibility ->
            _allProductKinds.flatMapLatest { allProductKinds ->
                flow {
                    emit(allProductKinds
                        .filter { item -> productKinds.map { it.prodKindId }.contains(item.productKind.id) }
                        .map {
                            it.copy(
                                detailsVisibility = it.productKind.id == visibility.first.num,
                                isExpanded = it.productKind.id == visibility.second.num
                            )
                        }
                    )
                }
            }
        }
    }

    private val _availableProductKinds = _subDepartmentProductKinds.flatMapLatest { productKinds ->
        _itemToAddId.flatMapLatest { selectedId ->
            _allProductKinds.flatMapLatest { allProductLines ->
                flow {
                    emit(
                        allProductLines
                            .filter { item -> !productKinds.map { it.prodKindId }.contains(item.productKind.id) }
                            .map { it.copy(isSelected = it.productKind.id == selectedId) }
                    )
                }
            }
        }
    }

    val componentKinds = _subDepartmentComponentKinds.flatMapLatest { productKinds ->
        _itemKindVisibility.flatMapLatest { visibility ->
            _allComponentKinds.flatMapLatest { allProductKinds ->
                flow {
                    emit(allProductKinds
                        .filter { item -> productKinds.map { it.compKindId }.contains(item.componentKind.id) }
                        .map {
                            it.copy(
                                detailsVisibility = it.componentKind.id == visibility.first.num,
                                isExpanded = it.componentKind.id == visibility.second.num
                            )
                        }
                    )
                }
            }
        }
    }

    private val _availableComponentKinds = _subDepartmentComponentKinds.flatMapLatest { productKinds ->
        _itemToAddId.flatMapLatest { selectedId ->
            _allComponentKinds.flatMapLatest { allProductLines ->
                flow {
                    emit(
                        allProductLines
                            .filter { item -> !productKinds.map { it.compKindId }.contains(item.componentKind.id) }
                            .map { it.copy(isSelected = it.componentKind.id == selectedId) }
                    )
                }
            }
        }
    }

    val stageKinds = _subDepartmentStageKinds.flatMapLatest { productKinds ->
        _itemKindVisibility.flatMapLatest { visibility ->
            _allStageKinds.flatMapLatest { allProductKinds ->
                flow {
                    emit(allProductKinds
                        .filter { item -> productKinds.map { it.stageKindId }.contains(item.componentStageKind.id) }
                        .map {
                            it.copy(
                                detailsVisibility = it.componentStageKind.id == visibility.first.num,
                                isExpanded = it.componentStageKind.id == visibility.second.num
                            )
                        }
                    )
                }
            }
        }
    }

    private val _availableStageKinds = _subDepartmentStageKinds.flatMapLatest { productKinds ->
        _itemToAddId.flatMapLatest { selectedId ->
            _allStageKinds.flatMapLatest { allProductLines ->
                flow {
                    emit(
                        allProductLines
                            .filter { item -> !productKinds.map { it.stageKindId }.contains(item.componentStageKind.id) }
                            .map { it.copy(isSelected = it.componentStageKind.id == selectedId) }
                    )
                }
            }
        }
    }

    val availableItemKinds: Flow<List<DomainBaseModel<Any>>> = _itemKindPref.flatMapLatest {
        when (it) {
            ProductPref.char -> _availableProductKinds
            ComponentPref.char -> _availableComponentKinds
            ComponentStagePref.char -> _availableStageKinds
            else -> flow { emit(emptyList()) }
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

    fun onAddProductKind() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.run {
                when (_itemKindPref.value) {
                    ProductPref.char -> insertSubDepartmentProductKind(DomainProductKindToSubDepartment(subDepId = _route.value.subDepartmentId, prodKindId = _itemToAddId.value))
                    ComponentPref.char -> insertSubDepartmentComponentKind(DomainComponentKindToSubDepartment(subDepId = _route.value.subDepartmentId, compKindId = _itemToAddId.value))
                    ComponentStagePref.char -> insertSubDepartmentStageKind(DomainStageKindToSubDepartment(subDepId = _route.value.subDepartmentId, stageKindId = _itemToAddId.value))
                    else -> return@run
                }.consumeEach { event ->
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

    fun onDeleteProductKind(productLineId: ID) {
        when (_itemKindPref.value) {
            ProductPref.char -> _subDepartmentProductKinds.value.find { it.prodKindId == productLineId }?.id
            ComponentPref.char -> _subDepartmentComponentKinds.value.find { it.compKindId == productLineId }?.id
            ComponentStagePref.char -> _subDepartmentStageKinds.value.find { it.stageKindId == productLineId }?.id
            else -> null
        }?.let { itemId ->
            viewModelScope.launch(Dispatchers.IO) {
                repository.run {
                    when (_itemKindPref.value) {
                        ProductPref.char -> deleteSubDepartmentProductKind(itemId)
                        ComponentPref.char -> deleteSubDepartmentComponentKind(itemId)
                        ComponentStagePref.char -> deleteSubDepartmentStageKind(itemId)
                        else -> return@run
                    }.consumeEach { event ->
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