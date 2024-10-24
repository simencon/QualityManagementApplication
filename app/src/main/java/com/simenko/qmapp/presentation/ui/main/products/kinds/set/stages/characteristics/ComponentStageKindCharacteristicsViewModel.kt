package com.simenko.qmapp.presentation.ui.main.products.kinds.set.stages.characteristics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.domain.SelectedNumber
import com.simenko.qmapp.domain.entities.products.DomainCharGroup
import com.simenko.qmapp.domain.entities.products.DomainCharSubGroup
import com.simenko.qmapp.domain.entities.products.DomainCharacteristic
import com.simenko.qmapp.domain.entities.products.DomainCharacteristicComponentStageKind
import com.simenko.qmapp.domain.entities.products.DomainComponentStageKind
import com.simenko.qmapp.other.Status
import com.simenko.qmapp.data.repository.ProductsRepository
import com.simenko.qmapp.presentation.ui.main.main.MainPageHandler
import com.simenko.qmapp.presentation.ui.main.main.MainPageState
import com.simenko.qmapp.presentation.ui.main.main.content.Page
import com.simenko.qmapp.navigation.AppNavigator
import com.simenko.qmapp.navigation.Route
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
class ComponentStageKindCharacteristicsViewModel @Inject constructor(
    private val appNavigator: AppNavigator,
    private val mainPageState: MainPageState,
    private val repository: ProductsRepository,
) : ViewModel() {
    private val _componentStageKind = MutableStateFlow(DomainComponentStageKind.DomainComponentStageKindComplete())
    private val _charGroupVisibility = MutableStateFlow(Pair(NoRecord, NoRecord))
    private val _charSubGroupVisibility = MutableStateFlow(Pair(NoRecord, NoRecord))
    private val _characteristicVisibility = MutableStateFlow(Pair(SelectedNumber(NoRecord.num), NoRecord))
    private val _itemKindCharsComplete = _componentStageKind.flatMapLatest {
        repository.itemKindCharsComplete("s${it.componentStageKind.id}")
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    private val _isAddItemDialogVisible = MutableStateFlow(false)
    private val _characteristicToAdd = MutableStateFlow(Triple(NoRecord.num, NoRecord.num, NoRecord.num))
    private val _allCharacteristics = _componentStageKind.flatMapLatest { componentStageKind ->
        _itemKindCharsComplete.flatMapLatest { itemCharacteristics ->
            repository.productLineCharacteristics(componentStageKind.componentKind.productKind.productLine.manufacturingProject.id).map { list ->
                list.subtract(itemCharacteristics.map { it.characteristicWithParents }.toSet()).toList()
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    /**
     * Main page setup -------------------------------------------------------------------------------------------------------------------------------
     * */
    private var mainPageHandler: MainPageHandler? = null

    fun onEntered(route: Route.Main.ProductLines.ProductKinds.ProductSpecification.ComponentStageKindCharacteristics.ComponentStageKindCharacteristicsList) {
        viewModelScope.launch(Dispatchers.IO) {
            if (mainPageHandler == null) {
                _componentStageKind.value = repository.componentStageKind(route.componentStageKindId)
                _characteristicVisibility.value = Pair(SelectedNumber(route.characteristicId), NoRecord)
            }

            mainPageHandler = MainPageHandler.Builder(Page.COMPONENT_KIND_CHARACTERISTICS, mainPageState)
                .setOnNavMenuClickAction { appNavigator.navigateBack() }
                .setOnFabClickAction { setAddItemDialogVisibility(true) }
                .setOnPullRefreshAction { updateCharacteristicsData() }
                .build()
                .apply { setupMainPage(0, true) }
        }
    }

    /**
     * UI operations ---------------------------------------------------------------------------------------------------------------------------------
     * */
    val componentStageKind = _componentStageKind.asStateFlow()

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
            _characteristicToAdd.value = _characteristicToAdd.value.copy(first = visibility.first.num)
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
                _characteristicToAdd.value = _characteristicToAdd.value.copy(second = sgVisibility.first.num)
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
            _characteristicToAdd.value = Triple(_charGroupVisibility.value.first.num, _charSubGroupVisibility.value.first.num, NoRecord.num)
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

    private fun updateCharacteristicsData() = viewModelScope.launch(Dispatchers.IO) {
        try {
            mainPageHandler?.updateLoadingState?.invoke(Triple(true, false, null))

            repository.syncCharacteristicsComponentStageKinds()

            mainPageHandler?.updateLoadingState?.invoke(Triple(false, false, null))
        } catch (e: Exception) {
            mainPageHandler?.updateLoadingState?.invoke(Triple(false, false, e.message))
        }
    }

    fun onDeleteCharacteristicClick(id: ID) = viewModelScope.launch(Dispatchers.IO) {
        _itemKindCharsComplete.value.find { it.characteristicItemKind.charId == id }?.let { itemKindChar ->
            with(repository) {
                deleteComponentStageKindCharacteristic(itemKindChar.characteristicItemKind.id).consumeEach { event ->
                    event.getContentIfNotHandled()?.let { resource ->
                        when (resource.status) {
                            Status.LOADING -> mainPageHandler?.updateLoadingState?.invoke(Triple(true, false, null))
                            Status.SUCCESS -> mainPageHandler?.updateLoadingState?.invoke(Triple(false, false, null))
                            Status.ERROR -> mainPageHandler?.updateLoadingState?.invoke(Triple(false, false, resource.message))
                        }
                    }
                }
            }
        }
    }

    fun onAddItemClick() = viewModelScope.launch(Dispatchers.IO) {
        val componentStageKind = _componentStageKind.value.componentStageKind.id
        val charId = _characteristicToAdd.value.third
        if (componentStageKind != NoRecord.num && charId != NoRecord.num) {
            //  make record!
            with(repository) {
                _isAddItemDialogVisible.value = false
                _characteristicToAdd.value = _characteristicToAdd.value.copy(third = NoRecord.num)
                insertComponentStageKindCharacteristic(
                    DomainCharacteristicComponentStageKind(
                        id = NoRecord.num,
                        componentStageKindId = componentStageKind,
                        charId = charId
                    )
                ).consumeEach { event ->
                    event.getContentIfNotHandled()?.let { resource ->
                        when (resource.status) {
                            Status.LOADING -> mainPageHandler?.updateLoadingState?.invoke(Triple(true, false, null))
                            Status.SUCCESS -> {
                                mainPageHandler?.updateLoadingState?.invoke(Triple(false, false, null))
                                resource.data?.let {
                                    if (_characteristicToAdd.value.first != _charGroupVisibility.value.first.num) setGroupsVisibility(dId = SelectedNumber(_characteristicToAdd.value.first))
                                    if (_characteristicToAdd.value.second != _charSubGroupVisibility.value.first.num) setCharSubGroupsVisibility(dId = SelectedNumber(_characteristicToAdd.value.second))
                                    setCharacteristicsVisibility(dId = SelectedNumber(it.charId))
                                }
                            }

                            Status.ERROR -> mainPageHandler?.updateLoadingState?.invoke(Triple(true, false, resource.message))
                        }
                    }
                }
            }
        }
    }
}