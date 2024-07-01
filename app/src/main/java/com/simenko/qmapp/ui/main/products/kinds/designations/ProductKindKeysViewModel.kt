package com.simenko.qmapp.ui.main.products.kinds.designations

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simenko.qmapp.domain.EmptyString
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.domain.SelectedNumber
import com.simenko.qmapp.domain.entities.products.DomainProductKind
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ProductKindKeysViewModel @Inject constructor(
    private val appNavigator: AppNavigator,
    private val mainPageState: MainPageState,
    private val repository: ProductsRepository,
    val storage: Storage,
) : ViewModel() {
    private val _productKind = MutableStateFlow(DomainProductKind.DomainProductKindComplete())
    private val _productKindKeysVisibility = MutableStateFlow(Pair(SelectedNumber(NoRecord.num), NoRecord))
    private val _productKindKeys = _productKind.flatMapLatest { repository.productKindKeys(it.productKind.id) }

    private val _isAddItemDialogVisible = MutableStateFlow(false)
    private val _itemToAddId: MutableStateFlow<ID> = MutableStateFlow(NoRecord.num)
    private val _itemToAddSearchStr: MutableStateFlow<String> = MutableStateFlow(EmptyString.str)


    /**
     * Main page setup -------------------------------------------------------------------------------------------------------------------------------
     * */
    private var mainPageHandler: MainPageHandler? = null

    fun onEntered(route: Route.Main.ProductLines.ProductKinds.ProductKindKeys.ProductKindKeysList) {
        viewModelScope.launch(Dispatchers.IO) {
            if (mainPageHandler == null) {
                _productKind.value = repository.productKind(route.productKindId)
                _productKindKeysVisibility.value = Pair(SelectedNumber(route.productKindKeyId), NoRecord)
            }

            mainPageHandler = MainPageHandler.Builder(Page.PRODUCT_KIND_KEYS, mainPageState)
                .setOnNavMenuClickAction { appNavigator.navigateBack() }
                .setOnFabClickAction { onAddProductKindKeyClick() }
                .setOnPullRefreshAction { updateCompanyProductsData() }
                .build()
                .apply { setupMainPage(0, true) }
        }
    }

    /**
     * UI operations ---------------------------------------------------------------------------------------------------------------------------------
     * */
    fun setProductKindKeysVisibility(dId: SelectedNumber = NoRecord, aId: SelectedNumber = NoRecord) {
        _productKindKeysVisibility.value = _productKindKeysVisibility.value.setVisibility(dId, aId)
    }

    /**
     * UI state --------------------------------------------------------------------------------------------------------------------------------------
     * */
    val productKind = _productKind.asStateFlow()
    val productKindKeys = _productKindKeys.flatMapLatest { productKindKeys ->
        _productKindKeysVisibility.flatMapLatest { visibility ->
            val cpy = productKindKeys.map {
                it.copy(key = it.key.copy(detailsVisibility = it.key.productLineKey.id == visibility.first.num, isExpanded = it.key.productLineKey.id == visibility.second.num))
            }
            flow { emit(cpy) }
        }
    }

    val availableKeys = _productKind.flatMapLatest { productKind ->
        _productKindKeys.flatMapLatest { addedKeys ->
            _itemToAddSearchStr.flatMapLatest { searchStr ->
                _itemToAddId.flatMapLatest { id ->
                    repository.productLineKeys(productKind.productLine.manufacturingProject.id).map { list ->
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
    fun onDeleteProductKindKeyClick(it: ID) {
        TODO("Not yet implemented")
    }

    private fun updateCompanyProductsData() {
        TODO("Not yet implemented")
    }

    /**
     * Navigation ------------------------------------------------------------------------------------------------------------------------------------
     * */


    private fun onAddProductKindKeyClick() {
        _isAddItemDialogVisible.value = true
    }

    fun onItemSelect(id: ID) {
        _itemToAddId.value = id
    }

    fun onAddItemClick() {
        if (productKind.value.productKind.id != NoRecord.num && _itemToAddId.value != NoRecord.num) {
            //  make record!
        }
    }
}