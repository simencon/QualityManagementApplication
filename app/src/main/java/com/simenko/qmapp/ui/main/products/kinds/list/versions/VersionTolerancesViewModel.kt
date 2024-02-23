package com.simenko.qmapp.ui.main.products.kinds.list.versions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simenko.qmapp.di.CharGroupIdParameter
import com.simenko.qmapp.di.CharSubGroupIdParameter
import com.simenko.qmapp.di.CharacteristicIdParameter
import com.simenko.qmapp.di.ToleranceIdParameter
import com.simenko.qmapp.di.VersionFIdParameter
import com.simenko.qmapp.domain.FillInInitialState
import com.simenko.qmapp.domain.FillInState
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.domain.SelectedNumber
import com.simenko.qmapp.domain.ZeroValue
import com.simenko.qmapp.domain.entities.products.DomainCharGroup
import com.simenko.qmapp.domain.entities.products.DomainCharSubGroup
import com.simenko.qmapp.domain.entities.products.DomainCharacteristic
import com.simenko.qmapp.domain.entities.products.DomainItemVersionComplete
import com.simenko.qmapp.domain.entities.products.DomainMetrix
import com.simenko.qmapp.repository.ProductsRepository
import com.simenko.qmapp.storage.ScrollStates
import com.simenko.qmapp.storage.Storage
import com.simenko.qmapp.ui.main.main.MainPageHandler
import com.simenko.qmapp.ui.main.main.MainPageState
import com.simenko.qmapp.ui.main.main.content.Page
import com.simenko.qmapp.ui.navigation.AppNavigator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class VersionTolerancesViewModel @Inject constructor(
    private val appNavigator: AppNavigator,
    private val mainPageState: MainPageState,
    private val repository: ProductsRepository,
    val storage: Storage,
    @VersionFIdParameter val versionFId: String,
    @CharGroupIdParameter val characteristicGroupId: ID,
    @CharSubGroupIdParameter val characteristicSubGroupId: ID,
    @CharacteristicIdParameter val characteristicId: ID,
    @ToleranceIdParameter val toleranceId: ID
) : ViewModel() {
    private val _characteristicGroupVisibility = MutableStateFlow(Pair(SelectedNumber(characteristicGroupId), NoRecord))
    private val _characteristicSubGroupVisibility = MutableStateFlow(Pair(SelectedNumber(characteristicSubGroupId), NoRecord))
    private val _characteristicVisibility = MutableStateFlow(Pair(SelectedNumber(characteristicId), NoRecord))
    private val _toleranceVisibility = MutableStateFlow(Pair(SelectedNumber(toleranceId), NoRecord))

    private val _itemVersion = MutableStateFlow(DomainItemVersionComplete())
    private val _itemVersionTolerances = repository.versionTolerancesComplete(versionFId)

    private val _characteristicGroups = _itemVersionTolerances.flatMapLatest { list ->
        _characteristicGroupVisibility.flatMapLatest { visibility ->
            flow {
                emit(list.distinctBy { it.metricWithParents.groupId }.map {
                    it.metricWithParents.run {
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
    private val _characteristicSubGroups = _itemVersionTolerances.flatMapLatest { list ->
        _characteristicGroupVisibility.flatMapLatest { gVisibility ->
            _characteristicSubGroupVisibility.flatMapLatest { sgVisibility ->
                flow {
                    emit(list.filter { it.metricWithParents.groupId == gVisibility.first.num }.distinctBy { it.metricWithParents.subGroupId }.map {
                        it.metricWithParents.run {
                            DomainCharSubGroup(
                                id = subGroupId,
                                charGroupId = groupId,
                                ishElement = subGroupDescription,
                                detailsVisibility = sgVisibility.first.num == subGroupId,
                                isExpanded = sgVisibility.second.num == subGroupId
                            )
                        }
                    })
                }
            }
        }
    }
    private val _characteristics = _itemVersionTolerances.flatMapLatest { list ->
        _characteristicSubGroupVisibility.flatMapLatest { sgVisibility ->
            _characteristicVisibility.flatMapLatest { cVisibility ->
                flow {
                    emit(list.filter { it.metricWithParents.subGroupId == sgVisibility.first.num }.distinctBy { it.metricWithParents.charId }.map {
                        it.metricWithParents.run {
                            DomainCharacteristic(
                                id = charId,
                                ishSubCharId = subGroupId,
                                charOrder = charOrder,
                                charDesignation = charDesignation,
                                charDescription = charDescription,
                                detailsVisibility = cVisibility.first.num == charId,
                                isExpanded = cVisibility.second.num == charId
                            )
                        }
                    })
                }
            }
        }
    }
    private val _tolerances = _itemVersionTolerances.flatMapLatest { list ->
        _characteristicVisibility.flatMapLatest { cVisibility ->
            _toleranceVisibility.flatMapLatest { tVisibility ->
                flow {
                    emit(list.filter { it.metricWithParents.charId == cVisibility.first.num }.distinctBy { it.metricWithParents.metricId }.map {
                        Pair(
                            first = it.metricWithParents.run {
                                DomainMetrix(
                                    id = metricId,
                                    charId = charId,
                                    metrixOrder = metricOrder,
                                    metrixDesignation = metricDesignation,
                                    metrixDescription = metricDescription,
                                    detailsVisibility = tVisibility.first.num == metricId,
                                    isExpanded = tVisibility.second.num == metricId
                                )
                            },
                            second = it.itemTolerance
                        )
                    })
                }
            }
        }
    }

    /**
     * Main page setup -------------------------------------------------------------------------------------------------------------------------------
     * */
    val mainPageHandler: MainPageHandler

    init {
        mainPageHandler = MainPageHandler.Builder(Page.VERSION_TOLERANCES, mainPageState)
            .setOnNavMenuClickAction { appNavigator.navigateBack() }
            .setOnFabClickAction { onAddCharacteristicClick(Pair(versionFId, NoRecord.num)) }
            .setOnPullRefreshAction { updateTolerancesData() }
            .build()
        viewModelScope.launch(Dispatchers.IO) { _itemVersion.value = repository.itemVersionComplete(versionFId) }
    }

    /**
     * UI state -------------------------------------------------------------------------------------------------------------------------------------
     * */

    private val _itemVersionErrors: MutableStateFlow<ItemVersionErrors> = MutableStateFlow(ItemVersionErrors())
    val itemVersion get() = _itemVersion.asStateFlow()
    val itemVersionErrors get() = _itemVersionErrors.asStateFlow()

    fun setItemVersionDescription(it: String) {
        _itemVersion.value = _itemVersion.value.copy(itemVersion = _itemVersion.value.itemVersion.copy(versionDescription = it))
        _itemVersionErrors.value = _itemVersionErrors.value.copy(versionDescriptionError = false)
        _fillInState.value = FillInInitialState
    }

    private val _fillInState = MutableStateFlow<FillInState>(FillInInitialState)
    val fillInState get() = _fillInState.asStateFlow()

    private val _viewState = MutableStateFlow(false)
    val setViewState: (Boolean) -> Unit = {
        if (!it) _isComposed.value = BooleanArray(4) { false }
        _viewState.value = it
    }

    private val _isComposed = MutableStateFlow(BooleanArray(4) { false })
    val setIsComposed: (Int, Boolean) -> Unit = { i, value ->
        val cpy = _isComposed.value.copyOf()
        cpy[i] = value
        _isComposed.value = cpy
    }
    val isSecondColumnVisible: StateFlow<Boolean> = _isComposed.flatMapLatest { isComposed ->
        _characteristicVisibility.flatMapLatest { visibility ->
            flow { emit((visibility.first != NoRecord) && (isComposed.component3())) }
        }
    }.flowOn(Dispatchers.IO).conflate().stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    val listsIsInitialized: Flow<Pair<Boolean, Boolean>> = _viewState.flatMapLatest { viewState ->
        _characteristicGroups.flatMapLatest { firstList ->
            _secondListIsInitialized.flatMapLatest { secondListState ->
                if (viewState)
                    flow {
                        if (characteristicGroupId != NoRecord.num) {
                            storage.setLong(ScrollStates.VERSION_CHAR_GROUPS.indexKey, firstList.map { it.id }.indexOf(characteristicGroupId).toLong())
                            storage.setLong(ScrollStates.VERSION_CHAR_GROUPS.offsetKey, ZeroValue.num)
                            emit(Pair(true, secondListState))
                        } else {
                            emit(Pair(true, secondListState))
                        }
                    }
                else
                    flow {
                        emit(Pair(false, false))
                    }
            }
        }
    }

    private val _secondListIsInitialized: Flow<Boolean> = _tolerances.flatMapLatest { secondList ->
        flow {
            if (toleranceId != NoRecord.num) {
                storage.setLong(ScrollStates.VERSION_TOLERANCES.indexKey, secondList.map { it.first.id }.indexOf(toleranceId).toLong())
                storage.setLong(ScrollStates.VERSION_TOLERANCES.offsetKey, ZeroValue.num)
                emit(true)
            } else {
                emit(true)
            }
        }
    }

    /**
     * REST operations -------------------------------------------------------------------------------------------------------------------------------
     * */
    private fun updateTolerancesData() {
        TODO("Not yet implemented")
    }

    /**
     * Navigation ------------------------------------------------------------------------------------------------------------------------------------
     * */
    private fun onAddCharacteristicClick(pair: Pair<String, Long>) {
        TODO("Not yet implemented")
    }
}

data class ItemVersionErrors(
    val versionDescriptionError: Boolean = false,
    val versionDateError: Boolean = false,
    val versionStatusError: Boolean = false,
    val versionIsDefaultError: Boolean = false
)