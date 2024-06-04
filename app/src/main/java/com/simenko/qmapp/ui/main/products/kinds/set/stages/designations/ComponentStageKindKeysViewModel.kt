package com.simenko.qmapp.ui.main.products.kinds.set.stages.designations

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
class ComponentStageKindKeysViewModel @Inject constructor(
    private val appNavigator: AppNavigator,
    private val mainPageState: MainPageState,
    private val repository: ProductsRepository,
    val storage: Storage,
) : ViewModel() {
    private val _componentStageKindId = MutableStateFlow(NoRecord.num)
    private val _componentStageKindKeysVisibility = MutableStateFlow(Pair(SelectedNumber(NoRecord.num), NoRecord))
    private val _componentStageKindKeys = _componentStageKindId.flatMapLatest { repository.componentStageKindKeys(it) }

    /**
     * Main page setup -------------------------------------------------------------------------------------------------------------------------------
     * */
    private var mainPageHandler: MainPageHandler? = null

    fun onEntered(route: Route.Main.ProductLines.ProductKinds.ProductSpecification.ComponentStageKindKeys.ComponentStageKindKeysList) {
        viewModelScope.launch {
            _componentStageKindId.value = route.componentStageKindId
            _componentStageKindKeysVisibility.value = Pair(SelectedNumber(route.componentStageKindKeyId), NoRecord)

            mainPageHandler = MainPageHandler.Builder(Page.COMPONENT_STAGE_KIND_KEYS, mainPageState)
                .setOnNavMenuClickAction { appNavigator.navigateBack() }
                .setOnFabClickAction { onAddComponentStageKindKeyClick(route.componentStageKindId) }
                .setOnPullRefreshAction { updateCompanyProductsData() }
                .build()
                .apply { setupMainPage(0, true) }
        }
    }

    /**
     * UI operations ---------------------------------------------------------------------------------------------------------------------------------
     * */
    fun setComponentStageKindKeysVisibility(dId: SelectedNumber = NoRecord, aId: SelectedNumber = NoRecord) {
        _componentStageKindKeysVisibility.value = _componentStageKindKeysVisibility.value.setVisibility(dId, aId)
    }

    /**
     * UI state --------------------------------------------------------------------------------------------------------------------------------------
     * */
    val componentStageKind get() = _componentStageKindId.flatMapLatest { flow { emit(repository.componentStageKind(it)) } }.flowOn(Dispatchers.IO)

    val componentStageKindKeys = _componentStageKindKeys.flatMapLatest { productKindKeys ->
        _componentStageKindKeysVisibility.flatMapLatest { visibility ->
            val cpy = productKindKeys.map {
                it.copy(key = it.key.copy(detailsVisibility = it.key.productLineKey.id == visibility.first.num, isExpanded = it.key.productLineKey.id == visibility.second.num))
            }
            flow { emit(cpy) }
        }
    }

    /**
     * REST operations -------------------------------------------------------------------------------------------------------------------------------
     * */
    fun onDeleteComponentStageKindKeyClick(it: ID) {
        TODO("Not yet implemented")
    }

    private fun updateCompanyProductsData() {
        TODO("Not yet implemented")
    }

    /**
     * Navigation ------------------------------------------------------------------------------------------------------------------------------------
     * */
    private fun onAddComponentStageKindKeyClick(it: ID) {
        TODO("Not yet implemented")
    }

    fun onEditComponentStageKindKeyClick(it: Pair<ID, ID>) {
        TODO("Not yet implemented")
    }
}