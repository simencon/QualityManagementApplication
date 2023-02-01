package com.simenko.qmapp.viewmodels

import android.app.Application
import androidx.lifecycle.*
import com.simenko.qmapp.repository.QualityManagementRepository
import com.simenko.qmapp.room_implementation.getDatabase
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import java.io.IOException

class QualityManagementViewModel(application: Application) : AndroidViewModel(application) {
    /**
     * Gets data from [QualityManagementRepository.departments] - which is live data with list
     */
    private val qualityManagementRepository = QualityManagementRepository(getDatabase(application))

    val departments = qualityManagementRepository.departments
    val teamMembers = qualityManagementRepository.teamMembers
    val departmentsDetailed = qualityManagementRepository.departmentsDetailed
    val inputForOrder = qualityManagementRepository.inputForOrder

    /**
     *
     */
    private var _eventNetworkError = MutableLiveData<Boolean>(false)
    val eventNetworkError: LiveData<Boolean>
        get() = _eventNetworkError

    /**
     *
     */
    private var _isNetworkErrorShown = MutableLiveData<Boolean>(false)
    val isNetworkErrorShown: LiveData<Boolean>
        get() = _isNetworkErrorShown

    /**
     *
     */
    init {
        refreshDataFromRepository()
    }

    /**
     * Factory for constructing ViewModel with into specific application
     * Used here [com.simenko.qmapp.fragments.___DepartmentFragment.viewModel]
     */
    class Factory(val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(QualityManagementViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return QualityManagementViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }

    /**
     * Used in [com.simenko.qmapp.fragments.___DepartmentFragment.onNetworkError] to change trigger value
     */

    fun onNetworkErrorShown() {
        _isNetworkErrorShown.value = true
    }

    /**
     * Runs every time when ViewModel in initializing process
     */


    private fun refreshDataFromRepository() {
        val lock = Mutex()
        var job: Job? = null
        viewModelScope.launch {
            try {
//                runBlocking {
                    qualityManagementRepository.refreshCompanies()
                    qualityManagementRepository.refreshTeamMembers()
                    qualityManagementRepository.refreshDepartments()
                    qualityManagementRepository.refreshInputForOrder()
//                }
                _eventNetworkError.value = false
                _isNetworkErrorShown.value = false

            } catch (networkError: IOException) {
                // Show a Toast error message and hide the progress bar.
                if (departments.value.isNullOrEmpty())
                    _eventNetworkError.value = true
            }
        }
    }

}