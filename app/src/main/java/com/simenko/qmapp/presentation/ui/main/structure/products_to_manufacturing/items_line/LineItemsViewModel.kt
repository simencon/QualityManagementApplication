package com.simenko.qmapp.presentation.ui.main.structure.products_to_manufacturing.items_line

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
import com.simenko.qmapp.domain.entities.DomainManufacturingLine
import com.simenko.qmapp.domain.entities.products.DomainComponentInStageToLine
import com.simenko.qmapp.domain.entities.products.DomainComponentToLine
import com.simenko.qmapp.domain.entities.products.DomainProductToLine
import com.simenko.qmapp.other.Status
import com.simenko.qmapp.data.repository.ManufacturingRepository
import com.simenko.qmapp.data.repository.ProductsRepository
import com.simenko.qmapp.presentation.ui.main.main.MainPageHandler
import com.simenko.qmapp.presentation.ui.main.main.MainPageState
import com.simenko.qmapp.presentation.ui.main.main.content.Page
import com.simenko.qmapp.navigation.AppNavigator
import com.simenko.qmapp.navigation.Route.Main.CompanyStructure.LineItems
import com.simenko.qmapp.utils.InvestigationsUtils.setVisibility
import com.simenko.qmapp.utils.StringUtils
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
class LineItemsViewModel @Inject constructor(
    private val appNavigator: AppNavigator,
    private val mainPageState: MainPageState,
    private val repository: ManufacturingRepository,
    private val productRepository: ProductsRepository,
) : ViewModel() {
    private val _itemKindPref = MutableStateFlow(ProductPref.char)
    private val _route = MutableStateFlow(LineItems(subDepartmentId = NoRecord.num, channelId = NoRecord.num, lineId = NoRecord.num))
    private val _line = MutableStateFlow(DomainManufacturingLine.DomainManufacturingLineComplete())


    private val _allProductItems = _route.flatMapLatest { route ->
        productRepository.productsItemsBySubDepartmentIdAndChannelId(route.subDepartmentId, route.channelId)
    }
    private val _allComponentItems = _route.flatMapLatest { route ->
        productRepository.componentsItemsBySubDepartmentIdAndChannelId(route.subDepartmentId, route.channelId)
    }
    private val _allStageItems = _route.flatMapLatest { route ->
        productRepository.stageItemsBySubDepartmentIdAndChannelId(route.subDepartmentId, route.channelId)
    }


    private val _lineProductItems = _route.flatMapLatest { route ->
        repository.lineProductItems(route.lineId).flatMapLatest { items ->
            flow { emit(items) }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), listOf())
    private val _lineComponentItems = _route.flatMapLatest { route ->
        repository.lineComponentItems(route.lineId).flatMapLatest { items ->
            flow { emit(items) }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), listOf())
    private val _lineStageItems = _route.flatMapLatest { route ->
        repository.lineStageItems(route.lineId).flatMapLatest { items ->
            flow { emit(items) }
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

    fun onEntered(route: LineItems) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _route.value = route
                _line.value = repository.lineById(route.lineId)
                mainPageHandler = MainPageHandler.Builder(Page.LINE_ITEMS, mainPageState)
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
    fun setItemsVisibility(dId: SelectedNumber = NoRecord, aId: SelectedNumber = NoRecord) {
        _itemKindVisibility.value = _itemKindVisibility.value.setVisibility(dId, aId)
    }

    /**
     * UI State --------------------------------------------------------------------------------------------------------------------------------------
     * */
    val itemKindPref get() = _itemKindPref.asStateFlow()
    val line get() = _line.asStateFlow()

    val productItems = _lineProductItems.flatMapLatest { existingRecords ->
        _itemKindVisibility.flatMapLatest { visibility ->
            _allProductItems.flatMapLatest { allRecords ->
                flow {
                    emit(allRecords
                        .filter { item -> existingRecords.map { it.productId }.contains(item.product.id) }
                        .map {
                            it.copy(
                                detailsVisibility = it.product.id == visibility.first.num,
                                isExpanded = it.product.id == visibility.second.num
                            )
                        }
                    )
                }
            }
        }
    }

    private val _availableProductItems = _lineProductItems.flatMapLatest { existingRecords ->
        _itemToAddId.flatMapLatest { selectedId ->
            _allProductItems.flatMapLatest { allRecords ->
                _itemToAddSearchStr.flatMapLatest { searchStr ->
                    flow {
                        emit(
                            allRecords
                                .filter { item -> !existingRecords.map { it.productId }.contains(item.product.id) }
                                .filter {
                                    if (searchStr.isNotEmpty()) {
                                        it.productBase.componentBaseDesignation?.lowercase()?.contains(searchStr.lowercase()) ?: false
                                                || it.product.productDesignation.lowercase().contains(searchStr.lowercase())
                                    } else {
                                        true
                                    }
                                }
                                .map { it.copy(isSelected = it.product.id == selectedId) }
                        )
                    }
                }
            }
        }
    }

    val componentItems = _lineComponentItems.flatMapLatest { existingRecords ->
        _itemKindVisibility.flatMapLatest { visibility ->
            _allComponentItems.flatMapLatest { allRecords ->
                flow {
                    emit(allRecords
                        .filter { item -> existingRecords.map { it.componentId }.contains(item.component.id) }
                        .map {
                            it.copy(
                                detailsVisibility = it.component.id == visibility.first.num,
                                isExpanded = it.component.id == visibility.second.num
                            )
                        }
                    )
                }
            }
        }
    }

    private val _availableComponentItems = _lineComponentItems.flatMapLatest { existingRecords ->
        _itemToAddId.flatMapLatest { selectedId ->
            _allComponentItems.flatMapLatest { allRecords ->
                _itemToAddSearchStr.flatMapLatest { searchStr ->
                    flow {
                        emit(
                            allRecords
                                .filter { item -> !existingRecords.map { it.componentId }.contains(item.component.id) }
                                .filter {
                                    if (searchStr.isNotEmpty()) {
                                        StringUtils.concatTwoStrings3(it.key.componentKey, it.component.componentDesignation).lowercase().contains(searchStr.lowercase())
                                    } else {
                                        true
                                    }
                                }
                                .map { it.copy(isSelected = it.component.id == selectedId) }
                        )
                    }
                }
            }
        }
    }

    val stageItems = _lineStageItems.flatMapLatest { existingRecords ->
        _itemKindVisibility.flatMapLatest { visibility ->
            _allStageItems.flatMapLatest { allRecords ->
                flow {
                    emit(allRecords
                        .filter { item -> existingRecords.map { it.componentInStageId }.contains(item.componentStage.id) }
                        .map {
                            it.copy(
                                detailsVisibility = it.componentStage.id == visibility.first.num,
                                isExpanded = it.componentStage.id == visibility.second.num
                            )
                        }
                    )
                }
            }
        }
    }

    private val _availableStageItems = _lineStageItems.flatMapLatest { existingRecords ->
        _itemToAddId.flatMapLatest { selectedId ->
            _allStageItems.flatMapLatest { allRecords ->
                _itemToAddSearchStr.flatMapLatest { searchStr ->
                    flow {
                        emit(
                            allRecords
                                .filter { item -> !existingRecords.map { it.componentInStageId }.contains(item.componentStage.id) }
                                .filter {
                                    if (searchStr.isNotEmpty()) {
                                        StringUtils.concatTwoStrings3(it.key.componentKey, it.componentStage.componentInStageDescription).lowercase().contains(searchStr.lowercase())
                                    } else {
                                        true
                                    }
                                }
                                .map { it.copy(isSelected = it.componentStage.id == selectedId) }
                        )
                    }
                }
            }
        }
    }

    val availableItems: Flow<List<DomainBaseModel<Any>>> = _itemKindPref.flatMapLatest {
        when (it) {
            ProductPref.char -> _availableProductItems
            ComponentPref.char -> _availableComponentItems
            ComponentStagePref.char -> _availableStageItems
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

    fun onAddItem() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.run {
                when (_itemKindPref.value) {
                    ProductPref.char -> insertLineProduct(DomainProductToLine(lineId = _route.value.lineId, productId = _itemToAddId.value))
                    ComponentPref.char -> insertLineComponent(DomainComponentToLine(lineId = _route.value.lineId, componentId = _itemToAddId.value))
                    ComponentStagePref.char -> insertLineStage(DomainComponentInStageToLine(lineId = _route.value.lineId, componentInStageId = _itemToAddId.value))
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

    fun onDeleteItem(id: ID) {
        when (_itemKindPref.value) {
            ProductPref.char -> _lineProductItems.value.find { it.productId == id }?.id
            ComponentPref.char -> _lineComponentItems.value.find { it.componentId == id }?.id
            ComponentStagePref.char -> _lineStageItems.value.find { it.componentInStageId == id }?.id
            else -> null
        }?.let { itemId ->
            viewModelScope.launch(Dispatchers.IO) {
                repository.run {
                    when (_itemKindPref.value) {
                        ProductPref.char -> deleteLineProduct(itemId)
                        ComponentPref.char -> deleteLineComponent(itemId)
                        ComponentStagePref.char -> deleteLineStage(itemId)
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