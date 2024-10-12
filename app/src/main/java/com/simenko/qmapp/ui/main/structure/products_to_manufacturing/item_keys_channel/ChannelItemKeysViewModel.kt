package com.simenko.qmapp.ui.main.structure.products_to_manufacturing.item_keys_channel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simenko.qmapp.domain.ComponentPref
import com.simenko.qmapp.domain.ComponentStagePref
import com.simenko.qmapp.domain.DomainBaseModel
import com.simenko.qmapp.domain.EmptyString
import com.simenko.qmapp.domain.FirstTabId
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.domain.ProductPref
import com.simenko.qmapp.domain.SecondTabId
import com.simenko.qmapp.domain.SelectedNumber
import com.simenko.qmapp.domain.ThirdTabId
import com.simenko.qmapp.domain.entities.DomainManufacturingChannel
import com.simenko.qmapp.domain.entities.products.DomainComponentKeyToChannel
import com.simenko.qmapp.domain.entities.products.DomainProductKeyToChannel
import com.simenko.qmapp.domain.entities.products.DomainStageKeyToChannel
import com.simenko.qmapp.other.Status
import com.simenko.qmapp.repository.ManufacturingRepository
import com.simenko.qmapp.repository.ProductsRepository
import com.simenko.qmapp.ui.main.main.MainPageHandler
import com.simenko.qmapp.ui.main.main.MainPageState
import com.simenko.qmapp.ui.main.main.content.Page
import com.simenko.qmapp.ui.navigation.AppNavigator
import com.simenko.qmapp.ui.navigation.Route.Main.CompanyStructure.ChannelItemKeys
import com.simenko.qmapp.utils.InvestigationsUtils.setVisibility
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ChannelItemKeysViewModel @Inject constructor(
    private val appNavigator: AppNavigator,
    private val mainPageState: MainPageState,
    private val repository: ManufacturingRepository,
    private val productRepository: ProductsRepository,
) : ViewModel() {
    private val _itemKindPref = MutableStateFlow(ProductPref.char)
    private val _route = MutableStateFlow(ChannelItemKeys(subDepartmentId = NoRecord.num, channelId = NoRecord.num))
    private val _channel = MutableStateFlow(DomainManufacturingChannel.DomainManufacturingChannelComplete())


    private val _allProductKeys = _route.flatMapLatest { route ->
        productRepository.productKeysBySubDepartmentId(route.subDepartmentId)
    }
    private val _allComponentKinds = _route.flatMapLatest { route ->
        productRepository.componentKeysBySubDepartmentId(route.subDepartmentId)
    }
    private val _allStageKinds = _route.flatMapLatest { route ->
        productRepository.stageKeysBySubDepartmentId(route.subDepartmentId)
    }


    private val _channelProductKeys = _route.flatMapLatest { route ->
        repository.channelProductKeys(route.channelId).flatMapLatest { items ->
            flow { emit(items) }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), listOf())
    private val _channelComponentKeys = _route.flatMapLatest { route ->
        repository.channelComponentKeys(route.subDepartmentId).flatMapLatest { items ->
            flow { emit(items) }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), listOf())
    private val _channelStageKeys = _route.flatMapLatest { route ->
        repository.channelStageKeys(route.subDepartmentId).flatMapLatest { items ->
            flow { emit(items) }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), listOf())


    private val _itemKindVisibility = MutableStateFlow(Pair(SelectedNumber(NoRecord.num), NoRecord))

    private val _isAddItemDialogVisible = MutableStateFlow(false)
    private val _itemToAddId: MutableStateFlow<ID> = MutableStateFlow(NoRecord.num)
    private val _itemToAddSearchStr: MutableStateFlow<String> = MutableStateFlow(EmptyString.str)

    /**
     * Main page setup -------------------------------------------------------------------------------------------------------------------------------
     * */
    private var mainPageHandler: MainPageHandler? = null

    fun onEntered(route: ChannelItemKeys) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _route.value = route
                _channel.value = repository.channelById(route.channelId)
                mainPageHandler = MainPageHandler.Builder(Page.CHANNEL_ITEM_KEYS, mainPageState)
                    .setOnNavMenuClickAction { appNavigator.navigateBack() }
                    .setOnFabClickAction { setAddItemDialogVisibility(true) }
                    .setOnTabSelectAction {
                        when (it) {
                            FirstTabId -> {
                                _itemKindPref.value = ProductPref.char
                            }

                            SecondTabId -> {
                                _itemKindPref.value = ComponentPref.char
                            }

                            ThirdTabId -> {
                                _itemKindPref.value = ComponentStagePref.char
                            }
                        }
                        _itemKindVisibility.value = Pair(SelectedNumber(NoRecord.num), NoRecord)
                    }
                    .build()
                    .apply { setupMainPage(0, true) }
            }
        }
    }

    /**
     * UI operations ---------------------------------------------------------------------------------------------------------------------------------
     * */
    fun setItemKeysVisibility(dId: SelectedNumber = NoRecord, aId: SelectedNumber = NoRecord) {
        _itemKindVisibility.value = _itemKindVisibility.value.setVisibility(dId, aId)
    }

    /**
     * UI State --------------------------------------------------------------------------------------------------------------------------------------
     * */
    val itemPref get() = _itemKindPref.asStateFlow()
    val channel get() = _channel.asStateFlow()

    private val _productKeys = _channelProductKeys.flatMapLatest { existingRecords ->
        _itemKindVisibility.flatMapLatest { visibility ->
            _allProductKeys.flatMapLatest { allRecords ->
                flow {
                    emit(allRecords
                        .filter { item -> existingRecords.map { it.keyId }.contains(item.id) }
                        .map {
                            it.copy(
                                detailsVisibility = it.id == visibility.first.num,
                                isExpanded = it.id == visibility.second.num
                            )
                        }
                    )
                }
            }
        }
    }

    private val _availableProductKeys = _channelProductKeys.flatMapLatest { existingRecords ->
        _itemToAddId.flatMapLatest { selectedId ->
            _allProductKeys.flatMapLatest { allRecords ->
                flow {
                    emit(
                        allRecords
                            .filter { item -> !existingRecords.map { it.keyId }.contains(item.id) }
                            .map { it.copy(isSelected = it.id == selectedId) }
                    )
                }
            }
        }
    }

    private val _componentKeys = _channelComponentKeys.flatMapLatest { existingRecords ->
        _itemKindVisibility.flatMapLatest { visibility ->
            _allComponentKinds.flatMapLatest { allRecords ->
                flow {
                    emit(allRecords
                        .filter { item -> existingRecords.map { it.keyId }.contains(item.id) }
                        .map {
                            it.copy(
                                detailsVisibility = it.id == visibility.first.num,
                                isExpanded = it.id == visibility.second.num
                            )
                        }
                    )
                }
            }
        }
    }

    private val _availableComponentKinds = _channelComponentKeys.flatMapLatest { existingRecords ->
        _itemToAddId.flatMapLatest { selectedId ->
            _allComponentKinds.flatMapLatest { allRecords ->
                flow {
                    emit(
                        allRecords
                            .filter { item -> !existingRecords.map { it.keyId }.contains(item.id) }
                            .map { it.copy(isSelected = it.id == selectedId) }
                    )
                }
            }
        }
    }

    private val _stageKeys = _channelStageKeys.flatMapLatest { existingRecords ->
        _itemKindVisibility.flatMapLatest { visibility ->
            _allStageKinds.flatMapLatest { allRecords ->
                flow {
                    emit(allRecords
                        .filter { item -> existingRecords.map { it.keyId }.contains(item.id) }
                        .map {
                            it.copy(
                                detailsVisibility = it.id == visibility.first.num,
                                isExpanded = it.id == visibility.second.num
                            )
                        }
                    )
                }
            }
        }
    }

    private val _availableStageKinds = _channelStageKeys.flatMapLatest { existingRecords ->
        _itemToAddId.flatMapLatest { selectedId ->
            _allStageKinds.flatMapLatest { allRecords ->
                flow {
                    emit(
                        allRecords
                            .filter { item -> !existingRecords.map { it.keyId }.contains(item.id) }
                            .map { it.copy(isSelected = it.id == selectedId) }
                    )
                }
            }
        }
    }

    val itemKeys = _itemKindPref.flatMapLatest {
        when (it) {
            ProductPref.char -> _productKeys
            ComponentPref.char -> _componentKeys
            ComponentStagePref.char -> _stageKeys
            else -> flow { emit(emptyList()) }
        }
    }

    val availableItemKeys: Flow<List<DomainBaseModel<Any>>> = _itemKindPref.flatMapLatest {
        when (it) {
            ProductPref.char -> _availableProductKeys
            ComponentPref.char -> _availableComponentKinds
            ComponentStagePref.char -> _availableStageKinds
            else -> flow { emit(emptyList()) }
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

    fun onItemSelect(id: ID) {
        _itemToAddId.value = id
    }

    /**
     * Navigation ------------------------------------------------------------------------------------------------------------------------------------
     * */

    fun onAddProductKind() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.run {
                when (_itemKindPref.value) {
                    ProductPref.char -> insertChannelProductKey(DomainProductKeyToChannel(chId = _route.value.subDepartmentId, keyId = _itemToAddId.value))
                    ComponentPref.char -> insertChannelComponentKey(DomainComponentKeyToChannel(chId = _route.value.subDepartmentId, keyId = _itemToAddId.value))
                    ComponentStagePref.char -> insertChannelStageKey(DomainStageKeyToChannel(chId = _route.value.subDepartmentId, keyId = _itemToAddId.value))
                    else -> return@run
                }.consumeEach { event ->
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

    fun onDeleteProductKind(id: ID) {
        when (_itemKindPref.value) {
            ProductPref.char -> _channelProductKeys.value.find { it.keyId == id }?.id
            ComponentPref.char -> _channelComponentKeys.value.find { it.keyId == id }?.id
            ComponentStagePref.char -> _channelStageKeys.value.find { it.keyId == id }?.id
            else -> null
        }?.let { itemId ->
            viewModelScope.launch(Dispatchers.IO) {
                repository.run {
                    when (_itemKindPref.value) {
                        ProductPref.char -> deleteChannelProductKey(itemId)
                        ComponentPref.char -> deleteChannelComponentKey(itemId)
                        ComponentStagePref.char -> deleteChannelStageKey(itemId)
                        else -> return@run
                    }.consumeEach { event ->
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