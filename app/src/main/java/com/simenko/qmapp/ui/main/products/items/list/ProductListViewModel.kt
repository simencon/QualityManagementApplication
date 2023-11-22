package com.simenko.qmapp.ui.main.products.items.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simenko.qmapp.di.ComponentIdParameter
import com.simenko.qmapp.di.ComponentKindIdParameter
import com.simenko.qmapp.di.ComponentStageIdParameter
import com.simenko.qmapp.di.ComponentStageKindIdParameter
import com.simenko.qmapp.di.ProductIdParameter
import com.simenko.qmapp.di.ProductKindIdParameter
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.domain.SelectedNumber
import com.simenko.qmapp.domain.ZeroValue
import com.simenko.qmapp.domain.entities.products.DomainProductKind
import com.simenko.qmapp.repository.ProductsRepository
import com.simenko.qmapp.storage.ScrollStates
import com.simenko.qmapp.storage.Storage
import com.simenko.qmapp.ui.main.main.MainPageHandler
import com.simenko.qmapp.ui.main.main.MainPageState
import com.simenko.qmapp.ui.main.main.content.Page
import com.simenko.qmapp.ui.navigation.AppNavigator
import com.simenko.qmapp.utils.InvestigationsUtils.setOnlyOneItem
import com.simenko.qmapp.utils.InvestigationsUtils.setVisibility
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
    @ProductKindIdParameter private val productKindId: ID,
    @ProductIdParameter private val productId: ID,
    @ComponentKindIdParameter private val componentKindId: ID,
    @ComponentIdParameter private val componentId: ID,
    @ComponentStageKindIdParameter private val componentStageKindId: ID,
    @ComponentStageIdParameter private val componentStageId: ID
) : ViewModel() {
    //    ToDo: Add this param into constructor later
    private val componentVersionId: ID = NoRecord.num

    private val _productsVisibility = MutableStateFlow(Pair(SelectedNumber(productId), NoRecord))
    private val _componentKindsVisibility = MutableStateFlow(Pair(SelectedNumber(componentKindId), NoRecord))
    private val _componentsVisibility = MutableStateFlow(Pair(SelectedNumber(componentId), NoRecord))
    private val _componentStageKindsVisibility = MutableStateFlow(Pair(SelectedNumber(componentStageKindId), NoRecord))
    private val _componentStagesVisibility = MutableStateFlow(Pair(SelectedNumber(componentStageId), NoRecord))

    private val _productKind = MutableStateFlow(DomainProductKind.DomainProductKindComplete())
    private val _products = repository.productKindProducts(productKindId)
    private val _componentKinds = repository.componentKinds(productKindId)
    private val _components = _productsVisibility.flatMapLatest { product ->
        _componentKindsVisibility.flatMapLatest { componentKind -> repository.components(product.first.num, componentKind.first.num) }
    }
    private val _componentStageKinds = _componentKindsVisibility.flatMapLatest { repository.componentStageKinds(it.first.num) }
    private val _componentStages = _componentsVisibility.flatMapLatest { component ->
        _componentStageKindsVisibility.flatMapLatest { componentStageKind -> repository.componentStages(component.first.num, componentStageKind.first.num) }
    }

    private val _versionsForItem = MutableStateFlow(Triple(NoRecord, NoRecord, NoRecord))
    private val _versionsVisibility = MutableStateFlow(Pair(SelectedNumber(componentStageId), NoRecord))
    private val _productVersions = _versionsForItem.flatMapLatest { repository.productVersions(it.first.num) }
    private val _componentVersions = _versionsForItem.flatMapLatest { repository.componentVersions(it.second.num) }
    private val _componentStageVersions = _versionsForItem.flatMapLatest { repository.componentStageVersions(it.second.num) }

    /**
     * Main page setup -------------------------------------------------------------------------------------------------------------------------------
     * */
    val mainPageHandler: MainPageHandler

    init {
        mainPageHandler = MainPageHandler.Builder(Page.PRODUCT_KIND_LIST, mainPageState)
            .setOnNavMenuClickAction { appNavigator.navigateBack() }
            .setOnFabClickAction {
                if (isSecondColumnVisible.value)
                    if (_versionsForItem.value.first != NoRecord) onAddProductVersionClick(_productsVisibility.value.first.num)
                    else if (_versionsForItem.value.second != NoRecord) onAddComponentVersionClick(_componentsVisibility.value.first.num)
                    else onAddComponentStageVersionClick(_componentStagesVisibility.value.first.num)
                else
                    onAddProduct(productKindId)
            }
            .setOnPullRefreshAction { updateProductsData() }
            .build()

        viewModelScope.launch(Dispatchers.IO) { _productKind.value = repository.productKind(productKindId) }
    }


    /**
     * UI operations ---------------------------------------------------------------------------------------------------------------------------------
     * */
    fun setProductsVisibility(dId: SelectedNumber = NoRecord, aId: SelectedNumber = NoRecord) {
        _productsVisibility.value = _productsVisibility.value.setVisibility(dId, aId)
    }

    fun onProductVersionsClick(id: ID) {
        _versionsForItem.value = _versionsForItem.value.setOnlyOneItem(0, id)
    }

    fun setComponentKindsVisibility(dId: SelectedNumber = NoRecord, aId: SelectedNumber = NoRecord) {
        _componentKindsVisibility.value = _componentKindsVisibility.value.setVisibility(dId, aId)
    }

    fun setComponentsVisibility(dId: SelectedNumber = NoRecord, aId: SelectedNumber = NoRecord) {
        _componentsVisibility.value = _componentsVisibility.value.setVisibility(dId, aId)
    }

    fun onComponentVersionsClick(id: ID) {
        _versionsForItem.value = _versionsForItem.value.setOnlyOneItem(1, id)
    }

    fun setComponentStageKindsVisibility(dId: SelectedNumber = NoRecord, aId: SelectedNumber = NoRecord) {
        _componentStageKindsVisibility.value = _componentStageKindsVisibility.value.setVisibility(dId, aId)
    }

    fun setComponentStagesVisibility(dId: SelectedNumber = NoRecord, aId: SelectedNumber = NoRecord) {
        _componentStagesVisibility.value = _componentStagesVisibility.value.setVisibility(dId, aId)
    }

    fun onComponentStageVersionsClick(id: ID) {
        _versionsForItem.value = _versionsForItem.value.setOnlyOneItem(1, id)
    }

    fun setVersionsVisibility(dId: SelectedNumber = NoRecord, aId: SelectedNumber = NoRecord) {
        _versionsVisibility.value = _versionsVisibility.value.setVisibility(dId, aId)
    }

    /**
     * UI state -------------------------------------------------------------------------------------------------------------------------------------
     * */
    val productKind get() = _productKind.asStateFlow()
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
                    (((versionsVisibility.first != NoRecord) && isComposed.component1()) ||
                            ((versionsVisibility.second != NoRecord) && isComposed.component3()) ||
                            ((versionsVisibility.third != NoRecord) && isComposed.component5()))
                )
            }
        }
    }.flowOn(Dispatchers.IO).conflate().stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    val listsIsInitialized: Flow<Pair<Boolean, Boolean>> = _viewState.flatMapLatest { viewState ->
        _products.flatMapLatest { products ->
            _versionListIsInitialized.flatMapLatest { sl ->
                if (viewState)
                    flow {
                        if (productId != NoRecord.num) {
                            storage.setLong(ScrollStates.PRODUCTS.indexKey, products.map { it.product.product.id }.indexOf(productId).toLong())
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

    private val _versionListIsInitialized: Flow<Boolean> = _componentVersions.flatMapLatest { versions ->
        flow {
            if (componentVersionId != NoRecord.num) {
                storage.setLong(ScrollStates.VERSIONS.indexKey, versions.map { it.version.id }.indexOf(componentVersionId).toLong())
                storage.setLong(ScrollStates.VERSIONS.offsetKey, ZeroValue.num)
                emit(true)
            } else {
                emit(true)
            }
        }
    }

    val productsVisibility = _productsVisibility.asStateFlow()
    val products = _products.flatMapLatest { products ->
        _productsVisibility.flatMapLatest { visibility ->
            val cpy = products.map { it.copy(detailsVisibility = it.productKindProduct.productId == visibility.first.num, isExpanded = it.productKindProduct.productId == visibility.second.num) }
            flow { emit(cpy) }
        }
    }

    val componentKindsVisibility = _componentKindsVisibility.asStateFlow()
    val componentKinds = _componentKinds.flatMapLatest { componentKinds ->
        _components.flatMapLatest { components ->
            _componentKindsVisibility.flatMapLatest { visibility ->
                val cpy = componentKinds.map {
                    it.copy(
                        hasComponents = components.filter { c -> c.component.componentKindComponent.componentKindId == it.componentKind.id }.isNotEmpty(),
                        detailsVisibility = it.componentKind.id == visibility.first.num,
                        isExpanded = it.componentKind.id == visibility.second.num
                    )
                }
                flow { emit(cpy) }
            }
        }
    }

    val componentsVisibility = _componentsVisibility.asStateFlow()
    val components = _components.flatMapLatest { components ->
        _componentsVisibility.flatMapLatest { visibility ->
            val cpy =
                components.map { it.copy(detailsVisibility = it.productComponent.componentId == visibility.first.num, isExpanded = it.productComponent.componentId == visibility.second.num) }
            flow { emit(cpy) }
        }
    }

    val componentStageKindsVisibility = _componentStageKindsVisibility.asStateFlow()
    val componentStageKinds = _componentStageKinds.flatMapLatest { componentKinds ->
        _componentStageKindsVisibility.flatMapLatest { visibility ->
            val cpy = componentKinds.map { it.copy(detailsVisibility = it.componentStageKind.id == visibility.first.num, isExpanded = it.componentStageKind.id == visibility.second.num) }
            flow { emit(cpy) }
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
    }

    val componentVersions = _componentVersions.flatMapLatest { versions ->
        _versionsVisibility.flatMapLatest { visibility ->
            val cpy = versions.map { it.copy(detailsVisibility = it.version.id == visibility.first.num, isExpanded = it.version.id == visibility.second.num) }
            flow { emit(cpy) }
        }
    }

    /**
     * REST operations -------------------------------------------------------------------------------------------------------------------------------
     * */
    private fun updateProductsData() {
        TODO("Not yet implemented")
    }

    fun onDeleteProductClick(it: ID) {
        TODO("Not yet implemented")
    }

    fun onDeleteComponentClick(it: ID) {
        TODO("Not yet implemented")
    }

    fun onDeleteComponentStageClick(it: ID) {
        TODO("Not yet implemented")
    }

    fun onDeleteVersionClick(it: ID) {
        TODO("Not yet implemented")
    }

    /**
     * Navigation ------------------------------------------------------------------------------------------------------------------------------------
     * */

    private fun onAddProduct(productKindId: ID) {
        TODO("Not yet implemented")
    }

    fun onEditProductClick(it: Pair<ID, ID>) {
        TODO("Not yet implemented")
    }

    fun onAddComponentClick(it: Pair<ID, ID>) {
        TODO("Not yet implemented")
    }

    fun onEditComponentClick(it: Pair<ID, ID>) {
        TODO("Not yet implemented")
    }

    fun onAddComponentStageClick(it: Pair<ID, ID>) {
        TODO("Not yet implemented")
    }

    fun onEditComponentStageClick(it: Pair<ID, ID>) {
        TODO("Not yet implemented")
    }

    private fun onAddProductVersionClick(productId: ID) {
        TODO("Not yet implemented")
    }

    private fun onAddComponentVersionClick(componentId: ID) {
        TODO("Not yet implemented")
    }

    private fun onAddComponentStageVersionClick(componentStageId: ID) {
        TODO("Not yet implemented")
    }

    fun onEditVersionClick(it: Pair<ID, ID>) {
        TODO("Not yet implemented")
    }

    fun onSpecificationClick(it: ID) {
        TODO("Not yet implemented")
    }
}