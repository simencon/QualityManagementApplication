package com.simenko.qmapp.ui.main.settings

import androidx.lifecycle.ViewModel
import com.simenko.qmapp.repository.UserRepository
import com.simenko.qmapp.repository.UserState
import com.simenko.qmapp.storage.Principle
import com.simenko.qmapp.ui.main.AddEditMode
import com.simenko.qmapp.ui.main.MainActivityViewModel
import com.simenko.qmapp.ui.user.registration.enterdetails.EnterDetailsViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/**
 * SettingsViewModel is the ViewModel that [SettingsFragment] uses to handle complex logic.
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    private lateinit var _mainViewModel: MainActivityViewModel
    fun initMainActivityViewModel(viewModel: MainActivityViewModel) {
        this._mainViewModel = viewModel
    }

    fun setAddEditMode(addEditMode: AddEditMode) {
        _mainViewModel.setAddEditMode(addEditMode)
    }

    fun getAddEditMode() = _mainViewModel.addEditMode.value

    private val _isApproveActionVisible = MutableStateFlow(false)
    val isApproveActionVisible: StateFlow<Boolean> = _isApproveActionVisible
    fun hideActionApproveDialog() {
        _isApproveActionVisible.value = false
    }

    fun showActionApproveDialog() {
        _isApproveActionVisible.value = true
    }

    val userState: StateFlow<UserState> get() = userRepository.userState
    val userLocalData: Principle get() = userRepository.user

    fun clearLoadingState(error: String? = null) {
        _mainViewModel.updateLoadingState(Pair(false, error))
        userRepository.clearErrorMessage()
    }

    fun logout() {
        _mainViewModel.updateLoadingState(Pair(true, null))
        userRepository.logout()
    }

    fun deleteAccount(userEmail: String, password: String) {
        _mainViewModel.updateLoadingState(Pair(true, null))
        userRepository.deleteAccount(userEmail, password)
    }

    fun updateUserData() {
        _mainViewModel.updateLoadingState(Pair(true, null))
        userRepository.updateUserData()
    }

    private lateinit var _userDetailsModel: EnterDetailsViewModel

    fun initUserDetailsModel(model: EnterDetailsViewModel) {
        this._userDetailsModel = model
    }

    private val _performEditUserData: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val performEditUserData: StateFlow<Boolean> get() = _performEditUserData
    fun setPerformEditUserData(value: Boolean) {
        this._performEditUserData.value = value
    }

    fun validateInput() = _userDetailsModel.validateInput()

    fun editUserData() {
        assert(userRepository.rawUser != null)
        _mainViewModel.updateLoadingState(Pair(true, null))
        userRepository.editUserData(userRepository.rawUser!!)
    }
}