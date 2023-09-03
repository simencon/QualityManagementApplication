package com.simenko.qmapp.ui.main.settings

import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import com.simenko.qmapp.repository.UserRepository
import com.simenko.qmapp.repository.UserState
import com.simenko.qmapp.storage.Principle
import com.simenko.qmapp.ui.main.MainActivityViewModel
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
    private lateinit var _mainActivityViewModel: MainActivityViewModel
    fun initMainActivityViewModel(viewModel: MainActivityViewModel) {
        this._mainActivityViewModel = viewModel
    }

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

    fun clearLoadingState() {
        _mainActivityViewModel.updateLoadingState(Pair(false, null))
    }

    fun logout() {
        _mainActivityViewModel.updateLoadingState(Pair(true, null))
        userRepository.logout()
    }

    fun deleteAccount(userEmail: String, password: String) {
        _mainActivityViewModel.updateLoadingState(Pair(true, null))
        userRepository.deleteAccount(userEmail, password)
    }

    fun getUserData() {
        _mainActivityViewModel.updateLoadingState(Pair(true, null))
        userRepository.getUserData()
    }

    fun updateUserCompleteData() {
        _mainActivityViewModel.updateLoadingState(Pair(true, null))
        userRepository.updateUserCompleteData()
    }
}