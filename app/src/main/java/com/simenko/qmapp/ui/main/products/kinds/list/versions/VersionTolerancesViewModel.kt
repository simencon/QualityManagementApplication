package com.simenko.qmapp.ui.main.products.kinds.list.versions

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Save
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simenko.qmapp.domain.ComponentPref
import com.simenko.qmapp.domain.ComponentStagePref
import com.simenko.qmapp.domain.FillInInitialState
import com.simenko.qmapp.domain.FillInState
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.domain.NoRecordStr
import com.simenko.qmapp.domain.NoString
import com.simenko.qmapp.domain.ProductPref
import com.simenko.qmapp.domain.SelectedNumber
import com.simenko.qmapp.domain.ZeroValue
import com.simenko.qmapp.domain.entities.products.DomainCharGroup
import com.simenko.qmapp.domain.entities.products.DomainCharSubGroup
import com.simenko.qmapp.domain.entities.products.DomainCharacteristic
import com.simenko.qmapp.domain.entities.products.DomainItemTolerance
import com.simenko.qmapp.domain.entities.products.DomainItemVersionComplete
import com.simenko.qmapp.domain.entities.products.DomainMetrix
import com.simenko.qmapp.repository.ProductsRepository
import com.simenko.qmapp.storage.ScrollStates
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
) : ViewModel() {
    private val _versionFId = MutableStateFlow(NoRecordStr.str)
    private val _charGroupVisibility = MutableStateFlow(Pair(SelectedNumber(NoRecord.num), NoRecord))
    private val _charSubGroupVisibility = MutableStateFlow(Pair(SelectedNumber(NoRecord.num), NoRecord))
    private val _characteristicVisibility = MutableStateFlow(Pair(SelectedNumber(NoRecord.num), NoRecord))
    private val _toleranceVisibility = MutableStateFlow(Pair(SelectedNumber(NoRecord.num), NoRecord))

    private val _itemVersion = MutableStateFlow(DomainItemVersionComplete())
    private val _versionEditMode = MutableStateFlow(false)
    private val _itemVersionTolerances = MutableStateFlow(listOf<DomainItemTolerance.DomainItemToleranceComplete>())

    private val _isAddItemDialogVisible = MutableStateFlow(false)
    private val _characteristicToAdd = MutableStateFlow(Triple(NoRecord.num, NoRecord.num, NoRecord.num))

    /**
     * Main page setup -------------------------------------------------------------------------------------------------------------------------------
     * */
    private var mainPageHandler: MainPageHandler? = null
    private val page = mutableStateOf(Page.VERSION_TOLERANCES)

    fun onEntered(route: Route.Main.ProductLines.ProductKinds.Products.VersionTolerances.VersionTolerancesDetails) {
        viewModelScope.launch {

            _versionFId.value = route.versionFId
            _charGroupVisibility.value = Pair(SelectedNumber(route.charGroupId), NoRecord)
            _charSubGroupVisibility.value = Pair(SelectedNumber(route.charSubGroupId), NoRecord)
            _characteristicVisibility.value = Pair(SelectedNumber(route.characteristicId), NoRecord)
            _toleranceVisibility.value = Pair(SelectedNumber(route.toleranceId), NoRecord)
            _itemVersion.value = repository.itemVersionComplete(route.versionFId)
            _versionEditMode.value = route.versionEditMode

            when(_itemVersion.value.itemVersion.fId.firstOrNull()) {
                ProductPref.char -> {
                    repository.itemVersionComplete
                    repository.productById(_itemVersion.value.itemVersion.id).let {

                    }
                }
                ComponentPref.char -> {}
                ComponentStagePref.char -> {}
            }

            mainPageHandler = MainPageHandler.Builder(page.value.withCustomFabIcon(if (_versionEditMode.value) Icons.Filled.Save else Icons.Filled.Edit), mainPageState)
                .setOnNavMenuClickAction { appNavigator.navigateBack() }
                .setOnFabClickAction { onFabClick() }
                .setOnPullRefreshAction { updateTolerancesData() }
                .build()
                .apply { setupMainPage(0, true) }

            repository.versionTolerancesComplete(route.versionFId).collect {
                _itemVersionTolerances.value = it
            }
        }
    }

    /**
     * UI state -------------------------------------------------------------------------------------------------------------------------------------
     * */

    // Header state ---------------
    private val _itemVersionErrors: MutableStateFlow<ItemVersionErrors> = MutableStateFlow(ItemVersionErrors())
    val itemVersion get() = _itemVersion.asStateFlow()
    val versionEditMode get() = _versionEditMode.asStateFlow()
    val itemVersionErrors get() = _itemVersionErrors.asStateFlow()

    fun setItemVersionDescription(it: String) {
        _itemVersion.value = _itemVersion.value.copy(itemVersion = _itemVersion.value.itemVersion.copy(versionDescription = it))
        _itemVersionErrors.value = _itemVersionErrors.value.copy(versionDescriptionError = false)
        _fillInState.value = FillInInitialState
    }

    fun setItemVersionDate(it: Long) {
        _itemVersion.value = _itemVersion.value.copy(itemVersion = _itemVersion.value.itemVersion.copy(versionDate = it))
        _itemVersionErrors.value = _itemVersionErrors.value.copy(versionDateError = false)
        _fillInState.value = FillInInitialState
    }

    val versionStatuses = repository.versionStatuses.flatMapLatest { statuses ->
        _itemVersion.flatMapLatest { itemVersion ->
            flow { emit(statuses.map { Triple(it.id, it.statusDescription ?: NoString.str, it.id == itemVersion.itemVersion.statusId) }) }
        }
    }

    fun setVersionStatus(it: ID) {
        _itemVersion.value = _itemVersion.value.copy(itemVersion = _itemVersion.value.itemVersion.copy(statusId = it))
        _itemVersionErrors.value = _itemVersionErrors.value.copy(versionStatusError = false)
        _fillInState.value = FillInInitialState
    }

    fun setVersionIsDefault(it: Boolean) {
        _itemVersion.value = _itemVersion.value.copy(itemVersion = _itemVersion.value.itemVersion.copy(isDefault = it))
        _itemVersionErrors.value = _itemVersionErrors.value.copy(versionIsDefaultError = false)
        _fillInState.value = FillInInitialState
    }

    private val _fillInState = MutableStateFlow<FillInState>(FillInInitialState)
    val fillInState get() = _fillInState.asStateFlow()

    // Lists state ------------------------------
    val characteristicGroups = _itemVersionTolerances.flatMapLatest { list ->
        _charGroupVisibility.flatMapLatest { visibility ->
            _characteristicToAdd.value = _characteristicToAdd.value.copy(first = visibility.first.num)
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
    val characteristicSubGroups = _itemVersionTolerances.flatMapLatest { list ->
        _charGroupVisibility.flatMapLatest { gVisibility ->
            _charSubGroupVisibility.flatMapLatest { sgVisibility ->
                _characteristicToAdd.value = _characteristicToAdd.value.copy(second = sgVisibility.first.num)
                flow {
                    emit(list.filter { it.metricWithParents.groupId == gVisibility.first.num }.distinctBy { it.metricWithParents.subGroupId }.map {
                        it.metricWithParents.run {
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
    val characteristics = _itemVersionTolerances.flatMapLatest { list ->
        _charSubGroupVisibility.flatMapLatest { sgVisibility ->
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

    private val _indexOfToleranceErrorRow = MutableStateFlow(NoRecord.num)
    val indexOfToleranceErrorRow get() = _indexOfToleranceErrorRow.asStateFlow()
    fun clearIndexOfToleranceErrorRow() {
        _indexOfToleranceErrorRow.value = NoRecord.num
    }

    val tolerances = _itemVersionTolerances.flatMapLatest { list ->
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
                                    units = metricUnits,
                                    detailsVisibility = tVisibility.first.num == metricId,
                                    isExpanded = tVisibility.second.num == metricId
                                )
                            },
                            second = it.itemTolerance
                        )
                    }.sortedBy { it.first.metrixOrder })
                }
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    fun setActuality(toleranceId: ID, value: Boolean) {
        _itemVersionTolerances.value = _itemVersionTolerances.value.map {
            if (it.itemTolerance.id == toleranceId) it.copy(itemTolerance = it.itemTolerance.copy(isActual = value)) else it
        }
    }

    fun setLsl(toleranceId: ID, value: String) {
        _itemVersionTolerances.value = _itemVersionTolerances.value.map {
            if (it.itemTolerance.id == toleranceId) {
                value.toFloatOrNull()?.let { lsl ->
                    it.copy(itemTolerance = it.itemTolerance.copy(lsl = lsl, isLslError = false))
                } ?: run {
                    if (value.isNotEmpty() && value != NoString.str)
                        it.copy(itemTolerance = it.itemTolerance.copy(isLslError = true))
                    else
                        it.copy(itemTolerance = it.itemTolerance.copy(isLslError = false, lsl = null))
                }
            } else it
        }
    }

    fun setNominal(toleranceId: ID, value: String) {
        _itemVersionTolerances.value = _itemVersionTolerances.value.map {
            if (it.itemTolerance.id == toleranceId) {
                value.toFloatOrNull()?.let { nominal ->
                    it.copy(itemTolerance = it.itemTolerance.copy(nominal = nominal, isNominalError = false))
                } ?: run {
                    if (value.isNotEmpty() && value != NoString.str)
                        it.copy(itemTolerance = it.itemTolerance.copy(isNominalError = true))
                    else
                        it.copy(itemTolerance = it.itemTolerance.copy(isNominalError = false, nominal = null))
                }
            } else it
        }
    }

    fun setUsl(toleranceId: ID, value: String) {
        _itemVersionTolerances.value = _itemVersionTolerances.value.map {
            if (it.itemTolerance.id == toleranceId) {
                value.toFloatOrNull()?.let { usl ->
                    it.copy(itemTolerance = it.itemTolerance.copy(usl = usl, isUslError = false))
                } ?: run {
                    if (value.isNotEmpty() && value != NoString.str)
                        it.copy(itemTolerance = it.itemTolerance.copy(isUslError = true))
                    else
                        it.copy(itemTolerance = it.itemTolerance.copy(isUslError = false, usl = null))
                }
            } else it
        }
    }

    // Rendered state --------------------------------------
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
        characteristicGroups.flatMapLatest { firstList ->
            _secondListIsInitialized.flatMapLatest { secondListState ->
                if (viewState)
                    flow {
                        if (_charGroupVisibility.value.first.num != NoRecord.num) {
                            storage.setLong(ScrollStates.VERSION_CHAR_GROUPS.indexKey, firstList.map { it.id }.indexOf(_charGroupVisibility.value.first.num).toLong())
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

    private val _secondListIsInitialized: Flow<Boolean> = tolerances.flatMapLatest { secondList ->
        flow {
            if (_toleranceVisibility.value.first.num != NoRecord.num) {
                storage.setLong(ScrollStates.VERSION_TOLERANCES.indexKey, secondList.map { it.first.id }.indexOf(_toleranceVisibility.value.first.num).toLong())
                storage.setLong(ScrollStates.VERSION_TOLERANCES.offsetKey, ZeroValue.num)
                emit(true)
            } else {
                emit(true)
            }
        }
    }

    /**
     * UI operations ---------------------------------------------------------------------------------------------------------------------------------
     * */
    fun setCharGroupsVisibility(dId: SelectedNumber = NoRecord, aId: SelectedNumber = NoRecord) {
        _charGroupVisibility.value = _charGroupVisibility.value.setVisibility(dId, aId)
    }

    fun setCharSubGroupsVisibility(dId: SelectedNumber = NoRecord, aId: SelectedNumber = NoRecord) {
        _charSubGroupVisibility.value = _charSubGroupVisibility.value.setVisibility(dId, aId)
    }

    fun setCharacteristicsVisibility(dId: SelectedNumber = NoRecord, aId: SelectedNumber = NoRecord) {
        tolerances.value.let { values ->
            listOf(
                values.indexOfFirst { it.second.isLslError },
                values.indexOfFirst { it.second.isNominalError },
                values.indexOfFirst { it.second.isUslError }
            ).filter { it != NoRecord.num.toInt() }.minOrNull()?.let {
                _indexOfToleranceErrorRow.value = it.toLong()
            } ?: run {
                _indexOfToleranceErrorRow.value = NoRecord.num
                _characteristicVisibility.value = _characteristicVisibility.value.setVisibility(dId, aId)
            }
        }
    }

    /**
     * PRE REST operations -------------------------------------------------------------------------------------------------------------------------------
     * */

    fun addCharacteristic(versionFId: String) {
        TODO("Not yet implemented")
    }

    fun deleteCharacteristic(id: ID) {
        TODO("Not yet implemented")
    }

    /**
     * REST operations -------------------------------------------------------------------------------------------------------------------------------
     * */
    private fun updateTolerancesData() {
        TODO("Not yet implemented")
    }

    private fun onFabClick() {
        val fabIcon = if (_versionEditMode.value) {
//            ToDoMe - save all changes here
            Icons.Filled.Edit
        } else {
            Icons.Filled.Save
        }
        _versionEditMode.value = !_versionEditMode.value
        page.value = page.value.withCustomFabIcon(fabIcon)
        viewModelScope.launch { mainPageHandler?.setFabIcon?.invoke(fabIcon) }
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