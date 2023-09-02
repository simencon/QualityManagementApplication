package com.simenko.qmapp.ui.main.settings

import androidx.lifecycle.ViewModel
import com.simenko.qmapp.repository.UserRepository
import com.simenko.qmapp.repository.UserState
import com.simenko.qmapp.storage.Principle
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
    val userState: StateFlow<UserState>
        get() = userRepository.userState

    val userLocalData: Principle
        get() = userRepository.user

    fun logout() {
        userRepository.logout()
    }

    fun deleteAccount(userEmail: String, password: String) {
        userRepository.deleteAccount(userEmail, password)
    }

    fun getUserData() {
        userRepository.getUserData()
    }

    fun updateUserCompleteData() {
        userRepository.updateUserCompleteData()
    }

    private val _isApproveActionVisible = MutableStateFlow(false)
    val isApproveActionVisible: StateFlow<Boolean> = _isApproveActionVisible
    fun hideActionApproveDialog() {
        _isApproveActionVisible.value = false
    }
    fun showActionApproveDialog() {
        _isApproveActionVisible.value = true
    }
}
