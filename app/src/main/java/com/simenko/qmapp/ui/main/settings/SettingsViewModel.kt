package com.simenko.qmapp.ui.main.settings

import androidx.lifecycle.ViewModel
import com.simenko.qmapp.other.Event
import com.simenko.qmapp.repository.UserRepository
import com.simenko.qmapp.repository.UserState
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
    val userState: StateFlow<Event<UserState>>
        get() = userRepository.userState

    fun getUserEmail(): String = userRepository.userEmail

    fun getUserPassword(userEmail: String): String {
        return userRepository.getUserPassword(userEmail)
    }

    fun logout() {
        userRepository.logout()
    }

    fun deleteAccount(userEmail: String, password: String) {
        userRepository.deleteAccount(userEmail, password)
    }

    fun getUserData() {
        userRepository.getUserData(getUserEmail(), getUserPassword(getUserEmail()))
    }

    fun logUserData() {
        userRepository.logUserData(null)
    }

    fun updateUserData() {
        userRepository.updateUserData(null)
    }

    fun createUserOnApiSide() {
        userRepository.createNewUser()
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
