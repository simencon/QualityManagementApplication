package com.simenko.qmapp.ui.main.products.kinds.set.designations

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simenko.qmapp.domain.EmptyString
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.domain.SelectedNumber
import com.simenko.qmapp.domain.entities.products.DomainComponentKind
import com.simenko.qmapp.domain.entities.products.DomainComponentKindKey
import com.simenko.qmapp.other.Status
import com.simenko.qmapp.repository.ProductsRepository
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ComponentKindKeysViewModel @Inject constructor(
    private val appNavigator: AppNavigator,
    private val mainPageState: MainPageState,
    private val repository: ProductsRepository,
    val storage: Storage,
) : ViewModel() {
    private val _componentKind = MutableStateFlow(DomainComponentKind.DomainComponentKindComplete())
    private val _componentKindKeysVisibility = MutableStateFlow(Pair(SelectedNumber(NoRecord.num), NoRecord))
    private val _componentKindKeys = _componentKind.flatMapLatest { repository.componentKindKeys(it.componentKind.id) }

    private val _isAddItemDialogVisible = MutableStateFlow(false)
    private val _itemToAddId: MutableStateFlow<ID> = MutableStateFlow(NoRecord.num)
    private val _itemToAddSearchStr: MutableStateFlow<String> = MutableStateFlow(EmptyString.str)

    /**
     * Main page setup -------------------------------------------------------------------------------------------------------------------------------
     * */
    private var mainPageHandler: MainPageHandler? = null

    fun onEntered(route: Route.Main.ProductLines.ProductKinds.ProductSpecification.ComponentKindKeys.ComponentKindKeysList) {
        viewModelScope.launch(Dispatchers.IO) {
            if (mainPageHandler == null) {
                _componentKind.value = repository.componentKind(route.componentKindId)
                _componentKindKeysVisibility.value = Pair(SelectedNumber(route.componentKindKeyId), NoRecord)
            }

            mainPageHandler = MainPageHandler.Builder(Page.COMPONENT_KIND_KEYS, mainPageState)
                .setOnNavMenuClickAction { appNavigator.navigateBack() }
                .setOnFabClickAction { onAddComponentKindKeyClick() }
                .setOnPullRefreshAction { updateCompanyProductsData() }
                .build()
                .apply { setupMainPage(0, true) }
        }
    }

    /**
     * UI operations ---------------------------------------------------------------------------------------------------------------------------------
     * */
    fun setComponentKindKeysVisibility(dId: SelectedNumber = NoRecord, aId: SelectedNumber = NoRecord) {
        _componentKindKeysVisibility.value = _componentKindKeysVisibility.value.setVisibility(dId, aId)
    }

    /**
     * UI state --------------------------------------------------------------------------------------------------------------------------------------
     * */
    val componentKind = _componentKind.asStateFlow()

    val componentKindKeys = _componentKindKeys.flatMapLatest { productKindKeys ->
        _componentKindKeysVisibility.flatMapLatest { visibility ->
            val cpy = productKindKeys.map {
                it.copy(key = it.key.copy(detailsVisibility = it.key.productLineKey.id == visibility.first.num, isExpanded = it.key.productLineKey.id == visibility.second.num))
            }
            flow { emit(cpy) }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    val availableKeys = _componentKind.flatMapLatest { componentKind ->
        _componentKindKeys.flatMapLatest { addedKeys ->
            _itemToAddSearchStr.flatMapLatest { searchStr ->
                _itemToAddId.flatMapLatest { id ->
                    repository.productLineKeys(componentKind.productKind.productLine.manufacturingProject.id).map { list ->
                        list
                            .subtract(addedKeys.map { it.key }.toSet())
                            .filter {
                                if (searchStr.isNotEmpty()) {
                                    it.productLineKey.componentKeyDescription?.lowercase()?.contains(searchStr.lowercase()) ?: false
                                            || it.productLineKey.componentKey.lowercase().contains(searchStr.lowercase())
                                } else {
                                    true
                                }
                            }
                            .map { item -> item.copy(isSelected = item.productLineKey.id == id) }
                    }
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

    /**
     * REST operations -------------------------------------------------------------------------------------------------------------------------------
     * */
    fun onDeleteComponentKindKeyClick(it: ID) = viewModelScope.launch(Dispatchers.IO) {
        componentKindKeys.value.find { it.key.isExpanded }?.let { itemToDelete ->
            if (itemToDelete.componentKindKey.keyId == it)
                with(repository) {
                    deleteComponentKindKey(itemToDelete.componentKindKey.id).consumeEach { event ->
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

    private fun updateCompanyProductsData() = viewModelScope.launch(Dispatchers.IO) {
        try {
            mainPageHandler?.updateLoadingState?.invoke(Pair(true, null))

            repository.syncComponentKindsKeys()

            mainPageHandler?.updateLoadingState?.invoke(Pair(false, null))
        } catch (e: Exception) {
            mainPageHandler?.updateLoadingState?.invoke(Pair(false, e.message))
        }
    }

    /**
     * Navigation ------------------------------------------------------------------------------------------------------------------------------------
     * */
    private fun onAddComponentKindKeyClick() {
        _isAddItemDialogVisible.value = true
    }

    fun onItemSelect(id: ID) {
        _itemToAddId.value = id
    }

    fun onAddItemClick() = viewModelScope.launch(Dispatchers.IO) {
        val componentKindId = _componentKind.value.componentKind.id
        val keyId = _itemToAddId.value
        if (componentKindId != NoRecord.num && keyId != NoRecord.num) {
            //  make record!
            with(repository) {
                _isAddItemDialogVisible.value = false
                _itemToAddId.value = NoRecord.num
                insertComponentKindKey(
                    DomainComponentKindKey(
                        id = NoRecord.num,
                        componentKindId = componentKindId,
                        keyId = keyId
                    )
                ).consumeEach { event ->
                    event.getContentIfNotHandled()?.let { resource ->
                        when (resource.status) {
                            Status.LOADING -> mainPageHandler?.updateLoadingState?.invoke(Pair(true, null))
                            Status.SUCCESS -> {
                                mainPageHandler?.updateLoadingState?.invoke(Pair(false, null))
                                resource.data?.let { setComponentKindKeysVisibility(dId = SelectedNumber(it.keyId)) }
                            }

                            Status.ERROR -> mainPageHandler?.updateLoadingState?.invoke(Pair(true, resource.message))
                        }
                    }
                }
            }
        }
    }
}