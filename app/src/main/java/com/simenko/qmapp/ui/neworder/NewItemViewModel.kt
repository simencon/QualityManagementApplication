package com.simenko.qmapp.ui.neworder

import android.content.Context
import androidx.lifecycle.*
import com.simenko.qmapp.di.main.MainScope
import com.simenko.qmapp.di.neworder.NewItemScope
import com.simenko.qmapp.domain.*
import com.simenko.qmapp.repository.QualityManagementInvestigationsRepository
import com.simenko.qmapp.repository.QualityManagementManufacturingRepository
import com.simenko.qmapp.repository.QualityManagementProductsRepository
import com.simenko.qmapp.room.implementation.getDatabase
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@NewItemScope
class NewItemViewModel @Inject constructor(
    context: Context
) : ViewModel() {
    /**
     * Gets data from Repositories - which is live data with list
     */

    private val roomDatabase = getDatabase(context)

    private val qualityManagementInvestigationsRepository =
        QualityManagementInvestigationsRepository(roomDatabase)

    init {
        refreshDataFromRepository()
    }

    private val pairedTrigger: MutableLiveData<Boolean> = MutableLiveData(true)

    val inputForOrder = qualityManagementInvestigationsRepository.inputForOrder
    val inputForOrderMediator: MediatorLiveData<Pair<List<DomainInputForOrder>?, Boolean?>> =
        MediatorLiveData<Pair<List<DomainInputForOrder>?, Boolean?>>().apply {
            addSource(inputForOrder) { value = Pair(it, pairedTrigger.value) }
            addSource(pairedTrigger) { value = Pair(inputForOrder.value, it) }
        }

    /**
     *
     */
    private var _eventNetworkError = MutableLiveData<Boolean>(false)
    val eventNetworkError: LiveData<Boolean>
        get() = _eventNetworkError

    private var _isNetworkErrorShown = MutableLiveData<Boolean>(false)
    val isNetworkErrorShown: LiveData<Boolean>
        get() = _isNetworkErrorShown

    fun onNetworkErrorShown() {
        _isNetworkErrorShown.value = true
    }

    /**
     * Runs every time when ViewModel in initializing process
     */

    private fun refreshDataFromRepository() {
        viewModelScope.launch {
            try {
                qualityManagementInvestigationsRepository.refreshInputForOrder()
            } catch (networkError: IOException) {
                // Show a Toast error message and hide the progress bar.
                if (inputForOrder.value.isNullOrEmpty())
                    _eventNetworkError.value = true
            }
        }
    }

}