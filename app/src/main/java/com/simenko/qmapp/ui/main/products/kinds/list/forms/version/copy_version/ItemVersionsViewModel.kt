package com.simenko.qmapp.ui.main.products.kinds.list.forms.version.copy_version

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simenko.qmapp.domain.ComponentPref
import com.simenko.qmapp.domain.ComponentStagePref
import com.simenko.qmapp.domain.EmptyString
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.domain.NoRecordStr
import com.simenko.qmapp.domain.ProductPref
import com.simenko.qmapp.domain.entities.products.DomainItemVersionComplete
import com.simenko.qmapp.domain.entities.products.DomainKey
import com.simenko.qmapp.repository.ProductsRepository
import com.simenko.qmapp.ui.navigation.AppNavigator
import com.simenko.qmapp.ui.navigation.Route
import com.simenko.qmapp.utils.StringUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ItemVersionsViewModel @Inject constructor(
    private val appNavigator: AppNavigator,
    val repository: ProductsRepository,
) : ViewModel() {
    private val _availableKeys = MutableStateFlow(emptyList<DomainKey>())
    private val _route = MutableStateFlow(Route.Main.ProductLines.ProductKinds.Products.CopyItemVersion(NoRecord.num, NoRecordStr.str))

    private val _filterKeyId = MutableStateFlow(NoRecord.num)
    private val _filterItemFId = MutableStateFlow(NoRecordStr.str)
    private val _searchValue = MutableStateFlow(EmptyString.str)
    private val _selectedVersionFId = MutableStateFlow(NoRecordStr.str)

    private val _allItemVersions = MutableStateFlow<List<DomainItemVersionComplete>>(emptyList())

    /**
     * Main page setup -------------------------------------------------------------------------------------------------------------------------------
     * */
    fun onEntered(route: Route.Main.ProductLines.ProductKinds.Products.CopyItemVersion) {
        viewModelScope.launch(Dispatchers.IO) {
            _route.value = route

            val currentItem = repository.itemComplete(route.itemFId)
            _filterKeyId.value = currentItem.key.id
            _filterItemFId.value = currentItem.item.fId

            when (route.itemFId.firstOrNull()) {
                ProductPref.char -> {
                    repository.productKind(route.itemKindID).let { pk ->
                        _availableKeys.value = repository.productKindKeysByParent(pk.productKind.id).map { pkk -> pkk.key.productLineKey }
                    }
                }

                ComponentPref.char -> {
                    repository.componentKind(route.itemKindID).let { ck ->
                        _availableKeys.value = repository.componentKindKeysByParent(ck.componentKind.id).map { ckk -> ckk.key.productLineKey }
                    }
                }

                ComponentStagePref.char -> {
                    repository.componentStageKind(route.itemKindID).let { sk ->
                        _availableKeys.value = repository.componentStageKindKeysByParent(sk.componentStageKind.id).map { skk -> skk.key.productLineKey }
                    }
                }
            }


            launch {
                combine(repository.itemVersionsComplete(NoRecordStr.str), _availableKeys) { list, keys ->
                    list.filter { itemVersion ->
                        keys
                            .distinctBy { it.id }
                            .map { it.id }
                            .contains(itemVersion.itemComplete.key.id)
                    }
                }.collect {
                    _allItemVersions.value = it
                }
            }
        }
    }

    /**
     * UI State --------------------------------------------------------------------------------------------------------------------------------------
     * */
    val availableDesignations = _availableKeys.flatMapLatest { list ->
        _filterKeyId.flatMapLatest { keyId ->
            flow {
                emit(list.map { Triple(it.id, it.componentKey, it.id == keyId) })
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    fun onSelectDesignation(id: ID) {
        if (_filterKeyId.value != id) _filterKeyId.value = id
    }

    val availableItems = _allItemVersions.flatMapLatest { list ->
        _filterKeyId.flatMapLatest { keyId ->
            _filterItemFId.flatMapLatest { itemId ->
                flow {
                    emit(list
                        .distinctBy { it.itemComplete.item.id }
                        .filter { it.itemComplete.key.id == keyId }
                        .map {
                            Triple(
                                first = it.itemComplete.item.fId,
                                second = StringUtils.concatTwoStrings3(it.itemComplete.key.componentKey, it.itemComplete.item.itemDesignation),
                                third = it.itemComplete.item.fId == itemId
                            )
                        }
                    )
                }
            }
        }
    }

    fun onVersionItem(fId: String) {
        if (_filterItemFId.value != fId) _filterItemFId.value = fId
    }

    val searchValue = _searchValue.asStateFlow()
    fun onChangeSearchValue(value: String) {
        if (_searchValue.value != value) _searchValue.value = value
    }

    val availableVersions = _allItemVersions.flatMapLatest { list ->
        _filterKeyId.flatMapLatest { keyId ->
            _filterItemFId.flatMapLatest { itemId ->
                _searchValue.flatMapLatest { searchValue ->
                    _selectedVersionFId.flatMapLatest { versionId ->
                        flow {
                            emit(
                                list
                                    .asSequence()
                                    .filter { it.itemComplete.key.id == keyId }
                                    .filter { it.itemComplete.item.fId == itemId }
                                    .filter { searchValue.isEmpty() || (it.itemComplete.item.itemDesignation?.lowercase()?.contains(searchValue.lowercase()) ?: false) }
                                    .map { it.copy(isSelected = it.itemVersion.fId == versionId) }
                                    .toList())
                        }
                    }
                }
            }
        }
    }

    fun onSelectVersion(fId: String) {
        if (_selectedVersionFId.value != fId) {
            _selectedVersionFId.value = fId
        }
    }

    val isReadyToCopy = _selectedVersionFId.flatMapLatest {
        flow { emit(it != NoRecordStr.str) }
    }

    /**
     * Navigation ------------------------------------------------------------------------------------------------------------------------------------
     * */

    fun onCopy() {
        appNavigator.tryNavigateTo(
            route = Route.Main.ProductLines.ProductKinds.Products.VersionTolerancesDetails(
                itemKindId = _route.value.itemKindID,
                itemFId = _route.value.itemFId,
                referenceVersionFId = _selectedVersionFId.value,
                versionEditMode = true
            )
        )
    }

    fun navBack() {
        appNavigator.tryNavigateBack(inclusive = false)
    }
}