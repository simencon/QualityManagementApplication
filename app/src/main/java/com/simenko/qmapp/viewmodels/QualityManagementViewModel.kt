package com.simenko.qmapp.viewmodels

import android.app.Application
import androidx.lifecycle.*
import com.simenko.qmapp.domain.DomainDepartment
import com.simenko.qmapp.network.QualityManagementNetwork
import com.simenko.qmapp.network.asDomainModel
import com.simenko.qmapp.repository.DepartmentsRepository
import com.simenko.qmapp.room.getDatabase
import kotlinx.coroutines.launch
import java.io.IOException

class QualityManagementViewModel(application: Application) : AndroidViewModel(application) {

    init {
        refreshDataFromNetwork()
//        refreshDataFromRepository()
    }

    /**
     * Factory for constructing QualityManagementViewModel with parameter
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

    fun onNetworkErrorShown() {
        _isNetworkErrorShown.value = true
    }


    private fun refreshDataFromRepository() {
        viewModelScope.launch {
            try {
                departmentsRepository
                    .refreshDepartments()
                _eventNetworkError.value = false
                _isNetworkErrorShown.value = false

            } catch (networkError: IOException) {
                // Show a Toast error message and hide the progress bar.
                if (departments.value.isNullOrEmpty())
                    _eventNetworkError.value = true
            }
        }
    }

    private fun refreshDataFromNetwork() = viewModelScope.launch {
        try {
            val departments = QualityManagementNetwork.serviceholder.getDepartments()
            _departments.postValue(departments.asDomainModel())

            _eventNetworkError.value = false
            _isNetworkErrorShown.value = false

        } catch (networkError: IOException) {
            // Show a Toast error message and hide the progress bar.
            _eventNetworkError.value = true
        }
    }

    /**
     *
     */
    private val departmentsRepository = DepartmentsRepository(getDatabase(application))
    val departments = departmentsRepository.departments

    private val _departments = MutableLiveData<List<DomainDepartment>>() //in use in case when works directly from internet
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
}