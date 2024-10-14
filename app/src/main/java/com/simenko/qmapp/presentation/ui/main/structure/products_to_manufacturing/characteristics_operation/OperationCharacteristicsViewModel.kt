package com.simenko.qmapp.presentation.ui.main.structure.products_to_manufacturing.characteristics_operation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simenko.qmapp.domain.ComponentPref
import com.simenko.qmapp.domain.ComponentStagePref
import com.simenko.qmapp.domain.FirstTabId
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.domain.ProductPref
import com.simenko.qmapp.domain.SecondTabId
import com.simenko.qmapp.domain.SelectedNumber
import com.simenko.qmapp.domain.ThirdTabId
import com.simenko.qmapp.domain.entities.DomainManufacturingOperation
import com.simenko.qmapp.domain.entities.products.DomainCharGroup
import com.simenko.qmapp.domain.entities.products.DomainCharSubGroup
import com.simenko.qmapp.domain.entities.products.DomainCharacteristic
import com.simenko.qmapp.domain.entities.products.DomainCharacteristicToOperation
import com.simenko.qmapp.other.Status
import com.simenko.qmapp.data.repository.ManufacturingRepository
import com.simenko.qmapp.data.repository.ProductsRepository
import com.simenko.qmapp.data.cache.prefs.storage.Storage
import com.simenko.qmapp.presentation.ui.main.main.MainPageHandler
import com.simenko.qmapp.presentation.ui.main.main.MainPageState
import com.simenko.qmapp.presentation.ui.main.main.content.Page
import com.simenko.qmapp.navigation.AppNavigator
import com.simenko.qmapp.navigation.Route.Main.CompanyStructure.OperationCharacteristics
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
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class OperationCharacteristicsViewModel @Inject constructor(
    private val appNavigator: AppNavigator,
    private val mainPageState: MainPageState,
    private val repository: ManufacturingRepository,
    private val productRepository: ProductsRepository,
    val storage: Storage,
) : ViewModel() {
    private val _itemKindPref = MutableStateFlow(ProductPref.char)
    private val _route = MutableStateFlow(OperationCharacteristics(NoRecord.num, NoRecord.num))
    private val _manufacturingOperation = MutableStateFlow(DomainManufacturingOperation.DomainManufacturingOperationComplete())

    private val _allProductChars = _route.flatMapLatest { route -> productRepository.productCharsByLineId(route.lineId) }
    private val _allComponentChars = _route.flatMapLatest { route -> productRepository.componentCharsByLineId(route.lineId) }
    private val _allStageChars = _route.flatMapLatest { route -> productRepository.stageCharsByLineId(route.lineId) }

    //    ToDoMe - pay attention - this data not specify which item belongs chars (product/component/stage)
    private val _operationCharacteristics = _route.flatMapLatest { route ->
        repository.itemCharsByOperationId(route.operationId)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), listOf())

    private val _charGroupVisibility = MutableStateFlow(Pair(NoRecord, NoRecord))
    private val _charSubGroupVisibility = MutableStateFlow(Pair(NoRecord, NoRecord))
    private val _characteristicVisibility = MutableStateFlow(Pair(NoRecord, NoRecord))

    private val _availableOperationItemCharacteristics = _itemKindPref.flatMapLatest { pref ->
        _operationCharacteristics.flatMapLatest { existingRecords ->
            when (pref) {
                ProductPref.char -> _allProductChars
                ComponentPref.char -> _allComponentChars
                ComponentStagePref.char -> _allStageChars
                else -> flow { emit(emptyList()) }
            }.flatMapLatest { allRecords ->
                val records = allRecords.filter { item -> !existingRecords.map { it.charId }.contains(item.charId) }
                flow { emit(records) }
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    private val _operationItemCharacteristics = _itemKindPref.flatMapLatest { pref ->
        _operationCharacteristics.flatMapLatest { existingRecords ->
            when (pref) {
                ProductPref.char -> _allProductChars
                ComponentPref.char -> _allComponentChars
                ComponentStagePref.char -> _allStageChars
                else -> flow { emit(emptyList()) }
            }.flatMapLatest { allRecords ->
                val records = allRecords.filter { item -> existingRecords.map { it.charId }.contains(item.charId) }
                flow { emit(records) }
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    private val _isAddItemDialogVisible = MutableStateFlow(false)
    private val _characteristicToAdd = MutableStateFlow(Triple(NoRecord.num, NoRecord.num, NoRecord.num))

    /**
     * Main page setup -------------------------------------------------------------------------------------------------------------------------------
     * */
    var mainPageHandler: MainPageHandler? = null

    fun onEntered(route: OperationCharacteristics) {
        viewModelScope.launch(Dispatchers.IO) {
            _route.value = route
            _manufacturingOperation.value = repository.operationById(route.operationId)


            mainPageHandler = MainPageHandler.Builder(Page.OPERATION_CHARACTERISTICS, mainPageState)
                .setOnNavMenuClickAction { appNavigator.navigateBack() }
                .setOnFabClickAction { setAddItemDialogVisibility(true) }
                .setOnTabSelectAction {
                    _itemKindPref.value = when (it) {
                        FirstTabId -> ProductPref.char
                        SecondTabId -> ComponentPref.char
                        ThirdTabId -> ComponentStagePref.char
                        else -> ProductPref.char
                    }
                    _charGroupVisibility.value = Pair(NoRecord, NoRecord)
                    _charSubGroupVisibility.value = Pair(NoRecord, NoRecord)
                    _characteristicVisibility.value = Pair(NoRecord, NoRecord)
                }
                .build()
                .apply { setupMainPage(0, true) }
        }
    }

    /**
     * UI operations ---------------------------------------------------------------------------------------------------------------------------------
     * */
    val manufacturingOperation = _manufacturingOperation.asStateFlow()

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
    val charGroups = _operationItemCharacteristics.flatMapLatest { list ->
        _charGroupVisibility.flatMapLatest { visibility ->
            _characteristicToAdd.value = _characteristicToAdd.value.copy(first = visibility.first.num)
            flow {
                emit(list.distinctBy { it.groupId }.map {
                    it.run {
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
    val charSubGroups = _operationItemCharacteristics.flatMapLatest { list ->
        _charGroupVisibility.flatMapLatest { gVisibility ->
            _charSubGroupVisibility.flatMapLatest { sgVisibility ->
                _characteristicToAdd.value = _characteristicToAdd.value.copy(second = sgVisibility.first.num)
                flow {
                    emit(list.filter { it.groupId == gVisibility.first.num }.distinctBy { it.subGroupId }.map {
                        it.run {
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

    val characteristics = _operationItemCharacteristics.flatMapLatest { list ->
        _charSubGroupVisibility.flatMapLatest { sgVisibility ->
            _characteristicVisibility.flatMapLatest { cVisibility ->
                flow {
                    emit(list.filter { it.subGroupId == sgVisibility.first.num }.distinctBy { it.charId }.map {
                        it.run {
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
            _characteristicToAdd.value = Triple(_charGroupVisibility.value.first.num, _charSubGroupVisibility.value.first.num, NoRecord.num)
        }
        _isAddItemDialogVisible.value = value
    }

    val isReadyToAdd = _characteristicToAdd.flatMapLatest { charToAdd ->
        flow { emit(charToAdd.third != NoRecord.num) }
    }

    val charGroupsFilter = _availableOperationItemCharacteristics.flatMapLatest { list ->
        _characteristicToAdd.flatMapLatest { charToAdd ->
            flow { emit(list.distinctBy { it.groupId }.map { Triple(it.groupId, it.groupDescription, it.groupId == charToAdd.first) }) }
        }
    }

    val charSubGroupsFilter = _availableOperationItemCharacteristics.flatMapLatest { list ->
        _characteristicToAdd.flatMapLatest { charToAdd ->
            flow { emit(list.distinctBy { it.subGroupId }.filter { it.groupId == charToAdd.first }.map { Triple(it.subGroupId, it.subGroupDescription, it.subGroupId == charToAdd.second) }) }
        }
    }

    val charsFilter = _availableOperationItemCharacteristics.flatMapLatest { list ->
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

    fun onAddItemClick() = viewModelScope.launch(Dispatchers.IO) {
        repository.run {
            insertOperationCharacteristic(DomainCharacteristicToOperation(operationId = _route.value.operationId, charId = _characteristicToAdd.value.third)).consumeEach { event ->
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

    fun onDeleteCharacteristicClick(id: ID) {
        _operationCharacteristics.value.find { it.charId == id }?.id?.let { itemId ->
            viewModelScope.launch(Dispatchers.IO) {
                repository.run {
                    deleteOperationCharacteristic(itemId).consumeEach { event ->
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