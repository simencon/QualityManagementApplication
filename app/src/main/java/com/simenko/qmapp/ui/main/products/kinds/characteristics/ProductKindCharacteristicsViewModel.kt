package com.simenko.qmapp.ui.main.products.kinds.characteristics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.domain.SelectedNumber
import com.simenko.qmapp.domain.entities.products.DomainCharGroup
import com.simenko.qmapp.domain.entities.products.DomainCharSubGroup
import com.simenko.qmapp.domain.entities.products.DomainCharacteristic
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
class ProductKindCharacteristicsViewModel @Inject constructor(
    private val appNavigator: AppNavigator,
    private val mainPageState: MainPageState,
    private val repository: ProductsRepository,
    val storage: Storage,
) : ViewModel() {
    private val _productKind = MutableStateFlow(DomainProductKind.DomainProductKindComplete())
    private val _charGroupVisibility = MutableStateFlow(Pair(NoRecord, NoRecord))
    private val _charSubGroupVisibility = MutableStateFlow(Pair(NoRecord, NoRecord))
    private val _characteristicVisibility = MutableStateFlow(Pair(SelectedNumber(NoRecord.num), NoRecord))
    private val _itemKindCharsComplete = _productKind.flatMapLatest { repository.itemKindCharsComplete("p$it") }

    private val _isAddItemDialogVisible = MutableStateFlow(false)
    private val _characteristicToAdd = MutableStateFlow(Triple(NoRecord.num, NoRecord.num, NoRecord.num))
    private val _allCharacteristics = _productKind.flatMapLatest { productKind ->
        _itemKindCharsComplete.flatMapLatest { itemCharacteristics ->
            repository.productLineCharacteristics(productKind.productLine.manufacturingProject.id).map { list ->
                list.subtract(itemCharacteristics.map { it.characteristicWithParents }.toSet()).toList()
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    /**
     * Main page setup -------------------------------------------------------------------------------------------------------------------------------
     * */
    var mainPageHandler: MainPageHandler? = null

    fun onEntered(route: Route.Main.ProductLines.ProductKinds.ProductKindCharacteristics.ProductKindCharacteristicsList) {
        viewModelScope.launch(Dispatchers.IO) {
            if (mainPageHandler == null) {
                _productKind.value = repository.productKind(route.productKindId)
                _characteristicVisibility.value = Pair(SelectedNumber(route.characteristicId), NoRecord)
            }

            mainPageHandler = MainPageHandler.Builder(Page.PRODUCT_KIND_CHARACTERISTICS, mainPageState)
                .setOnNavMenuClickAction { appNavigator.navigateBack() }
                .setOnFabClickAction { onAddCharacteristicClick() }
                .setOnPullRefreshAction { updateCharacteristicsData() }
                .build()
                .apply { setupMainPage(0, true) }
        }
    }

    /**
     * UI operations ---------------------------------------------------------------------------------------------------------------------------------
     * */
    val productKind = _productKind.asStateFlow()

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
    val charGroups = _itemKindCharsComplete.flatMapLatest { list ->
        _charGroupVisibility.flatMapLatest { visibility ->
            flow {
                emit(list.distinctBy { it.characteristicWithParents.groupId }.map {
                    it.characteristicWithParents.run {
                        DomainCharGroup(
                            id = groupId,
                            ishElement = groupDescription,
                            detailsVisibility = visibility.first.num == groupId,
                            isExpanded = visibility.second.num == groupId
                        )
                    }
                })
            }
        }
    }
    val charSubGroups = _itemKindCharsComplete.flatMapLatest { list ->
        _charGroupVisibility.flatMapLatest { gVisibility ->
            _charSubGroupVisibility.flatMapLatest { sgVisibility ->
                flow {
                    emit(list.filter { it.characteristicWithParents.groupId == gVisibility.first.num }.distinctBy { it.characteristicWithParents.subGroupId }.map {
                        it.characteristicWithParents.run {
                            DomainCharSubGroup(
                                id = subGroupId,
                                charGroupId = groupId,
                                ishElement = subGroupDescription,
                                measurementGroupRelatedTime = subGroupRelatedTime,
                                detailsVisibility = sgVisibility.first.num == subGroupId,
                                isExpanded = sgVisibility.second.num == subGroupId
                            )
                        }
                    })
                }
            }
        }
    }

    val characteristics = _itemKindCharsComplete.flatMapLatest { list ->
        _charSubGroupVisibility.flatMapLatest { sgVisibility ->
            _characteristicVisibility.flatMapLatest { cVisibility ->
                flow {
                    emit(list.filter { it.characteristicWithParents.subGroupId == sgVisibility.first.num }.distinctBy { it.characteristicWithParents.charId }.map {
                        it.characteristicWithParents.run {
                            DomainCharacteristic(
                                id = charId,
                                ishSubCharId = subGroupId,
                                charOrder = charOrder,
                                charDesignation = charDesignation,
                                charDescription = charDescription,
                                sampleRelatedTime = sampleRelatedTime,
                                measurementRelatedTime = measurementRelatedTime,
                                detailsVisibility = cVisibility.first.num == charId,
                                isExpanded = cVisibility.second.num == charId
                            )
                        }
                    })
                }
            }
        }
    }

    val isAddItemDialogVisible = _isAddItemDialogVisible.asStateFlow()
    fun setAddItemDialogVisibility(value: Boolean) {
        if (!value) {
            _characteristicToAdd.value = Triple(NoRecord.num, NoRecord.num, NoRecord.num)
        }
        _isAddItemDialogVisible.value = value
    }

    val isReadyToAdd = _characteristicToAdd.flatMapLatest { charToAdd ->
        flow { emit(charToAdd.third != NoRecord.num) }
    }

    val charGroupsFilter = _allCharacteristics.flatMapLatest { list ->
        _characteristicToAdd.flatMapLatest { charToAdd ->
            flow { emit(list.distinctBy { it.groupId }.map { Triple(it.groupId, it.groupDescription, it.groupId == charToAdd.first) }) }
        }
    }

    val charSubGroupsFilter = _allCharacteristics.flatMapLatest { list ->
        _characteristicToAdd.flatMapLatest { charToAdd ->
            flow { emit(list.distinctBy { it.subGroupId }.filter { it.groupId == charToAdd.first }.map { Triple(it.subGroupId, it.subGroupDescription, it.subGroupId == charToAdd.second) }) }
        }
    }

    val charsFilter = _allCharacteristics.flatMapLatest { list ->
        _characteristicToAdd.flatMapLatest { charToAdd ->
            flow {
                emit(
                    list.distinctBy { it.charId }.filter { it.subGroupId == charToAdd.second }.map {
                        DomainCharacteristic(
                            id = it.charId,
                            ishSubCharId = it.subGroupId,
                            charOrder = it.charOrder,
                            charDesignation = it.charDesignation,
                            charDescription = it.charDescription,
                            sampleRelatedTime = it.sampleRelatedTime,
                            measurementRelatedTime = it.measurementRelatedTime,
                            isSelected = it.charId == charToAdd.third
                        )
                    }
                )
            }
        }
    }

    fun onSelectCharGroup(value: ID) {
        if (value != _characteristicToAdd.value.first) _characteristicToAdd.value = _characteristicToAdd.value.copy(first = value, second = NoRecord.num, third = NoRecord.num)
    }

    fun onSelectCharSubGroup(value: ID) {
        if (value != _characteristicToAdd.value.second) _characteristicToAdd.value = _characteristicToAdd.value.copy(second = value, third = NoRecord.num)
    }

    fun onSelectChar(value: ID) {
        if (value != _characteristicToAdd.value.third) _characteristicToAdd.value = _characteristicToAdd.value.copy(third = value)
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

    fun onDeleteCharacteristicClick(it: ID) {
        TODO("Not yet implemented")
    }

    private fun onAddCharacteristicClick() {
        _isAddItemDialogVisible.value = true
    }

    fun onAddItemClick() = viewModelScope.launch(Dispatchers.IO) {

    }

}