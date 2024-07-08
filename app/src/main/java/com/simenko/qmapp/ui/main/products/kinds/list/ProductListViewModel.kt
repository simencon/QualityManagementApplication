package com.simenko.qmapp.ui.main.products.kinds.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simenko.qmapp.domain.EmptyString
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.domain.NoRecordStr
import com.simenko.qmapp.domain.SelectedNumber
import com.simenko.qmapp.domain.SelectedString
import com.simenko.qmapp.domain.ZeroValue
import com.simenko.qmapp.other.Status
import com.simenko.qmapp.repository.ProductsRepository
import com.simenko.qmapp.storage.ScrollStates
import com.simenko.qmapp.storage.Storage
import com.simenko.qmapp.ui.main.main.MainPageHandler
import com.simenko.qmapp.ui.main.main.MainPageState
import com.simenko.qmapp.ui.main.main.content.Page
import com.simenko.qmapp.ui.navigation.AppNavigator
import com.simenko.qmapp.ui.navigation.Route
import com.simenko.qmapp.utils.InvestigationsUtils.setVisibility
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.Flow
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
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ProductListViewModel @Inject constructor(
    private val appNavigator: AppNavigator,
    private val mainPageState: MainPageState,
    private val repository: ProductsRepository,
    val storage: Storage,
) : ViewModel() {
    private val _searchString = MutableStateFlow(EmptyString.str)
    private val _productKindId = MutableStateFlow(NoRecord.num)
    private val _productsVisibility = MutableStateFlow(Pair(SelectedNumber(NoRecord.num), NoRecord))
    private val _componentKindsVisibility = MutableStateFlow(Pair(SelectedNumber(NoRecord.num), NoRecord))
    private val _componentsVisibility = MutableStateFlow(Pair(SelectedNumber(NoRecord.num), NoRecord))
    private val _componentStageKindsVisibility = MutableStateFlow(Pair(SelectedNumber(NoRecord.num), NoRecord))
    private val _componentStagesVisibility = MutableStateFlow(Pair(SelectedNumber(NoRecord.num), NoRecord))
    private val _versionsVisibility = MutableStateFlow(Pair(SelectedString(NoRecordStr.str), NoRecordStr))
    private val _products = _productKindId.flatMapLatest { repository.productKindProducts(it) }
    private val _componentKinds = _productKindId.flatMapLatest { repository.componentKinds(it) }
    private val _components = _productsVisibility.flatMapLatest { product ->
        _componentKindsVisibility.flatMapLatest { componentKind -> repository.components(product.first.num, componentKind.first.num) }
    }
    private val _componentsAll = _productsVisibility.flatMapLatest { product -> repository.components(product.first.num, NoRecord.num) }

    private val _componentStageKinds = _componentKindsVisibility.flatMapLatest { repository.componentStageKinds(it.first.num) }
    private val _componentStages = _componentsVisibility.flatMapLatest { component ->
        _componentStageKindsVisibility.flatMapLatest { componentStageKind -> repository.componentStages(component.first.num, componentStageKind.first.num) }
    }
    private val _componentStagesAll = _componentsVisibility.flatMapLatest { component -> repository.componentStages(component.first.num, NoRecord.num) }

    private val _versionsForItem = MutableStateFlow(NoRecordStr)
    private val _versions = _versionsForItem.flatMapLatest { repository.itemVersionsComplete(it.str) }

    /**
     * Main page setup -------------------------------------------------------------------------------------------------------------------------------
     * */
    var mainPageHandler: MainPageHandler? = null

    fun onEntered(route: Route.Main.ProductLines.ProductKinds.Products.ProductsList) {
        viewModelScope.launch {
            if (mainPageHandler == null) {
                _productKindId.value = route.productKindId
                _productsVisibility.value = Pair(SelectedNumber(route.productId), NoRecord)
                _componentKindsVisibility.value = Pair(SelectedNumber(route.componentKindId), NoRecord)
                _componentsVisibility.value = Pair(SelectedNumber(route.componentId), NoRecord)
                _componentStageKindsVisibility.value = Pair(SelectedNumber(route.componentStageKindId), NoRecord)
                _componentStagesVisibility.value = Pair(SelectedNumber(route.componentStageId), NoRecord)
                _versionsVisibility.value = Pair(SelectedString(route.versionFId), NoRecordStr)
            }

            mainPageHandler = MainPageHandler.Builder(Page.PRODUCT_KIND_LIST, mainPageState)
                .setOnNavMenuClickAction { appNavigator.navigateBack() }
                .setOnFabClickAction { if (isSecondColumnVisible.value) onAddVersionClick(_versionsForItem.value.str) else onAddProductKindProduct(SheetInputData.AddProductKindProduct(productKindId = route.productKindId)) }
                .setOnPullRefreshAction { updateProductsData() }
                .setOnSearchClickAction { _searchString.value = it.stringToSearch ?: EmptyString.str }
                .build()
                .apply { setupMainPage(0, true) }
        }
    }


    /**
     * UI operations ---------------------------------------------------------------------------------------------------------------------------------
     * */
    fun setProductsVisibility(dId: SelectedNumber = NoRecord, aId: SelectedNumber = NoRecord) {
        _productsVisibility.value = _productsVisibility.value.setVisibility(dId, aId)
    }

    fun setComponentKindsVisibility(dId: SelectedNumber = NoRecord, aId: SelectedNumber = NoRecord) {
        _componentKindsVisibility.value = _componentKindsVisibility.value.setVisibility(dId, aId)
    }

    fun setComponentsVisibility(dId: SelectedNumber = NoRecord, aId: SelectedNumber = NoRecord) {
        _componentsVisibility.value = _componentsVisibility.value.setVisibility(dId, aId)
    }

    fun setComponentStageKindsVisibility(dId: SelectedNumber = NoRecord, aId: SelectedNumber = NoRecord) {
        _componentStageKindsVisibility.value = _componentStageKindsVisibility.value.setVisibility(dId, aId)
    }

    fun setComponentStagesVisibility(dId: SelectedNumber = NoRecord, aId: SelectedNumber = NoRecord) {
        _componentStagesVisibility.value = _componentStagesVisibility.value.setVisibility(dId, aId)
    }

    fun onVersionsClick(fpId: String) {
        if (fpId != _versionsForItem.value.str) _versionsForItem.value = SelectedString(fpId) else _versionsForItem.value = NoRecordStr
    }

    fun setVersionsVisibility(dId: SelectedString = NoRecordStr, aId: SelectedString = NoRecordStr) {
        _versionsVisibility.value = _versionsVisibility.value.setVisibility(dId, aId)
    }

    /**
     * UI state -------------------------------------------------------------------------------------------------------------------------------------
     * */
    val productKind get() = _productKindId.flatMapLatest { flow { emit(repository.productKind(it)) } }.flowOn(Dispatchers.IO)
    val versionsForItem get() = _versionsForItem.asStateFlow()

    private val _viewState = MutableStateFlow(false)
    val setViewState: (Boolean) -> Unit = {
        if (!it) _isComposed.value = BooleanArray(6) { false }
        _viewState.value = it
    }

    private val _isComposed = MutableStateFlow(BooleanArray(6) { false })
    val setIsComposed: (Int, Boolean) -> Unit = { i, value ->
        val cpy = _isComposed.value.copyOf()
        cpy[i] = value
        _isComposed.value = cpy
    }

    val isSecondColumnVisible: StateFlow<Boolean> = _isComposed.flatMapLatest { isComposed ->
        _versionsForItem.flatMapLatest { versionsVisibility ->
            flow {
                emit(
                    ((versionsVisibility != NoRecordStr) && (isComposed.component1() || isComposed.component3() || isComposed.component5()))
                )
            }
        }
    }.flowOn(Dispatchers.IO).conflate().stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    private val _bottomSheetState: MutableStateFlow<SheetInputData> = MutableStateFlow(SheetInputData.Nothing)
    fun onHideBottomSheet() {
        _bottomSheetState.value = SheetInputData.Nothing
    }

    val bottomSheetState = _bottomSheetState.asStateFlow()

    val listsIsInitialized: Flow<Pair<Boolean, Boolean>> = _viewState.flatMapLatest { viewState ->
        _products.flatMapLatest { products ->
            _versionListIsInitialized.flatMapLatest { sl ->
                if (viewState)
                    flow {
                        if (_productsVisibility.value.first.num != NoRecord.num) {
                            storage.setLong(ScrollStates.PRODUCTS.indexKey, products.map { it.product.product.id }.indexOf(_productsVisibility.value.first.num).toLong())
                            storage.setLong(ScrollStates.PRODUCTS.offsetKey, ZeroValue.num)
                            emit(Pair(true, sl))

                        } else {
                            emit(Pair(true, sl))
                        }
                    }
                else
                    flow {
                        emit(Pair(false, false))
                    }
            }
        }
    }

    private val _versionListIsInitialized: Flow<Boolean> = _versions.flatMapLatest { versions ->
        flow {
            if (_versionsVisibility.value.first.str != NoRecordStr.str) {
                storage.setLong(ScrollStates.VERSIONS.indexKey, versions.map { it.itemVersion.fId }.indexOf(_versionsVisibility.value.first.str).toLong())
                storage.setLong(ScrollStates.VERSIONS.offsetKey, ZeroValue.num)
                emit(true)
            } else {
                emit(true)
            }
        }
    }

    val products = _products.flatMapLatest { products ->
        _searchString.flatMapLatest { searchString ->
            _productsVisibility.flatMapLatest { visibility ->
                val cpy = products.filter {
                    searchString.isEmpty() ||
                            it.product.product.productDesignation.lowercase().contains(searchString.lowercase()) ||
                            (it.product.productBase.componentBaseDesignation ?: EmptyString.str).lowercase().contains(searchString.lowercase())
                }.map { it.copy(detailsVisibility = it.productKindProduct.productId == visibility.first.num, isExpanded = it.productKindProduct.productId == visibility.second.num) }
                flow { emit(cpy) }
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    val componentKinds = _componentKinds.flatMapLatest { componentKinds ->
        _componentsAll.flatMapLatest { components ->
            _componentKindsVisibility.flatMapLatest { visibility ->
                val cpy = componentKinds.map {
                    it.copy(
                        hasComponents = components.any { c -> c.component.componentKind.id == it.componentKind.id },
                        detailsVisibility = it.componentKind.id == visibility.first.num,
                        isExpanded = it.componentKind.id == visibility.second.num
                    )
                }
                flow { emit(cpy) }
            }
        }
    }

    val components = _components.flatMapLatest { components ->
        _componentsVisibility.flatMapLatest { visibility ->
            val cpy =
                components.map { it.copy(detailsVisibility = it.productComponent.componentId == visibility.first.num, isExpanded = it.productComponent.componentId == visibility.second.num) }
            flow { emit(cpy) }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    val componentStageKinds = _componentStageKinds.flatMapLatest { componentStageKinds ->
        _componentStagesAll.flatMapLatest { componentStages ->
            _componentStageKindsVisibility.flatMapLatest { visibility ->
                val cpy = componentStageKinds.map {
                    it.copy(
                        hasComponentStages = componentStages.any { c -> c.componentStage.componentStageKind.id == it.componentStageKind.id },
                        detailsVisibility = it.componentStageKind.id == visibility.first.num,
                        isExpanded = it.componentStageKind.id == visibility.second.num
                    )
                }
                flow { emit(cpy) }
            }
        }
    }

    val componentStages = _componentStages.flatMapLatest { components ->
        _componentStagesVisibility.flatMapLatest { visibility ->
            val cpy = components.map {
                it.copy(
                    detailsVisibility = it.componentComponentStage.componentStageId == visibility.first.num,
                    isExpanded = it.componentComponentStage.componentStageId == visibility.second.num
                )
            }
            flow { emit(cpy) }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    val componentVersions = _versions.flatMapLatest { versions ->
        _versionsVisibility.flatMapLatest { visibility ->
            val cpy = versions.map { it.copy(detailsVisibility = it.itemVersion.fId == visibility.first.str, isExpanded = it.itemVersion.fId == visibility.second.str) }
            flow { emit(cpy) }
        }
    }

    /**
     * REST operations -------------------------------------------------------------------------------------------------------------------------------
     * */
    private fun updateProductsData() {
        TODO("Not yet implemented")
    }

    fun onDeleteProductClick(it: ID) = viewModelScope.launch {
        products.value.find { it.isExpanded }?.let { productKindProduct ->
            if (productKindProduct.product.product.id == it) {
                with(repository) {
                    deleteProductKindProduct(productKindProduct.productKindProduct.id).consumeEach { event ->
                        event.getContentIfNotHandled()?.let { resource ->
                            when (resource.status) {
                                Status.LOADING -> mainPageHandler?.updateLoadingState?.invoke(Pair(true, null))
                                Status.SUCCESS -> mainPageHandler?.updateLoadingState?.invoke(Pair(false, null))
                                Status.ERROR -> mainPageHandler?.updateLoadingState?.invoke(Pair(false, resource.message))
                            }
                        }
                    }
                }
            }
        }
    }

    fun onDeleteComponentClick(it: ID) = viewModelScope.launch {
        components.value.find { it.isExpanded }?.let { productComponent ->
            if (productComponent.productComponent.componentId == it) {
                with(repository) {
                    deleteProductComponent(productComponent.productComponent.id).consumeEach { event ->
                        event.getContentIfNotHandled()?.let { resource ->
                            when (resource.status) {
                                Status.LOADING -> mainPageHandler?.updateLoadingState?.invoke(Pair(true, null))
                                Status.SUCCESS -> mainPageHandler?.updateLoadingState?.invoke(Pair(false, null))
                                Status.ERROR -> mainPageHandler?.updateLoadingState?.invoke(Pair(false, resource.message))
                            }
                        }
                    }
                }
            }
        }
    }

    fun onDeleteComponentStageClick(it: ID) = viewModelScope.launch {
        componentStages.value.find { it.isExpanded }?.let { componentStage ->
            if (componentStage.componentComponentStage.componentStageId == it) {
                with(repository) {
                    deleteComponentComponentStage(componentStage.componentComponentStage.id).consumeEach { event ->
                        event.getContentIfNotHandled()?.let { resource ->
                            when (resource.status) {
                                Status.LOADING -> mainPageHandler?.updateLoadingState?.invoke(Pair(true, null))
                                Status.SUCCESS -> mainPageHandler?.updateLoadingState?.invoke(Pair(false, null))
                                Status.ERROR -> mainPageHandler?.updateLoadingState?.invoke(Pair(false, resource.message))
                            }
                        }
                    }
                }
            }
        }
    }

    fun onDeleteVersionClick(it: String) {
        TODO("Not yet implemented")
    }

    /**
     * Navigation ------------------------------------------------------------------------------------------------------------------------------------
     * */

    private fun onAddProductKindProduct(inputData: SheetInputData) {
        _bottomSheetState.value = inputData
    }

    fun onAddNewItem(inputData: SheetInputData) {
        _bottomSheetState.value = SheetInputData.Nothing
        when (inputData) {
            is SheetInputData.AddProductKindProduct -> {
                appNavigator.tryNavigateTo(route = Route.Main.ProductLines.ProductKinds.Products.AddEditProduct(productKindId = inputData.productKindId))
            }

            is SheetInputData.AddProductComponent -> {
                appNavigator.tryNavigateTo(
                    route = Route.Main.ProductLines.ProductKinds.Products.AddEditComponent(
                        productKindId = inputData.productKindId,
                        productId = inputData.productId,
                        componentKindId = inputData.componentKindId
                    )
                )
            }

            is SheetInputData.AddComponentComponentStage -> {
                appNavigator.tryNavigateTo(
                    route = Route.Main.ProductLines.ProductKinds.Products.AddEditComponentStage(
                        productKindId = inputData.productKindId,
                        productId = inputData.productId,
                        componentKindId = inputData.componentKindId,
                        componentId = inputData.componentId,
                        componentStageKindId = inputData.componentStageKindId
                    )
                )
            }

            is SheetInputData.Nothing -> Unit
        }
    }

    fun onSelectExistingItem(inputData: SheetInputData) {
        _bottomSheetState.value = SheetInputData.Nothing
        when (inputData) {
            is SheetInputData.AddProductKindProduct -> {
                appNavigator.tryNavigateTo(route = Route.Main.ProductLines.ProductKinds.Products.AddProductKindProduct(productKindId = inputData.productKindId))
            }

            is SheetInputData.AddProductComponent -> {
                appNavigator.tryNavigateTo(
                    route = Route.Main.ProductLines.ProductKinds.Products.AddProductComponent(
                        productId = inputData.productId,
                        componentKindId = inputData.componentKindId
                    )
                )
            }

            is SheetInputData.AddComponentComponentStage -> {
                appNavigator.tryNavigateTo(
                    route = Route.Main.ProductLines.ProductKinds.Products.AddComponentComponentStage(
                        productKindId = _productKindId.value,
                        productId = _productsVisibility.value.first.num,
                        componentKindId = _componentKindsVisibility.value.first.num,
                        componentId = _componentsVisibility.value.first.num,
                        componentStageKindId = _componentStageKindsVisibility.value.first.num
                    )
                )
            }

            is SheetInputData.Nothing -> Unit
        }
    }

    fun onEditProductClick(it: Pair<ID, ID>) {
        appNavigator.tryNavigateTo(route = Route.Main.ProductLines.ProductKinds.Products.AddEditProduct(productKindId = it.first, productId = it.second))
    }

    fun onAddComponentClick() {
        _bottomSheetState.value = SheetInputData.AddProductComponent(
            productKindId = _productKindId.value,
            productId = _productsVisibility.value.first.num,
            componentKindId = _componentKindsVisibility.value.first.num
        )
    }

    fun onEditComponentClick(id: ID) {
        appNavigator.tryNavigateTo(
            route = Route.Main.ProductLines.ProductKinds.Products.AddEditComponent(
                productKindId = _productKindId.value,
                productId = _productsVisibility.value.first.num,
                componentKindId = _componentKindsVisibility.value.first.num,
                componentId = id
            )
        )
    }

    fun onAddComponentStageClick() {
        _bottomSheetState.value = SheetInputData.AddComponentComponentStage(
            productKindId = _productKindId.value,
            productId = _productsVisibility.value.first.num,
            componentKindId = _componentKindsVisibility.value.first.num,
            componentId = _componentsVisibility.value.first.num,
            componentStageKindId = _componentStageKindsVisibility.value.first.num
        )
    }

    fun onEditComponentStageClick(it: ID) {
        appNavigator.tryNavigateTo(
            route = Route.Main.ProductLines.ProductKinds.Products.AddEditComponentStage(
                productKindId = _productKindId.value,
                productId = _productsVisibility.value.first.num,
                componentKindId = _componentKindsVisibility.value.first.num,
                componentId = _componentsVisibility.value.first.num,
                componentStageKindId = _componentStageKindsVisibility.value.first.num,
                componentStageId = it
            )
        )
    }

    private fun onAddVersionClick(fId: String) {
        TODO("Not yet implemented")
    }

    fun onEditVersionClick(it: Pair<String, String>) {
        TODO("Not yet implemented")
    }

    fun onSpecificationClick(it: String) {
        appNavigator.tryNavigateTo(route = Route.Main.ProductLines.ProductKinds.Products.VersionTolerances.VersionTolerancesDetails(versionFId = it))
    }
}

sealed interface SheetInputData {
    val addItemBtnText: String
    val selectExistingItemBtnText: String

    data object Nothing : SheetInputData {
        override val addItemBtnText = EmptyString.str
        override val selectExistingItemBtnText = EmptyString.str
    }

    data class AddProductKindProduct(
        override val addItemBtnText: String = "Create new product",
        override val selectExistingItemBtnText: String = "Select existing product",
        val productKindId: ID
    ) : SheetInputData

    data class AddProductComponent(
        override val addItemBtnText: String = "Create new component",
        override val selectExistingItemBtnText: String = "Select existing component",
        val productKindId: ID, val productId: ID, val componentKindId: ID
    ) : SheetInputData

    data class AddComponentComponentStage(
        override val addItemBtnText: String = "Create new component stage",
        override val selectExistingItemBtnText: String = "Select existing component stage",
        val productKindId: ID, val productId: ID, val componentKindId: ID, val componentId: ID, val componentStageKindId: ID
    ) : SheetInputData
}