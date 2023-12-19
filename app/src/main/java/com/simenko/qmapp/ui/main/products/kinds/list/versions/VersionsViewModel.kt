package com.simenko.qmapp.ui.main.products.kinds.list.versions

import com.simenko.qmapp.di.CharacteristicIdParameter
import com.simenko.qmapp.di.ToleranceIdParameter
import com.simenko.qmapp.di.VersionFIdParameter
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.domain.SelectedNumber
import com.simenko.qmapp.repository.ProductsRepository
import com.simenko.qmapp.storage.Storage
import com.simenko.qmapp.ui.main.main.MainPageState
import com.simenko.qmapp.ui.navigation.AppNavigator
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
class VersionsViewModel @Inject constructor(
    private val appNavigator: AppNavigator,
    private val mainPageState: MainPageState,
    private val repository: ProductsRepository,
    val storage: Storage,
    @VersionFIdParameter val versionFId: String,
    @CharacteristicIdParameter val characteristicId: ID,
    @ToleranceIdParameter val toleranceId: ID
) {
    private val _characteristicVisibility = MutableStateFlow(Pair(SelectedNumber(characteristicId), NoRecord))
    private val _toleranceId = MutableStateFlow(Pair(SelectedNumber(toleranceId), NoRecord))
    private val _characteristics = repository.versionCharacteristics(versionFId)
    private val _characteristicTolerances = _characteristicVisibility.flatMapLatest { repository.characteristicTolerances(versionFId, it.first.num) }
}