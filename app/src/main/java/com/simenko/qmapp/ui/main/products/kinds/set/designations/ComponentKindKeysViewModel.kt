package com.simenko.qmapp.ui.main.products.kinds.set.designations

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simenko.qmapp.di.ComponentKindIdParameter
import com.simenko.qmapp.di.ComponentKindKeyIdParameter
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.domain.SelectedNumber
import com.simenko.qmapp.domain.entities.products.DomainComponentKind
import com.simenko.qmapp.repository.ProductsRepository
import com.simenko.qmapp.storage.Storage
import com.simenko.qmapp.ui.main.main.MainPageHandler
import com.simenko.qmapp.ui.main.main.MainPageState
import com.simenko.qmapp.ui.main.main.content.Page
import com.simenko.qmapp.ui.navigation.AppNavigator
import com.simenko.qmapp.utils.InvestigationsUtils.setVisibility
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ComponentKindKeysViewModel @Inject constructor(
    private val appNavigator: AppNavigator,
    private val mainPageState: MainPageState,
    private val repository: ProductsRepository,
    val storage: Storage,
    @ComponentKindIdParameter private val componentKindId: ID,
    @ComponentKindKeyIdParameter private val componentKindKeyId: ID
) : ViewModel() {
    private val _componentKindKeysVisibility = MutableStateFlow(Pair(SelectedNumber(componentKindKeyId), NoRecord))
    private val _componentKind = MutableStateFlow(DomainComponentKind.DomainComponentKindComplete())
    private val _componentKindKeys = repository.componentKindKeys(componentKindId)

    /**
     * Main page setup -------------------------------------------------------------------------------------------------------------------------------
     * */
    val mainPageHandler: MainPageHandler

    init {
        mainPageHandler = MainPageHandler.Builder(Page.COMPONENT_KIND_KEYS, mainPageState)
            .setOnNavMenuClickAction { appNavigator.navigateBack() }
            .setOnFabClickAction { onAddComponentKindKeyClick(componentKindId) }
            .setOnPullRefreshAction { updateCompanyProductsData() }
            .build()
        viewModelScope.launch(Dispatchers.IO) { _componentKind.value = repository.componentKind(componentKindId) }
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
    val componentKind get() = _componentKind.asStateFlow()
    val componentKindKeys = _componentKindKeys.flatMapLatest { productKindKeys ->
        _componentKindKeysVisibility.flatMapLatest { visibility ->
            val cpy = productKindKeys.map {
                it.copy(key = it.key.copy(detailsVisibility = it.key.productLineKey.id == visibility.first.num, isExpanded = it.key.productLineKey.id == visibility.second.num))
            }
            flow { emit(cpy) }
        }
    }

    /**
     * REST operations -------------------------------------------------------------------------------------------------------------------------------
     * */
    fun onDeleteComponentKindKeyClick(it: ID) {
        TODO("Not yet implemented")
    }

    private fun updateCompanyProductsData() {
        TODO("Not yet implemented")
    }

    /**
     * Navigation ------------------------------------------------------------------------------------------------------------------------------------
     * */
    private fun onAddComponentKindKeyClick(it: ID) {
        TODO("Not yet implemented")
    }

    fun onEditComponentKindKeyClick(it: Pair<ID, ID>) {
        TODO("Not yet implemented")
    }
}