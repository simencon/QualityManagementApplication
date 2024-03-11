package com.simenko.qmapp.ui.main.products.kinds.characteristics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simenko.qmapp.di.CharacteristicIdParameter
import com.simenko.qmapp.di.ProductKindIdParameter
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
class ProductKindCharacteristicsViewModel @Inject constructor(
    private val appNavigator: AppNavigator,
    private val mainPageState: MainPageState,
    private val repository: ProductsRepository,
    val storage: Storage,
    @ProductKindIdParameter private val productKindId: ID,
    @CharacteristicIdParameter private val characteristicId: ID
): ViewModel() {
    private val _charGroupVisibility = MutableStateFlow(Pair(NoRecord, NoRecord))
    private val _charSubGroupVisibility = MutableStateFlow(Pair(NoRecord, NoRecord))
    private val _characteristicVisibility = MutableStateFlow(Pair(SelectedNumber(characteristicId), NoRecord))

    private val _charGroups = repository.productKindCharGroups(productKindId)
    private val _charSubGroups = _charGroupVisibility.flatMapLatest { repository.charSubGroups(it.first.num) }
    private val _characteristics = _charSubGroupVisibility.flatMapLatest { repository.characteristicsByParent(it.first.num) }

    private val _productKind: MutableStateFlow<DomainProductKind.DomainProductKindComplete> = MutableStateFlow(DomainProductKind.DomainProductKindComplete())
    val productKind get() = _productKind.asStateFlow()

    /**
     * Main page setup -------------------------------------------------------------------------------------------------------------------------------
     * */
    val mainPageHandler: MainPageHandler
    init {
        mainPageHandler = MainPageHandler.Builder(Page.PRODUCT_LINE_CHARACTERISTICS, mainPageState)
            .setOnNavMenuClickAction { appNavigator.navigateBack() }
            .setOnFabClickAction { onAddCharacteristicClick(Pair(productKindId, NoRecord.num)) }
            .setOnPullRefreshAction { updateCharacteristicsData() }
            .build()
        viewModelScope.launch(Dispatchers.IO) { _productKind.value = repository.productKind(productKindId) }
    }

    /**
     * UI operations ---------------------------------------------------------------------------------------------------------------------------------
     * */
    fun setGroupsVisibility(dId: SelectedNumber = NoRecord, aId: SelectedNumber = NoRecord) {
        _charGroupVisibility.value = _charGroupVisibility.value.setVisibility(dId, aId)
    }
    fun setCharSubGroupsVisibility(dId: SelectedNumber = NoRecord, aId: SelectedNumber = NoRecord) {
        _charSubGroupVisibility.value = _charSubGroupVisibility.value.setVisibility(dId, aId)
    }
    fun setCharacteristicsVisibility(dId: SelectedNumber = NoRecord, aId: SelectedNumber = NoRecord) {
        _characteristicVisibility.value = _characteristicVisibility.value.setVisibility(dId, aId)
    }

    /**
     * UI state --------------------------------------------------------------------------------------------------------------------------------------
     * */

    val charGroupVisibility get() = _charGroupVisibility.asStateFlow()
    val charGroups = _charGroups.flatMapLatest { charGroups ->
        _charGroupVisibility.flatMapLatest { visibility ->
            val cpy = charGroups.map { it.copy(detailsVisibility = it.charGroup.id == visibility.first.num, isExpanded = it.charGroup.id == visibility.second.num) }
            flow { emit(cpy) }
        }
    }
    val charSubGroupVisibility get() = _charSubGroupVisibility.asStateFlow()
    val charSubGroups = _charSubGroups.flatMapLatest { charSubGroups ->
        _charSubGroupVisibility.flatMapLatest { visibility ->
            val cpy = charSubGroups.map { it.copy(detailsVisibility = it.charSubGroup.id == visibility.first.num, isExpanded = it.charSubGroup.id == visibility.second.num) }
            flow { emit(cpy) }
        }
    }

    val characteristics = _characteristics.flatMapLatest { characteristics ->
        _characteristicVisibility.flatMapLatest { visibility ->
            val cpy = characteristics.map { it.copy(detailsVisibility = it.characteristic.id == visibility.first.num, isExpanded = it.characteristic.id == visibility.second.num) }
            flow { emit(cpy) }
        }
    }

    /**
     * REST operations -------------------------------------------------------------------------------------------------------------------------------
     * */

    private fun updateCharacteristicsData() {
        TODO("Not yet implemented")
    }

    /**
     * Navigation ------------------------------------------------------------------------------------------------------------------------------------
     * */
    private fun onAddCharacteristicClick(it: Pair<ID, ID>) {
        TODO("Not yet implemented")
    }
}