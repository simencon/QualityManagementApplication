package com.simenko.qmapp.ui.main.products.kinds.set.designations

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.domain.SelectedNumber
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
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
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
    private val _componentKindId = MutableStateFlow(NoRecord.num)
    private val _componentKindKeysVisibility = MutableStateFlow(Pair(SelectedNumber(NoRecord.num), NoRecord))
    private val _componentKindKeys = _componentKindId.flatMapLatest { repository.componentKindKeys(it) }

    /**
     * Main page setup -------------------------------------------------------------------------------------------------------------------------------
     * */
    private var mainPageHandler: MainPageHandler? = null

    fun onEntered(route: Route.Main.ProductLines.ProductKinds.ProductSpecification.ComponentKindKeys.ComponentKindKeysList) {
        viewModelScope.launch {
            if (mainPageHandler == null) {
                _componentKindId.value = route.componentKindId
                _componentKindKeysVisibility.value = Pair(SelectedNumber(route.componentKindKeyId), NoRecord)
            }

            mainPageHandler = MainPageHandler.Builder(Page.COMPONENT_KIND_KEYS, mainPageState)
                .setOnNavMenuClickAction { appNavigator.navigateBack() }
                .setOnFabClickAction { onAddComponentKindKeyClick(route.componentKindId) }
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
    val componentKind get() = _componentKindId.flatMapLatest { flow { emit(repository.componentKind(it)) } }.flowOn(Dispatchers.IO)

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
}